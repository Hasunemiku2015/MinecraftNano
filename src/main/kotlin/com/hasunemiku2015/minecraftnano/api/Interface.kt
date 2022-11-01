package com.hasunemiku2015.minecraftnano.api

import com.hasunemiku2015.minecraftnano.TextEditor


/**
 * Implements codes that should run when a TextEditor session is created.
 * @author hasunemiku2015
 * @date 2022/09/25 22:04
 */
interface Preprocessor {
    fun onEditorCreate(editor: TextEditor)
}

/**
 * Defines a command that starts with '!!'.
 * @author hasunemiku2015
 * @date 2022/09/25 22:04
 */
interface Function {
    /**
     * Check if the command entered is the command specified.
     * @param lowerCaseChar Character entered in lower case.
     * @return Return true if the character is the one specified.
     */
    fun match(lowerCaseChar: Char): Boolean

    /**
     * Specifies the execution of the command.
     * @param editor TextEditor Object
     * @param args Additional arguments given, separated by space.
     */
    fun exec(editor: TextEditor, args: List<String>)
}

/**
 * Defines a command that starts with '!^'.
 * @author hasunemiku2015
 * @date 2022/09/25 22:04
 */
interface AltFunction {
    /**
     * Check if the command entered is the command specified.
     * @param lowerCaseChar Character entered in lower case.
     * @return Return true if the character is the one specified.
     */
    fun match(lowerCaseChar: Char): Boolean

    /**
     * Specifies the execution of the command.
     * @param editor TextEditor Object
     * @param args Additional arguments given, separated by space.
     */
    fun exec(editor: TextEditor, args: List<String>)
}

/**
 * Implements codes that runs after all services are ran.
 * @author hasunemiku2015
 * @date 2022/09/25 22:04
 */
interface Postprocessor {
    fun exec(editor: TextEditor)
}

/**
 * Implements codes that should run when a TextEditor session is destroyed.
 * Something like removing values from HashMap.
 * @author hasunemiku2015
 * @date 2022/09/25 22:04
 */
interface DestructionProcessor {
    fun onEditorDestroy(editor: TextEditor)
}

/**
 * Convenient interface for implementing Preprocessor and DestructionProcessor.
 * @see Preprocessor
 * @see DestructionProcessor
 * @author hasunemiku2015
 * @date 2022/09/25 23:28
 */
interface LifecycleProcessor: Preprocessor, DestructionProcessor