package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extra.noise.uniform
import tools.leafNodes
import tools.subdivideImage

fun main() = application {
    configure {
        width = 1240 / 2
        height = 1748 / 2
    }
    program {

        val rt = renderTarget(width, height) {
            colorBuffer()
        }

        extend {
            drawer.isolatedWithTarget(rt) {
                drawer.clear(ColorRGBa.WHITE)
                for (i in 0 until 3) {
                    drawer.fill = ColorRGBa.BLACK
                    drawer.circle(
                        Double.uniform(0.0, width.toDouble()),
                        Double.uniform(0.0, height.toDouble()),
                        Double.uniform(40.0, 300.0)
                    )
                }

            }
            drawer.clear(ColorRGBa.WHITE)

            println("subdividing")
            val treemap = subdivideImage(rt.colorBuffer(0), 11)

            val leafNodes = treemap.leafNodes()
            val rectangles = leafNodes.map {
                it.area
            }
            drawer.stroke = ColorRGBa.BLACK
            drawer.strokeWeight = 0.5
            drawer.fill = null
            drawer.rectangles(rectangles)
            Thread.sleep(1000)
        }
    }
}