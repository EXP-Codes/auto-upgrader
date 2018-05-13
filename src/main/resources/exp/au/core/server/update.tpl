<?xml version="1.0" encoding="UTF-8"?>
<update>
  
  <name caption="升级包名称">@{patch-name}@</name>
  <MD5 caption="升级包 MD5">@{MD5}@</MD5>
  
  <steps caption="自动升级步骤" hint="
        1.升级时根据升级命令依次执行
        2.只有三种原子命令: add, mov, del
        3.新增命令 [add] : [新文件] from [补丁包相对位置] to [应用程序相对位置]
        4.移动命令 [mov] : [原文件] from [应用程序相对位置(旧)] to [应用程序相对位置(新)]
        5.删除命令 [del] : [原文件] from [应用程序相对位置] 删除
  ">
@{cmds}@
  </steps>
   
</update>