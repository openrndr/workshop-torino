package examples

import org.openrndr.application
import org.openrndr.draw.loadImage
import tools.calculateBlocks
import tools.drawBlock

fun main() {
    application {
        program {
            val image = loadImage("data/images/cheeta.jpg")
            val blocks = calculateBlocks(image, 16, 16)

            extend {
                drawer.stroke = null
                drawer.strokeWeight = 0.0
                for (block in blocks) {
                    drawBlock(drawer, block, levels = 4)
                }
            }
        }
    }
}

