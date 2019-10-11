## java repl

1. mvn package runner 打包成jar

2. 修改 server/src/resources/application.yml 中 runner.jar-path

3. 修改 server/src/resources/application.yml 中 work-dir

4. mvn package server, jar为可执行web程序

5. 将doc/structure.java放入work-dir

6. 在work-dir中创建目录target

7. 解压server.jar, 将BOOT-INF/lib放入work-dir, 将BOOT-INF/classes放入work-dir

8. 配置环境变量 JAVA_HOME=jdk根目录

9. 启动server, java -jar server.jar --spring.profiles.active=dev


