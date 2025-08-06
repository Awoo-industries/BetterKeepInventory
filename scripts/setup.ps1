# cba to properly set up a setup script for windows (i mostly do dev on macos)
# so uh, just pretend this is all automated and places a spigot.jar in the _dev_server folder, mkay?

echo "Setting up Dev Server for MC 1.21"

mkdir BuildTools
cd BuildTools
curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
C:\Users\micro\AppData\Roaming\ModrinthApp\meta\java_versions\zulu17.60.17-ca-jre17.0.16-win_x64\bin\javaw.exe -jar BuildTools.jar