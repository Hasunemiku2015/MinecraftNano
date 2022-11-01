package com.hasunemiku2015.minecraftnano.files

object CommentCharConfiguration: AbstractConfiguration("comment.yml") {
    private const val DEFAULT_COMMENT_CHAR = "#"

    /**
     * Gets the comment character used in a file, default is '#'.
     * @param fileExtension: Extension of a file, including the 'dot'. (example: .txt)
     * @return A string that represents the Comment Character. (Tab or X-Spaces)
     */
    fun getCommentChar(fileExtension: String): String {
        return yamlConfiguration.getString(fileExtension.removePrefix(".")) ?: DEFAULT_COMMENT_CHAR
    }
}