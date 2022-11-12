package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.datastruct.ChangeRecord
import com.hasunemiku2015.minecraftnano.datastruct.UndoBuffer

/**
 * @author hasunemiku2015
 * @date 2022/09/18 11:45
 */
object BackspaceFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'h'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val lineString = it.fileData[it.cursorPosition]
            val outputBufferLineString = it.outputBuffer[it.cursorPosition]
            if (it.cursorCharPosition == 0) {
                if (it.cursorPosition == 0) {
                    return@repeatTimes
                }
                it.cursorPosition -= 1
                it.cursorCharPosition = it.fileData[it.cursorPosition].length

                it.fileData[it.cursorPosition] = it.fileData[it.cursorPosition] + lineString
                it.fileData.remove(it.cursorPosition + 1)
                it.outputBuffer[it.cursorPosition] = it.outputBuffer[it.cursorPosition] + outputBufferLineString
                it.outputBuffer.remove(it.cursorPosition + 1)
            } else {
                deletePrevChar(editor)
            }

            UndoBuffer[editor.player]?.pushRecord(ChangeRecord(lineString, it.cursorPosition))
        }
    }

    /**
     * Deletes the character at the current cursor position. Shared function with DeleteWordFunction
     * @param editor The TextEditor Object to work on
     */
    fun deletePrevChar(editor: TextEditor) {
        val lineString = editor.fileData[editor.cursorPosition]
        val outputBufferLineString = editor.outputBuffer[editor.cursorPosition]

        editor.fileData[editor.cursorPosition] = lineString.substring(0, editor.cursorCharPosition - 1) +
                lineString.substring(editor.cursorCharPosition)
        editor.outputBuffer[editor.cursorPosition] = outputBufferLineString.substring(0, editor.cursorCharPosition - 1) +
                outputBufferLineString.substring(editor.cursorCharPosition)
        editor.cursorCharPosition -= 1
    }
}