package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import tools.calculateBlocks
import tools.drawBlock
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    application {

        configure {
            width = 1240 / 2
            height = 1748 / 2
        }

        program {

            val rt = renderTarget(width, height) {
                colorBuffer()
                depthBuffer()
            }

            extend {

                drawer.isolatedWithTarget(rt) {
                    drawer.clear(ColorRGBa.WHITE)

                    drawer.fill = ColorRGBa.RED
                    drawer.stroke = null
                    drawer.circle(
                        width / 2.0 + cos(seconds) * 200.0, height / 2.0 + sin(seconds) * 200.0,
                        200.0
                    )

                    drawer.fill = ColorRGBa.BLUE
                    drawer.stroke = null
                    drawer.circle(
                        width / 2.0 + cos(seconds*1.32) * 200.0, height / 2.0 + sin(seconds) * 200.0,
                        200.0
                    )

                }


                val blocks = calculateBlocks(rt.colorBuffer(0), 64, 64)

                drawer.clear(ColorRGBa.WHITE)
                drawer.stroke = null

                for (block in blocks) {
                    drawBlock(drawer, block, levels = 256)
                }
            }
        }
    }
}

