package com.hasunemiku2015.minecraftnano.builtin

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.commands.NanoPrefCommand.preference
import java.io.BufferedWriter
import java.io.FileWriter

object FunctionHelper {
    @JvmStatic
    fun centerString(editor: TextEditor, string: String): String {
        return StringBuffer().let {
            val numOfSpaces = editor.preference.maxCharLine / 2.0 - string.length / 2.0
            for (i in 1..numOfSpaces.toInt()) {
                it.append(" ")
            }
            it.append(string)
            it.toString()
        }
    }

    @JvmStatic
    fun saveDataToFile(editor: TextEditor) {
        BufferedWriter(FileWriter(editor.file, false)).use {
            val list = editor.fileData.asList()
            for ((idx, i) in list.withIndex()) {
                it.write(i)
                if (idx != list.size - 1) {
                    it.newLine()
                }
            }
        }
    }

    @JvmStatic
    fun repeatTimes(editor: TextEditor, args: List<String>, eventHandler: EventHandler? = null,
                    func: (editor: TextEditor) -> Unit) {
        if (eventHandler == null) {
            val numOfTimes = if (args.isEmpty()) 1 else args[0].toIntOrNull() ?: 1
            for (i in 1..numOfTimes) {
                func(editor)
            }
        } else {
            eventHandler.d(editor) {
                val numOfTimes = if (args.isEmpty()) 1 else args[0].toIntOrNull() ?: 1
                for (i in 1..numOfTimes) {
                    func(editor)
                }
            }
        }
    }
}