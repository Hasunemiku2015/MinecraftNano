package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.*
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.altfunction.AnchorStore.addAnchor
import com.hasunemiku2015.minecraftnano.builtin.altfunction.AnchorStore.isAnchored
import com.hasunemiku2015.minecraftnano.builtin.altfunction.AnchorStore.removeAnchor
import org.bukkit.entity.Player

// [ There are no anchors ]
/**
 * @author hasunemiku2015
 * @date 2022/10/06 10:31
 */
object AnchorStore {
    private val EDITOR_ANCHOR = hashMapOf<Player, HashSet<Int>>()

    @ProcessPriority(ProcessPriorityLevel.NORMAL)
    object AnchorProcessor: LifecycleProcessor{
        override fun onEditorCreate(editor: TextEditor) {
            EDITOR_ANCHOR[editor.player] = hashSetOf()
        }

        override fun onEditorDestroy(editor: TextEditor) {
            EDITOR_ANCHOR.remove(editor.player)
        }
    }

    internal fun TextEditor.addAnchor(lineNumber: Int) {
        val set = EDITOR_ANCHOR[player]
        set!!.add(lineNumber)
    }

    internal fun TextEditor.removeAnchor(lineNumber: Int) {
        val set = EDITOR_ANCHOR[player]
        set!!.remove(lineNumber)
    }

    fun TextEditor.isAnchored(lineNumber: Int): Boolean {
        val set = EDITOR_ANCHOR[player]
        return set!!.contains(lineNumber)
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/06 10:36
 */
object AnchorModifyFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'i'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            var anchorPos = editor.cursorPosition
            if (args.isNotEmpty()) {
                if (args[0].toIntOrNull() != null) {
                    anchorPos = args[0].toInt()
                }
            }

            if (editor.isAnchored(anchorPos)) {
                editor.removeAnchor(anchorPos)
            } else {
                editor.addAnchor(anchorPos)
            }
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/06 10:36
 */
object AnchorForwardFunction: AltFunction, EventHandler()  {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'v'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            if (editor.cursorPosition == editor.fileData.size()) {
                return@repeatTimes
            }

            for (i in editor.cursorPosition + 1 until editor.fileData.size()) {
                if (editor.isAnchored(i)) {
                    editor.cursorPosition = i
                    break
                }
            }
        }
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/06 10:36
 */
object AnchorBackwardFunction: AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == 'y'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        FunctionHelper.repeatTimes(editor, args, this) {
            if (editor.cursorPosition == 0) {
                return@repeatTimes
            }

            for (i in editor.cursorPosition - 1 downTo 0) {
                if (editor.isAnchored(i)) {
                    editor.cursorPosition = i
                    break
                }
            }
        }
    }
}