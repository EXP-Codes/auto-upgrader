#!/bin/bash
# 兼容不同版本的 sed 命令
#-----------------------------------------------------------------
# 命令执行示例：
# bin/_sed.sh "from_str" "to_str" "/file/path"
#-----------------------------------------------------------------

FROM=$1
TO=$2
FILEPATH=$3

# Mac
if [ "$(uname)" == "Darwin" ]; then
    sed -i '' "s@${FROM}@${TO}@g" ${FILEPATH}

# Linux
else
    sed -i "s@${FROM}@${TO}@g" ${FILEPATH}
fi