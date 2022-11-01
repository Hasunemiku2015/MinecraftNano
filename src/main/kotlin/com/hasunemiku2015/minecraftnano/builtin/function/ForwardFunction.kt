package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper

/**
 * @author hasunemiku2015
 * @date 2022/09/13 12:59
 */
object ForwardFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'f'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            if (it.cursorPosition == editor.fileData.size() - 1 && editor.fileData[it.cursorPosition].isEmpty()) {
                return@repeatTimes
            } else if (it.cursorCharPosition == editor.fileData[it.cursorPosition].length) {
                if (it.cursorPosition == editor.fileData.size() - 1) {
                    it.fileData.append("")
                }
                it.cursorPosition += 1
                it.cursorCharPosition = 0
            } else {
                it.cursorCharPosition += 1
            }
        }
    }
}