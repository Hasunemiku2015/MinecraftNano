package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.deanveloper.kbukkit.chat.plus
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectFunction.isSelectionMode
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectionRange.Companion.DEFAULT_SELECTION_RANGE
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectionRange.Companion.selectionRange
import com.hasunemiku2015.minecraftnano.builtin.function.*
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.rightHeader
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.showCursor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min


/**
 * Allow players to enable/disable selection.
 */
object SelectFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'a'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        val toggle = editor.toggleSelection()
        if (toggle) {
            editor.lowBlankLine = "[ Mark Set ]"
            editor.rightHeader = "SELECT"
            editor.runPostprocessors()
            editor.lowBlankLine = ""
        } else {
            editor.lowBlankLine = "[ Mark Unset ]"
            editor.rightHeader = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.rightHeader
            editor.runPostprocessors()
            editor.lowBlankLine = ""
        }
    }

    /**
     * Toggles selection for a specified TextEditor. No param is used for stop editing.
     * @param linePos Line position of the cursor to start editing.
     * @param charPos Char position of the cursor to start editing.
     * @return True if selection mode is toggled, false otherwise.
     */
    private fun TextEditor.toggleSelection(linePos: Int = -1, charPos: Int = -1): Boolean {
        return if (this.isSelectionMode()) {
            this.selectionRange = DEFAULT_SELECTION_RANGE
            false
        } else {
            this.selectionRange = SelectionRange(linePos, charPos, linePos, charPos)
            true
        }
    }

    /**
     * Check if a specified TextEditor session is in selection mode.
     * @return True if the TextEditor is in selection mode, false otherwise.
     */
    internal fun TextEditor.isSelectionMode(): Boolean {
        return this.selectionRange != DEFAULT_SELECTION_RANGE
    }
}

/**
 * Data class for storing the selection range of a particular TextEditor session.
 * @param startLinePos Line Position (starting from 0) where the selection starts.
 * @param startCharPos Character Position (starting from 0) where the selection starts.
 * @param endLinePos   Line Position (starting from 0) where the selection ends.
 * @param endCharPos   Character Position (starting from 0) where the selection ends.
 * @see SelectFunction
 * @author hasunemiku2015
 * @date 2022/11/03 15:19
 */
data class SelectionRange(val startLinePos: Int, val startCharPos: Int, val endLinePos: Int, val endCharPos: Int) {
    companion object {
        private val EDITOR_SELECTION = hashMapOf<Player, SelectionRange>()
        val DEFAULT_SELECTION_RANGE = SelectionRange(-1, -1, -1 ,-1)

        /**
         * Extension field for modifying the SelectionRange of a TextEditor, bound to the underlying player.
         */
        var TextEditor.selectionRange: SelectionRange
            get() {
                return EDITOR_SELECTION[player] ?: DEFAULT_SELECTION_RANGE
            }
            internal set(value) {
                EDITOR_SELECTION[player] = value
            }
    }

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object SelectionLifecycleProcessor: LifecycleProcessor {
        override fun onEditorCreate(editor: TextEditor) {
            EDITOR_SELECTION[editor.player] = DEFAULT_SELECTION_RANGE
        }

        override fun onEditorDestroy(editor: TextEditor) {
            EDITOR_SELECTION.remove(editor.player)
        }
    }
}

// ================================================================================================================== //
//                                                       EVENTS                                                       //
// ================================================================================================================== //

/**
 * Event subscriber for events regarding with SelectionFunction.
 * @author hasunemiku2015
 * @date 2022/11/03 15:38
 * @see SelectFunction
 */
object SelectionEventSubscriber {
    /**
     * Function for registering all events. Should be run in NanoPlugin.onEnable().
     *  @see NanoPlugin
     */
    fun init() {
        // SelectionCursorNavigationEvent
        TextEditor.TextAdditionHandler.subscribeEvent(SelectionCursorNavigationEvent)

        BackspaceFunction.subscribeEvent(SelectionCursorNavigationEvent)
        BackwardFunction.subscribeEvent(SelectionCursorNavigationEvent)
        BackwardWordFunction.subscribeEvent(SelectionCursorNavigationEvent)
        ForwardFunction.subscribeEvent(SelectionCursorNavigationEvent)
        ForwardWordFunction.subscribeEvent(SelectionCursorNavigationEvent)
        TabFunction.subscribeEvent(SelectionCursorNavigationEvent)
        SpaceFunction.subscribeEvent(SelectionCursorNavigationEvent)
        InsertFileFunction.subscribeEvent(SelectionCursorNavigationEvent)
        LineBeginFunction.subscribeEvent(SelectionCursorNavigationEvent)
        LineEndFunction.subscribeEvent(SelectionCursorNavigationEvent)
        PrevLineFunction.subscribeEvent(SelectionCursorNavigationEvent)
        NextLineFunction.subscribeEvent(SelectionCursorNavigationEvent)
        NewLineFunction.subscribeEvent(SelectionCursorNavigationEvent)
        NextPageFunction.subscribeEvent(SelectionCursorNavigationEvent)
        PrevPageFunction.subscribeEvent(SelectionCursorNavigationEvent)

        AnchorForwardFunction.subscribeEvent(SelectionCursorNavigationEvent)
        AnchorBackwardFunction.subscribeEvent(SelectionCursorNavigationEvent)
        BackwardWordFunction.subscribeEvent(SelectionCursorNavigationEvent)
        FirstLineFunction.subscribeEvent(SelectionCursorNavigationEvent)
        GoMatchBracketFunction.subscribeEvent(SelectionCursorNavigationEvent)
        IndentFunction.subscribeEvent(SelectionCursorNavigationEvent)
        LastLineFunction.subscribeEvent(SelectionCursorNavigationEvent)
        LastLineFunction.subscribeEvent(SelectionCursorNavigationEvent)
        PreviousTextBlockFunction.subscribeEvent(SelectionCursorNavigationEvent)
        NextTextBlockFunction.subscribeEvent(SelectionCursorNavigationEvent)

        // SelectionTerminateEvent
        GotoLineFunction.subscribeEvent(SelectionTerminateEvent)
        ReplaceFunction.subscribeEvent(SelectionTerminateEvent)
        SearchFunction.subscribeEvent(SelectionTerminateEvent)
        SaveFunction.subscribeEvent(SelectionTerminateEvent)

        NextOccurrenceForwardFunction.subscribeEvent(SelectionTerminateEvent)
        NextOccurrenceBackwardFunction.subscribeEvent(SelectionTerminateEvent)

        // Function Specific Events

    }
}

