package xyz.nietongxue.watching

import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.awt.Image

interface State {
    fun getImage(): Image
    fun getLabel(): String
    fun blink(): Boolean = false
    fun getSecondImage(): Image? = null
}

class WatchingState(val context: ResourceLoader) : State {

    override fun getImage(): Image {
        return createImage(context.getResource("classpath:/watching.png"))
    }

    override fun getLabel(): String {
        return "Watching"
    }
}


open class OverlayState(val base: State, val imageResource: Resource, val addedString: String?) : State {
    override fun getImage(): Image {
        return overlay(base.getImage(), createImage(imageResource))
    }

    override fun getLabel(): String {
        return base.getLabel() + (addedString ?: "")
    }
}


class BlinkingOverlayState(
    base: State,
    imagePath: Resource,
    addedString: String?,
) : OverlayState(base, imagePath, addedString) {
    override fun blink(): Boolean = true

    override fun getSecondImage(): Image {
        return this.base.getImage()
    }
}


