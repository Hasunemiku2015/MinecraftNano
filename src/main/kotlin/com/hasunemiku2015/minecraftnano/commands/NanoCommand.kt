package com.hasunemiku2015.minecraftnano.commands

import com.deanveloper.kbukkit.chat.plus
import com.hasunemiku2015.minecraftnano.TextEditor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File

/**
 * @author hasunemiku2015
 * @date 2022/09/11 17:32
 */
object NanoCommand: CommandExecutor, NanoCompleter, Listener {
    val PLAYER_EDITOR = hashMapOf<Player, TextEditor>()

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
            p0.sendMessage(ChatColor.DARK_RED + "The file specified does not exist!")
            return true
        }
        if (file.isDirectory) {
            p0.sendMessage(ChatColor.DARK_RED + "The file specified is a directory!")
            return true
        }

        if (!PLAYER_EDITOR.containsKey(p0)) {
            val editor = TextEditor(p0, file)
            PLAYER_EDITOR[p0] = editor
            editor.runPostprocessors()
        } else {
            p0.sendMessage(ChatColor.DARK_RED + "You already have an editor opened.")
            return true
        }

        return true
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PLAYER_EDITOR[event.player]?.onDestroy()
        PLAYER_EDITOR.remove(event.player)
    }
}