/**
 * Changes the selection range position and its size.
 * @author hasunemiku2015
 * @date 2022/11/03 15:38
 * @see SelectionEventSubscriber
 */
object SelectionCursorNavigationEvent: ProcessEvent {
    override fun onProcessEnd(editor: TextEditor) {
        if (!editor.isSelectionMode()) {
            return
        }
        editor.showCursor = false

        val range = editor.selectionRange
        editor.selectionRange = SelectionRange(
            min(range.startLinePos, editor.cursorPosition),
            min(range.startCharPos, editor.cursorCharPosition),
            max(range.startLinePos, editor.cursorPosition),
            max(range.startCharPos, editor.cursorCharPosition)
        )

        val updatedRange = editor.selectionRange
        if (updatedRange.startLinePos == updatedRange.endLinePos) {
            val str = editor.outputBuffer[updatedRange.startLinePos].ifEmpty { " " }
            editor.outputBuffer[updatedRange.startLinePos] = str.substring(0, updatedRange.startCharPos) +
                    ChatColor.LIGHT_PURPLE + str.substring(updatedRange.startCharPos, updatedRange.endCharPos) +
                    ChatColor.UNDERLINE + str.substring(updatedRange.endCharPos, updatedRange.endCharPos + 1) +
                    ChatColor.RESET + str.substring(updatedRange.endCharPos + 1)
        } else {
            for ((idx, dat) in editor.outputBuffer.asList().withIndex()) {
                if (idx == editor.selectionRange.startLinePos) {
                    val str = editor.outputBuffer[updatedRange.startLinePos].ifEmpty { " " }
                    editor.outputBuffer[updatedRange.startLinePos] = str.substring(0, updatedRange.startCharPos) +
                            ChatColor.LIGHT_PURPLE + str.substring(updatedRange.startCharPos)
                }

                if (idx > editor.selectionRange.startLinePos && idx < editor.selectionRange.endLinePos) {
                    editor.outputBuffer[idx] = ChatColor.LIGHT_PURPLE + editor.outputBuffer[idx]
                }

                if (idx == editor.selectionRange.endLinePos) {
                    val str = editor.outputBuffer[updatedRange.endLinePos].ifEmpty { " " }
                    editor.outputBuffer[updatedRange.startLinePos] = ChatColor.LIGHT_PURPLE +
                            str.substring(0, updatedRange.endCharPos) + ChatColor.UNDERLINE +
                            str.substring(updatedRange.endCharPos, updatedRange.endCharPos + 1) +
                            ChatColor.RESET + str.substring(updatedRange.endCharPos + 1)
                }
            }
        }
    }
}

///**
// * Changes the selection range position only. Reset its size to 1.
// * @author hasunemiku2015
// * @date 2022/11/03 16:13
// * @see SelectionEventSubscriber
// */
//object SelectionCursorJumpEvent: ProcessEvent {
//    override fun onProcessEnd(editor: TextEditor) {
//        if (!editor.isSelectionMode()) {
//            return
//        }
//        val range = editor.selectionRange
//        editor.selectionRange = SelectionRange(editor.cursorPosition, editor.cursorCharPosition,
//            editor.cursorPosition, editor.cursorCharPosition)
//    }
//}

/**
 * Terminates the selection when process starts.
 * @author hasunemiku2015
 * @date 2022/11/03 16:19
 * @see SelectionEventSubscriber
 */
object SelectionTerminateEvent: ProcessEvent {
    override fun onProcessStart(editor: TextEditor): Boolean {
        if (!editor.isSelectionMode()) {
            return false
        }
        editor.selectionRange = DEFAULT_SELECTION_RANGE
        editor.rightHeader = DisplayPostProcessor.DisplayConfiguration.DEFAULT_CONFIG.rightHeader
        return false
    }
}