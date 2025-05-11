data class FileCategory(
    val category: String,
    val extensions: List<String>
)

data class FileExtensionsConfig(
    val text_files: List<FileCategory>  // 注意这里要和 YAML 文件中的键名完全匹配
)

sealed interface GitignoreResult {
    data class Success(val patterns: List<String>) : GitignoreResult
    data class Error(val message: String) : GitignoreResult
}