param(
    [string]$OutputDirectory = "",
    [string]$GroupId = "com.example.demo",
    [string]$ArtifactId = "demo-framework",
    [string]$Package = "",
    [string]$Version = "1.0-SNAPSHOT",
    [switch]$Compile
)

$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")
$projectParent = Split-Path -Parent $projectRoot
if ([string]::IsNullOrWhiteSpace($OutputDirectory)) {
    $OutputDirectory = Join-Path $projectParent "quick-framework-archetype-test"
}

$archetypeGroupId = "org.weixin.framework"
$archetypeArtifactId = "quick-framework-archetype"
$archetypeVersion = "1.0-SNAPSHOT"

if ([string]::IsNullOrWhiteSpace($Package)) {
    $Package = $GroupId
}

if (Test-Path -LiteralPath $OutputDirectory) {
    Remove-Item -LiteralPath $OutputDirectory -Recurse -Force
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null

Push-Location $OutputDirectory
try {
    mvn archetype:generate `
        "-DarchetypeGroupId=$archetypeGroupId" `
        "-DarchetypeArtifactId=$archetypeArtifactId" `
        "-DarchetypeVersion=$archetypeVersion" `
        "-DgroupId=$GroupId" `
        "-DartifactId=$ArtifactId" `
        "-Dversion=$Version" `
        "-Dpackage=$Package" `
        "-DprojectName=$ArtifactId" `
        "-DdeveloperName=developer" `
        "-DdeveloperEmail=developer@example.com" `
        "-DdeveloperUrl=https://example.com" `
        "-DrepositoryUrl=https://example.com/$ArtifactId" `
        "-DinteractiveMode=false"
}
finally {
    Pop-Location
}

$generatedRoot = Join-Path $OutputDirectory $ArtifactId
$checks = @(
    @{ Path = (Join-Path $generatedRoot "archetype"); ShouldExist = $false; Message = "生成项目不应包含 archetype 模块" },
    @{ Path = (Join-Path $generatedRoot "LICENSE"); ShouldExist = $false; Message = "生成项目不应包含当前仓库 LICENSE" },
    @{ Path = (Join-Path $generatedRoot "dependencies"); ShouldExist = $true; Message = "生成项目应包含 dependencies" },
    @{ Path = (Join-Path $generatedRoot "frameworks"); ShouldExist = $true; Message = "生成项目应包含 frameworks" },
    @{ Path = (Join-Path $generatedRoot "services"); ShouldExist = $true; Message = "生成项目应包含 services" }
)

foreach ($check in $checks) {
    $exists = Test-Path -LiteralPath $check.Path
    if ($exists -ne $check.ShouldExist) {
        throw $check.Message
    }
}

$residualPatterns = @("org.weixin.framework", "org/weixin/framework", "quick-framework")
foreach ($pattern in $residualPatterns) {
    $matches = rg --fixed-strings $pattern $generatedRoot --glob "!target/**"
    if ($LASTEXITCODE -eq 0) {
        $matches | Select-Object -First 20
        throw "生成项目仍残留：$pattern"
    }
    elseif ($LASTEXITCODE -gt 1) {
        throw "rg 检查失败：$pattern"
    }
}

if ($Compile) {
    Push-Location $generatedRoot
    try {
        mvn compile
    }
    finally {
        Pop-Location
    }
}

Write-Host "Archetype 生成验证通过：$generatedRoot"
