package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper

object DeleteRegionFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'h'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            it.fileData[it.cursorPosition] = it.fileData[it.cursorPosition + 1]
            it.outputBuffer[it.cursorPosition] = it.outputBuffer[it.cursorPosition + 1]
            it.fileData.remove(it.cursorPosition + 1)
            it.outputBuffer.remove(it.cursorPosition + 1)
        }
    }
}