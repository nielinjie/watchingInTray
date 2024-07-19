package xyz.nietongxue.watching

import jakarta.annotation.PostConstruct
import org.springframework.context.ConfigurableApplicationContext
import java.awt.PopupMenu
import java.awt.Taskbar

class TaskbarComponent(
    val providers: List<EntryItemProvider>,
    val context: ConfigurableApplicationContext,
    val imagePath: String,
    val frame: Frame?
) {

    @PostConstruct
    fun init() {
        val taskbar = Taskbar.getTaskbar()
        val image = createImage(context.getResource(imagePath))
        if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
            taskbar.iconImage = image
        }
        if (taskbar.isSupported(Taskbar.Feature.MENU)) {
            taskbar.menu = PopupMenu().also { popup ->
                setupMenu(popup, providers)
            }
        }

    }
}

