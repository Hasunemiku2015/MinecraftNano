package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler

/**
 * @author hasunemiku2015
 * @date 2022/09/23 12:10
 */
object LastLineFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '/'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            editor.cursorPosition = editor.fileData.size()
            if (editor.fileData.last().isEmpty()) {
                editor.cursorPosition--
            }
            editor.cursorCharPosition = 0
        }
    }
}