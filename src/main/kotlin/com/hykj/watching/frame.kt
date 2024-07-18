package com.hykj.watching

import jakarta.annotation.PostConstruct
import org.springframework.context.ConfigurableApplicationContext
import java.awt.FlowLayout
import java.awt.Label
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File

class Frame(
    val context: ConfigurableApplicationContext,
    val providers: List<EntryItemProvider>,
    val frameTitle:String
) {
    val frame = java.awt.Frame()
    @PostConstruct
    fun init() {
        frame.apply {
            // Set the title of the frame
            title = frameTitle
            // Set the size of the frame
            setSize(400, 300)
            // Set the layout manager
            layout = FlowLayout()
            // Add a button to the frame
            setupButtons(this, providers)

            add(Label().also {
                it.text = "CWD(System.getProperty(\"user.dir\")) - ${System.getProperty("user.dir")}"
            })
            add(Label().also {
                File(".").absolutePath.let { path ->
                    it.text = "CWD(File(\".\").absolutePath) - $path"
                }
            })

            // Add a window listener to handle window closing event
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
//                    frame.isVisible = false
                }
            })
            isVisible = true
        }
    }
}