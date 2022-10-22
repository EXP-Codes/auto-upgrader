# 设置 pom.xml 中的项目版本号
#-----------------------------------------------------------------
# 命令执行示例：
# bin/set_version.ps1 [old_version] [new_version]
#-----------------------------------------------------------------

$OLD_VERSION = "<version>" + ${args}[0] + "</version>"
$NEW_VERSION = "<version>" + ${args}[1] + "</version>"
$POM_PATH = "./pom.xml"

bin/_sed.ps1 $OLD_VERSION $NEW_VERSION $POM_PATH

