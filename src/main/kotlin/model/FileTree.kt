data class FileTree(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val children: List<FileTree> = emptyList(),
    val content: String? = null
)

data class ProcessConfig(
    val outputPath: String? = null,
    val maxLines: Int? = null,
    val ignorePatterns: List<String> = emptyList()
)