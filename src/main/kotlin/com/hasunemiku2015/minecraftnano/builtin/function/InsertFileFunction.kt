package com.hasunemiku2015.minecraftnano.builtin.function

import com.deanveloper.kbukkit.chat.plus
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
import com.hasunemiku2015.minecraftnano.commands.NanoPrefCommand.preference
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent
import com.hasunemiku2015.minecraftnano.events.NanoPacketEvent.sendMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * @author hasunemiku2015
 * @date 2022-10-23 10:26
 */
object InsertFileFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'r'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            if (args.isNotEmpty()) {
                // Path Specified
                var path = args[0]
                val file: File

                if (path.startsWith("~")) {
                    // Start from server.jar directory
                    path.removePrefix("~")
                    path = ".$path"
                    file = File(path)
                } else {
                    // Start from directory of the editing file
                    path = path.removePrefix("./")
                    file = File(editor.file.parentFile.canonicalPath + "/" + path)
                }

                if (!file.exists() || file.isDirectory) {
                    val line = if (!file.exists()) {
                        FunctionHelper.centerString(editor, ChatColor.DARK_RED + "[ File ${file.name} doesn't exist]")
                    } else {
                        FunctionHelper.centerString(editor, ChatColor.DARK_RED + "[ File ${file.name} is a directory]")
                    }

                    editor.lowBlankLine = line
                    runTaskLater(NanoPlugin.PLUGIN, 5*20) {
                        if (editor.lowBlankLine == line) {
                            editor.lowBlankLine = ""
                        }
                    }
                }

