param(
    [switch]$Force
)

$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")
$targetRoot = Join-Path $projectRoot "archetype/src/main/resources/archetype-resources"

function Test-RelativePathExcluded {
    param(
        [string]$RelativePath
    )

    $normalized = $RelativePath -replace "\\", "/"
    $first = ($normalized -split "/")[0]

    $excludedTopLevel = @(
        ".git",
        ".idea",
        ".vscode",
        "archetype",
        "mydocs",
        "target"
    )

    if ($excludedTopLevel -contains $first) {
        return $true
    }

    if ($normalized -eq "LICENSE") {
        return $true
    }

    if ($normalized -like "script/*.ps1") {
        return $true
    }

    if ($normalized -like "*/target/*") {
        return $true
    }

    return $false
}

function Convert-TemplateText {
    param(
        [string]$Text,
        [string]$RelativePath
    )

    $result = $Text

    if ($RelativePath -eq "pom.xml" -or $RelativePath -like "*/pom.xml") {
        $result = $result.Replace("org.weixin.framework", '${groupId}')
        $result = $result.Replace("<artifactId>quick-framework</artifactId>", '<artifactId>${artifactId}</artifactId>')
        $result = $result.Replace("<version>1.0-SNAPSHOT</version>", '<version>${version}</version>')
        $result = $result.Replace("基于SpringBoot3+Java17开发用于快速搭建项目的框架，提供开发基础的工具和配置", '${projectName}')
        $result = $result.Replace("<name>weixin</name>", '<name>${developerName}</name>')
        $result = $result.Replace("<email>1320627222@qq.com</email>", '<email>${developerEmail}</email>')
        $result = $result.Replace("<url>https://github.com/Linweixinyo</url>", '<url>${developerUrl}</url>')
    }
    elseif ($RelativePath -eq "README.md") {
        $result = $result.Replace("org.weixin.framework", '${groupId}')
        $result = $result.Replace("Quick Framework", '${projectName}')
        $result = $result.Replace("quick-framework", '${artifactId}')
        $result = $result.Replace('https://github.com/Linweixinyo/${artifactId}', '${repositoryUrl}')
        $result = $result.Replace("https://github.com/Linweixinyo", '${developerUrl}')
        $result = $result.Replace("- 作者：weixin", '- 作者：${developerName}')
        $result = $result.Replace("1320627222@qq.com", '${developerEmail}')
        $result = $result.Replace("[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)`r`n", "")
        $result = $result.Replace("[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)`n", "")
        $result = $result.Replace("本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。", "许可证信息请按新项目实际情况补充。")
        $result = [regex]::Replace(
            $result,
            "(?s)### 4\. 作为 Maven Archetype 使用.*?(?=### 5\. 创建业务模块)",
            ""
        )
        $result = [regex]::Replace(
            $result,
            "(?s)## ⭐ Star History.*?(?=---)",
            ""
        )
        $result = $result.Replace("### 5. 创建业务模块", "### 4. 创建业务模块")
        $result = $result.Replace("### 6. 引入所需模块", "### 5. 引入所需模块")
        $result = $result.Replace("### 7. 配置文件", "### 6. 配置文件")
    }
    else {
        $result = $result.Replace("org.weixin.framework", '${package}')
        $result = $result.Replace("quick-framework", '${artifactId}')
    }

    return $result
}

Write-Host "即将刷新 Archetype 模板资源目录：$targetRoot"
Write-Host "该操作会删除并重建 archetype-resources，适合由当前源码重新生成模板副本。"

if (-not $Force) {
    $answer = Read-Host "请输入 CONFIRM 继续"
    if ($answer -ne "CONFIRM") {
        Write-Host "已取消。"
        exit 1
    }
}

if (Test-Path -LiteralPath $targetRoot) {
    Remove-Item -LiteralPath $targetRoot -Recurse -Force
}

New-Item -ItemType Directory -Path $targetRoot -Force | Out-Null

$textExtensions = @(
    ".example",
    ".gitignore",
    ".imports",
    ".java",
    ".json",
    ".lua",
    ".md",
    ".sh",
    ".sql",
    ".yml",
    ".xml"
)

Get-ChildItem -LiteralPath $projectRoot -Recurse -File |
    ForEach-Object {
        $relativePath = [System.IO.Path]::GetRelativePath($projectRoot, $_.FullName)
        $normalized = $relativePath -replace "\\", "/"

        if (-not (Test-RelativePathExcluded -RelativePath $normalized)) {
            $templateRelativePath = $relativePath -replace "org[\\/]weixin[\\/]framework", "__packageInPathFormat__"
            $destination = Join-Path $targetRoot $templateRelativePath
            $destinationDirectory = Split-Path -Parent $destination
            New-Item -ItemType Directory -Path $destinationDirectory -Force | Out-Null

            $extension = $_.Extension.ToLowerInvariant()
            if ($textExtensions -contains $extension -or $_.Name -eq ".gitignore" -or $_.Name -like "Dockerfile*") {
                $content = Get-Content -LiteralPath $_.FullName -Raw -Encoding UTF8
                $converted = Convert-TemplateText -Text $content -RelativePath $normalized
                Set-Content -LiteralPath $destination -Value $converted -Encoding UTF8 -NoNewline
            }
            else {
                Copy-Item -LiteralPath $_.FullName -Destination $destination
            }
        }
    }

$generatedPom = Join-Path $targetRoot "pom.xml"
if (Test-Path -LiteralPath $generatedPom) {
    $pom = Get-Content -LiteralPath $generatedPom -Raw -Encoding UTF8
    $pom = $pom.Replace("        <module>archetype</module>`r`n", "")
    $pom = $pom.Replace("        <module>archetype</module>`n", "")
    Set-Content -LiteralPath $generatedPom -Value $pom -Encoding UTF8 -NoNewline
}

Write-Host "模板资源刷新完成：$targetRoot"
