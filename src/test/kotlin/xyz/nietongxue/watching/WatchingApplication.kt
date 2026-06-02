package xyz.nietongxue.watching

import com.logviewer.springboot.LogViewerSpringBootConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean


@SpringBootApplication(
    scanBasePackages = ["xyz.nietongxue.watching"], scanBasePackageClasses = [LogViewerSpringBootConfig::class]
)
class WatchingApplication {

    @Bean
    fun trayIcon(
        providers: List<EntryItemProvider>,
        context: ConfigurableApplicationContext,
    ): TrayUI {
        return TrayUI(
            providers, context, WatchingState(context)
        )
    }

    /*
    * 在 bean PostConstruct 时显示。
    * 跟其他部分关系不大。
     */
    @Bean
    fun frame(
        context: ConfigurableApplicationContext, providers: List<EntryItemProvider>,
    ): FrameUI {
        return FrameUI(
            context, providers, "Watching", initSize = 600 to 400, initState = WatchingState(context)
        )
    }

    @Bean
    fun taskbarPoint(
        providers: List<EntryItemProvider>,
        context: ConfigurableApplicationContext,
    ): TaskbarUI {
        return TaskbarUI(
            providers,
            context, "classpath:/printer.png",

            )
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(WatchingApplication::class.java).headless(false).run(*args)
}

