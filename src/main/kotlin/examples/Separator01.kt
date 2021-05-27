package examples

import extensions.AdditiveSeparator
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage

fun main() = application {
    program {
        extend(AdditiveSeparator()) {
            colors = arrayOf(ColorRGBa.RED, ColorRGBa.WHITE)

        }
        val image = loadImage("data/images/cheeta.jpg")
        extend {
            drawer.image(image)
        }
    }
}