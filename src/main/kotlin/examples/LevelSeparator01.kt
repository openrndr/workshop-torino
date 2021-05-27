package examples

import extensions.LevelSeparator
import inks.Riso
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.IndexType
import org.openrndr.draw.indexBuffer
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import org.openrndr.extra.shadestyles.linearGradient

fun main() = application {
    configure {
        width = 1240 / 2
        height = 1748 / 2
    }
    program {
        extend(LevelSeparator()) {
            colors = arrayOf(
                Riso.BLACK,
                Riso.RED,
                Riso.LAGOON,
            Riso.YELLOW,
            )
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.shadeStyle = linearGradient(ColorRGBa.BLACK, ColorRGBa.WHITE)
            drawer.stroke = null
            drawer.rectangle(drawer.bounds)

            val ib = indexBuffer(100, IndexType.INT16)

        }

    }

}