                insertFileToBuffer(file, editor)
            } else {
                val session = InsertFileSession(editor.file.parentFile, 0)
                InsertFileChatEvent.PLAYER_ARGS[editor.player] = session
                NanoChatEvent.PAUSE_EDITING.add(editor.player)
                printDirOutputBuffer(editor, session)
            }
        }
    }

    internal fun printDirOutputBuffer(editor: TextEditor, session: InsertFileSession) {
        var header = session.currentDir.path
        // Replaces part of path with ../ if too long
        while (header.length > editor.preference.maxCharLine) {
            if (!header.contains('/')) {
                header = header.substring(0, editor.preference.maxCharLine)
                break
            }

            if (header.contains("../")) {
                var temp = header.replace("../", "\uD83E\uDE94\uD83D\uDE55\uD83E\uDC1F\uD83D\uDE7F")
                temp = temp.replaceBefore('/', "..")
                header = temp.replace("\uD83E\uDE94\uD83D\uDE55\uD83E\uDC1F\uD83D\uDE7F", "../")
            } else {
                header = header.replaceBefore('/', "..")
            }
        }

        val fileList = session.currentDir.listFiles()
        val outputBufferArr = arrayListOf<String>()
        var offset = 1
        if (session.currentDir != NanoPlugin.PLUGIN.dataFolder.parentFile.parentFile) {
            offset = 2
            val str = "${ChatColor.DARK_GRAY}1. ${ChatColor.RESET} .."
            val suffix = "(parent dir)"
            val dat = str.padEnd(editor.preference.maxCharLine - suffix.length, ' ')
            outputBufferArr.add(dat + suffix)
        }

        if (fileList != null) {
            for ((idx, file) in fileList.withIndex()) {
                val str = "${ChatColor.DARK_GRAY}${idx + offset}. ${ChatColor.RESET} ${file.name}"
                val suffix = if (file.isDirectory) "(dir)" else file.let {
                    if (file.length() >= 1024) {
                        // Express in KB if greater than 1 KB
                        return@let "${file.length() / 1024} KB"
                    } else if (file.length() >= 1024*1024) {
                        // Express in MB if greater than 1 MB
                        return@let "${file.length() / (1024 * 1024)} MB"
                    } else {
                        return@let "${file.length()}  B"
                    }
                }

                val dat = str.padEnd(editor.preference.maxCharLine - suffix.length, ' ')
                outputBufferArr.add(dat + suffix)
            }
        }

        val lowBlankLine = "Enter a line number or command: "
        val helpLine1 = "!!Y Prev Page    !!L Refresh"
        val helpLine2 = "!!N Next Page    !!C Cancel"

        val maxLine = editor.preference.maxLine - 4
        editor.sendMessage(header)
        for (i in maxLine * session.currentPage until maxLine * (session.currentPage + 1)) {
            print("i: $i")
            if (i > outputBufferArr.size - 1) {
                editor.sendMessage("")
            } else {
                editor.sendMessage(outputBufferArr[i])
            }
        }
        editor.sendMessage(lowBlankLine)
        editor.sendMessage(helpLine1)
        editor.sendMessage(helpLine2)
    }

    /**
     * Inserts all lines in a file to the editor session's file data.
     * @param fileInsert The file to insert
     * @param editor The editor to insert into
     */
    internal fun insertFileToBuffer(fileInsert: File, editor: TextEditor) {
        val fileData = arrayListOf<String>()
        with (BufferedReader(FileReader(fileInsert))) {
            fileData.addAll(this.readLines())
        }

        val currentLine = editor.fileData[editor.cursorPosition]
        val curLinePrefix = currentLine.substring(0, editor.cursorCharPosition)
        val curLineSuffix = currentLine.substring(editor.cursorCharPosition)

        if (fileData.size == 1) {
            editor.fileData[editor.cursorPosition] = curLinePrefix + fileData[0] + curLineSuffix
            editor.outputBuffer[editor.cursorPosition] = editor.fileData[editor.cursorPosition]
        } else if (fileData.size > 1) {
            editor.fileData[editor.cursorPosition] = curLinePrefix + fileData[0]
            editor.outputBuffer[editor.cursorPosition] = editor.fileData[editor.cursorPosition]
            for ((idx, str) in fileData.withIndex()) {
                if (idx == 0) {
                    continue
                }
                if (idx == fileData.size - 1) {
                    editor.fileData.insert(editor.cursorPosition + 1, str + curLineSuffix)
                    editor.outputBuffer[editor.cursorPosition] = editor.fileData[editor.cursorPosition]
                    break
                }
                editor.fileData.insert(editor.cursorPosition + 1, str)
                editor.outputBuffer[editor.cursorPosition] = editor.fileData[editor.cursorPosition]
            }
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022-10-23 10:27
 */
object InsertFileChatEvent: Listener {
    internal val PLAYER_ARGS = hashMapOf<Player, InsertFileSession>()

    @org.bukkit.event.EventHandler
    fun onArgEnter(event: AsyncPlayerChatEvent) {
        val player = event.player
        val message = event.message
        val playerArgs = PLAYER_ARGS[player] ?: return
        val editor = NanoCommand.PLAYER_EDITOR[player] ?: return

        event.isCancelled = true

        if (message.startsWith("!!")) {
            if (message.length < 3) {
                return
            }
            when (message[2].lowercaseChar()) {
                'y' -> {
                    playerArgs.currentPage += 1
                    PLAYER_ARGS[player] = playerArgs
                }

                'n' -> {
                    if (playerArgs.currentPage > 1) {
                        playerArgs.currentPage -= 1
                        PLAYER_ARGS[player] = playerArgs
                    }
                }

                'c' -> {
                    onInsertSessionEnd(editor, null)
                    return
                }
            }
            InsertFileFunction.printDirOutputBuffer(editor, playerArgs)
            return
        }

        val fileNum = message.toIntOrNull() ?: return
        if (fileNum < 1) {
            return
        }

        if (fileNum == 1) {
            playerArgs.currentDir = playerArgs.currentDir.parentFile
            PLAYER_ARGS[player] = playerArgs
            InsertFileFunction.printDirOutputBuffer(editor, playerArgs)
            return
        }

        val fileList = playerArgs.currentDir.listFiles() ?: return
        if (fileNum - 2 > fileList.size) {
            return
        }
        val fileInsert = fileList[fileNum - 2]
        if (fileInsert.isDirectory) {
            playerArgs.currentDir = fileInsert
            PLAYER_ARGS[player] = playerArgs
            InsertFileFunction.printDirOutputBuffer(editor, playerArgs)
            return
        } else {
            // Not directory, so directly insert into file
            InsertFileFunction.insertFileToBuffer(fileInsert, editor)
            onInsertSessionEnd(editor, fileInsert.name)
        }
    }

    /**
     * Denotes code to be run when player finish selecting the file to insert data.
     * @param editor TextEditor to end file insert session
     * @param fileName File name of the file inserted, null if operation is cancelled
     */
    private fun onInsertSessionEnd(editor: TextEditor, fileName: String?) {
        PLAYER_ARGS.remove(editor.player)
        NanoChatEvent.PAUSE_EDITING.remove(editor.player)

        if (fileName == null) {
            editor.lowBlankLine = FunctionHelper.centerString(editor, "[ Cancelled ]")
        } else {
            editor.lowBlankLine = FunctionHelper.centerString(editor, "[ Inserted $fileName ]")
        }

        runTaskLater(NanoPlugin.PLUGIN, 5*20) {
            editor.lowBlankLine = ""
        }
        editor.helpLine1 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine1
        editor.helpLine2 = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.helpLine2
    }

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object EventDestructionProcessor: DestructionProcessor {
        override fun onEditorDestroy(editor: TextEditor) {
            PLAYER_ARGS.remove(editor.player)
        }
    }
}

data class InsertFileSession(var currentDir: File, var currentPage: Int)