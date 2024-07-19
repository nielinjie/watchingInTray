package com.hykj.watching

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean


@SpringBootApplication(
    scanBasePackages = ["com.hykj.watching"],
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
        @Value("\${trying.hello}")  trying:String
    ): Frame {
        return Frame(context, providers, "Watching",trying)
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

