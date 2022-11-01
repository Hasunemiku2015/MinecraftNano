package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.datastruct.UndoBuffer

/**
 * @author hasunemiku2015
 * @date 2022/09/24 17:40
 */
object UndoFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'u'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
//            val record = UndoBuffer[editor.player]?.undo() ?: return@d
//            editor.fileData[record.lineNumber] = record.oldString
        }
    }
}