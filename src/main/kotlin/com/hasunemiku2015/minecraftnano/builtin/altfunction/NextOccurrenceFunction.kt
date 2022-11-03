package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.function.SearchConfiguration.Companion.searchConfiguration
import com.hasunemiku2015.minecraftnano.builtin.function.SearchFunction
import com.hasunemiku2015.minecraftnano.builtin.function.SearchFunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine1
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine2
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine

/**
 * @author hasunemiku2015
 * @date 2022/11/03 14:00
 */
object NextOccurrenceBackwardFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'q'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        NextOccurrenceHelper.exec(editor, this, args, false)
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/11/03 14:00
 */
object NextOccurrenceForwardFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'w'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        NextOccurrenceHelper.exec(editor, this, args, false)
    }
}

/**
 * Utility class for holding the exec function for NextOccurrenceFunctions.
 * @author hasunemiku2015
 * @date 2022/11/03 14:15
 * @see NextOccurrenceForwardFunction
 * @see NextOccurrenceBackwardFunction
 */
object NextOccurrenceHelper {
    /**
     * Combined function for searching next occurrence forward/backwards.
     * @param editor TextEditor instance.
     * @param eventHandler Function object as an eventHandler to allow event subscription.
     * @param args Arguments entered into the function.
     * @param isBackwards True for backward function, vice versa.
     * @see NextOccurrenceForwardFunction.exec
     * @see NextOccurrenceBackwardFunction.exec
     */
    internal fun exec(editor: TextEditor, eventHandler: EventHandler, args: List<String>, isBackwards: Boolean) {
        val config = editor.searchConfiguration
        config.isBackwards = isBackwards
        config.isReplace = false
        editor.searchConfiguration = config

        if (editor.searchConfiguration.searchReplaceRecord.isEmpty()) {
            SearchFunction.exec(editor, arrayListOf())
            return
        }

        val record = editor.searchConfiguration.searchReplaceRecord
        val searchStr = record[editor.searchConfiguration.recPos]
        FunctionHelper.repeatTimes(editor, args, eventHandler) {
            SearchFunctionHelper.searchString(editor, searchStr)
        }
        editor.lowBlankLine = ""
        editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
        editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
        editor.runPostprocessors()
    }
}