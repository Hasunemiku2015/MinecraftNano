package com.hasunemiku2015.minecraftnano.files

import com.hasunemiku2015.minecraftnano.NanoPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * @author hasunemiku2015
 * @date 2022/09/15 19:01
 */
abstract class AbstractConfiguration(private val fileName: String) {
    internal val yamlConfiguration = YamlConfiguration()

    fun load() {
        val file = File(NanoPlugin.PLUGIN.dataFolder, fileName)
        if (!file.exists()) {
            NanoPlugin.PLUGIN.saveResource(fileName, false)
        }
        yamlConfiguration.load(file)
    }

    fun reload() {
        yamlConfiguration.load(File(NanoPlugin.PLUGIN.dataFolder, fileName))
    }
}