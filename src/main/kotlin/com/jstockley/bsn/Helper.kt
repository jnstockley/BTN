package com.jstockley.bsn

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

private var gui: MultiWindowTextGUI? = null

fun getSelectedItemsMap(items: Map<String, String>, title: String, checkedItems: Map<String, String> = mutableMapOf()): MutableMap<String, Boolean> {
    val sortedItems = items.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER)

    val panel = setupCheckBoxPanel(title, sortedItems.keys, checkedItems.keys)

    val checkBoxList = panel.childrenList[0] as CheckBoxList<*>

    val currentCheckedItems = checkBoxList.checkedItems

    val itemsMap: MutableMap<String, Boolean> = mutableMapOf()
    for (item in currentCheckedItems) {
        itemsMap[items[item]!!] = false
    }
    return itemsMap
}

fun getSelectedItemsList(items: Map<String, String>, title: String, checkedItems: Map<String, String> = mutableMapOf()): List<String> {
    val sortedItems = items.toSortedMap(java.lang.String.CASE_INSENSITIVE_ORDER)

    val panel = setupCheckBoxPanel(title, sortedItems.keys, checkedItems.keys)

    val checkBoxList = panel.childrenList[0] as CheckBoxList<*>

    val currentCheckedItems = checkBoxList.checkedItems

    val itemsList: MutableList<String> = mutableListOf()
    for (item in currentCheckedItems) {
        itemsList.add(items[item]!!)
    }
    return itemsList
}


fun getSelectedItemsList(items: List<String>, title: String, checkedItems: List<String> = mutableListOf()): List<String> {
    val panel = setupCheckBoxPanel(title, items.toSet(), checkedItems.toSet())

    val checkBoxList = panel.childrenList[0] as CheckBoxList<*>

    val itemList = mutableListOf<String>()
    for (checkedItem in checkBoxList.checkedItems){
        itemList.add(checkedItem as String)
    }
    return itemList
}

fun getMultiTextBox(titles: List<String>, mainWindow: String, enteredItems: List<String> = mutableListOf()): Map<String, String> {
    val mainPanel = Panel()
    mainPanel.layoutManager = LinearLayout(Direction.VERTICAL)

    val size = TerminalSize(50, 2)
    for (i in titles.indices) {
        val textBox = TextBox(size)
        if (enteredItems.isNotEmpty()) {
            textBox.text = enteredItems[i]
        }
        mainPanel.addComponent(textBox.withBorder(Borders.singleLine(titles[i])))
    }

    val window = setupWindow(mainWindow, mainPanel)

    mainPanel.addComponent(Button("Enter") { window.close() })
    gui!!.addWindowAndWait(window)
    gui!!.screen.stopScreen()

    val clientIdBox = (mainPanel.childrenList[0] as Border).childrenList[0] as TextBox
    val clientSecretBox = (mainPanel.childrenList[1] as Border).childrenList[0] as TextBox

    return mapOf("clientId" to clientIdBox.text, "clientSecret" to clientSecretBox.text)
}

fun getSelectedItemsList(title: String, items: List<String> = mutableListOf()): List<String> {
    val panel = setupTextBoxPanel(title, items)

    val textBox = panel.childrenList[0] as TextBox

    return textBox.text.split('\n')
}


//@Deprecated("Use CLIHelper")

private fun setupTextBoxPanel(title: String, currentText: List<String>): Panel{
    val contentPanel = Panel(GridLayout(1))
    val gridLayout = contentPanel.layoutManager as GridLayout
    gridLayout.horizontalSpacing = 1
    val size = TerminalSize(50, 5)
    val textBox = TextBox(size)
    if (currentText.isNotEmpty()) {
        textBox.text = currentText.joinToString(separator = "\n") { it }
    }
    contentPanel.addComponent(textBox)
    val button = Button("Enter")
    //contentPanel.addComponent(button)
    val window = setupWindow(title, contentPanel)
    //val textGUI = MultiWindowTextGUI(window)
    contentPanel.addComponent(Button("Enter") { window.close() })

    //setupWindow(title, contentPanel)
    gui!!.addWindowAndWait(window)
    gui!!.screen.stopScreen()

    return contentPanel
}


//@Deprecated("Use CLIHelper")

private fun setupCheckBoxPanel(title: String, items: Set<String>, checkedItems: Set<String>): Panel {
    val controlPanel = Panel(GridLayout(1))
    val gridLayout = controlPanel.layoutManager as GridLayout
    gridLayout.horizontalSpacing = 1
    val size = TerminalSize(50, 20)
    val checkBoxList = CheckBoxList<String>(size)
    for (item in items) {
        checkBoxList.addItem(item)
    }
    if (checkedItems.isNotEmpty()) {
        for (item in checkedItems) {
            checkBoxList.setChecked(item, true)
        }
    }
    controlPanel.addComponent(checkBoxList)
    val button = Button("Enter")
    //controlPanel.addComponent(button)
    val window = setupWindow(title, controlPanel)

    controlPanel.addComponent(Button("Enter") { window.close() })
    gui!!.addWindowAndWait(window)
    gui!!.screen.stopScreen()
    return controlPanel
}
//@Deprecated("Use CLIHelper")

private fun setupWindow(title: String, panel: Panel): Window {
    val terminalFactory = DefaultTerminalFactory()
    val screen: Screen = terminalFactory.createScreen()
    val textGUI = MultiWindowTextGUI(screen)
    screen.startScreen()
    val window = BasicWindow(title)
    val hints = listOf(Window.Hint.CENTERED)
    window.setHints(hints)
    window.component = panel
    //textGUI.addWindowAndWait(window)
    gui = textGUI
    return window
}
