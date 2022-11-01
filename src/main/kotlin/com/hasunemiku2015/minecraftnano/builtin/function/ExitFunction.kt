package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent
import com.hasunemiku2015.minecraftnano.datastruct.SparseVector
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.DestructionProcessor
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine1
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine2
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.io.BufferedReader
import java.io.FileReader

private val DEFAULT_DISPLAY_CONFIG = DisplayPostProcessor.DisplayConfiguration()

fun setQuitWithoutSaving(editor: TextEditor) {
    editor.lowBlankLine = FunctionHelper.centerString(editor, "[ Quit without Saving ]")
    editor.helpLine1 = DEFAULT_DISPLAY_CONFIG.helpLine1
    editor.helpLine2 = DEFAULT_DISPLAY_CONFIG.helpLine2
    editor.runPostprocessors()
    editor.lowBlankLine = ""
    ExitFunction.destruct(editor.player, false)
    editor.onDestroy()
}

/**
 * @author hasunemiku2015
 * @date 2022/09/11 21:59
 */
object ExitFunction : Function, com.hasunemiku2015.minecraftnano.api.EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'x'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            val originalFileData = SparseVector("")
            BufferedReader(FileReader(editor.file)).use {
                it.readLines().forEach { i -> originalFileData.append(i) }
            }

            if (originalFileData.asList() != editor.fileData.asList()) {
                ExitChatEvent.PENDING_CONFIRMATION.add(editor.player)
                NanoChatEvent.PAUSE_EDITING.add(editor.player)
                editor.lowBlankLine = "Save Modified Buffer?"
                editor.helpLine1 = "Y  Yes"
                editor.helpLine2 = "N  No   !!C Cancel"
                editor.runPostprocessors()

                runTaskLater(NanoPlugin.PLUGIN, 1) {
                    editor.lowBlankLine = DEFAULT_DISPLAY_CONFIG.lowBlankLine
                    editor.helpLine1 = DEFAULT_DISPLAY_CONFIG.helpLine1
                    editor.helpLine2 = DEFAULT_DISPLAY_CONFIG.helpLine2
                }

                runTaskLater(NanoPlugin.PLUGIN, 10 * 20) {
                    if (ExitChatEvent.PENDING_CONFIRMATION.contains(editor.player)) {
                        destruct(editor.player, true)
                    }
                }
            } else {
                setQuitWithoutSaving(editor)
            }
        }
    }

    @JvmStatic
    fun destruct(player: Player, isRefresh: Boolean) {
        ExitChatEvent.PENDING_CONFIRMATION.remove(player)
        NanoChatEvent.PAUSE_EDITING.remove(player)
        if (isRefresh) {
            NanoCommand.PLAYER_EDITOR[player]?.runPostprocessors()
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/09/11 21:59
 */
object ExitChatEvent : Listener, DestructionProcessor {
    val PENDING_CONFIRMATION = arrayListOf<Player>()

    @EventHandler
    fun onConfirmType(event: AsyncPlayerChatEvent) {
        if (!PENDING_CONFIRMATION.contains(event.player)) {
            return
        }

        val editor = NanoCommand.PLAYER_EDITOR[event.player]
        if (event.message.equals("y", true) ) {
            if (editor != null) {
                FunctionHelper.saveDataToFile(editor)
            }
            editor!!.lowBlankLine = FunctionHelper.centerString(editor, "[ Saved and Quit ]")
            editor.helpLine1 = DEFAULT_DISPLAY_CONFIG.helpLine1
            editor.helpLine2 = DEFAULT_DISPLAY_CONFIG.helpLine2
            editor.runPostprocessors()
            editor.lowBlankLine = ""
            editor.onDestroy()
            ExitFunction.destruct(event.player, false)
        } else if (event.message.equals("n", true)) {
            if (editor != null) {
                setQuitWithoutSaving(editor)
            }
        } else if (event.message.equals("!!c", true)) {
            ExitFunction.destruct(event.player, true)
        }

        event.isCancelled = true
    }

    override fun onEditorDestroy(editor: TextEditor) {
        PENDING_CONFIRMATION.remove(editor.player)
    }
}
