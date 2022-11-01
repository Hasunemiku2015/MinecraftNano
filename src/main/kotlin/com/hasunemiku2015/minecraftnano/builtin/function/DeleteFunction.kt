package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.datastruct.ChangeRecord
import com.hasunemiku2015.minecraftnano.datastruct.UndoBuffer

/**
 * @author hasunemiku2015
 * @date 2022/09/18 11:50
 */
object DeleteFunction: Function,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'd'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val lineString = it.fileData[it.cursorPosition]
            val outputBufferString = it.outputBuffer[it.cursorPosition]

            if (it.cursorCharPosition == lineString.length) {
                it.fileData[it.cursorPosition] = lineString + it.fileData[it.cursorPosition + 1]
                it.outputBuffer[it.cursorPosition] = outputBufferString + it.outputBuffer[it.cursorPosition + 1]
                it.fileData.delete(it.cursorPosition + 1)
                it.outputBuffer.delete(it.cursorPosition + 1)
            } else {
                deleteCharAtCursor(editor)
            }

            UndoBuffer[editor.player]?.pushRecord(ChangeRecord(lineString, it.cursorPosition))
        }
    }

    /**
     * Deletes the character at the current cursor position. Shared function with DeleteWordFunction
     * @param editor The TextEditor Object to work on
     */
   fun deleteCharAtCursor(editor: TextEditor) {
        val lineString = editor.fileData[editor.cursorPosition]
        val outputBufferString = editor.outputBuffer[editor.cursorPosition]

        if (editor.cursorCharPosition == 0) {
            editor.fileData[editor.cursorPosition] = lineString.substring(1)
            editor.outputBuffer[editor.cursorPosition] = outputBufferString.substring(1)
        } else {
            editor.fileData[editor.cursorPosition] = lineString.substring(0, editor.cursorCharPosition) +
                    lineString.substring(editor.cursorCharPosition + 1)
            editor.outputBuffer[editor.cursorPosition] = outputBufferString.substring(0, editor.cursorCharPosition) +
                    outputBufferString.substring(editor.cursorCharPosition + 1)
        }
    }
}