package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.function.DeleteFunction

/**
 * @author hasunemiku2015
 * @since 2022/09/18 12:25
 */
object DeleteWordFunction : AltFunction,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '>'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            while (it.fileData[it.cursorPosition].length > it.cursorCharPosition &&
                it.fileData[it.cursorPosition][it.cursorCharPosition] != ' '
            ) {
                DeleteFunction.deleteCharAtCursor(editor)
            }
            do {
                try {
                    DeleteFunction.deleteCharAtCursor(editor)
                } catch (ex: Exception) {
                    return@repeatTimes
                }
            } while (it.fileData[it.cursorPosition].length > it.cursorCharPosition
                && it.fileData[it.cursorPosition][it.cursorCharPosition] == ' '
            )
        }
    }
}