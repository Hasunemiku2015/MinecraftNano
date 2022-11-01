package com.hasunemiku2015.minecraftnano.api

import com.hasunemiku2015.minecraftnano.TextEditor
import kotlin.reflect.full.hasAnnotation

/**
 * @author hasunemiku2015
 * @date 2022/09/26 13:49
 */
abstract class EventHandler {
    private val handlers = arrayListOf<ProcessEvent>()
    private var a = false

    private fun b(editor: TextEditor) {
        for (i in handlers) {
            val cancel = i.onProcessStart(editor)
            if (cancel) {
               a = true
            }
        }
    }

    private fun c(editor: TextEditor) {
        handlers.forEach {
            it.onProcessEnd(editor)
        }
    }

    fun d(editor: TextEditor, var0: (TextEditor) -> Unit) {
        b(editor)
        if (a) {
            return
        }
        var0(editor)
        c(editor)
    }

    /**
     * Subscribes an event to an EventHandler
     * @param event ProcessEvent to subscribe, annotated with ProcessPriority
     */
    fun subscribeEvent(event: ProcessEvent) {
        if (event::class.hasAnnotation<ProcessPriority>()) {
            handlers.add(event)
            handlers.sortWith(NanoRegistry::a)
        }
    }
}

/**
 * Interface for a process event, override methods to implement.
 * @author hasunemiku2015
 * @date 2022/09/26 13:31
 */
interface ProcessEvent {
    /**
     * This function is called when a process is started.
     * @param editor TextEditor object inputted to the process.
     * @return Return true if the process should be cancelled.
     */
    fun onProcessStart(editor: TextEditor): Boolean {
        return false
    }

    /**
     * This function is called when a process is ended.
     * @param editor TextEditor object outputted from the process.
     */
    fun onProcessEnd(editor: TextEditor) {}
}