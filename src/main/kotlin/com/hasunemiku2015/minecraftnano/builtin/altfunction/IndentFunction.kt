package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.files.TabCharConfiguration

/**
 * @author hasunemiku2015
 * @date 2022/10/04 19:10
 */
object IndentFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '}'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            editor.fileData[editor.cursorPosition]
                .prependIndent(TabCharConfiguration.getTabChar(".${editor.file.extension}"))
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/04 19:10
 */
object UnIndentFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '{'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            editor.fileData[editor.cursorPosition]
                .removePrefix(TabCharConfiguration.getTabChar(".${editor.file.extension}"))
        }
    }
}