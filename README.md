# Code2Prompt

Code2Prompt 是一个将代码目录转换为 prompt 文本的工具，它可以帮助你快速生成包含目录结构和文件内容的文本，适用于与 AI 助手交流时分享代码上下文。

## 功能特点

- 生成目录树结构，类似 `tree` 命令的输出
- 支持读取并包含文本文件的内容
- 支持 .gitignore 文件过滤
- 支持按最大行数分割输出
- 支持输出到文件或剪贴板
- 智能识别文本文件，避免读取二进制文件

## 安装

确保你的系统已安装 Java 8 或更高版本。

下载最新的发布版本：
code2prompt-kotlinversion-1.0-SNAPSHOT.jar

## 使用方法
基本用法：
```sh 
java -jar code2prompt-kotlinversion-1.0-SNAPSHOT.jar <directory> [options]
```

### 令行选项
- <directory> : 要处理的目录路径（必需）
- -o, --output <file> : 指定输出文件路径
- -m, --maxlines <number> : 设置每个文件的最大行数
- -g, --gitignore : 启用 .gitignore 文件过滤
- --help : 显示帮助信息

### 示例
1. 将目录内容复制到剪贴板：

```sh 
java -jar code2prompt-kotlinversion-1.0-SNAPSHOT.jar D:\your\project
```

2. 将目录内容输出到文件：

```sh
java -jar code2prompt-kotlinversion-1.0-SNAPSHOT.jar D:\your\project -o output.txt
```

3. 启用.gitignore 文件过滤：

```sh
java -jar code2prompt-kotlinversion-1.0-SNAPSHOT.jar D:\your\project -g
```

4. 设置每个文件的最大行数：

```sh
java -jar code2prompt-kotlinversion-1.0-SNAPSHOT.jar D:\your\project -m 100
```
### 输出说明
- 不指定 -o 选项时，内容将被复制到剪贴板
- 指定 -o 选项时，内容将被写入到指定文件
- 当使用 -m 选项限制行数时，超出行数限制的内容将被分割到多个文件中（例如：output.txt, output_1.txt, output_2.txt）
- 第一个输出文件总是包含目录树结构

## 注意事项
- 工具会自动识别文本文件，避免读取二进制文件
- 使用 -g 选项时，会自动查找并使用目录下的 .gitignore 文件
- 文件分割时会保证同一个文件的内容不会被分割到不同的输出文件中

