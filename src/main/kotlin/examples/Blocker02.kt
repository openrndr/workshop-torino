package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import tools.calculateBlocks

fun main() {
    application {
        program {
            val image = loadImage("data/images/pm5544.png")
            val blocks = calculateBlocks(image, 16, 16)

            extend {
                val length = (mouse.position.y / height) * 64.0
                drawer.clear(ColorRGBa.WHITE)
                drawer.stroke = ColorRGBa.BLACK
                drawer.strokeWeight = 1.0
                for (block in blocks) {
                    drawer.lineSegment(
                        block.x + block.width / 2.0,
                        block.y + block.height / 2.0,
                        block.x + block.width / 2.0 + block.cr * length,
                        block.y + block.height / 2.0 - block.br * length,
                    )
                }
            }
        }
    }
}

