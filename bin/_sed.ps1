# 模拟 sed 命令的正则替换效果
#-----------------------------------------------------------------
# 命令执行示例：
# bin/_sed.ps1 "from_str" "to_str" "/file/path"
#-----------------------------------------------------------------

$FROM = ${args}[0]
$TO = ${args}[1]
$FILEPATH = ${args}[2]

$UTF8_NOBOM = New-Object System.Text.UTF8Encoding($False)
[System.IO.File]::WriteAllLines(${FILEPATH}, ((Get-Content ${FILEPATH} -encoding utf8) -replace "${FROM}", "${TO}"), ${UTF8_NOBOM})
