package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.chat.plus
import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.function.ReplaceSession.Companion.clearReplaceSession
import com.hasunemiku2015.minecraftnano.builtin.function.ReplaceSession.Companion.replaceSession
import com.hasunemiku2015.minecraftnano.builtin.function.SearchConfiguration.Companion.searchConfiguration
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine1
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine2
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * @author hasunemiku2015
 * @date 2022/10/19 10:40
 */
object ReplaceFunction : Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '\\'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            NanoChatEvent.PAUSE_EDITING.add(editor.player)
            SearchChatEvent.PENDING_ARGUMENT.add(editor.player)
            val config = editor.searchConfiguration
            config.isReplace = true
            editor.searchConfiguration = config
            SearchFunctionHelper.setHelpLines(editor)
            SearchFunctionHelper.setLowBlankLine(editor)
        }
    }
}

/**
 * Event for handling player chat when prompted with "Replace With"
 * > Replace/Search Command -> ReplaceWithSession (Here) -> ReplaceSession -> Execute Replace
 * @author hasunemiku2015
 * @date 2022/10/19 10:42
 */
object ReplaceWithSessionChatEvent: Listener {
    private val SESSION_PLAYER_DATA_MAP = hashMapOf<Player, String>()
    private val REPLACE_HISTORY_PLAYER_MAP = hashMapOf<Player, Pair<ArrayList<String>, Int>>()

    private var TextEditor.replaceWithRecord: Pair<ArrayList<String>, Int>
        get() {
            return if (REPLACE_HISTORY_PLAYER_MAP[player] == null) {
                val pair = Pair(arrayListOf<String>(), 0)
                REPLACE_HISTORY_PLAYER_MAP[player] = pair
                pair
            } else {
                REPLACE_HISTORY_PLAYER_MAP[player]!!
            }
        }
        set(value) {
            REPLACE_HISTORY_PLAYER_MAP[player] = value
        }

    fun addPlayer(player: Player, searchPattern: String) {
        SESSION_PLAYER_DATA_MAP[player] = searchPattern
    }

    fun promptSearchPattern(editor: TextEditor) {
        val recordList = editor.replaceWithRecord.first
        val recordPos = editor.replaceWithRecord.second

        editor.lowBlankLine = "Replace with: ${if (recordPos >= 0) recordList[recordPos] else ""}"
        editor.helpLine1 = "!!G Help      !!P Older    !!. Use History"
        editor.helpLine2 = "!!C Cancel    !!N Newer"
        editor.runPostprocessors()
    }

    @org.bukkit.event.EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val searchPattern = SESSION_PLAYER_DATA_MAP[event.player] ?: return
        val editor = NanoCommand.PLAYER_EDITOR[player] ?: return
        val message = event.message
        event.isCancelled = true

        if (message.startsWith("!!")) {
            // Message is a command.
            if (message.length < 3) {
                return
            }

            when (message[2].lowercaseChar()) {
                'g' -> {
                    //TODO: Display Help Message
                }

                'c' -> {
                    SESSION_PLAYER_DATA_MAP.remove(player)
                    NanoChatEvent.PAUSE_EDITING.remove(editor.player)
                    editor.lowBlankLine = FunctionHelper.centerString(editor, "[ Cancelled ]")
                    editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
                    editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }
                    editor.runPostprocessors()
                    return
                }

                'p' -> {
                    if (editor.replaceWithRecord.second > 1) {
                        val newPair = editor.replaceWithRecord.first to (editor.replaceWithRecord.second - 1)
                        editor.replaceWithRecord = newPair
                    }
                }

                'n' -> {
                    if (editor.replaceWithRecord.second + 1 < editor.replaceWithRecord.first.size) {
                        val newPair = editor.replaceWithRecord.first to (editor.replaceWithRecord.second + 1)
                        editor.replaceWithRecord = newPair
                    }
                }
            }

            promptSearchPattern(editor)
            return
        }

        val forwardList  = SearchFunction.searchStringForward(editor, searchPattern)
        val backwardList = SearchBackwardFunction.searchStringBackward(editor, searchPattern)
        val searchList   = arrayListOf<Triple<Int, Int, Int>>()
        if (editor.searchConfiguration.isBackwards) {
            searchList.addAll(backwardList)
            searchList.addAll(forwardList.reversed())
        } else {
            searchList.addAll(forwardList)
            searchList.addAll(backwardList.reversed())
        }

        if (searchList.isNotEmpty()) {
            editor.replaceSession = ReplaceSession(searchList, 0, message)
            ReplaceSessionChatEvent.addPlayer(player)
            ReplaceSessionChatEvent.promptReplaceString(editor)
        } else {
            NanoChatEvent.PAUSE_EDITING.remove(editor.player)
            editor.lowBlankLine = FunctionHelper.centerString(editor, ChatColor.DARK_RED +
                    "[ \"$message\" not found ]")
            editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
            editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
            runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                editor.lowBlankLine = ""
            }
        }

        SESSION_PLAYER_DATA_MAP.remove(player)
    }

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object EventLifecycleProcessor: DestructionProcessor {
        override fun onEditorDestroy(editor: TextEditor) {
            SESSION_PLAYER_DATA_MAP.remove(editor.player)
            REPLACE_HISTORY_PLAYER_MAP.remove(editor.player)
        }
    }
}


