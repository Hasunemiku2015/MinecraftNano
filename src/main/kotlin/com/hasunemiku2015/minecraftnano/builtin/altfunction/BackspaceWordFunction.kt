package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.function.BackspaceFunction

/**
 * @author hasunemiku2015
 * @date 2022/09/18 12:24
 */
object BackspaceWordFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '<'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            // While true since if the using 'or' throws IndexOutOfBoundsException.
            while (true) {
                if (it.cursorCharPosition == 0) {
                    break
                }
                if (it.fileData[it.cursorPosition][it.cursorCharPosition - 1] == ' ') {
                    break
                }
                BackspaceFunction.deletePrevChar(it)
            }

            do {
                if (it.cursorCharPosition > 0) {
                    BackspaceFunction.deletePrevChar(it)
                }
            } while (it.cursorCharPosition > 0 &&
                it.fileData[it.cursorPosition][it.cursorCharPosition - 1] == ' '
            )
        }
    }
}