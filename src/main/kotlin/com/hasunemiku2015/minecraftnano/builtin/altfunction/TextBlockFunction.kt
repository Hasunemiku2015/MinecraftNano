package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper

/**
 * Goes to the previous text block. (Separated by a blank line)
 * @author hasunemiku2015
 * @date 2022/11/03 14:32
 */
object PreviousTextBlockFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '7'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val fileData = editor.fileData

            var cursorPos = editor.cursorPosition
            while (fileData[cursorPos].isNotEmpty()) {
                cursorPos--
            }

            if(cursorPos < 1) {
                editor.cursorPosition = 0
                // Exit since reached first line
                return@repeatTimes
            } else {
                // Find previous non-empty line.
                var cursorPos2 = cursorPos
                while(fileData[cursorPos2].isEmpty() && cursorPos2 > 1) {
                    cursorPos2--
                }
                editor.cursorPosition = cursorPos2
            }
        }
    }
}

/**
 * Goes to the next text block. (Separated by a blank line)
 * @author hasunemiku2015
 * @date 2022/11/03 14:32
 */
object NextTextBlockFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '8'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            val fileData = editor.fileData
            var cursorPos = editor.cursorPosition
            while (fileData[cursorPos].isNotEmpty()) {
                cursorPos++
            }

            if(cursorPos == editor.fileData.size()) {
                editor.cursorPosition = editor.fileData.size()
                // Exit since reached last line
                return@repeatTimes
            } else {
                // Find next non-empty line.
                var cursorPos2 = cursorPos
                while(fileData[cursorPos2].isEmpty() && cursorPos2 < editor.fileData.size()) {
                    cursorPos2++
                }
                editor.cursorPosition = cursorPos2
            }
        }
    }
}