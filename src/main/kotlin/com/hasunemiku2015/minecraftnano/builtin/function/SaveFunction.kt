package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine

/**
 * @author hasunemiku2015
 * @date 2022/09/11 22:26
 */
object SaveFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'o'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            FunctionHelper.saveDataToFile(editor)

            editor.lowBlankLine = FunctionHelper.centerString(editor, "[ Wrote ${editor.fileData.size()} line ]")
            runTaskLater(NanoPlugin.PLUGIN, 5*20) {
                editor.lowBlankLine = ""
            }
        }
    }
}