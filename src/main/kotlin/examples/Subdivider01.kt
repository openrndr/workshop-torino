package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import tools.leafNodes
import tools.subdivideImage

fun main() = application {
    configure {
        width = 1240/2
        height = 1748/2
    }
    program {
        val image = loadImage("data/images/cheeta.jpg")
        val treemap = subdivideImage(image, 11)

        extend {
            drawer.clear(ColorRGBa.WHITE)
            val leafNodes = treemap.leafNodes()
            val rectangles = leafNodes.map {
                it.area
            }
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 0.5
            drawer.fill = null
            drawer.rectangles(rectangles)
        }
    }
}