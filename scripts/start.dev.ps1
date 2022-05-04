rm .\dev_server\plugins\build.jar
cp .\target\BetterKeepInventory-*.jar .\dev_server\plugins\build.jar
cd dev_server
& "C:\Program Files\Java\jdk-17.0.1\bin\java.exe" -Xms4G -Xmx4G -XX:+UseG1GC -jar spigot.jar nogui