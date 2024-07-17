package com.hykj.watching

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*


@Component
class User(
    @Autowired val trayIcon: MyTrayIcon,
    @Autowired val context: ConfigurableApplicationContext
) {
    var timer: Timer? = null
    val logger = org.slf4j.LoggerFactory.getLogger(User::class.java)

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

@SpringBootApplication(
    scanBasePackages = ["com.hykj.watching"],
    scanBasePackageClasses = [com.logviewer.springboot.LogViewerSpringBootConfig::class]
)
class WatchingApplication {

    @Bean
    fun trayIcon(
        providers: List<PopupItemProvider>,
        context: ConfigurableApplicationContext,
    ): MyTrayIcon {
        return MyTrayIcon(
            providers, context,

            )
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(WatchingApplication::class.java).headless(false).run(*args)
}

