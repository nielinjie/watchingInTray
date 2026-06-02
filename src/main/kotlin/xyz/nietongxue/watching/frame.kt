package xyz.nietongxue.watching

import jakarta.annotation.PostConstruct
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import xyz.nietongxue.watching.ui.setupButtons
import java.awt.FlowLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel


class FrameUI(
    val context: ConfigurableApplicationContext,
    val providers: List<EntryItemProvider>,
    val frameTitle: String,
    val initSize: Pair<Int, Int> = Pair(400, 300),
    initState: State

) {
    val frame = java.awt.Frame()
    val logger = org.slf4j.LoggerFactory.getLogger(FrameUI::class.java)
    var innerState = initState
    var headLabel = JLabel()

    @PostConstruct
    fun init() {
        frame.apply {
            // Set the title of the frame
            title = frameTitle
            // Set the size of the frame
            setSize(initSize.first, initSize.second)
            // Set the layout manager
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            //TODO headLabel不需要，用来做 state。
            JPanel().also { jp ->
                jp.layout = FlowLayout()
                headLabel.also {
                    it.text = this@FrameUI.innerState.getLabel()
                    jp.add(it)
                }
                add(jp)
            }

            // Add a button to the frame
            setupButtons(this, providers)
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


            // handle window closing event？
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

    fun setState(state: State) {
        this.innerState = state
        headLabel.text = state.getLabel()
    }
}