package com.hasunemiku2015.minecraftnano.commands

import com.deanveloper.kbukkit.chat.plus
import com.google.gson.GsonBuilder
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.*

/**
 * @author hasunemiku2015
 * @date 2022/09/11 17:32
 */
object NanoPrefCommand : CommandExecutor {
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private lateinit var PLAYER_PREFERENCE: HashMap<Player, Preference>

    val TextEditor.preference: Preference
        get() {
            return getPreference(player)
        }

    init {
        if (!NanoPlugin.PLUGIN.dataFolder.exists()) {
            NanoPlugin.PLUGIN.dataFolder.mkdir()
        }

        val prefFile = File(NanoPlugin.PLUGIN.dataFolder.path + "/preference.json")
        if (!prefFile.exists()) {
            prefFile.createNewFile()
            BufferedWriter(FileWriter(prefFile)).use {
                it.write(GSON.toJson(PLAYER_PREFERENCE))
            }
        }

        PLAYER_PREFERENCE = try {
            @Suppress("UNCHECKED_CAST")
            GSON.fromJson(BufferedReader(FileReader(prefFile)), HashMap::class.java) as HashMap<Player, Preference>
        } catch (ex: Exception) {
            hashMapOf()
        }
    }

    private fun setMaxLine(player: Player, line: Int, charLine: Int): Boolean {
        if (line < 6 || charLine < 10) {
            return false
        }

        PLAYER_PREFERENCE[player] = Preference(line, charLine)
        return true
    }

    fun getPreference(player: Player): Preference {
        return PLAYER_PREFERENCE[player] ?: Preference(20, 80)
    }

    fun saveToFile() {
        val prefFile = File(NanoPlugin.PLUGIN.dataFolder.path + "/preference.json")
        BufferedWriter(FileWriter(prefFile)).use {
            it.write(GSON.toJson(PLAYER_PREFERENCE))
        }
    }

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (p0 !is Player) {
            p0.sendMessage(ChatColor.DARK_RED + "Only Player can use this command!")
            return true
        }
        if (!p0.hasPermission("minecraftnano.use")) {
            p0.sendMessage(ChatColor.DARK_RED + "No Permission!")
            return true
        }
        if (p3.size < 2) {
            p0.sendMessage(ChatColor.DARK_RED + "Insufficient argument!")
            return true
        }

        val maxLength = p3[0].toIntOrNull() ?: 0
        val maxCharLine = p3[1].toIntOrNull() ?: 0
        val isSuccess = setMaxLine(p0, maxLength, maxCharLine)

        if (isSuccess) {
            p0.sendMessage(ChatColor.GREEN + "Successfully set max. number of line in chat to $maxLength.")
            p0.sendMessage(ChatColor.GREEN + "Successfully set max. number character per line in chat to $maxCharLine.")
        } else {
            p0.sendMessage(ChatColor.DARK_RED + "Invalid argument, max_line_chat must be integer greater than 6.")
            p0.sendMessage(ChatColor.DARK_RED + "Invalid argument, max_char_line must be integer greater than 10.")
        }

        return true
    }

    data class Preference(val maxLine: Int, val maxCharLine: Int)
}