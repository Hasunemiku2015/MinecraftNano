package com.hasunemiku2015.minecraftnano.builtin.processor

import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.LifecycleProcessor
import com.hasunemiku2015.minecraftnano.api.ProcessPriority
import com.hasunemiku2015.minecraftnano.api.ProcessPriorityLevel
import com.hasunemiku2015.minecraftnano.datastruct.UndoBuffer

@ProcessPriority(priority = ProcessPriorityLevel.NORMAL)
object UndoProcessor : LifecycleProcessor {
    override fun onEditorCreate(editor: TextEditor) {
        UndoBuffer(editor.player)
    }

    override fun onEditorDestroy(editor: TextEditor) {
        UndoBuffer.remove(editor.player)
    }
}