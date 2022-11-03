package com.hasunemiku2015.minecraftnano

import com.comphenix.protocol.ProtocolLibrary
import com.deanveloper.kbukkit.util.Commands
import com.deanveloper.kbukkit.util.registerEvents
import com.hasunemiku2015.minecraftnano.builtin.BuiltinRegistry
import com.hasunemiku2015.minecraftnano.builtin.altfunction.SelectionEventSubscriber
import com.hasunemiku2015.minecraftnano.builtin.function.*
import com.hasunemiku2015.minecraftnano.commands.*
import com.hasunemiku2015.minecraftnano.events.NanoChatEvent
import com.hasunemiku2015.minecraftnano.events.NanoPacketEvent
import com.hasunemiku2015.minecraftnano.files.CommentCharConfiguration
import com.hasunemiku2015.minecraftnano.files.TabCharConfiguration
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main Class.
 * @author hasunemiku2015
 * @date 2022/09/11 17:32
 */
class NanoPlugin: JavaPlugin() {
    companion object {
        lateinit var PLUGIN: NanoPlugin
    }

    override fun onEnable() {
        PLUGIN = this
        BuiltinRegistry.registerAll()

        Commands["nanopref"]!!.setExecutor(NanoPrefCommand)
        Commands["nano"]!!.setExecutor(NanoCommand)
        Commands["rm"]!!.setExecutor(RmCommand)
        Commands["mkdir"]!!.setExecutor(MakeCommand.MakeDir)
        Commands["mkfile"]!!.setExecutor(MakeCommand.MakeFile)
        Commands["mv"]!!.setExecutor(MoveCommand)
        Commands["cp"]!!.setExecutor(CopyCommand)
        Commands["path"]!!.setExecutor(PathCommand)

        if(Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(NanoPacketEvent)
        }

        // Bukkit Events
        CopyCommand.registerEvents(this)
        MoveCommand.registerEvents(this)
        NanoCommand.registerEvents(this)
        NanoChatEvent.registerEvents(this)
        RmCommand.registerEvents(this)
        ExitChatEvent.registerEvents(this)
        GotoLineChatEvent.registerEvents(this)
        InsertFileChatEvent.registerEvents(this)
        LineNavigationChatEvent.registerEvents(this)
        ReplaceWithSessionChatEvent.registerEvents(this)
        ReplaceSessionChatEvent.registerEvents(this)
        SearchChatEvent.registerEvents(this)

        // MinecraftNano Events
        SelectionEventSubscriber.init()

        CommentCharConfiguration.load()
        TabCharConfiguration.load()
    }

    override fun onDisable() {
        NanoPrefCommand.saveToFile()
    }
}