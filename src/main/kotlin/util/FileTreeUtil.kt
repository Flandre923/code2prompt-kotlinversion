import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.PathMatcher

object FileTreeUtil {
    private val config: FileExtensionsConfig by lazy {
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val configFile = FileTreeUtil::class.java.getResourceAsStream("/file-extensions.yaml")
        mapper.readValue(configFile, FileExtensionsConfig::class.java)
    }

    private val textFileExtensions: Set<String> by lazy {
        config.text_files  // 修改这里以匹配新的属性名
            .flatMap { it.extensions }
            .toSet()
    }

    fun loadGitignore(rootPath: String): GitignoreResult {
        val gitignoreFile = File(rootPath, ".gitignore")
        return when {
            gitignoreFile.exists() -> GitignoreResult.Success(gitignoreFile.readLines())
            else -> GitignoreResult.Error("未找到 .gitignore 文件，请在项目根目录添加该文件")
        }
    }

    fun isTextFile(file: File): Boolean =
        textFileExtensions.contains(file.extension.lowercase())

    fun buildFileTree(rootPath: String, useGitignore: Boolean): FileTree {
        val ignorePatterns = when {
            useGitignore -> when (val result = loadGitignore(rootPath)) {
                is GitignoreResult.Success -> result.patterns
                is GitignoreResult.Error -> throw IllegalStateException(result.message)
            }
            else -> emptyList()
        }
        return buildFileTreeInternal(File(rootPath), ignorePatterns)
    }

    private fun shouldIgnore(file: File, ignorePatterns: List<String>): Boolean =
        ignorePatterns.any { pattern ->
            val matcher = FileSystems.getDefault()
                .getPathMatcher("glob:$pattern")
            matcher.matches(file.toPath())
        }

    private fun buildFileTreeInternal(file: File, ignorePatterns: List<String>): FileTree =
        when {
            shouldIgnore(file, ignorePatterns) -> 
                FileTree(file.absolutePath, file.name, file.isDirectory)
            
            file.isDirectory -> FileTree(
                path = file.absolutePath,
                name = file.name,
                isDirectory = true,
                children = file.listFiles()
                    ?.map { buildFileTreeInternal(it, ignorePatterns) }
                    ?.filter { !shouldIgnore(File(it.path), ignorePatterns) }
                    ?: emptyList()
            )
            
            else -> FileTree(
                path = file.absolutePath,
                name = file.name,
                isDirectory = false,
                content = when {
                    isTextFile(file) -> try {
                        file.readText()
                    } catch (e: Exception) {
                        null
                    }
                    else -> null
                }
            )
        }

    fun generateTreeString(fileTree: FileTree, prefix: String = ""): String {
        val result = StringBuilder()
        result.append("$prefix${fileTree.name}\n")
        
        fileTree.children.forEachIndexed { index, child ->
            val isLast = index == fileTree.children.size - 1
            val newPrefix = prefix + if (isLast) "└── " else "├── "
            val childPrefix = prefix + if (isLast) "    " else "│   "
            result.append(generateTreeString(child, newPrefix))
        }
        
        return result.toString()
    }
}