/**
 * Event for handling players within a replacement session.
 * > Replace/Search Command -> ReplaceWithSession -> ReplaceSession (Here) -> Execute Replace
 * @author hasunemiku2015
 * @date 2022/10/19 10:42
 */
object ReplaceSessionChatEvent : Listener {
    private val replaceSessionPlayer = arrayListOf<Player>()

    fun addPlayer(player: Player) {
        replaceSessionPlayer.add(player)
    }

    @org.bukkit.event.EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val message = event.message
        val editor = NanoCommand.PLAYER_EDITOR[event.player] ?: return

        if (!replaceSessionPlayer.contains(player)) {
            return
        }
        event.isCancelled = true
        val session = editor.replaceSession

        fun replaceString(cursorPos: Int, cursorCharPos: Int, strLength: Int) {
            val baseString = editor.fileData[cursorPos]
            val replacedString = baseString.substring(0, cursorCharPos) + editor.replaceSession.replaceString +
                    baseString.substring(cursorCharPos + strLength)
            editor.fileData[cursorPos] = replacedString
            editor.outputBuffer[cursorPos] = replacedString
        }

        when(message.lowercase().first()) {
            'y' -> {
                // Yes
                val replaceData = editor.replaceSession.replaceData[0]
                replaceString(replaceData.first, replaceData.second, replaceData.third)
                session.replaceData = session.replaceData.drop(0)
                editor.replaceSession = session
            }

            'n' -> {
                // No
                session.replaceData = session.replaceData.drop(0)
                editor.replaceSession = session
            }

            'a' -> {
                // All
                for ((cursorPos, cursorCharPos, strLength) in editor.replaceSession.replaceData) {
                    replaceString(cursorPos, cursorCharPos, strLength)
                    session.replaceCount++
                    editor.replaceSession = session
                    onReplaceComplete(editor)
                }
            }

            'c' -> {
                // Cancel
                onReplaceComplete(editor)
            }
        }

        if (editor.replaceSession.replaceData.isEmpty()) {
            onReplaceComplete(editor)
        } else {
            promptReplaceString(editor)
        }
    }

    /**
     * Sets the lower lines of a TextEditor session upon completion of replacement.
     * Not a EventHandler.
     * @param editor TextEditor to set lower lines.
     */
    private fun onReplaceComplete(editor: TextEditor) {
        replaceSessionPlayer.remove(editor.player)
        editor.clearReplaceSession()
        NanoChatEvent.PAUSE_EDITING.remove(editor.player)
        editor.lowBlankLine = FunctionHelper.centerString(editor,
            "[ Replaces ${editor.replaceSession.replaceCount} occurrences ]")
        editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
        editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
        runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
            editor.lowBlankLine = ""
        }
        editor.runPostprocessors()
    }

    /**
     * Prompts the owner of the editor session (a player) on which string is going to be replaced
     * and the command options.
     * @param editor TextEditor session for this function to run on.
     */
    internal fun promptReplaceString(editor: TextEditor) {
        val replaceData = editor.replaceSession.replaceData[0]
        SearchFunctionHelper.highlightString(editor, replaceData.first, replaceData.second, replaceData.third)
        editor.lowBlankLine = "Replace this instance?"
        editor.helpLine1 = "Y Yes      A All"
        editor.helpLine2 = "N No       C Cancel"
    }
}

/**
 * Stores a per player list of replaceable strings during replacement operations.
 * @author hasunemiku2015
 * @date 2022/10/19 10:43
 */
data class ReplaceSession(var replaceData: List<Triple<Int, Int, Int>>, var replaceCount: Int,
                          var replaceString: String) {
    companion object {
        private val PLAYER_REPLACE_SESSION  = hashMapOf<Player, ReplaceSession>()
        private val DEFAULT_REPLACE_SESSION = ReplaceSession(listOf(), 0 , "")

        internal var TextEditor.replaceSession: ReplaceSession
            get() {
                if (PLAYER_REPLACE_SESSION[player] == null) {
                    PLAYER_REPLACE_SESSION[player] = DEFAULT_REPLACE_SESSION
                }
                return PLAYER_REPLACE_SESSION[player] ?: DEFAULT_REPLACE_SESSION
            }
            set(value) {
                PLAYER_REPLACE_SESSION[player] = value
            }

        internal fun TextEditor.clearReplaceSession() {
            PLAYER_REPLACE_SESSION[player] = DEFAULT_REPLACE_SESSION
        }
    }

    /**
     * Destruction Processor to prevent memory leaks.
     */
    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object EventDestructionProcessor: DestructionProcessor {
        override fun onEditorDestroy(editor: TextEditor) {
            PLAYER_REPLACE_SESSION.remove(editor.player)
        }
    }
}