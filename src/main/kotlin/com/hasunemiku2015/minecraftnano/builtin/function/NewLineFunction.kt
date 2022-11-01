package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper

/**
 * @author hasunemiku2015
 * @time 2022/09/17 11:35
 */
object NewLineFunction: Function,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'm'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val lineString = it.fileData[it.cursorPosition]
            if (it.cursorCharPosition > 0) {
                it.fileData[it.cursorPosition] = lineString.substring(0, it.cursorCharPosition)
                it.fileData.insert(it.cursorPosition + 1, lineString.substring(it.cursorCharPosition))
                it.outputBuffer[it.cursorPosition] = lineString.substring(0, it.cursorCharPosition)
                it.outputBuffer.insert(it.cursorPosition + 1, lineString.substring(it.cursorCharPosition))
            } else {
                it.fileData[it.cursorPosition] = ""
                it.fileData.insert(it.cursorPosition + 1, lineString)
                it.outputBuffer[it.cursorPosition] = ""
                it.outputBuffer.insert(it.cursorPosition + 1, lineString)
            }

            it.cursorPosition += 1
            it.cursorCharPosition = 0
        }
    }
}