package extensions

import org.ejml.simple.SimpleMatrix
import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import java.io.File

class AdditiveSeparator : Extension {
    override var enabled = true
    var colors = arrayOf(ColorRGBa.RED, ColorRGBa.BLUE)
    set(value) {
        field = value

        val data = Array(100) {
            ColorRGBa(Math.random(), Math.random(), Math.random())
        }
        println("hey hey")

        val X = SimpleMatrix(data.size, colors.size)
        val Y = SimpleMatrix(data.size, 3)

        for (row in 0 until data.size) {
            for (c in 0 until colors.size) {
                X[row, c] = data[row].r * colors[c].r + data[row].g * colors[c].g + data[row].b * colors[c].b
            }
            Y[row, 0] = data[row].r
            Y[row, 1] = data[row].g
            Y[row, 2] = data[row].b

        }
        val result = X.solve(Y)
        coefficients = DoubleArray(result.numRows() * result.numCols())
        for (y in 0 until result.numRows()) {
            for (x in 0 until 3) {
                coefficients[y*3+x] = result.get(y, x)
            }
        }

    }

    init {
        colors = arrayOf(ColorRGBa.PINK, ColorRGBa.GREEN)
    }

    var activeColor = 0
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
                    x_fill = p_colors[p_activeColor] * w;
                } else {
                    x_fill = vec4(w, w, w, 1.0);
                }
            } else {
               x_fill = vec4(0.0, 0.0, 0.0, 1.0);
               for (int i = 0; i < p_colorCount; ++i) {
                    float w = c.r * p_coefficients[i*3] + 
                    c.g * p_coefficients[i*3+1] +
                    c.b * p_coefficients[i*3+2];
                    w = clamp(w, 0.0, 1.0);
                    x_fill.rgb += w * p_colors[i].rgb;
               }
            }
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
        drawer.isolated {
            drawer.defaults()
            drawer.shadeStyle = ss
            ss.parameter("activeColor", activeColor)
            ss.parameter("colors", colors)
            ss.parameter("coefficients", coefficients)
            ss.parameter("colorCount", colors.size)
            ss.parameter("weightsOnly", false)
            drawer.image(rt.colorBuffer(0))
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
                    ss.parameter("colors", colors)
                    ss.parameter("activeColor", i)
                    ss.parameter("weightsOnly", true)
                    ss.parameter("coefficients", coefficients)
                    ss.parameter("colorCount", colors.size)
                    drawer.image(rt.colorBuffer(0))
                }
                tempTarget.colorBuffer(0).saveToFile(File(separationDir, "$ts-${i}.png"))

            }
            save = false
            tempTarget.colorBuffer(0).destroy()
            tempTarget.detachColorAttachments()
            tempTarget.destroy()

        }

    }



}