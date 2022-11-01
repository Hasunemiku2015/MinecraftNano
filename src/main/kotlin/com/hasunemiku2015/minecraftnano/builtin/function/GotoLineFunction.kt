package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine1
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine2
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import kotlin.math.max
import kotlin.math.min

/**
 * @author hasunemiku2015
 * @date 2022/10/30 15:45
 */
object GotoLineFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '/'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            if (args.isEmpty()) {
                editor.lowBlankLine = "Enter line number, column number:"
                editor.helpLine1 = "!!G Help      !!W Begin of Paragr.    !!Y First Line     !!T Go To Text"
                editor.helpLine2 = "!!C Cancel    !!Y End of Paragraph    !!V Last Line"
                editor.runPostprocessors()

                runTaskLater(NanoPlugin.PLUGIN, 1) {
                    NanoChatEvent.PAUSE_EDITING.add(editor.player)
                    GotoLineChatEvent.PENDING_ARGS.add(editor.player)
                }
                return@d
            }

            if (args.size == 1) {
                val cursorPos = args[0].toIntOrNull() ?: return@d
                editor.cursorPosition = max(min(0, cursorPos - 1), editor.fileData.size())
                if (editor.cursorCharPosition > editor.fileData[editor.cursorPosition].length) {
                    editor.cursorCharPosition = editor.fileData[editor.cursorPosition].length
                }
                return@d
            }

            val cursorPos = args[0].toIntOrNull() ?: return@d
            val cursorCharPos = args[1].toIntOrNull() ?: return@d
            editor.cursorPosition = max(min(0, cursorPos - 1), editor.fileData.size())
            editor.cursorCharPosition = max(min(0, cursorCharPos - 1), editor.fileData[editor.cursorPosition].length)
            return@d
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/30 16:10
 */
object GotoLineChatEvent: Listener {
    internal val PENDING_ARGS = arrayListOf<Player>()

    @org.bukkit.event.EventHandler
    fun onArgType(event: AsyncPlayerChatEvent) {
        val player = event.player
        val editor = NanoCommand.PLAYER_EDITOR[player] ?: return

        if (!PENDING_ARGS.contains(player)) {
            return
        }

        event.isCancelled = true
        if (event.message.isEmpty()) {
            return
        }

        if (event.message.startsWith("!!")) {
            if (event.message.length < 3) {
                onComplete(editor, true, runPostprocessor = true)
                return
            }

            when (event.message[2].lowercaseChar()) {
                'g' -> {
                    // TODO: Help Menu
                }

                'c' -> {
                    // Cancel
                    val str = FunctionHelper.centerString(editor, "[ Cancelled ]")
                    editor.lowBlankLine = str
                    runTaskLater(NanoPlugin.PLUGIN, 5*20) {
                        if (editor.lowBlankLine == str) {
                            editor.lowBlankLine = ""
                        }
                    }
                    onComplete(editor, false, runPostprocessor = true)
                    return
                }

                'w' -> {
                    // TODO: Begin of Paragraph
                }

                'o' -> {
                    // TODO: End of Paragraph
                }

                'y' -> {
                    // First Line
                    editor.cursorPosition = 0
                    editor.cursorCharPosition = 0
                }

                'v' -> {
                    // Last Line
                    editor.cursorPosition = editor.fileData.size()
                    editor.cursorCharPosition = 0
                }

                't' -> {
                    // Go To Text (Search Text)
                    onComplete(editor, true,  runPostprocessor = false)
                    SearchFunction.exec(editor, arrayListOf())
                    return
                }
            }

            onComplete(editor, true,  runPostprocessor = true)
            return
        }

        val args = event.message.split(',')
        if (args.size == 1) {
            val cursorPos = args[0].toIntOrNull()
            if (cursorPos == null) {
                onComplete(editor, true,  runPostprocessor = true)
                return
            }
            editor.cursorPosition = max(min(0, cursorPos - 1), editor.fileData.size())
            if (editor.cursorCharPosition > editor.fileData[editor.cursorPosition].length) {
                editor.cursorCharPosition = editor.fileData[editor.cursorPosition].length
            }
        } else {
            val cursorPos = args[0].toIntOrNull()
            val cursorCharPos = args[1].toIntOrNull()

            if (cursorPos == null || cursorCharPos == null) {
                onComplete(editor, true,  runPostprocessor = true)
                return
            }
            editor.cursorPosition = max(min(0, cursorPos - 1), editor.fileData.size())
            editor.cursorCharPosition = max(min(0, cursorCharPos - 1), editor.fileData[editor.cursorPosition].length)
        }
    }

    /**
     * Helper function for resetting the TextEditor to default upon exit or completion.
     * @param editor TextEditor to reset.
     * @param setLowBlankLine If lowBlankLine should be reset. (Useful for !!c).
     * @param runPostprocessor Should postProcessor be run to reload screen. (Useful for !!t)
     */
    private fun onComplete(editor: TextEditor, setLowBlankLine: Boolean, runPostprocessor: Boolean) {
        NanoChatEvent.PAUSE_EDITING.remove(editor.player)
        PENDING_ARGS.remove(editor.player)
        if (setLowBlankLine) {
            editor.lowBlankLine = ""
        }
        editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
        editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2

        if (runPostprocessor) {
            editor.runPostprocessors()
        }
    }

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object EventDestructionProcessor: DestructionProcessor {
        override fun onEditorDestroy(editor: TextEditor) {
            PENDING_ARGS.remove(editor.player)
        }
    }
}