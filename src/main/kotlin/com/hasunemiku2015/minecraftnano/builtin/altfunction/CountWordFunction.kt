package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine

/**
 * @author hasunemiku2015
 * @date 2022/09/26 17:54
 */
object CountWordFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'd'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            val (numLines, numWords, numChar) = countLineWordChar(editor)
            // "[ 024 lines, 0014 words, 0090 characters]"
            editor.lowBlankLine = FunctionHelper.centerString(editor,
                "[ $numLines ${correctGrammar("line", numLines)},  " +
                        "$numWords ${correctGrammar("word", numWords)},  " +
                        "$numChar ${correctGrammar("character", numChar)} ]")
            runTaskLater(NanoPlugin.PLUGIN, 5*20) {
                editor.lowBlankLine = ""
            }
        }
    }

    private fun correctGrammar(singularString: String, count: Int): String {
        return if (count > 0) "${singularString}s" else singularString
    }

    /**
     * Common function to count total numLine, numWord, numChar.
     * @param editor The editor session to count on.
     * @return First: Total number of lines. Second: Total number of words. Third: Total number of characters.
     */
    fun countLineWordChar(editor: TextEditor): Triple<Int, Int, Int> {
        val numLines = editor.fileData.size()
        var numWords = 0
        var numChar = 0
        for (i in editor.fileData.asList()) {
            if (i.isNotEmpty()) {
                numWords += i.split(" ").size
                numChar += (i.length + 1)
            } else {
                numChar++
            }
        }
        return Triple(numLines, numWords, numChar)
    }
}