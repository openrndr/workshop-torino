package examples

import extensions.AdditiveSeparator
import extensions.RisoSeparator
import inks.Riso
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage

fun main() = application {
    program {
       extend(RisoSeparator()) {
           colors = arrayOf(
               Riso.RED,
               Riso.GREEN
           )
        }

        val image = loadImage("data/images/pm5544.png")

        extend {
            drawer.image(image)
        }
    }
}