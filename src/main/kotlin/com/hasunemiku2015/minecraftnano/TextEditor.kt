package com.hasunemiku2015.minecraftnano

import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.api.NanoRegistry
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import com.hasunemiku2015.minecraftnano.datastruct.SparseVector
import org.bukkit.entity.Player
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Represents a TextEditing session, create extension fields and functions should you want to add functionality.
 * @author hasunemiku2015
 * @date 2022/09/26 14:01
 */
class TextEditor(val player: Player, val file: File) {

    class SessionDestroyedException(message: String = "Session is marked destroyed!"): Exception(message)
    private var destroyed = false
    private fun assertAlive() {
        if (destroyed) {
            throw SessionDestroyedException()
        }
    }

    /*
        Minimal required data for TextEditor.
        - fileData:           A SparseVector containing the data that would actually be written to a file
        - outputBuffer:       A SparseVector containing the data that would be shown during display.
        - cursorPosition:     The line index (start from 0) that the cursor is on.
        - cursorCharPosition: The horizontal position (start from 0) of the cursor on a line.
        - currentPage:        The page that the editor should show during display. (deprecated)
     */
    var fileData = SparseVector("")
    var outputBuffer: SparseVector<String>
    var cursorPosition = 0
    var cursorCharPosition = 0
    var currentPage = 0

    /*
        TextEditor session workflow.
        1. Read file
        2. Run preprocessors
        3. Parse chat and run functions if necessary
        4. Run postprocessor (display)
        5. Run destruction-processor when object is destroyed.
     */
    init {
        BufferedReader(FileReader(file)).use {
            it.readLines().forEach { i -> fileData.append(i) }
        }
        outputBuffer = SparseVector(fileData)

        NanoRegistry.b()
        for (i in NanoRegistry.c()) {
            i.onEditorCreate(this)
        }
    }

    /**
     * Called by AsyncPlayerChatEvent to parse the message, runs step 3, 4.
     */
    fun parseChat(message: String, runPostprocessor: Boolean = true) {
        assertAlive()
        outputBuffer = SparseVector(fileData)

        val msgArray = message.split(" ")
        val cmd = if (msgArray[0].length > 2) msgArray[0].substring(2) else null

        if (message.startsWith("!!")) {
            if (cmd?.toIntOrNull() != null) {
                cursorPosition = cmd.toInt() - 1
                cursorCharPosition = 0
            } else {
                if (cmd != null) {
                    for (fn in NanoRegistry.d()) {
                        if (fn.match(cmd.toCharArray()[0].lowercaseChar())) {
                            fn.exec(this, msgArray.drop(1))
                        }
                    }
                }
            }
        } else if (message.startsWith("!^")) {
            if (cmd != null) {
                for (fn in NanoRegistry.e()) {
                    if (fn.match(cmd.toCharArray()[0].lowercaseChar())) {
                        fn.exec(this, msgArray.drop(1))
                    }
                }
            }
        } else {
            // Add text to editor if not command.
            TextAdditionHandler.insertText(this, message)
        }

        if (runPostprocessor) {
            runPostprocessors()
        }
    }

    /**
     * Runs step 4. Call if you want to only display.
     */
    fun runPostprocessors() {
        assertAlive()
        NanoRegistry.f().forEach {
            it.exec(this)
        }
    }

    /**
     * Calls destruction-processors and mark the object destroyed.
     */
    fun onDestroy() {
        if (!destroyed) {
            NanoCommand.PLAYER_EDITOR.remove(player)
            NanoRegistry.g().forEach {
                it.onEditorDestroy(this)
            }
            destroyed = true
        }
    }

    /**
     * Free up memory just in case onDestroy is not called.
     */
    fun finalize() {
        onDestroy()
    }


    data class TextEditorFormatter(val player: Player, val fileName: File, val fileData: SparseVector<String>,
                                   val outputBuffer: SparseVector<String>, val cursorPosition: Int,
                                   val cursorCharPosition: Int, val currentPage: Int, val destroyed: Boolean)

    override fun toString(): String {
        return TextEditorFormatter(this.player, this.file, this.fileData, this.outputBuffer, this.cursorPosition,
            this.cursorCharPosition, this.currentPage, this.destroyed).toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextEditor

        if (player != other.player) return false
        if (file != other.file) return false
        if (destroyed != other.destroyed) return false
        if (fileData != other.fileData) return false
        if (outputBuffer != other.outputBuffer) return false
        if (cursorPosition != other.cursorPosition) return false
        if (cursorCharPosition != other.cursorCharPosition) return false
        if (currentPage != other.currentPage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = player.hashCode()
        result = 31 * result + file.hashCode()
        result = 31 * result + destroyed.hashCode()
        result = 31 * result + fileData.hashCode()
        result = 31 * result + outputBuffer.hashCode()
        result = 31 * result + cursorPosition
        result = 31 * result + cursorCharPosition
        result = 31 * result + currentPage
        return result
    }

    /**
     * Using a handler to allow subscription of events for text editing.
     * @author hasunemiku2015
     * @date 2022/11/03 15:09
     * @see TextEditor
     * @see EventHandler
     */
    object TextAdditionHandler: EventHandler() {
        /**
         * Function for inserting text to TextEditor. Separated to allow event subscription.
         * @param editor TextEditor for string insertion.
         * @param message The string to insert.
         * @see TextEditor
         * @see EventHandler
         */
        fun insertText(editor: TextEditor, message: String) {
            super.d(editor) {
                val lineString = editor.fileData[editor.cursorPosition]
                if (editor.cursorCharPosition > 0) {
                    val s1 = lineString.substring(0, editor.cursorCharPosition) + message
                    editor.fileData[editor.cursorPosition] = s1 + lineString.substring(editor.cursorCharPosition)
                    editor.cursorCharPosition = s1.length
                } else {
                    editor.fileData[editor.cursorPosition] = message + lineString
                    editor.cursorCharPosition = message.length - 1
                }
                editor.outputBuffer[editor.cursorPosition] = editor.fileData[editor.cursorPosition]
            }
        }
    }
}