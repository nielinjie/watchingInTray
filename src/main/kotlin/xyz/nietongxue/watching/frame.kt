package xyz.nietongxue.watching

import jakarta.annotation.PostConstruct
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import java.awt.FlowLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel


class Frame(
    val context: ConfigurableApplicationContext,
    val providers: List<EntryItemProvider>,
    val frameTitle: String,
    val headLabel: String = frameTitle,
    val initSize: Pair<Int, Int> = Pair(400, 300)
) {
    val frame = java.awt.Frame()
    val logger = org.slf4j.LoggerFactory.getLogger(Frame::class.java)

    @PostConstruct
    fun init() {
        frame.apply {
            // Set the title of the frame
            title = frameTitle
            // Set the size of the frame
            setSize(initSize.first, initSize.second)
            // Set the layout manager
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            JPanel().also { jp ->
                jp.layout = FlowLayout()
                JLabel().also {
                    it.text = headLabel
                    jp.add(it)
                }
                add(jp)
            }

            // Add a button to the frame
            JPanel().also {
                setupButtons(it, providers)
                add(it)
            }
            context.environment.getProperty("watching.debug")?.also {
                JPanel().also { jp ->

                    if (it == "true") {
                        jp.add(JLabel().also {
                            it.text = "CWD(System.getProperty(\"user.dir\")) - ${System.getProperty("user.dir")}"
                        })
                        jp.add(JLabel().also {
                            File(".").absolutePath.let { path ->
                                it.text = "CWD(File(\".\").absolutePath) - $path"
                            }
                        })
                        jp.add(JLabel().also {
                            it.text = "Debugging is enabled"
                        })
                    }
                    add(jp)
                }
            }


            // Add a window listener to handle window closing event
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
//                    frame.isVisible = false
                }
            })
            isVisible = true
        }
        context.addApplicationListener {
            if (it is ContextClosedEvent) {
                logger.info("Context Closed. Closing Frame")
                frame.isVisible = false
                frame.dispose()
            }
        }
    }
}