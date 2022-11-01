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
object BackwardFunction: Function,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'b'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            if (it.cursorCharPosition > 0) {
                it.cursorCharPosition -= 1
            } else if (it.cursorPosition != 0) {
                it.cursorPosition -= 1
                it.cursorCharPosition = it.fileData[it.cursorPosition].length
            }
        }
    }
}