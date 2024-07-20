package xyz.nietongxue.watching

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean


@SpringBootApplication(
    scanBasePackages = ["xyz.nietongxue.watching"],
    scanBasePackageClasses = [com.logviewer.springboot.LogViewerSpringBootConfig::class]
)
class WatchingApplication {

    @Bean
    fun trayIcon(
        providers: List<EntryItemProvider>,
        context: ConfigurableApplicationContext,
    ): TrayComponent {
        return TrayComponent(
            providers, context
        )
    }

    @Bean
    fun frame(
        context: ConfigurableApplicationContext, providers: List<EntryItemProvider>,
    ): Frame {
        return Frame(context, providers, "Watching", initSize = 600 to 400)
    }

    @Bean
    fun taskbarPoint(
        providers: List<EntryItemProvider>,
        context: ConfigurableApplicationContext,
        @Autowired(required = false)
        frame: Frame?
    ): TaskbarComponent {
        return TaskbarComponent(
            providers,
            context, "classpath:/printer.png", frame

        )
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(WatchingApplication::class.java).headless(false).run(*args)
}

