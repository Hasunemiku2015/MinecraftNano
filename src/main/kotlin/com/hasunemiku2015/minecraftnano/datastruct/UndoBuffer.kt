package com.hasunemiku2015.minecraftnano.datastruct

import org.bukkit.entity.Player
import java.util.Stack

/**
 * @author hasunemiku2015
 * @date 2022/09/23 12:30
 */
class UndoBuffer private constructor () {
    companion object {
        private val PLAYER_BUFFER = hashMapOf<Player, UndoBuffer>()

        fun remove(player: Player) {
            PLAYER_BUFFER.remove(player)
        }

        operator fun get(player: Player): UndoBuffer? {
            return PLAYER_BUFFER[player]
        }
    }

    constructor(player: Player): this() {
        PLAYER_BUFFER[player] = this
    }

    private val undoStack = Stack<Record>()
    private val redoStack = Stack<Record>()

    fun undo(): Record? {
        return if (undoStack.isEmpty()) {
            null
        } else {
            val record = this.undoStack.pop()
            redoStack.push(record)
            record
        }
    }

    fun redo(): Record? {
        return if (redoStack.isEmpty()) {
            null
        } else {
            val record = this.redoStack.pop()
            undoStack.push(record)
            record
        }
    }

    fun pushRecord(record: Record) {
        this.undoStack.push(record)
    }
}

/**
 * Classifiers for creation of record.
 * @author hasunemiku2015
 * @time 2022/09/26 18:40
 */
enum class RecordType {
    /**
     * Represents a record that added a return character '\n' to file buffer.
     */
    ADD_LINE,

    /**
     * Represents a record that removed a return character '\n' from file buffer.
     */
    REMOVE_LINE,

    /**
     * Represents a record that a specific line is changed in file buffer.
     */
    CHANGE_LINE
}

/*
   Implementations of Record.
 */
abstract class Record(recordType: RecordType)
class AddLineRecord(val lineNumber: Int): Record(RecordType.ADD_LINE)
class RemoveLineRecord(val lineNumber: Int): Record(RecordType.REMOVE_LINE)
data class ChangeRecord(val oldString: String, val lineNumber: Int): Record(RecordType.CHANGE_LINE)