echo pwd
rm ./dev_server/plugins/build.jar
cp ./target/BetterKeepInventory-*.jar ./dev_server/plugins/build.jar
cd dev_server
/Users/rickmegens/Library/Java/JavaVirtualMachines/corretto-17.0.4/Contents/Home/bin/java -DIReallyKnowWhatIAmDoingISwear -Xms4G -Xmx4G -XX:+UseG1GC -jar spigot-1.19.2.jar nogui
