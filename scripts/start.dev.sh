echo pwd
rm ./_dev_server/plugins/BetterKeepInventory.jar
rm ./_dev_server/plugins/plugin_api_consumer.jar
cp ./plugin/target/BetterKeepInventory-plugin-*.jar ./_dev_server/plugins/BetterKeepInventory.jar
cp ./plugin_api_consumer/target/plugin_api_consumer-*.jar ./_dev_server/plugins/plugin_api_consumer.jar
cd _dev_server

jenv local 21
java -DIReallyKnowWhatIAmDoingISwear -Xms4G -Xmx4G -XX:+UseG1GC -jar spigot.jar nogui
