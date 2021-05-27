package examples

import extensions.AdditiveSeparator
import extensions.RisoSeparator
import inks.Riso
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.noise.uniform

fun main() = application {
    configure {
        width = 1240/2
        height = 1748/2

    }

    program {
       extend(RisoSeparator()) {
           colors = arrayOf(
               Riso.BLUE,
               Riso.SCARLET,


           )
        }

        val image = loadImage("data/images/pm5544.png")

        extend {
            drawer.clear(ColorRGBa.WHITE)

            val baseColor = ColorRGBa.RED

            for (i in 0 until 100) {
                drawer.fill = baseColor.toHSVa().shiftHue(i*3.60).toRGBa().toSRGB()
                val x = simplex(4032, seconds+i*0.01) * 500.0 + 500.0
                val y = simplex(3467, -seconds-i*0.01432) * 500.0 + 500.0



                drawer.circle(x, y, 100.0)
            }

        }
    }
}