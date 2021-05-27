package extensions

import inks.Riso
import org.ejml.simple.SimpleMatrix
import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extras.imageFit.imageFit
import java.io.File
import kotlin.math.ceil
import kotlin.math.sqrt

enum class RisoSeparatorMode {
    PASS_THROUGH,
    GALLERY,
    PLANAR
}

class RisoSeparator : Extension {
    override var enabled = true
    var mode = RisoSeparatorMode.PLANAR
    var colors = arrayOf(Riso.RED, Riso.BLUE)


    set(value) {
        field = value

        val data = Array(100) {
            ColorRGBa(Math.random(), Math.random(), Math.random())
        }


        val X = SimpleMatrix(data.size, colors.size)
        val Y = SimpleMatrix(data.size, 3)

        for (row in 0 until data.size) {
            for (c in 0 until colors.size) {
                X[row, c] =
                    data[row].r * (1.0 - colors[c].color.r) + data[row].g * (1.0 - colors[c].color.g) + data[row].b * (1.0 - colors[c].color.b)
                //X[row, c * 2 + 1] = 1.0

            }
           Y[row, 0] = data[row].r
           Y[row, 1] = data[row].g
           Y[row, 2] = data[row].b

        }
        val result = X.solve(Y)
        println("result size: ${result.numRows()} x ${result.numCols()}")
        coefficients = DoubleArray(result.numRows() * result.numCols())
        for (y in 0 until result.numRows()) {
            for (x in 0 until 3) {
                coefficients[y*3+x] = result.get(y, x)
            }
        }

    }

    init {
        colors = arrayOf(Riso.RED, Riso.BLUE)
    }

    var activeColor = -1
    var coefficients = doubleArrayOf(1.0, 0.0, 0.0,   0.0, 0.0, 1.0)

    private var save = false
    var rt = renderTarget(100, 100) {
        colorBuffer()
        depthBuffer()
    }
    val ss = shadeStyle {
        fragmentTransform = """
            vec3 c = x_fill.rgb;
            c = pow(c, vec3(2.2));
            if (p_activeColor >= 0) {
                float w = c.r * p_coefficients[p_activeColor*3] + 
                c.g * p_coefficients[p_activeColor*3+1] +
                c.b * p_coefficients[p_activeColor*3+2];
                w = clamp(w, 0.0, 1.0);
                if (!p_weightsOnly) {
                    x_fill.rgb = vec3(1.0) -  (vec3(1.0) - p_colors[p_activeColor].rgb) * (1.0-w);
                } else {
                    x_fill = vec4(w, w, w, 1.0);
                }
            } else {
               x_fill = vec4(1.0, 1.0, 1.0, 1.0);
               for (int i = 0; i < p_colorCount; ++i) {
                    float w = c.r * p_coefficients[i*3] + c.g * p_coefficients[i*3+1] + c.b * p_coefficients[i*3+2];
                    w = clamp(w, 0.0, 1.0);
                    x_fill.rgb -= (1.0-w) * (vec3(1.0) - p_colors[i].rgb);
                    
               }
            }
            x_fill.rgb = clamp(x_fill.rgb, vec3(0.0), vec3(1.0));
            x_fill.rgb = pow(x_fill.rgb, vec3(1.0/2.2)); 
        """
    }

    override fun setup(program: Program) {
        program.keyboard.character.listen {
            if (it.character == '1') {
                activeColor = 0
            }
            if (it.character == '2') {
                activeColor = 1
            }
            if (it.character == '3') {
                activeColor = 2
            }
            if (it.character == '4') {
                activeColor = 3
            }
            if (it.character == '5') {
                activeColor = 4
            }
            if (it.character == '0') {
                activeColor = -1
            }
            if (it.character == 's') {
                save = true
            }

            if (it.character == 'q') {
                mode = RisoSeparatorMode.PASS_THROUGH
            }
            if (it.character == 'w') {
                mode = RisoSeparatorMode.PLANAR
            }
            if (it.character == 'e') {
                mode = RisoSeparatorMode.GALLERY
            }
        }
    }

    override fun beforeDraw(drawer: Drawer, program: Program) {
        val art = RenderTarget.active
        if (!(art.width == rt.width && art.height == rt.height)) {
            rt.colorBuffer(0).destroy()
            rt.depthBuffer?.destroy()
            rt.detachColorAttachments()
            rt.detachDepthBuffer()
            rt.destroy()
            rt = renderTarget(art.width, art.height) {
                colorBuffer()
                depthBuffer()
            }
        }
        rt.bind()
    }

    override fun afterDraw(drawer: Drawer, program: Program) {
        rt.unbind()

        when (mode) {
            RisoSeparatorMode.PASS_THROUGH -> {
                drawer.defaults()
                drawer.image(rt.colorBuffer(0))
            }
            RisoSeparatorMode.PLANAR -> {
                drawer.isolated {
                    drawer.defaults()
                    drawer.shadeStyle = ss
                    ss.parameter("activeColor", activeColor)
                    ss.parameter("colors", colors.map { it.color }.toTypedArray())
                    ss.parameter("coefficients", coefficients)
                    ss.parameter("colorCount", colors.size)
                    ss.parameter("weightsOnly", false)
                    drawer.image(rt.colorBuffer(0))
                }
            }
            RisoSeparatorMode.GALLERY -> {

                val s = ceil(sqrt(colors.size+1.0)).toInt()

                drawer.isolated {
                    drawer.defaults()
                    drawer.shadeStyle = ss

                    var active = -1


                    outer@ for (y in 0 until s) {
                        for (x in 0 until s) {
                            ss.parameter("activeColor", active)
                            ss.parameter("colors", colors.map { it.color }.toTypedArray())
                            ss.parameter("coefficients", coefficients)
                            ss.parameter("colorCount", colors.size)
                            ss.parameter("weightsOnly", false)

                            val cellWidth = width.toDouble() / s
                            val cellHeight = height.toDouble() / s

                            drawer.imageFit(rt.colorBuffer(0),cellWidth * x, cellHeight * y, cellWidth, cellHeight)
                            active ++
                            if (active >= colors.size) {
                                break@outer
                            }

                        }

                    }



                }
            }
        }
        if (save) {
            val separationDir = File("separations")
            if (!separationDir.exists()) {
                separationDir.mkdirs()
            }

            val tempTarget = renderTarget(rt.width, rt.height) {
                colorBuffer()
            }
            val ts = System.currentTimeMillis()/1000

            for (i in 0 until colors.size) {
                drawer.isolatedWithTarget(tempTarget) {
                    drawer.ortho(tempTarget)
                    drawer.clear(ColorRGBa.TRANSPARENT)
                    drawer.shadeStyle = ss
                    ss.parameter("colors", colors.map { it.color }.toTypedArray())
                    ss.parameter("activeColor", i)
                    ss.parameter("weightsOnly", true)
                    ss.parameter("coefficients", coefficients)
                    ss.parameter("colorCount", colors.size)
                    drawer.image(rt.colorBuffer(0))
                }
                tempTarget.colorBuffer(0).saveToFile(File(separationDir, "$ts-${colors[i].name.replace(" ","-")}.png"))

            }
            save = false
            tempTarget.colorBuffer(0).destroy()
            tempTarget.detachColorAttachments()
            tempTarget.destroy()

        }

    }



}