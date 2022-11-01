package com.hasunemiku2015.minecraftnano.builtin.altfunction

import com.deanveloper.kbukkit.chat.plus
import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.api.AltFunction
import com.hasunemiku2015.minecraftnano.api.EventHandler
import com.hasunemiku2015.minecraftnano.builtin.FunctionHelper
import com.hasunemiku2015.minecraftnano.builtin.processor.DisplayPostProcessor.lowBlankLine
import org.bukkit.ChatColor

object GoMatchBracketFunction : AltFunction, EventHandler() {
    override fun match(lowerCaseChar: Char): Boolean {
        return lowerCaseChar == ']'
    }

    override fun exec(editor: TextEditor, args: List<String>) {
        super.d(editor) {
            if (it.cursorCharPosition >= it.fileData[it.cursorPosition].length) {
                return@d
            }

            val bracket = it.fileData[it.cursorPosition][it.cursorCharPosition]
            var count = 0
            var cursorCharPos = it.cursorCharPosition

            when (bracket) {
                '(', '[', '{', '<' -> {
                    val currentLine = it.fileData[it.cursorPosition].substring(it.cursorCharPosition + 1)
                    val closingBracket = when (bracket) {
                        '[' -> ']'
                        '{' -> '}'
                        '<' -> '>'
                        else -> ')'
                    }

                    for (char in currentLine.toCharArray()) {
                        if (char == bracket) {
                            count++
                        }
                        if (char == closingBracket) {
                            count--
                            if (count == 0) {
                                it.cursorCharPosition = cursorCharPos
                                return@d
                            }
                        }
                        cursorCharPos++
                    }

                    for (i in it.cursorPosition until it.fileData.size()) {
                        val charArr = it.fileData[i].toCharArray()
                        cursorCharPos = 0

                        for (char in charArr) {
                            if (char == bracket) {
                                count++
                            }
                            if (char == closingBracket) {
                                count--
                                if (count <= 0) {
                                    it.cursorPosition = i
                                    it.cursorCharPosition = cursorCharPos
                                    return@d
                                }
                            }
                            cursorCharPos++
                        }
                    }

                    editor.lowBlankLine = ChatColor.DARK_RED + FunctionHelper.centerString(editor, "[ No matching bracket ]")
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }
                }
                ')', ']', '}', '>' -> {
                    val currentLine = it.fileData[it.cursorPosition].substring(0, it.cursorCharPosition)
                    val openingBracket = when (bracket) {
                        ']' -> '['
                        '}' -> '{'
                        '>' -> '<'
                        else -> '('
                    }

                    for (char in currentLine.toCharArray().reversed()) {
                        if (char == bracket) {
                            count++
                        }
                        if (char == openingBracket) {
                            count--
                            if (count == 0) {
                                it.cursorCharPosition = cursorCharPos
                                return@d
                            }
                        }
                        cursorCharPos--
                    }

                    for (i in it.cursorPosition downTo 0) {
                        val charArr = it.fileData[i].toCharArray()
                        cursorCharPos = it.fileData[i].toCharArray().lastIndex

                        for (char in charArr.reversed()) {
                            if (char == bracket) {
                                count++
                            }
                            if (char == openingBracket) {
                                count--
                                if (count <= 0) {
                                    it.cursorPosition = i
                                    it.cursorCharPosition = cursorCharPos
                                    return@d
                                }
                            }
                            cursorCharPos--
                        }
                    }

                    editor.lowBlankLine = ChatColor.DARK_RED + FunctionHelper.centerString(editor, "[ No matching bracket ]")
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }
                }
                else -> {
                    editor.lowBlankLine = ChatColor.DARK_RED + FunctionHelper.centerString(editor, "[ Not a bracket ]")
                    runTaskLater(NanoPlugin.PLUGIN, 5 * 20) {
                        editor.lowBlankLine = ""
                    }
                }
            }
        }
    }
}