#!/bin/bash
# ------------------------------------------------
# 一键自动发布到 sonatype 仓库
# ------------------------------------------------
# 示例：
#   ./bin/mvn_deploy.sh
# ------------------------------------------------

# 使得当前会话支持交互输入
export GPG_TTY=$(tty)

echo "Deploy all jars to sonatype ..."
mvn clean deploy -P ttyForDeploy
echo "Done ."
