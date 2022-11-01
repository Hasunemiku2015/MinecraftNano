package com.hasunemiku2015.minecraftnano.events

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.deanveloper.kbukkit.util.runTaskLater
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hasunemiku2015.minecraftnano.NanoPlugin
import com.hasunemiku2015.minecraftnano.TextEditor
import com.hasunemiku2015.minecraftnano.commands.NanoCommand
import org.apache.commons.lang.RandomStringUtils
import org.bukkit.Bukkit
import java.util.*

/**
 * ProtocolLib Event for blocking normal chat messages from sending to player.
 * @author hasunemiku2015
 * @date 2022/10/06 11:20
 */
object NanoPacketEvent: PacketAdapter(NanoPlugin.PLUGIN, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
    private val MESSAGE_PREFIX = RandomStringUtils.random(8)
    private val MESSAGE_MAP = hashMapOf<String, String>()

    override fun onPacketSending(event: PacketEvent) {
        if (NanoCommand.PLAYER_EDITOR[event.player] == null) {
            return
        }

        val packet = event.packet
        val jsonString = packet.chatComponents.read(0).json
        val jsonObject: JsonObject? = try {
            Gson().fromJson(jsonString, JsonObject::class.java)
        } catch (ex: Exception) {
            null
        }

        val firstMessage: String? = jsonObject?.get("extra")?.let {
            if (it is JsonArray) {
                val temp = it[0]
                if (temp is JsonObject) {
                    return@let temp["text"].toString()
                }
            }
            null
        }

        if (firstMessage?.contains(MESSAGE_PREFIX) == true) {
            val messageID = firstMessage.replace(MESSAGE_PREFIX, "").substring(1, 9)
            packet.chatComponents.write(0, WrappedChatComponent.fromChatMessage(MESSAGE_MAP[messageID])[0])
            MESSAGE_MAP.remove(messageID)
            return
        }
        event.isCancelled = true
    }

    fun TextEditor.sendMessage(message: String) {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            val messageID = UUID.randomUUID().toString().split('-')[0]
            MESSAGE_MAP[messageID] = message

            // Time out code to prevent memory leak (Just in case).
            runTaskLater(plugin, 5 * 20) {
                MESSAGE_MAP.remove(messageID)
            }

            player.sendMessage(MESSAGE_PREFIX + messageID + message)
        } else {
            player.sendMessage(message)
        }
    }
}