package xyz.nietongxue.watching

import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationContext
import xyz.nietongxue.watching.ui.createImage
import xyz.nietongxue.watching.ui.setupMenu
import java.awt.PopupMenu
import java.awt.Taskbar

class TaskbarUI(
    val providers: List<EntryItemProvider>,
    val context: ApplicationContext,
    val imagePath: String,
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

