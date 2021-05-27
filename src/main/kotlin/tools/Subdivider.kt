package tools

import org.openrndr.draw.ColorBuffer
import org.openrndr.shape.IntRectangle
import org.openrndr.shape.Rectangle
import studio.rndnr.packture.IntegralImage
import kotlin.random.Random

class RectNode(val area: Rectangle) {
    val children: MutableList<RectNode> = mutableListOf()
    var parent: RectNode? = null

    fun split4(xr: Double, yr: Double) {
        children.add(RectNode(area.sub(0.0, 0.0, xr, yr)))
        children.add(RectNode(area.sub(xr, 0.0, 1.0, yr)))
        children.add(RectNode(area.sub(0.0, yr, xr, 1.0)))
        children.add(RectNode(area.sub(xr, yr, 1.0, 1.0)))
    }

    fun split2H(r: Double) {
        children.add(RectNode(area.sub(0.0, 0.0, r, 1.0)))
        children.add(RectNode(area.sub(r, 0.0, 1.0, 1.0)))
    }

    fun split2V(r: Double) {
        children.add(RectNode(area.sub(0.0, 0.0, 1.0, r)))
        children.add(RectNode(area.sub(0.0, r, 1.0, 1.0)))
    }
}

fun RectNode.split(level: Int = 4, integralImage: IntegralImage) {
    if (level % 2 == 0) {
        if (area.width < 8.0) {
            return
        }
        val sum = integralImage.sum(area.toInt()).toDouble()
        var ratio = 0.0
        var sumRatio = -1.0
        if (sum > 0) {
            for (u in 1 until area.width.toInt()) {
                val r = IntRectangle(area.x.toInt(), area.y.toInt(), u, area.height.toInt())
                val sum0 = integralImage.sum(r).toDouble()
                val sum1 = (sum - sum0)
                if (sum0 / sum1 <= 1.0) {
                    ratio = u.toDouble() / area.width
                    sumRatio = sum0 / sum1
                } else {
                    break
                }
            }
        } else {
            ratio = 0.5
        }
        split2H(ratio)
    } else {
        if (area.height < 8.0) {
            return
        }
        val sum = integralImage.sum(area.toInt()).toDouble()
        var ratio = 0.0
        if (sum > 0) {
            for (u in 1 until area.height.toInt()) {
                val r = IntRectangle(area.x.toInt(), area.y.toInt(), area.width.toInt(), u)
                val sum0 = integralImage.sum(r).toDouble()
                val sum1 = sum - sum0
                if (sum0 / sum1 <= 1.0) {
                    ratio = u.toDouble() / area.height
                } else {
                    break
                }
            }
        } else {
            ratio = 0.5
        }
        split2V(ratio)
    }
    if (level > 0) {
        children.forEach {
            it.split(level - 1, integralImage)

        }
    }
}

fun RectNode.split(level: Int = 4, random: Random) {
    split4(random.nextDouble(0.1, 0.9), random.nextDouble(0.1, 0.9))
    children.forEach {
        it.parent = this
    }
    if (level > 0) {
        children.forEach {
            it.split(level - 1, random)
        }
    }
}

fun RectNode.leafNodes(): List<RectNode> {
    return if (children.isEmpty()) {
        listOf(this)
    } else {
        children.flatMap { it.leafNodes() }
    }
}

fun subdivideImage(image: ColorBuffer, levels:Int) : RectNode{
    val shadow = image.shadow
    shadow.download()
    val integralImage = IntegralImage.fromColorBufferShadow(shadow)
    val root = RectNode(Rectangle(0.0, 0.0, image.width.toDouble(), image.height.toDouble()))
    root.split(levels, integralImage)
    return root
}