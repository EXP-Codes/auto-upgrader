@echo off
title auto-upgrader

set threadname=auto-upgrader
set lib0=.\lib

javaw -Dtn=%threadname% -Xms32m -Xmx256m  -cp %lib0%\auto-upgrader.jar;%lib0%\exp-libs-simple.jar;%lib0%\exp-libs.jar;%lib0%\beauty-eye-3.7.jar;%lib0%\javase-2.2.jar;%lib0%\core-2.2.jar;%lib0%\dom4j-1.6.1.jar;%lib0%\javaini-1.1.0.0.jar;%lib0%\proxool-0.9.1.jar;%lib0%\proxool-cglib-0.9.1.jar;%lib0%\sqlite-jdbc-3.7.2.jar;%lib0%\poi-ooxml-3.9.jar;%lib0%\poi-3.9.jar;%lib0%\poi-ooxml-schemas-3.9.jar;%lib0%\dom4j-1.6.1.jar;%lib0%\json-lib-2.4-jdk15.jar;%lib0%\commons-beanutils-1.8.0.jar;%lib0%\commons-collections-3.2.1.jar;%lib0%\commons-lang-2.5.jar;%lib0%\ezmorph-1.0.6.jar;%lib0%\jackson-xc-1.9.9.jar;%lib0%\jackson-core-asl-1.9.9.jar;%lib0%\jackson-mapper-asl-1.9.9.jar;%lib0%\jackson-smile-1.9.9.jar;%lib0%\jackson-mrbean-1.9.9.jar;%lib0%\jackson-jaxrs-1.9.9.jar;%lib0%\jackson-core-lgpl-1.9.9.jar;%lib0%\jackson-mapper-lgpl-1.9.9.jar;%lib0%\commons-httpclient-3.1-rc1.jar;%lib0%\commons-net-3.3.jar;%lib0%\bcprov-jdk15on-1.54.jar;%lib0%\base64-1.0.jar;%lib0%\commons-compress-1.8.1.jar;%lib0%\commons-dbutils-1.5.jar;%lib0%\commons-io-2.4.jar;%lib0%\commons-logging-1.1.3.jar;%lib0%\commons-lang3-3.3.jar;%lib0%\xmlbeans-2.6.0.jar;%lib0%\stax-api-1.0.1.jar;%lib0%\commons-codec-1.8.jar;%lib0%\log4j-1.2.17.jar;%lib0%\slf4j-api-1.7.5.jar; exp.au.PatchInstaller   2>err.log

