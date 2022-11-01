package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel

/**
 * @author hasunemiku2015
 * @date 2022/09/11 22:00
 */
object RefreshFunction: Function {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'l'
    }

    override fun exec(editor: TextEditor, args: List<String>) {}
}