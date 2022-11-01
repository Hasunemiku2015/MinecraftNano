package com.hasunemiku2015.minecraftnano.commands

import com.deanveloper.kbukkit.chat.plus
import com.deanveloper.kbukkit.util.runTaskLater
import com.hasunemiku2015.minecraftnano.NanoPlugin
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * @author hasunemiku2015
 * @date 2022/10/04 21:35
 */
object PathCommand: CommandExecutor, NanoCompleter {
    data class Operation(val file: File, val mode: OperationMode)
    enum class OperationMode {
        COPY, MOVE
    }

    private val PENDING_PATH = hashMapOf<Player, Operation>()

    fun addOperation(player: Player, operation: Operation) {
        PENDING_PATH[player] = operation
        runTaskLater(NanoPlugin.PLUGIN, 30 * 20) {
            if (PENDING_PATH[player] != null) {
                player.sendMessage("Operation ${PENDING_PATH[player]?.mode} aborted.")
                PENDING_PATH.remove(player)
            }
        }
    }

    fun removeOperation(player: Player) {
        PENDING_PATH.remove(player)
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (p0 !is Player ||PENDING_PATH[p0] == null) {
            return true
        }

        val path = StringBuilder().let {
            p3.forEach { i ->
                it.append("/$i")
            }
            it.toString()
        }
        val file = File("./${path}")
        if (file.exists()) {
            p0.sendMessage(ChatColor.DARK_RED + "The file/folder specified does not exist!")
            p0.sendMessage("Operation ${PENDING_PATH[p0]?.mode} aborted.")
            return true
        }

        val fromFile = PENDING_PATH[p0]?.file
        val mode = PENDING_PATH[p0]?.mode

        if (mode == OperationMode.COPY) {
            fromFile?.copyTo(file)
            p0.sendMessage("Copied file ./${fromFile?.path} to $path.")
        } else {
            fromFile?.toPath()?.let { Files.move(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING) }
            p0.sendMessage("Moved file ./${fromFile?.path} to $path.")
        }
        return true
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/04 21:35
 */
object MoveCommand: CommandExecutor, NanoCompleter, Listener {
    private val SELECTING_OPERATION = hashMapOf<Player, File>()
    private val PENDING_NAME = hashMapOf<Player, File>()

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (p0 !is Player) {
            p0.sendMessage(ChatColor.DARK_RED + "Only Player can use this command!")
            return true
        }
        if (!p0.hasPermission("minecraftnano.use")) {
            p0.sendMessage(ChatColor.DARK_RED + "No Permission!")
            return true
        }

        val path = StringBuilder().let {
            p3.forEach { i ->
                it.append("/$i")
            }
            it.toString()
        }
        val file = File("./${path}")
        if (!file.exists()) {
            p0.sendMessage(ChatColor.DARK_RED + "The file/folder specified does not exist!")
            return true
        }
        if (NanoCommand.PLAYER_EDITOR.containsKey(p0)) {
            p0.sendMessage(ChatColor.DARK_RED + "Cannot move/rename file with a TextEditor opened!")
            return true
        }

        SELECTING_OPERATION[p0] = file
        p0.sendMessage("Select one of the following: (Type number in chat.)")
        p0.sendMessage("1 Rename File   2 Move File   3 Cancel")
        return true
    }

    @EventHandler
    fun onSelection(event: AsyncPlayerChatEvent) {
        if (SELECTING_OPERATION[event.player] == null) {
            return
        }

        if (event.message == "1") {
            val file = SELECTING_OPERATION[event.player] ?: return
            SELECTING_OPERATION.remove(event.player)
            PENDING_NAME[event.player] = file
            event.player.sendMessage("Enter the new name of file:")
            return
        }

        if (event.message == "2") {
            val file = SELECTING_OPERATION[event.player] ?: return
            SELECTING_OPERATION.remove(event.player)
            event.player.sendMessage("Use the /path command in 30 seconds to specify the move destination.")
            PathCommand.addOperation(event.player, PathCommand.Operation(file, PathCommand.OperationMode.MOVE))
            return
        }

        if (event.message == "3") {
            SELECTING_OPERATION.remove(event.player)
            event.player.sendMessage("Operation aborted.")
            return
        }

        event.player.sendMessage("Select one of the following: (Type number in chat.)")
        event.player.sendMessage("1 Rename File   2 Move File   3 Cancel")
    }

    @EventHandler
    fun onRename(event: AsyncPlayerChatEvent) {
        if (PENDING_NAME[event.player] == null) {
            return
        }

        val file = PENDING_NAME[event.player] ?: return
        val newFile = File(file.parentFile, event.message)

        if (newFile.exists()) {
            event.player.sendMessage(ChatColor.DARK_RED + "The specified filed already existed.")
            event.player.sendMessage(ChatColor.DARK_RED + "Operation aborted.")
            return
        }

        val success = file.renameTo(newFile)
        event.player.sendMessage(if (success) "Successfully renamed file to ${event.message}" else
            ChatColor.DARK_RED + "An unknown error occurred.")
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        SELECTING_OPERATION.remove(event.player)
        PENDING_NAME.remove(event.player)
        PathCommand.removeOperation(event.player)
    }
}

/**
 * @author hasunemiku2015
 * @date 2022/10/04 21:35
 */
object CopyCommand: CommandExecutor, NanoCompleter, Listener {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (p0 !is Player) {
            p0.sendMessage(ChatColor.DARK_RED + "Only Player can use this command!")
            return true
        }
        if (!p0.hasPermission("minecraftnano.use")) {
            p0.sendMessage(ChatColor.DARK_RED + "No Permission!")
            return true
        }

        val path = StringBuilder().let {
            p3.forEach { i ->
                it.append("/$i")
            }
            it.toString()
        }
        val file = File("./${path}")
        if (!file.exists()) {
            p0.sendMessage(ChatColor.DARK_RED + "The file/folder specified does not exist!")
            return true
        }

        p0.sendMessage("Use the /path command in 30 seconds to specify the move destination.")
        PathCommand.addOperation(p0 as Player, PathCommand.Operation(file, PathCommand.OperationMode.COPY))
        return true
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        PathCommand.removeOperation(event.player)
    }
}