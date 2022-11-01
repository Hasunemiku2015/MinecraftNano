package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.altfunction.CountWordFunction
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent

/**
 * @author hasunemiku2015
 * @date 2022/09/26 17:56
 */
object CursorPositionFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'c'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            val (totalLine, _, totalChar) = CountWordFunction.countLineWordChar(editor)
            val (totalLineString, totalCharString) = Pair(formatNumberApp(totalLine), formatNumberApp(totalChar))

            val cursorLinePos = formatNumber(editor.cursorPosition + 1, 2)
            val cursorLinePosPercent = formatNumber(((cursorLinePos.toDouble() / totalLine.toDouble()) * 100).toInt() , 3)

            var cursorCharPos = 0
            for ((idx, value) in editor.fileData.asList().withIndex()) {
                if (idx >= editor.cursorPosition) {
                    break
                }
                if (value.isNotEmpty()) {
                    cursorCharPos += value.length + 1
                } else {
                    cursorCharPos + 1
                }
            }
            cursorCharPos += editor.cursorCharPosition
            val cursorCharPosString = formatNumber(cursorCharPos, 2)
            val cursorCharPosPercent = formatNumber(((cursorCharPos.toDouble() / totalChar.toDouble()) * 100).toInt(), 3)

            val colPos = formatNumber(editor.cursorCharPosition + 1, 2)
            var maxCol = 0
            for (i in editor.fileData.asList()) {
                if (i.length > maxCol) {
                    maxCol = i.length + 1
                }
            }
            val maxColString = formatNumberApp(maxCol)
            val cursorColPercent = formatNumber(((colPos.toDouble() / maxCol.toDouble()) * 100).toInt(), 3)

            // [ line  1/4 (025%), col 2/1 (100%), char 20/29 (068%) ]
            editor.lowBlankLine = FunctionHelper.centerString(editor,
                "[ line $cursorLinePos/$totalLineString ($cursorLinePosPercent%), col $colPos/$maxColString " +
                        "($cursorColPercent%), char $cursorCharPosString/$totalCharString ($cursorCharPosPercent%) ]")
            runTaskLater(NanoPlugin.PLUGIN, 5*20) {
                editor.lowBlankLine = ""
            }
        }
    }

    private fun formatNumber(number: Int, minLength: Int): String {
        var numString = number.toString()
        while (numString.length < minLength) {
            numString = " $numString"
        }
        return numString
    }

    private fun formatNumberApp(number: Int): String {
        var numString = number.toString()
        while (numString.length < 2) {
            numString = "$numString "
        }
        return numString
    }
 }