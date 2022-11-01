package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.files.CommentCharConfiguration

object CommentFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '3'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            val commentChar = CommentCharConfiguration.getCommentChar(".${editor.file.extension}")

            if (editor.fileData[editor.cursorPosition].startsWith(commentChar)) {
                editor.fileData[editor.cursorPosition].removePrefix(commentChar)
            } else {
                editor.fileData[editor.cursorPosition].prependIndent(commentChar)
            }
        }
    }
}