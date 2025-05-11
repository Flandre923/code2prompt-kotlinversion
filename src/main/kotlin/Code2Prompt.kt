import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int  // 修改这个导入
import com.github.ajalt.clikt.parameters.options.flag
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File

class Code2PromptCommand : CliktCommand(
    help = "将代码目录转换为 prompt 文本",
    name = "code2prompt"
) {
    private val rootPath by argument(
        help = "要处理的目录路径"
    )
    
    private val outputPath by option("-o", "--output",
        help = "输出文件路径"
    )
    
    private val maxLines by option("-m", "--maxlines",
        help = "每个文件的最大行数"
    ).int()  // 使用正确的 int() 扩展函数
    
    private val useGitignore by option("-g", "--gitignore",
        help = "使用 .gitignore 文件过滤"
    ).flag()

    override fun run() {
        Code2Prompt().process(rootPath, outputPath, maxLines, useGitignore)
    }
}

class Code2Prompt {
    fun process(
        rootPath: String,
        outputPath: String? = null,
        maxLines: Int? = null,
        useGitignore: Boolean = false
    ) {
        val config = ProcessConfig(outputPath, maxLines)
        val fileTree = FileTreeUtil.buildFileTree(rootPath, useGitignore)
        val contentParts = ContentProcessor.processContent(fileTree, config)
        
        if (contentParts.size > 1 && outputPath != null) {
            // 如果有多个部分且指定了输出文件，按序号输出到多个文件
            contentParts.forEachIndexed { index, content ->
                val fileName = if (index == 0) {
                    // 第一个文件（目录树）使用原始文件名
                    outputPath
                } else {
                    // 其他文件添加序号
                    val dotIndex = outputPath.lastIndexOf('.')
                    if (dotIndex != -1) {
                        "${outputPath.substring(0, dotIndex)}_${index}${outputPath.substring(dotIndex)}"
                    } else {
                        "${outputPath}_$index"
                    }
                }
                File(fileName).writeText(content)
            }
        } else {
            // 如果只有一个部分或没有指定输出文件，按原来的方式处理
            handleOutput(contentParts.joinToString("\n\n"), config.outputPath)
        }
    }

    private fun handleOutput(content: String, outputPath: String?) {
        if (outputPath != null) {
            File(outputPath).writeText(content)
        } else {
            val selection = StringSelection(content)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
        }
    }
}
fun main(args: Array<String>) = Code2PromptCommand().main(args)