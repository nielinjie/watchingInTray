package com.hykj.watching

import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import java.awt.*
import java.util.*


class MyTrayIcon(
    val providers: List<PopupItemProvider>,
    val context: ConfigurableApplicationContext,
    val initState: State = WatchingState(context)

) : TrayIcon(initState.getImage(), initState.getLabel()) {
    private var state = initState
    private val popup = PopupMenu()
    val logger = LoggerFactory.getLogger(MyTrayIcon::class.java)
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
            this.image = this.stateImage
            this.toolTip = state.getLabel()
        } else {
            this.stateImage = state.getImage()
            this.image = this.stateImage
            this.toolTip = state.getLabel()
            this.secondImage = state.getSecondImage()
            startBlinking(1000)

        }
    }

    fun startBlinking(interval: Long) {
        timer?.cancel() // Cancel any existing timer
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                var isIconVisible = true

                override fun run() {
                    if (isIconVisible) {
                        setImage(secondImage) // Set to transparent or "off" image
                    } else {
                        setImage(this@MyTrayIcon.stateImage) // Set to original image
                    }
                    isIconVisible = !isIconVisible
                }
            }, 0, interval)
        }
    }

    fun stopBlinking() {
        timer?.cancel()
        timer?.purge()
    }

    @Throws(AWTException::class)
    private fun setup() {
        logger.info("Setting up TrayIcon")


//        this.providers.flatMap { it.getItems() }.sortedBy { it.position.order }.onEach { popup.add(it.menuItem) }

        val grouped = getOrderedGroup(providers.flatMap { it.getItems() })
        for(group in grouped) {
            when(group.first.type) {
                "flat" -> {
                    for(item in group.second) {
                        popup.add(item.menuItem)
                    }
                    popup.addSeparator()
                }
                "nested" -> {
                    val groupMenu = Menu(group.first.name)
                    for(item in group.second) {
                        groupMenu.add(item.menuItem)
                    }
                    popup.add(groupMenu)
                }
            }
//            val groupMenu = Menu(group.first)
//            for(item in group.second) {
//                groupMenu.add(item.menuItem)
//            }
//            popup.add(groupMenu)

        }
        // popup.addSeparator();
        popupMenu = popup
        tray.add(this)
        context.addApplicationListener {
            if (it is ContextClosedEvent) {
                logger.info("Context Closed. Removing TrayIcon")
                tray.remove(this@MyTrayIcon)
            }

        }
    }


}

