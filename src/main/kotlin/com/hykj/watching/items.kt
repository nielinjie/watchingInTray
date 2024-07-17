package com.hykj.watching

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Component
import java.awt.Desktop
import java.awt.MenuItem
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

data class Position(val order: Int, val group: Group = Group("None"))


class EntryItem(val menuItem: MenuItem, val position: Position)

interface PopupItemProvider {
    fun getItems(): List<EntryItem>
}

@Component
class ExitItems(
    @Autowired val context: ConfigurableApplicationContext
) : PopupItemProvider {
    val logger = LoggerFactory.getLogger(ExitItems::class.java)
    override fun getItems(): List<EntryItem> {
        val exitItem = MenuItem("Exit").also {
            it.addActionListener {
                logger.info("Exiting context")
                val exitCode = SpringApplication.exit(context, ExitCodeGenerator { 0 })
                exitProcess(exitCode)
            }
        }
        return listOf(EntryItem(exitItem, Position(10000, Group("Exit"))))
    }
}

@Component
class LoggingFileOpenItems(
    @Autowired val context: ConfigurableApplicationContext
) : PopupItemProvider {
    val logger = LoggerFactory.getLogger(LoggingFileOpenItems::class.java)
    override fun getItems(): List<EntryItem> {
        val loggerFilePath: String? = context.environment.getProperty("logging.file.path")
        val loggerFileName: String? = context.environment.getProperty("logging.file.name")
        return loggerFilePath?.let { filePath ->
            logger.info("Logging File Path: $filePath")
            val loggingFile = MenuItem("Logging File").also {
                it.addActionListener {
                    logger.info("Opening Logging File")
                    val desktop = Desktop.getDesktop()
                    val file = File(filePath, loggerFileName ?: "spring.log")
                    if (file.exists()) desktop.open(file)
                }
            }
            listOf(
                EntryItem(loggingFile, Position(100, Group("Logging", "nested")))
            )
        } ?: emptyList()
    }
}

@Component
class BrowserOpenItems(
    @Autowired val context: ConfigurableApplicationContext
) : PopupItemProvider {
    val logger = LoggerFactory.getLogger(BrowserOpenItems::class.java)
    override fun getItems(): List<EntryItem> {
        val port: String = context.environment.getProperty("server.port") ?: "8080"
        val url = "http://localhost:$port"
        val openBrowser = MenuItem("Open Browser - $url").also {
            it.addActionListener {
                logger.info("Opening Browser")
                val desktop = Desktop.getDesktop()
                desktop.browse(java.net.URI(url))
            }
        }
        return listOf(EntryItem(openBrowser, Position(1)))
    }
}

@Component
class LogViewerOpenItems(
    @Autowired val context: ConfigurableApplicationContext
) : PopupItemProvider {
    val logger = LoggerFactory.getLogger(LogViewerOpenItems::class.java)
    override fun getItems(): List<EntryItem> {
        val port: String = context.environment.getProperty("server.port") ?: "8080"
        val url = "http://localhost:$port/logs"
        val openLogViewer = MenuItem("Open Log Viewer - $url").also {
            it.addActionListener {
                logger.info("Opening Log Viewer")
                val desktop = Desktop.getDesktop()
                desktop.browse(java.net.URI(url))
            }
        }
        return listOf(
            EntryItem(openLogViewer, Position(101, Group("Logging", "nested")))
        )
    }
}