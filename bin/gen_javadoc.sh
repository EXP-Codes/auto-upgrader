#!/bin/bash
# ------------------------------------------------
# 一键自动生成所有子模块的 javadoc 文档
# ------------------------------------------------
# 示例：
#   ./bin/gen_javadoc.sh
# ------------------------------------------------

echo "Generate javadoc-jar ..."
mvn clean package -Dmaven.test.skip=true

srcFile=`ls ./$item/target | grep 'javadoc.jar$'`
srcPath="./$item/target/$srcFile"

if [ -f "$srcPath" ]; then
  snkPath="./$item/target/$item.zip"
  cp $srcPath $snkPath
  srcPath=$snkPath

  if [ -f "$srcPath" ]; then
    snkPath="./docs/javadocs/$item"
    if [ -d "$snkPath" ]; then
      rm -rf $snkPath
    fi
    mkdir -p $snkPath
    unzip -d $snkPath $srcPath
  fi
fi
echo "Done ."
