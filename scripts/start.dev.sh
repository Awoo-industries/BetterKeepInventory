echo pwd
rm ./dev_server/plugins/build.jar
cp ./target/BetterKeepInventory-*.jar ./dev_server/plugins/build.jar
cd dev_server

jenv local 21
java -DIReallyKnowWhatIAmDoingISwear -Xms4G -Xmx4G -XX:+UseG1GC -jar spigot.jar nogui
