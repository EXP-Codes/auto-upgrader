#!/bin/bash
# ------------------------------------------------
# 一键自动安装到本地仓库
# ------------------------------------------------
# 示例（若需签名可加上 --sign 参数）：
#   ./bin/mvn_install.sh [--sign]
# ------------------------------------------------

# 使得当前会话支持交互输入
export GPG_TTY=$(tty)

sign=""
if [ "x$1" = "x--sign" ]; then
  sign="-P ttyForInstall"
fi

echo "Install all jars for all modules ..."
mvn clean install -Dmaven.test.skip=true ${sign}
echo "Done ."
