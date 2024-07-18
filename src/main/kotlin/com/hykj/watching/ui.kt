package com.hykj.watching

import java.awt.Button
import java.awt.Container
import java.awt.Menu
import java.awt.MenuItem


fun EntryItem.getMenuItem(): MenuItem {
    return MenuItem(this.label).also {
        it.addActionListener { this.action() }
    }
}

fun EntryItem.getButton(): Button {
    return Button(this.label).also {
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
        for (item in group.second) {
            container.add(item.getButton())
        }
    }
}