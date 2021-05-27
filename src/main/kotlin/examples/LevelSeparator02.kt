package examples

import extensions.LevelSeparator
import inks.Riso
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint

fun main() = application {
    configure {
        width = 1240 / 2
        height = 1748 / 2
    }

    program {

        extend(LevelSeparator()) {
            colors = arrayOf(
                Riso.LAGOON,

            Riso.BLACK

            )

        }
        val image = loadImage("data/images/cheeta.jpg")

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(0.5))
            for (i in 0 until 5) {
                drawer.image(image)
                drawer.translate(0.0, 100.0)
            }


//            drawer.fill = ColorRGBa.BLACK.opacify(0.5)
//            drawer.circle(width/2.0, height/2.0, 200.0)
//            drawer.fill = ColorRGBa.GRAY
//            drawer.circle(width/2.0, height/2.0, 100.0)
//
//            drawer.fill = ColorRGBa.WHITE.shade(0.9)
//            drawer.circle(width/2.0, height/2.0, 50.0)
        }

    }

}