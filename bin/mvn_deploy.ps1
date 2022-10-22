#!/bin/bash
# ------------------------------------------------
# 一键自动发布到 sonatype 仓库
# ------------------------------------------------
# 示例：
#   ./bin/mvn_deploy.ps1
# ------------------------------------------------

echo "Deploy all jars to sonatype ..."
mvn clean deploy -P ttyForDeploy
echo "Done ."
