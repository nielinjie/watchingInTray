package xyz.nietongxue.watching

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.util.*

@Component
class User(
    @Autowired val trayIcon: TrayComponent,
    @Autowired val context: ConfigurableApplicationContext
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
        logger.info("stopping")
        this@User.timer?.cancel()
        this@User.timer?.purge()
    }
}