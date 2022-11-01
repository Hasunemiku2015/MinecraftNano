package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel

/**
 * Go to beginning of line.
 * @author hasunemiku2015
 * @date 2022/09/26 19:13
 */
object LineBeginFunction: Function,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'a'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            it.cursorCharPosition = 0
        }
    }

}

/**
 * Go to end of line.
 * @author hasunemiku2015
 * @date 2022/09/26 19:13
 */
object LineEndFunction: Function,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'e'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            it.cursorCharPosition = it.fileData[it.cursorPosition].length
        }
    }
}