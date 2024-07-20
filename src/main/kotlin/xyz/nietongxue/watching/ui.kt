package xyz.nietongxue.watching

import java.awt.Container
import java.awt.Menu
import java.awt.MenuItem
import javax.swing.JButton
import javax.swing.JPanel


fun EntryItem.getMenuItem(): MenuItem {
    return MenuItem(this.label).also {
        it.addActionListener { this.action() }
    }
}

fun EntryItem.getButton(): JButton {
    return JButton(this.label).also {
        it.addActionListener { this.action() }
    }
}

fun setupMenu(popup: Menu, providers: List<EntryItemProvider>) {
    val grouped = getOrderedGroup(providers.flatMap { it.getItems() })
    for (group in grouped) {
        when (group.first.type) {
            "flat" -> {
                for (item in group.second) {
                    popup.add(item.getMenuItem())
                }
                popup.addSeparator()
            }

            "nested" -> {
                val groupMenu = Menu(group.first.name)
                for (item in group.second) {
                    groupMenu.add(item.getMenuItem())
                }
                popup.add(groupMenu)
            }
        }

    }
}

fun setupButtons(container: Container, providers: List<EntryItemProvider>) {
    val grouped = getOrderedGroup(providers.flatMap { it.getItems() })
    for (group in grouped) {
        val jPanel = JPanel()
        for (item in group.second) {
            jPanel.add(item.getButton())
        }
        container.add(jPanel)
    }
}