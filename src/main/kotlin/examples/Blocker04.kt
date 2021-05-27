package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.text.Cursor
import org.openrndr.text.writer
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

                    drawer.fontMap = loadFont("data/fonts/default.otf", 196.0)

                    drawer.clear(ColorRGBa.WHITE)
                    drawer.fill = ColorRGBa.BLACK
                    writer {
                        box = Rectangle(50.0, 150.0, 1000.0, 1000.0)
                        text("HELLO")
                        newLine()
                        text("WORLD")
                        newLine()
                        text("TORINO")
                        newLine()
                        text("2021")
                    }

                }


                val blocks = calculateBlocks(rt.colorBuffer(0), 8, 16)

                drawer.clear(ColorRGBa.WHITE)
                //drawer.stroke = ColorRGBa.WHITE
                drawer.stroke = null
                drawer.strokeWeight = 0.25

                for (block in blocks) {
                    drawBlock(drawer, block, levels = 4)
                }
            }
        }
    }
}

