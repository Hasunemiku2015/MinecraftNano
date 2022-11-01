package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.files.TabCharConfiguration

/**
 * @author hasunemiku2015
 * @time 2022/09/17 11:35
 */
object TabFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'i'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val s1: String
            val lineString = it.fileData[it.cursorPosition]
            if (it.cursorCharPosition > 0) {
                s1 = lineString.substring(0, it.cursorCharPosition + 1) +
                        TabCharConfiguration.getTabChar(editor.file.extension)
                it.fileData[it.cursorPosition] = s1 + lineString.substring(it.cursorCharPosition + 1)
                it.cursorCharPosition = s1.length - 1
            } else {
                it.fileData[it.cursorPosition] = TabCharConfiguration.getTabChar(editor.file.extension) + lineString
                it.cursorCharPosition = TabCharConfiguration.getTabChar(editor.file.extension).length - 1
            }
            it.outputBuffer[it.cursorPosition] = it.fileData[it.cursorPosition]
        }
    }
}

/**
 * @author hasunemiku2015
 * @time 2022/09/26 17:34
 */
object SpaceFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 't'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val s1: String
            val lineString = it.fileData[it.cursorPosition]
            if (it.cursorCharPosition > 0) {
                s1 = lineString.substring(0, it.cursorCharPosition + 1) + " "
                it.fileData[it.cursorPosition] = s1 + lineString.substring(it.cursorCharPosition + 1)
                it.cursorCharPosition = s1.length - 1
            } else {
                it.fileData[it.cursorPosition] = " $lineString"
                it.cursorCharPosition = 0
            }
            it.outputBuffer[it.cursorPosition] = it.fileData[it.cursorPosition]
        }
    }

}
