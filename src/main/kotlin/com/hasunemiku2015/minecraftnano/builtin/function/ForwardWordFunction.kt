package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper

/**
 * @author hasunemiku2015
 * @date 2022/09/13 12:59
 */
object ForwardWordFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '>'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val data = it.fileData[it.cursorPosition]
            var isNextChar = false

            // Prevent IndexOutOfBoundException
            if (data.isEmpty() || it.cursorCharPosition >= data.length) {
                it.cursorPosition += 1
                it.cursorCharPosition = 0
                return@repeatTimes
            }

            if (data[it.cursorCharPosition] == ' ') {
                do {
                    it.cursorCharPosition += 1
                } while (it.cursorCharPosition < data.length && data[it.cursorCharPosition] == ' ')
            } else {
                while (data[it.cursorCharPosition] != ' ') {
                    if (it.cursorCharPosition == data.length - 1) {
                        if (data.isNotEmpty() && it.cursorPosition != it.fileData.size() - 1) {
                            it.cursorPosition += 1
                            it.cursorCharPosition = 0
                        }
                        isNextChar = false
                        break
                    } else {
                        it.cursorCharPosition += 1
                        isNextChar = true
                    }
                }

                if (isNextChar && it.cursorPosition < data.length) {
                    do {
                        it.cursorCharPosition += 1
                    } while (data[it.cursorCharPosition] == ' ')
                }
            }
        }
    }
}