# spigot-nms

## Development

add `local.properties` in project root directory.

```properties
GITHUB_ACTOR=
GITHUB_TOKEN=
GITHUB_REF_NAME=
SPIGOT_VERSION=
```

Download spigot dependencies.

```shell
cd ./server
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --rev $SPIGOT_VERSION
```