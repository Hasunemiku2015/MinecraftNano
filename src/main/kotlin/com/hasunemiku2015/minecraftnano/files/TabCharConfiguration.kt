package com.hasunemiku2015.minecraftnano.files

/**
 * @author hasunemiku2015
 * @date 2022/09/15 18:45
 */
object TabCharConfiguration: AbstractConfiguration("tab.yml") {
    private const val DEFAULT_TAB_CHAR = "\t"

    /**
     * Gets the tab character used in a file, default is '\t'.
     * @param fileExtension: Extension of a file, including the 'dot'. (example: .txt)
     * @return A string that represents the Tab Character. (Tab or X-Spaces)
     */
    fun getTabChar(fileExtension: String): String {
        val numOfSpace = yamlConfiguration.getInt(fileExtension.removePrefix("."), -1)
        return if (numOfSpace == -1) {
            DEFAULT_TAB_CHAR
        } else {
            StringBuffer().let {
                for (i in 0 until numOfSpace) {
                    it.append(' ')
                }
                it.toString()
            }
        }
    }
}