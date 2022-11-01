package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.function.LineNavigationChatEvent.cursorCharPositionCache
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * @author hasunemiku2015
 * @date 2022/09/26 17:33
 */
object PrevLineFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'p'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            if (it.cursorPosition > 0) {
                it.cursorPosition -= 1
                if (editor.cursorCharPositionCache < 0) {
                    editor.cursorCharPositionCache = editor.cursorCharPosition
                }
                if (editor.cursorCharPositionCache > editor.fileData[editor.cursorPosition].length) {
                    editor.cursorCharPosition = editor.fileData[editor.cursorPosition].length
                } else {
                    editor.cursorCharPosition = editor.cursorCharPositionCache
                }
            }
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/09/26 17:33
 */
object NextLineFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'n'
    }

    override fun exec(editor: TextEditor, args: List<String>) {

        FunctionHelper.repeatTimes(editor, args, this) {
            editor.cursorPosition += 1
            if (editor.cursorCharPositionCache < 0) {
                editor.cursorCharPositionCache = editor.cursorCharPosition
            }
            if (editor.cursorCharPositionCache > editor.fileData[editor.cursorPosition].length) {
                editor.cursorCharPosition = editor.fileData[editor.cursorPosition].length
            } else {
                editor.cursorCharPosition = editor.cursorCharPositionCache
            }
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/09/26 17:33
 */
@ProcessPriority(ProcessPriorityLevel.NORMAL)
object LineNavigationChatEvent: Listener, DestructionProcessor {
    private val CURSOR_CHAR_POS_CACHE = hashMapOf<TextEditor, Int>()

    internal var TextEditor.cursorCharPositionCache: Int
        set(value) {
            CURSOR_CHAR_POS_CACHE[this] = value
        }
        get() {
            return CURSOR_CHAR_POS_CACHE[this] ?: -1
        }

    @org.bukkit.event.EventHandler
    fun onKeyStrokeEnter(event: AsyncPlayerChatEvent) {
        if (event.message.startsWith("!!p") || event.message.startsWith("!!n")) {
            return
        }

        val editor = NanoCommand.PLAYER_EDITOR[event.player] ?: return
        editor.cursorCharPositionCache = -1
    }

    override fun onEditorDestroy(editor: TextEditor) {
        CURSOR_CHAR_POS_CACHE.remove(editor)
    }
}