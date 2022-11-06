package com.hasunemiku2015.minecraftnano.builtin.function

import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.api.Function
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectFunction.isSelectionMode
import org.bukkit.entity.Player

// ================================================================================================================== //
//                                                  Data Structures                                                   //
// ================================================================================================================== //

/**
 * Class for holding all clipboards for each TextEditor sessions.
 * @author hasunemiku2015
 * @date 2022/11/06 13:08
 */
object ClipboardStore {
    private val PLAYER_CLIPBOARD = hashMapOf<Player, Clipboard>()

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object ClipboardLifecycleProcessor: LifecycleProcessor {
        override fun onEditorCreate(editor: TextEditor) {
            PLAYER_CLIPBOARD[editor.player] = Clipboard.EMPTY_CLIPBOARD
        }

        override fun onEditorDestroy(editor: TextEditor) {
            PLAYER_CLIPBOARD.remove(editor.player)
        }
    }

    var TextEditor.clipBoard: Clipboard
        get() {
            return PLAYER_CLIPBOARD[player] ?: Clipboard.EMPTY_CLIPBOARD
        }
        set(value) {
            PLAYER_CLIPBOARD[player] = value
        }

    /**
     * Helper function for clearing the clipboard of a TextEditor
     */
    internal fun TextEditor.clearClipboard() {
        this.clipBoard = Clipboard.EMPTY_CLIPBOARD
    }

    /**
     * Check if the clipboard of a TextEditor is empty.
     * @return True if the clipboard is empty. False otherwise.
     */
    internal fun TextEditor.isEmptyClipboard(): Boolean {
        return this.clipBoard == Clipboard.EMPTY_CLIPBOARD
    }
}

/**
 * Data class for representing a clipboard for a specific text editor.
 * @author hasunemiku2015
 * @date 2022/11/06 13:04
 */
data class Clipboard(val clipboardArray: ArrayList<String>) {
    companion object {
        val EMPTY_CLIPBOARD = Clipboard(arrayListOf())
    }

    constructor(string: String) : this(arrayListOf()) {
        clipboardArray.addAll(string.split('\n'))
    }
}

// ================================================================================================================== //
//                                                  Functions                                                         //
// ================================================================================================================== //

/**
 * @author hasunemiku2015
 * @date 2022/11/06 13:21
 */
object CutFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'k'
    }

    override fun exec(editor: TextEditor, args: List<String>) {

    }
}

/**
 * Registered in SelectFunction since it is a clipboard manipulation function.
 * @author hasunemiku2015
 * @date 2022/11/06 13:28
 */
object CopyFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == '6'
    }

    override fun exec(editor: TextEditor, args: List<String>) {

    }
}

/**
 * @author hasunemiku2015
 * @date 2022/11/06 13:28
 */
object PasteFunction: Function, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'u'
    }

    override fun exec(editor: TextEditor, args: List<String>) {

    }
}


// ================================================================================================================== //
//                                                   Events                                                           //
// ================================================================================================================== //

/**
 * Subscribes all events regarding with clipboard manipulation. Should be run in NanoPlugin.onEnable.
 * @author hasunemiku2015
 * @date 2022/11/06 13:22
 * @see CutFunction
 */
object ClipboardEventSubscriber {
    /**
     * Function for registering all events. Should be run in NanoPlugin.onEnable().
     * @see NanoPlugin
     */
    fun init() {
        CutFunction.subscribeEvent(CutEvent)
        CopyFunction.subscribeEvent(CopyEvent)
    }
}

/**
 * Changes the cut function if a selection is made.
 * @author hasunemiku2015
 * @date 2022/11/03 15:38
 * @see ClipboardEventSubscriber
 */
@ProcessPriority(ProcessPriorityLevel.NORMAL)
object CutEvent: ProcessEvent {
    override fun onProcessStart(editor: TextEditor): Boolean {
        if (!editor.isSelectionMode()) {
            return true
        }

        return false
    }
}

/**
 * Changes the copy function if a selection is made.
 * @author hasunemiku2015
 * @date 2022/11/03 15:38
 * @see ClipboardEventSubscriber
 */
@ProcessPriority(ProcessPriorityLevel.NORMAL)
object CopyEvent: ProcessEvent {
    override fun onProcessStart(editor: TextEditor): Boolean {
        if (!editor.isSelectionMode()) {
            return true
        }

        return false
    }
}