package com.hykj.watching

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.awt.Desktop
import java.io.File
import kotlin.system.exitProcess

fun getOrderedGroup(entryItems: List<EntryItem>): List<Pair<Group, List<EntryItem>>> {
    return entryItems.groupBy { it.position.group }.toList().sortedBy { it ->
        it.second.minOf { it.position.order }
    }.map {
        it.first to it.second.sortedBy { it.position.order }
    }
}

data class Group(val name: String, val type: String = "flat")

data class Position(val order: Int, val group: Group = Group("None")) {
    constructor(order: Int, groupName: String, type: String = "flat") : this(order, Group(groupName, type))
}


class EntryItem(val label: String, val action: () -> Unit, val position: Position)

interface EntryItemProvider {
    fun getItems(): List<EntryItem>
}

@Component
class ExitItems(
    @Autowired val context: ConfigurableApplicationContext
) : EntryItemProvider {
    val logger: Logger = LoggerFactory.getLogger(ExitItems::class.java)
    override fun getItems(): List<EntryItem> {
        val exitItem = EntryItem("Exit", {
            logger.info("Exiting context")
            val exitCode = SpringApplication.exit(context, ExitCodeGenerator { 0 })
            exitProcess(exitCode)
        }, Position(10000, Group("Exit")))

        return listOf(exitItem)
    }
}

@Component
class LoggingFileOpenItems(
    @Autowired val context: ConfigurableApplicationContext
) : EntryItemProvider {
    val logger: Logger = LoggerFactory.getLogger(LoggingFileOpenItems::class.java)
    override fun getItems(): List<EntryItem> {
        val loggerFilePath: String? = context.environment.getProperty("logging.file.path")
        val loggerFileName: String? = context.environment.getProperty("logging.file.name")
        return loggerFilePath?.let { filePath ->
            logger.info("Logging File Path: $filePath")
            val loggingFile = EntryItem("Logging File", {
                logger.info("Opening Logging File")
                val desktop = Desktop.getDesktop()
                val file = File(filePath, loggerFileName ?: "spring.log")
                if (file.exists()) desktop.open(file)
            }, Position(100, Group("Logging", "nested")))

            listOf(
                loggingFile
            )
        } ?: emptyList()
    }
}

@Component
class BrowserOpenItems(
    @Autowired val context: ConfigurableApplicationContext
) : EntryItemProvider {
    val logger: Logger = LoggerFactory.getLogger(BrowserOpenItems::class.java)
    override fun getItems(): List<EntryItem> {
        val port: String = context.environment.getProperty("server.port") ?: "8080"
        val url = "http://localhost:$port"
        val openBrowser = EntryItem("Open Browser - $url", {
            logger.info("Opening Browser")
            val desktop = Desktop.getDesktop()
            desktop.browse(java.net.URI(url))
        }, Position(1))

        return listOf(openBrowser)
    }
}

@Component
class LogViewerOpenItems(
    @Autowired val context: ConfigurableApplicationContext
) : EntryItemProvider {
    val logger: Logger = LoggerFactory.getLogger(LogViewerOpenItems::class.java)
    override fun getItems(): List<EntryItem> {
        val port: String = context.environment.getProperty("server.port") ?: "8080"
        val url = "http://localhost:$port/logs"
        val openLogViewer = EntryItem("Open Log Viewer - $url", {
            logger.info("Opening Log Viewer")
            val desktop = Desktop.getDesktop()
            desktop.browse(java.net.URI(url))
        }, Position(101, Group("Logging", "nested")))

        return listOf(
            openLogViewer
        )
    }
}