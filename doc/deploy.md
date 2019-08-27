1. mvn package runner 打包成jar

2. 修改 server/src/resources/application.yml 中 runner-jar-path

3. 修改 server/src/resources/application.yml 中 work-dir

4. mvn package server, jar为可执行web程序

5. 将doc/structure.java放入work-dir

6. 解压server.jar, 将lib放入work-dir, 将classes放入work-dir

7. 启动server, java -jar server.jar --spring.profiles.active=dev


