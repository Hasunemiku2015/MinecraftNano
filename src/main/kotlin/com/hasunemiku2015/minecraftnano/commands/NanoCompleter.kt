package com.hasunemiku2015.minecraftnano.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.io.File

/**
 * TabCompleter for MinecraftNano Command, don't override the method.
 * @author hasunemiku2015
 * @date 2022/10/04 21:28
 */
interface NanoCompleter: TabCompleter {
    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String,
                               p3: Array<out String>): MutableList<String>? {
        val out = mutableListOf<String>()
        if (p0 !is Player) {
            return out
        }
        if (!p0.hasPermission("minecraftnano.use")) {
            return out
        }

        val path = StringBuilder().let {
            for ((idx, dat) in p3.withIndex()) {
                if (idx != p3.size - 1) {
                    it.append("$dat/")
                }
            }
            it.toString()
        }
        val file = File("./${path.removeSuffix("/")}")
        return if (file.exists() && file.isDirectory) {
            val var0 = file.listFiles()?.map { it.name }
            if (p3.last().isEmpty()) {
                var0?.toMutableList()
            } else {
                var0?.filter { it.lowercase().contains(p3.last().lowercase()) }?.toMutableList()
            }
        } else {
            out
        }
    }
}