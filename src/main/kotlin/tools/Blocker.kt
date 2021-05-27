package tools

import org.ejml.simple.SimpleMatrix
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.draw.shadeStyle

class Block(
    val ar: Double, val br: Double, val cr: Double,
    val ag: Double, val bg: Double, val cg: Double,
    val ab: Double, val bb: Double, val cb: Double,
    val x: Double, val y: Double, val width: Double, val height: Double
)

fun calculateBlocks(image: ColorBuffer, blockWidth: Int, blockHeight: Int) : List<Block> {
    image.shadow.download()
    val blocks = mutableListOf<Block>()
    for (y in 0 until image.height / blockHeight) {
        for (x in 0 until image.width / blockWidth) {
            blocks.add(calculateBlock(image,x * blockWidth, y * blockHeight, blockWidth, blockHeight))
        }
    }
    return blocks
}


fun calculateBlock(colorBuffer: ColorBuffer, x: Int, y: Int, width: Int, height: Int): Block {
    val s = colorBuffer.shadow
    val Y = SimpleMatrix(width * height, 3)
    val X = SimpleMatrix(width * height, 3)

    var row = 0
    for (v in 0 until height) {
        for (u in 0 until width) {
            X.set(row, 0, 1.0)
            X.set(row, 1, (u / (width - 1.0)) - 0.5)
            X.set(row, 2, (v / (height - 1.0)) - 0.5)
            Y.set(row, 0, s[u + x, v + y].toLinear().r)
            Y.set(row, 1, s[u + x, v + y].toLinear().g)
            Y.set(row, 2, s[u + x, v + y].toLinear().b)
            row++
        }
    }
    val m = X.solve(Y)
    return Block(
        m.get(0, 0), m.get(1, 0), m.get(2, 0),
        m.get(0, 1), m.get(1, 1), m.get(2, 1),
        m.get(0, 2), m.get(1, 2), m.get(2, 2),
        x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble()
    )
}

val blockSS = shadeStyle {

    fragmentPreamble = """
        float q(float x, int levels) {
            return floor(x*levels)/(levels-1.0);
        }
    """.trimIndent()

    fragmentTransform = """
        float rv = p_reg[0] + p_reg[1] * (va_texCoord0.x-0.5) + p_reg[2] * (va_texCoord0.y-0.5);
        float gv = p_reg[3] + p_reg[4] * (va_texCoord0.x-0.5) + p_reg[5] * (va_texCoord0.y-0.5);
        float bv = p_reg[6] + p_reg[7] * (va_texCoord0.x-0.5) + p_reg[8] * (va_texCoord0.y-0.5);
        rv = clamp(rv, 0.0, 1.0);
        rv = pow(rv, 1.0/ 2.2);
        rv = q(rv, p_levels);
        
        gv = clamp(gv, 0.0, 1.0);
        gv = pow(gv, 1.0/ 2.2);
        gv = q(gv, p_levels);
        
        bv = clamp(bv, 0.0, 1.0);
        bv = pow(bv, 1.0/ 2.2);
        bv = q(bv, p_levels);                
                
        
                
         x_fill.rgb = vec3(rv,gv,bv);
    """.trimIndent()

}

fun drawBlock(drawer: Drawer, block: Block,
              x: Double = block.x,
              y: Double = block.y,
              width: Double = block.width,
              height: Double = block.height, levels:Int = 4) {

    drawer.isolated {
        drawer.shadeStyle = blockSS
        blockSS.parameter("reg", doubleArrayOf(
            block.ar, block.br, block.cr,
            block.ag, block.bg, block.cg,
            block.ab, block.bb, block.cb,
        ))
        blockSS.parameter("levels", levels)
        drawer.rectangle(x, y, width, height)
    }

}