package com.hasunemiku2015.minecraftnano.commands

import com.deanveloper.kbukkit.chat.plus
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

/**
 * @author hasunemiku2015
 * @date 2022/10/04 21:28
 */
object RmCommand: CommandExecutor, NanoCompleter, Listener {
    private val PENDING_CONFIRM = hashMapOf<Player, File>()

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
            p0.sendMessage(ChatColor.DARK_RED + "Cannot delete file with a TextEditor opened!")
            return true
        }

        p0.sendMessage("About to delete .${path}.")
        p0.sendMessage("Are you sure [y/N]:")
        PENDING_CONFIRM[p0] = file
        return true
    }

    @EventHandler
    fun onConfirm(event: AsyncPlayerChatEvent) {
        if (PENDING_CONFIRM[event.player] == null) {
            return
        }
        event.isCancelled = true

        if (event.message.equals("y", true)) {
            val file = PENDING_CONFIRM[event.player]
            file?.delete()
            event.player.sendMessage("Deleted file ${file?.path}.")
            PENDING_CONFIRM.remove(event.player)
        } else if (event.message.equals("n", true)) {
            PENDING_CONFIRM.remove(event.player)
            event.player.sendMessage("Operation aborted.")
        } else {
            event.player.sendMessage("Are you sure [y/N]:")
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        PENDING_CONFIRM.remove(event.player)
    }
}