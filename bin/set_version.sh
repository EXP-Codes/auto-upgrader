#!/bin/bash
# 设置 pom.xml 中的项目版本号
#-----------------------------------------------------------------
# 命令执行示例：
# bin/set_version.sh [old_version] [new_version]
#-----------------------------------------------------------------

OLD_VERSION="<version>$1</version>"
NEW_VERSION="<version>$2</version>"
POM_PATH="./pom.xml"

bin/_sed.sh $OLD_VERSION $NEW_VERSION $POM_PATH
