echo "Setting up Dev Server using java $1 for MC $2"

brew install openjdk@$1
sudo ln -sfn /opt/homebrew/opt/openjdk@$1/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-$1.jdk
jenv add /opt/homebrew/opt/openjdk@$1/libexec/openjdk.jdk/Contents/Home

mkdir BuildTools
cd BuildTools

# set local jvm version for BuildTools folder
jenv local $1

curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --rev $2
mv ./spigot-*.jar ../../_dev_server/spigot.jar