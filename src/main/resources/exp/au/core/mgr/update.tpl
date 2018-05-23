<?xml version="1.0" encoding="UTF-8"?>
<update>
  
  <name caption="升级补丁名称">@{patch-name}@</name>
  <releaseTime caption="发布时间">@{release-time}@</releaseTime>
  
  <steps caption="自动升级步骤" hint="
        1.升级时根据升级命令依次执行
        2.只有四种原子命令: add, rpl, mov, del
          2.1.新增命令 [add] : [新文件/目录] from [补丁包相对位置] to [应用程序相对位置]
          2.2.替换命令 [rpl] : [新文件/目录] from [补丁包相对位置] to [应用程序相对位置] (若存在则替换)
          2.3.移动命令 [mov] : [原文件/目录] from [应用程序相对位置(旧)] to [应用程序相对位置(新)]
          2.4.删除命令 [del] : [原文件/目录] from [应用程序相对位置] 删除
  ">
@{cmds}@
  </steps>
   
</update>