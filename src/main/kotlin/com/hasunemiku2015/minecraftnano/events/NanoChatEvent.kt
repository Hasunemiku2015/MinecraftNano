package com.hasunemiku2015.minecraftnano.events

import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @author hasunemiku2015
 * @date 2022/09/11 17:32
 */
object NanoChatEvent: Listener {
    var PAUSE_EDITING = arrayListOf<Player>()

    @EventHandler
    fun onNanoUse(event: AsyncPlayerChatEvent) {
        event.format

        if (!NanoCommand.PLAYER_EDITOR.containsKey(event.player) || PAUSE_EDITING.contains(event.player)) {
            return
        }
        event.isCancelled = true

        val editor = NanoCommand.PLAYER_EDITOR[event.player]

        // Destruction code for removing references from hashmaps.
        try {
            editor!!.parseChat(event.message)
        } catch (ex: Exception) {
            NanoCommand.PLAYER_EDITOR.remove(event.player)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PAUSE_EDITING.remove(event.player)
    }
}