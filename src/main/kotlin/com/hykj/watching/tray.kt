package com.hykj.watching

import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import java.awt.*
import java.util.*


class TrayComponent(
    val providers: List<EntryItemProvider>,
    val context: ConfigurableApplicationContext,
    initState: State = WatchingState(context)

) {
    val icon = TrayIcon(initState.getImage(), initState.getLabel())
    private var state = initState
    private val popup = PopupMenu()
    val logger = LoggerFactory.getLogger(TrayComponent::class.java)
    val tray: SystemTray = SystemTray.getSystemTray()

    private var timer: Timer? = null
    private var secondImage: Image? = null
    private var stateImage: Image? = null

    init {
        try {
            setup()
        } catch (e: AWTException) {
            logger.error("TrayIcon could not be added. $e")
        }
    }

    fun setState(state: State) {
        logger.info("Setting State to $state")
        this.state = state
        if (!state.blink()) {
            stopBlinking()
            this.stateImage = state.getImage()
            this.icon.image = this.stateImage
            this.icon.toolTip = state.getLabel()
        } else {
            this.stateImage = state.getImage()
            this.icon.image = this.stateImage
            this.icon.toolTip = state.getLabel()
            this.secondImage = state.getSecondImage()
            startBlinking(1000)

        }
    }

    private fun startBlinking(interval: Long) {
        timer?.cancel() // Cancel any existing timer
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                var isIconVisible = true

                override fun run() {
                    if (isIconVisible) {
                        icon.setImage(secondImage) // Set to transparent or "off" image
                    } else {
                        icon.setImage(this@TrayComponent.stateImage) // Set to original image
                    }
                    isIconVisible = !isIconVisible
                }
            }, 0, interval)
        }
    }

    private fun stopBlinking() {
        timer?.cancel()
        timer?.purge()
    }

    @Throws(AWTException::class)
    private fun setup() {
        logger.info("Setting up TrayIcon")
        setupMenu(popup, providers)
        icon.popupMenu = popup
        tray.add(icon)
        context.addApplicationListener {
            if (it is ContextClosedEvent) {
                logger.info("Context Closed. Removing TrayIcon")
                tray.remove(icon)
            }

        }
    }


}

