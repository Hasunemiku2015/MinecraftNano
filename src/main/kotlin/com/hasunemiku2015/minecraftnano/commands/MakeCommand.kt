package com.hasunemiku2015.minecraftnano.commands

import com.deanveloper.kbukkit.chat.plus
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

/**
 * @author hasunemiku2015
 * @date 2022/10/04 21:28
 */
object MakeCommand {
    object MakeFile: CommandExecutor, NanoCompleter {
        override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
            return onCommand(p0, p3, false)
        }
    }

    object MakeDir: CommandExecutor, NanoCompleter {
        override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
            return onCommand(p0, p3, true)
        }
    }

    private fun onCommand(p0: CommandSender, p3: Array<out String>, isDir: Boolean): Boolean {
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

        if (file.exists()) {
            p0.sendMessage(ChatColor.DARK_RED + "The file already existed!")
        } else {
            if (isDir) {
                val success = file.mkdirs()
                p0.sendMessage(if (success) "Successfully created directory .${path}."
                else ChatColor.DARK_RED + "An unknown error occurred.")
            } else {
                val success = file.createNewFile()
                p0.sendMessage(if (success) "Successfully created file .${path}."
                else ChatColor.DARK_RED + "An unknown error occurred.")
            }
        }
        return true
    }
}
