package xyz.nietongxue.watching

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.util.*

/*
 * 是个 use case 演示。如何实现 state？
 */
@Component
class User(
    val trayIcon: TrayUI,
    val context: ConfigurableApplicationContext
) {
    var timer: Timer? = null
    val logger: Logger = LoggerFactory.getLogger(User::class.java)

    @PostConstruct
    fun init() {
        this.timer = Timer().also {
            it.schedule(object : TimerTask() {
                override fun run() {
                    trayIcon.setState(
                        BlinkingOverlayState(
                            WatchingState(context),
                            context.getResource("classpath:/printer.png"), " - printing"
                        )
                    )
                    this@User.stop()
                }
            }, 1000)
        }
    }

    fun stop() {
        logger.debug("stopping")
        this@User.timer?.cancel()
        this@User.timer?.purge()
    }
}