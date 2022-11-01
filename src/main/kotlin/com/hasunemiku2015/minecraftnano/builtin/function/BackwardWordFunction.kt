package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper

/**
 * @author hasunemiku2015
 * @date 2022/09/15 18:21
 */
object BackwardWordFunction: Function,EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return  lowerCaseChar == '<'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val data = editor.fileData[it.cursorPosition]
            var isPrevChar = false

            /**
             * Helper function to go to the head of a word
             */
            fun toWordHead(str: String) {
                while (it.cursorCharPosition > 0 && str[it.cursorCharPosition - 1] != ' ') {
                    it.cursorCharPosition -= 1
                }
            }

            // At head of file.
            if (it.cursorPosition == 0 && it.cursorCharPosition == 0) {
                return@repeatTimes
            }
            val prevData = editor.fileData[it.cursorPosition - 1]

            // Prevent IndexOutOfBoundException
            if (data.isEmpty()) {
                it.cursorPosition -= 1
                it.cursorCharPosition = prevData.length
                toWordHead(prevData)
                return@repeatTimes
            } else if (it.cursorCharPosition >= data.length) {
                toWordHead(data)
                return@repeatTimes
            }

            if (data[it.cursorCharPosition] == ' ') {
                do {
                    it.cursorCharPosition -= 1
                } while (it.cursorCharPosition < data.length && data[it.cursorCharPosition] == ' ')
                toWordHead(data)
            } else {
                while (data[it.cursorCharPosition] != ' ') {
                    if (it.cursorCharPosition == 0) {
                        it.cursorPosition -= 1
                        it.cursorCharPosition = prevData.length - 1
                        toWordHead(prevData)
                        break
                    } else {
                        it.cursorCharPosition -= 1
                        isPrevChar = true
                    }
                }

                if (isPrevChar && it.cursorCharPosition > 0) {
                    do {
                        if (it.cursorCharPosition > 0) {
                            it.cursorCharPosition -= 1
                        }
                    } while (data[it.cursorCharPosition] == ' ')
                    toWordHead(data)
                }
            }
        }
    }
}