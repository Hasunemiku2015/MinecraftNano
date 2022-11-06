package com.hasunemiku2015.minecraftnano.builtin.processor

import com.deanveloper.kbukkit.chat.plus
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.builtin.altfunction.AnchorStore.isAnchored
import com.hasunemiku2015.minecraftnano.commands.NanoPrefCommand.preference
import com.hasunemiku2015.minecraftnano.events.NanoPacketEvent.sendMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * Postprocessor to display text to player, should be run last.
 * @author hasunemiku2015
 * @date 2022/09/25 23:38
 */
@ProcessPriority(priority = ProcessPriorityLevel.DISPLAY)
object DisplayPostProcessor : Postprocessor, EventHandler() {
    data class DisplayConfiguration(
        var doPrint: Boolean = true, var showCursor: Boolean = true,
        var rightHeader: String = "INSERT",
        var lowBlankLine: String = "",
        var helpLine1: String =
            "!!G Help  !!O Write Out  !!W Where Us  !!K Cut    !!Y Prev Page  !!C Location",
        var helpLine2: String =
            "!!X Exit  !!R Read File  !!\\ Replace   !!U Paste  !!V Next Page  !!/ Go To Line"
    ) {
        companion object {
            val DEFAULT_CONFIG = DisplayConfiguration()
        }
    }

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object DisplayDestructionProcessor: DestructionProcessor {
        override fun onEditorDestroy(editor: TextEditor) {
            PLAYER_CONFIGURATIONS.remove(editor.player)
        }
    }

    private val PLAYER_CONFIGURATIONS = hashMapOf<Player, DisplayConfiguration>()

    var TextEditor.doPrint: Boolean
        set(value) {
            if (!PLAYER_CONFIGURATIONS.containsKey(player)) {
                PLAYER_CONFIGURATIONS[player] = DisplayConfiguration()
            }
            PLAYER_CONFIGURATIONS[player]!!.doPrint = value
        }
        get() = PLAYER_CONFIGURATIONS.let {
            if (!it.containsKey(player)) {
                it[player] = DisplayConfiguration()
            }
            return@let PLAYER_CONFIGURATIONS[player]!!.doPrint
        }

    var TextEditor.showCursor: Boolean
        set(value) {
            if (!PLAYER_CONFIGURATIONS.containsKey(player)) {
                PLAYER_CONFIGURATIONS[player] = DisplayConfiguration()
            }
            PLAYER_CONFIGURATIONS[player]!!.showCursor = value
        }
        get() = PLAYER_CONFIGURATIONS.let {
            if (!it.containsKey(player)) {
                it[player] = DisplayConfiguration()
            }
            return@let PLAYER_CONFIGURATIONS[player]!!.showCursor
        }

    var TextEditor.rightHeader: String
        set(value) {
            if (!PLAYER_CONFIGURATIONS.containsKey(player)) {
                PLAYER_CONFIGURATIONS[player] = DisplayConfiguration()
            }
            PLAYER_CONFIGURATIONS[player]!!.rightHeader = value
        }
        get() = PLAYER_CONFIGURATIONS.let {
            if (!it.containsKey(player)) {
                it[player] = DisplayConfiguration()
            }
            return@let PLAYER_CONFIGURATIONS[player]!!.rightHeader
        }

    var TextEditor.lowBlankLine: String
        set(value) {
            if (!PLAYER_CONFIGURATIONS.containsKey(player)) {
                PLAYER_CONFIGURATIONS[player] = DisplayConfiguration()
            }
            PLAYER_CONFIGURATIONS[player]!!.lowBlankLine = value
        }
        get() = PLAYER_CONFIGURATIONS.let {
            if (!it.containsKey(player)) {
                it[player] = DisplayConfiguration()
            }
            return@let PLAYER_CONFIGURATIONS[player]!!.lowBlankLine
        }

    var TextEditor.helpLine1: String
        set(value) {
            if (!PLAYER_CONFIGURATIONS.containsKey(player)) {
                PLAYER_CONFIGURATIONS[player] = DisplayConfiguration()
            }
            PLAYER_CONFIGURATIONS[player]!!.helpLine1 = value
        }
        get() = PLAYER_CONFIGURATIONS.let {
            if (!it.containsKey(player)) {
                it[player] = DisplayConfiguration()
            }
            return@let PLAYER_CONFIGURATIONS[player]!!.helpLine1
        }

    var TextEditor.helpLine2: String
        set(value) {
            if (!PLAYER_CONFIGURATIONS.containsKey(player)) {
                PLAYER_CONFIGURATIONS[player] = DisplayConfiguration()
            }
            PLAYER_CONFIGURATIONS[player]!!.helpLine2 = value
        }
        get() = PLAYER_CONFIGURATIONS.let {
            if (!it.containsKey(player)) {
                it[player] = DisplayConfiguration()
            }
            return@let PLAYER_CONFIGURATIONS[player]!!.helpLine2
        }

    override fun exec(editor: TextEditor) {
        super.d(editor) {
            if (!it.doPrint) {
                return@d
            }

            if (it.showCursor) {
                val data = it.outputBuffer[it.cursorPosition]
                if (data.isEmpty()) {
                    it.outputBuffer[it.cursorPosition] = ChatColor.UNDERLINE + " " + ChatColor.RESET
                } else if (it.cursorCharPosition == data.length) {
                    it.outputBuffer[it.cursorPosition] = data + ChatColor.UNDERLINE + " " + ChatColor.RESET
                } else {
                    it.outputBuffer[it.cursorPosition] = data.substring(0, it.cursorCharPosition) +
                            ChatColor.UNDERLINE + data[it.cursorCharPosition] + ChatColor.RESET +
                            data.substring(it.cursorCharPosition + 1)
                }
            }

            editor.sendMessage(getHeaderLine(it))
            editor.sendMessage("")

            var count = 0
            for ((idx, s) in it.outputBuffer.asList((it.currentPage + 1) * 15).withIndex()) {
                if (count >= 15) {
                    break
                }

                val lineString = (if (it.isAnchored(idx)) ChatColor.AQUA else ChatColor.DARK_GRAY) +
                        "${idx + 1} " + ChatColor.RESET + s
                editor.sendMessage(lineString)

                // Print less lines if longer than maxChar
                count += (lineString.length - 1) / it.preference.maxCharLine
                count++
            }

            editor.sendMessage(it.lowBlankLine)
            editor.sendMessage(it.helpLine1)
            editor.sendMessage(it.helpLine2)
        }
    }

    private fun getHeaderLine(editor: TextEditor): String {
        val preference = editor.preference
        val rightHeader = editor.rightHeader
        val file = editor.file

        return StringBuilder().let {
            val leftHeader = "Minecraft Nano ${NanoPlugin.PLUGIN.description.version}"
            it.append(leftHeader)

            fun appendSpace(numOfSpace: Int) {
                if (numOfSpace > 4) {
                    for (i in 1..numOfSpace) {
                        it.append(" ")
                    }
                } else {
                    it.append("    ")
                }
            }

            appendSpace(preference.maxCharLine / 2 - leftHeader.length)
            it.append(file.name)
            appendSpace(preference.maxCharLine - it.length - rightHeader.length)
            it.append(rightHeader)
            it.toString()
        }
    }
}