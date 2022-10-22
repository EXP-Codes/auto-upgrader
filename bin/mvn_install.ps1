# ------------------------------------------------
# 一键自动安装到本地仓库
# ------------------------------------------------
# 示例（若需签名可加上 --sign 参数）：
#   ./bin/mvn_install.ps1 [--sign]
# ------------------------------------------------

$sign = ""
if (${args}[0] -Eq "--sign") {
    $sign = "-P ttyForInstall"
}

Write-Output "Generate javadoc-jar for all modules ..."
mvn clean package -Dmaven.test.skip=true ${sign}
Write-Output "Done ."
