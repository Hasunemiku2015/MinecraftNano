package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectFunction.isSelectionMode
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectionRange.Companion.DEFAULT_SELECTION_RANGE
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectionRange.Companion.selectionRange
import org.bukkit.entity.Player


/**
 * Allow players to enable/disable selection.
 */
object SelectFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'a'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        editor.toggleSelection()
    }

    /**
     * Toggles selection for a specified TextEditor. No param is used for stop editing.
     * @param linePos Line position of the cursor to start editing.
     * @param charPos Char position of the cursor to start editing.
     */
    private fun TextEditor.toggleSelection(linePos: Int = -1, charPos: Int = -1) {
        if (this.isSelectionMode()) {
            this.selectionRange = DEFAULT_SELECTION_RANGE
            return
        } else {
            this.selectionRange = SelectionRange(linePos, charPos, linePos, charPos)
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
        TextEditor.TextAdditionHandler.subscribeEvent(SelectionCursorNavigationEvent)
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/11/03 15:38
 * @see SelectFunction
 */
object SelectionCursorNavigationEvent: ProcessEvent {
    override fun onProcessEnd(editor: TextEditor) {
        if (!editor.isSelectionMode()) {
            return
        }

        val range = editor.selectionRange
        editor.selectionRange = SelectionRange(range.startLinePos, range.startCharPos,
            editor.cursorPosition, editor.cursorCharPosition)
    }
}
