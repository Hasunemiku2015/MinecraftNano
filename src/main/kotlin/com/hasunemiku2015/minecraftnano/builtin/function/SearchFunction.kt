package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.chat.plus
import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.function.SearchConfiguration.Companion.searchConfiguration
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine1
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.helpLine2
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.showCursor
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * @author hasunemiku2015
 * @date 2022/10/04 16:17
 */
object SearchFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'w'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            if (args.isEmpty()) {
                NanoChatEvent.PAUSE_EDITING.add(editor.player)
                SearchChatEvent.PENDING_ARGUMENT.add(editor.player)
                SearchFunctionHelper.setHelpLines(editor)
                SearchFunctionHelper.setLowBlankLine(editor)
            } else {
                val searchFor = args.let {
                    val sb = StringBuilder()
                    for ((idx, dat) in args.withIndex()) {
                        sb.append(dat)
                        if (idx < args.size - 1) {
                            sb.append(" ")
                        }
                    }
                    sb.toString()
                }
                SearchFunctionHelper.searchString(editor, searchFor)
            }
        }
    }

    /**
     * Searches a String or Regex (Based on Configuration) from current cursor position to end of file.
     * @param editor TextEditor session to search string.
     * @param str String or Regex to search for.
     * @return A list of triple with the following data:
     * 1. The row cursor position of the found string.
     * 2. The column position of the found string.
     * 3. The length of found string.
     */
    internal fun searchStringForward(editor: TextEditor, str: String): List<Triple<Int, Int, Int>> {
        val currLineSearch = editor.fileData[editor.cursorPosition].substring(editor.cursorCharPosition)
        val temp = editor.fileData.asList().subList(editor.cursorPosition + 1, editor.fileData.size())
        val searchList = ArrayList(temp)
        searchList.add(0, currLineSearch)

        val outputList = arrayListOf<Triple<Int, Int, Int>>()
        val isRegex = editor.searchConfiguration.isRegex
        val ignoreCase = editor.searchConfiguration.isCaseSensitive

        for ((idx, searchString) in searchList.withIndex()) {
            if (isRegex) {
                val matchStrings = Regex(str).findAll(searchString)
                var currentIndex = 0
                matchStrings.forEach {
                    val matchIdx = searchString.indexOf(it.value, currentIndex, !ignoreCase)
                    outputList.add(Triple(editor.cursorPosition + idx, matchIdx, it.value.length))
                    currentIndex = matchIdx + it.value.length
                }
            } else {
                var currentIndex = 0
                while (true) {
                    val index = searchString.indexOf(str, currentIndex, !ignoreCase)
                    if (index < 0) {
                        break
                    } else {
                        outputList.add(Triple(editor.cursorPosition + idx, index, str.length))
                        currentIndex = index + str.length + 1
                        if (currentIndex > searchString.length - str.length - 1) {
                            break
                        }
                    }
                }
            }
        }
        return outputList
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/11 11:07
 */
object SearchBackwardFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'q'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            if (args.isEmpty()) {
                NanoChatEvent.PAUSE_EDITING.add(editor.player)
                SearchChatEvent.PENDING_ARGUMENT.add(editor.player)
                val config = editor.searchConfiguration
                config.isBackwards = true
                editor.searchConfiguration = config
                SearchFunctionHelper.setHelpLines(editor)
                SearchFunctionHelper.setLowBlankLine(editor)
            } else {
                val config = editor.searchConfiguration
                config.isBackwards = true
                editor.searchConfiguration = config

                val searchFor = args.let {
                    val sb = StringBuilder()
                    for ((idx, dat) in args.withIndex()) {
                        sb.append(dat)
                        if (idx < args.size - 1) {
                            sb.append(" ")
                        }
                    }
                    sb.toString()
                }
                SearchFunctionHelper.searchString(editor, searchFor)
            }
        }
    }

    /**
     * Searches a String or Regex (Based on Configuration) from current cursor position to start of file.
     * @param editor TextEditor session to search string.
     * @param str String or Regex to search for.
     * @return A list of triple with the following data:
     * 1. The row cursor position of the found string.
     * 2. The column position of the found string.
     * 3. The length of found string.
     */
    internal fun searchStringBackward(editor: TextEditor, str: String): List<Triple<Int, Int, Int>> {
        val currLineSearch = editor.fileData[editor.cursorPosition].substring(0, editor.cursorCharPosition)
        val temp = editor.fileData.asList().subList(0, editor.cursorPosition).reversed()
        val searchList = ArrayList(temp)
        searchList.add(0, currLineSearch)

        val outputList = arrayListOf<Triple<Int, Int, Int>>()
        val isRegex = editor.searchConfiguration.isRegex
        val ignoreCase = editor.searchConfiguration.isCaseSensitive

        for ((idx, searchString) in searchList.withIndex()) {
            val tempOutList = arrayListOf<Triple<Int, Int, Int>>()

            if (isRegex) {
                val matchStrings = Regex(str).findAll(searchString)
                var currentIndex = 0
                matchStrings.forEach {
                    val matchIdx = searchString.indexOf(it.value, currentIndex, !ignoreCase)
                    tempOutList.add(Triple(editor.cursorPosition + idx, matchIdx, it.value.length))
                    currentIndex = matchIdx + it.value.length
                }
            } else {
                var currentIndex = 0
                while (true) {
                    val index = searchString.indexOf(str, currentIndex, !ignoreCase)
                    if (index < 0) {
                        break
                    } else {
                        outputList.add(Triple(editor.cursorPosition + idx, index, str.length))
                        currentIndex = index + str.length + 1
                        if (currentIndex > searchString.length - str.length - 1) {
                            break
                        }
                    }
                }
            }

            outputList.addAll(tempOutList.reversed())
        }

        return outputList
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/04 16:17
 */
object SearchChatEvent: Listener {
    internal val PENDING_ARGUMENT = arrayListOf<Player>()

    @org.bukkit.event.EventHandler
    fun onArgumentType(event: AsyncPlayerChatEvent) {
        if (!PENDING_ARGUMENT.contains(event.player)) {
            return
        }
        val editor = NanoCommand.PLAYER_EDITOR[event.player] ?: return

        event.isCancelled = true

        if (!event.message.startsWith("!!") && !event.message.startsWith("!^") ) {
            SearchFunctionHelper.searchString(editor, event.message)
            val config = editor.searchConfiguration
            config.searchReplaceRecord.add(0, event.message)
            editor.searchConfiguration = config

            NanoChatEvent.PAUSE_EDITING.remove(event.player)
            PENDING_ARGUMENT.remove(event.player)

            editor.lowBlankLine = ""
            editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
            editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
            editor.runPostprocessors()
            return
        }

        if (event.message.length < 3) {
            return
        }
        val cmdChar = event.message[2]

        if (event.message.startsWith("!!")) {
            // MUST use config to set, or else things won't work!
            val config = editor.searchConfiguration
            val record = editor.searchConfiguration.searchReplaceRecord
            when (cmdChar.lowercaseChar()) {
                // !!c
                'c' -> {
                    editor.lowBlankLine = FunctionHelper.centerString(editor, " [ Cancelled ] ")
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }

                    editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
                    editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
                    NanoChatEvent.PAUSE_EDITING.remove(event.player)
                    PENDING_ARGUMENT.remove(event.player)
                    editor.runPostprocessors()
                    return
                }

                // !!.
                '.' -> {
                    if (editor.searchConfiguration.searchReplaceRecord.isNotEmpty()) {
                        val str = editor.searchConfiguration.searchReplaceRecord[editor.searchConfiguration.recPos]
                        SearchFunctionHelper.searchString(editor, str)
                        editor.lowBlankLine = ""
                    } else {
                        editor.lowBlankLine = FunctionHelper.centerString(editor, " [ Cancelled ] ")
                        runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                            editor.lowBlankLine = ""
                        }
                    }

                    editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
                    editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
                    NanoChatEvent.PAUSE_EDITING.remove(event.player)
                    PENDING_ARGUMENT.remove(event.player)
                    editor.runPostprocessors()
                    return
                }

                // !!r
                'r' -> config.isReplace = !config.isReplace

                // !!p
                'p' -> {
                    if (record.isNotEmpty()) {
                        if (config.recPos < record.size - 2) {
                            config.recPos += 1
                        }
                    }
                }

                // !!n
                'n' -> {
                    if (record.isNotEmpty()) {
                        if (config.recPos > 0) {
                            config.recPos -= 1
                        }
                    }
                }

                // !!t
                't' -> {
                    PENDING_ARGUMENT.remove(event.player)
                    GotoLineFunction.exec(editor, arrayListOf())
                    return
                }

                // !!g
                'g' -> {
                    // TODO: Display Help Menu
                }
            }
            editor.searchConfiguration = config
        }

        if (event.message.startsWith("!^")) {
            // MUST use config to set, or else things won't work!
            val config = editor.searchConfiguration
            when (cmdChar.lowercaseChar()) {
                // !^b
                'b' -> config.isBackwards = !config.isBackwards

                // !^c
                'c' -> config.isCaseSensitive = !config.isCaseSensitive

                // !^r
                'r' -> config.isReplace = !config.isReplace
            }
            editor.searchConfiguration = config
        }

        SearchFunctionHelper.setLowBlankLine(editor)
        editor.runPostprocessors()
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/04 16:17
 */
data class SearchConfiguration (var isCaseSensitive: Boolean, var isRegex: Boolean, var isBackwards: Boolean,
                               var isReplace: Boolean, var searchReplaceRecord: ArrayList<String>, var recPos: Int) {
    companion object {
        private val SEARCH_CONFIGURATION = hashMapOf<Player, SearchConfiguration>()
        internal var TextEditor.searchConfiguration: SearchConfiguration
            get() = SEARCH_CONFIGURATION[player] ?: SearchConfiguration(
                isCaseSensitive = false,
                isRegex = false,
                isBackwards = false,
                isReplace = false,
                searchReplaceRecord = arrayListOf(),
                recPos = 0
            )
            set(value) {
                SEARCH_CONFIGURATION[player] = value
            }
    }

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object SearchProcessor: LifecycleProcessor {
        override fun onEditorCreate(editor: TextEditor) {
            SEARCH_CONFIGURATION[editor.player] = SearchConfiguration(
                isCaseSensitive = false,
                isRegex = false,
                isBackwards = false,
                isReplace = false,
                searchReplaceRecord = arrayListOf(),
                recPos = 0
            )
        }

        override fun onEditorDestroy(editor: TextEditor) {
            SEARCH_CONFIGURATION.remove(editor.player)
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/04 17:40
 */
object SearchFunctionHelper {
    fun setHelpLines(editor: TextEditor) {
        editor.helpLine1 = "!!G Help    !^C Case Sens  !^B Backwards   !!P Older  !!T Go To Line"
        editor.helpLine2 = if (editor.searchConfiguration.isReplace)
            "!!C Cancel  !^R Reg.exp.   !^R No Replace  !!N Newer  !!. Use History"
        else "!!C Cancel  !^R Reg.exp.   !^R Replace     !!N Newer !!. Use History"
    }

    fun setLowBlankLine(editor: TextEditor) {
        val record = editor.searchConfiguration.searchReplaceRecord
        val recPos = editor.searchConfiguration.recPos

        val sb = StringBuilder()
        sb.append("Search")

        // Order is important to follow convention of GNU Nano.
        if (editor.searchConfiguration.isCaseSensitive) {
            sb.append(" [Case Sensitive]")
        }
        if (editor.searchConfiguration.isRegex) {
            sb.append(" [RegExp]")
        }
        if (editor.searchConfiguration.isBackwards) {
            sb.append(" [Backwards]")
        }
        if (editor.searchConfiguration.isReplace) {
            sb.append(" (to replace)")
        }
        if (record.isNotEmpty()) {
            sb.append(" [${record[recPos]}]")
        }

        sb.append(":")
        editor.lowBlankLine = sb.toString()
    }

    internal fun searchString(editor: TextEditor, searchFor: String) {
        // String replacement logic in ReplaceFunction.kt
        if (editor.searchConfiguration.isReplace) {
            ReplaceWithSessionChatEvent.addPlayer(editor.player, searchFor)
            ReplaceWithSessionChatEvent.promptSearchPattern(editor)
            return
        }

        fun searchDirectionByConfig(): List<Triple<Int, Int, Int>> {
            return if (!editor.searchConfiguration.isBackwards) {
                SearchFunction.searchStringForward(editor, searchFor)
            } else {
                SearchBackwardFunction.searchStringBackward(editor, searchFor)
            }
        }

        val positions = searchDirectionByConfig()
        if (positions.isEmpty()) {
            val oldCursorPosition = editor.cursorPosition
            val oldCursorCharPosition = editor.cursorCharPosition

            if (editor.searchConfiguration.isBackwards) {
                editor.cursorPosition = editor.fileData.size()
                editor.cursorCharPosition = editor.fileData.size()
            } else {
                editor.cursorPosition = 0
                editor.cursorCharPosition = 0
            }

            val newPositions = searchDirectionByConfig()

            if (newPositions.isEmpty()) {
                editor.cursorPosition = oldCursorPosition
                editor.cursorCharPosition = oldCursorCharPosition
                editor.lowBlankLine = FunctionHelper.centerString(editor,
                    ChatColor.DARK_RED + " [ \"$searchFor\" not found ] ")
                runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                    editor.lowBlankLine = ""
                }
            } else {
                if (positions[0].first == oldCursorPosition && positions[0].second == oldCursorCharPosition) {
                    highlightString(editor, oldCursorPosition, oldCursorCharPosition, positions[0].third)
                    editor.lowBlankLine = FunctionHelper.centerString(editor, "[ This is the only occurrence ]")
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }
                } else {
                    highlightString(editor, positions[0].first, positions[0].second, positions[0].third)
                    editor.lowBlankLine = FunctionHelper.centerString(editor, " [ Search Wrapped ] ")
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }
                }
            }
        } else {
            highlightString(editor, positions[0].first, positions[0].second, positions[0].third)
        }

        NanoChatEvent.PAUSE_EDITING.remove(editor.player)
        SearchChatEvent.PENDING_ARGUMENT.remove(editor.player)
        val var0 = DisplayPostProcessor.DisplayConfiguration()
        editor.helpLine1 = var0.helpLine1
        editor.helpLine2 = var0.helpLine2
    }

    /**
     * Highlights a string based on search result.
     */
    fun highlightString(editor: TextEditor, cursorPosition: Int, cursorCharPosition: Int, stringLength: Int) {
        editor.cursorPosition = cursorPosition
        editor.cursorCharPosition = cursorCharPosition

        editor.showCursor = false
        val originalString = editor.outputBuffer[cursorPosition]
        editor.outputBuffer[editor.cursorPosition] = originalString.substring(0, editor.cursorCharPosition) +
                ChatColor.GREEN + ChatColor.UNDERLINE + originalString.substring(editor.cursorCharPosition, editor.cursorCharPosition + 1) + ChatColor.RESET +
                ChatColor.GREEN + originalString.substring(editor.cursorCharPosition + 1, editor.cursorCharPosition + stringLength) +
                ChatColor.RESET + originalString.substring(editor.cursorCharPosition + stringLength)
        runTaskLater(NanoPlugin.PLUGIN, 1) {
            editor.showCursor = true
        }
    }
}