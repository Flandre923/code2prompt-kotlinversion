import java.io.File

object ContentProcessor {
    fun processContent(fileTree: FileTree, config: ProcessConfig): List<String> {
        val treeString = FileTreeUtil.generateTreeString(fileTree)
        val contentParts = mutableListOf<String>()
        
        // 收集所有文件内容
        val fileContents = mutableListOf<Pair<String, String>>()
        
        fun collectContent(node: FileTree) {
            if (!node.isDirectory && node.content != null) {
                fileContents.add(node.path to node.content)
            }
            node.children.forEach { collectContent(it) }
        }
        
        collectContent(fileTree)
        
        // 如果没有设置 maxLines，所有内容放在一个文件中
        if (config.maxLines == null) {
            val allContent = StringBuilder()
            allContent.append(treeString).append("\n\n")
            fileContents.forEach { (path, content) ->
                allContent.append("File: $path\n$content\n\n")
            }
            return listOf(allContent.toString())
        }
        
        // 如果设置了 maxLines，按最大行数分组
        contentParts.add(treeString)
        var currentPart = StringBuilder()
        var currentLines = 0
        var partIndex = 1
        
        fileContents.forEach { (path, content) ->
            val lines = content.lines()
            if (currentLines + lines.size > config.maxLines) {
                if (currentLines > 0) {
                    contentParts.add("Part $partIndex:\n$currentPart")
                    partIndex++
                    currentPart = StringBuilder()
                    currentLines = 0
                }
            }
            currentPart.append("File: $path\n$content\n\n")
            currentLines += lines.size
        }
        
        if (currentLines > 0) {
            contentParts.add("Part $partIndex:\n$currentPart")
        }
        
        return contentParts
    }
}