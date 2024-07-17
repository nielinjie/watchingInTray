package com.hykj.watching

import org.springframework.core.io.Resource
import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


fun createImage(resource: Resource): Image {
    val imageURL = resource.url
    return ImageIO.read(imageURL)
}

fun overlay(image: Image, upperImage: Image): Image {
    require(image is BufferedImage)
    val x = (image.getWidth(null) / 2)
    val y = (image.getHeight(null) / 2)
    //copy an image
    val newImage = deepCopy(image)
    val g = newImage.graphics
    g.drawImage(resize(upperImage, x, y), x, y, null)
    g.dispose()
    return newImage
}

fun resize(image: Image, toWidth: Int, toHeight: Int): Image {
    val resized = image.getScaledInstance(toWidth, toHeight, Image.SCALE_FAST)
    return resized
}


fun deepCopy(bi: BufferedImage): BufferedImage {
    val cm = bi.colorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = bi.copyData(null)
    return BufferedImage(cm, raster, isAlphaPremultiplied, null)
}