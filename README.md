# auto-upgrader

> <b>升级补丁列表&nbsp;:&nbsp;</b>[[ Management-Page ]](https://lyy289065406.github.io/auto-upgrader/)


------

## 运行环境

　![](https://img.shields.io/badge/Platform-Windows%20x64-brightgreen.svg)  ![](https://img.shields.io/badge/JDK-1.7%2B-brightgreen.svg)

## 插件介绍

　此插件是供开发者使用的，通过它可以利用Github服务器发布若干个应用的升级补丁。

　然后这些应用可以通过Github服务器提取自身的升级补丁实现在线升级。


## 实现原理

![实现原理](https://raw.githubusercontent.com/lyy289065406/auto-upgrader/master/doc/01-%E8%BD%AF%E4%BB%B6%E8%87%AA%E5%8A%A8%E5%8D%87%E7%BA%A7%E5%8E%9F%E7%90%86.png)


　在Github的每个Repository都是可以发布一个静态页面的，而且这个静态页面可以在公网访问。

　这个静态页面初衷是用来介绍Repository的，但是也可以利用它作为一个静态服务页，实现其他功能。


> **为某个Repository发布静态页面的方法：**
<br/>　　（1） 打开这个Repository的仓库首页
<br/>　　（2） 进入 `Settings` 页面
<br/>　　（3） 找到 `GitHub Pages`，在 `Source`下面有一个下拉框， 默认是 `None`， 修改为 `master branch`
<br/>　　（4） 然后刷新 `Settings` 页面，在 `GitHub Pages` 的位置会提示：Your site is published at `xxx-url`
<br/>　　（5） 在Repository根目录新建一个 `index.html` 文件，写入页面内容，就可以通过这个 `xxx-url` 访问它了


　回到这个插件，其实原理就很简单了，要实现的功能主要有两个：

- （在开发环境）通过更新 `index.html`，在 `xxx-url` 静态页面维护若干个应用的版本信息和升级补丁
- （在生产环境）应用从 `xxx-url` 静态页面提取比当前版本更高的补丁，下载并进行升级


## 使用方式

　由于插件存在两部分功能，因此使用方式也是对应拆分为两部分。


<br/>　**（开发环境）在 `xxx-url` 静态页面维护应用的版本信息和升级补丁：**

- 01.　通过 git 命令 clone 这个插件仓库到本地，并通过 Eclipse 导入这个插件项目

- 04.　通过 git 提交变更，即成功把应用的版本信息和升级补丁提交到了 Github服务器
- 05.　此时刷新静态页面地址 `xxx-url`，会发现页面内容已变更，如：
![静态页面](https://raw.githubusercontent.com/lyy289065406/auto-upgrader/master/doc/02-%E9%9D%99%E6%80%81%E9%A1%B5%E9%9D%A2%E7%9A%84%E5%8D%87%E7%BA%A7%E8%A1%A5%E4%B8%81.png)



<br/>　**（生产环境）应用从 `xxx-url` 静态页面下载高版本补丁并升级：**

- 01.　通过 `maven install` 命令发布本插件作为 jar构件，目标应用通过 pom.xml 依赖此构件：
```xml
<dependency>
  <groupId>exp.au</groupId>
  <artifactId>auto-upgrader</artifactId>
  <version>1.0</version>
</dependency>
```
- 02.　构件内提供了API：`exp.certificate.api.getAppInfo` ，可据此从 `xxx-url` 获取自身的授权信息
- 03.　获取到的授权信息会以明文方式保存到 `exp.certificate.bean.AppInfo`， 按需使用即可


## 版权声明

　[![Copyright (C) 2016-2018 By EXP](https://img.shields.io/badge/Copyright%20(C)-2006~2018%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------