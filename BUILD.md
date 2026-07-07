# Building Orbital Railgun (Fabric 1.21.1)

## Requirements

- **JDK 21** — Minecraft 1.21.1 needs it. Gradle 8.14 rejects JDK 25 (the system default here).
  - Path on this box: `/usr/lib/jvm/java-21-openjdk-amd64`
- **Network access to `maven.fabricmc.net`** — hosts fabric-loom, yarn mappings, fabric-loader, intermediary. Loom hardcodes this host; no mirror substitutes.
  - Also needed: `libraries.minecraft.net`, `piston-data.mojang.com`, `resources.download.minecraft.net`, `meta.fabricmc.net`
- Gradle wrapper (`gradlew` + `gradle/wrapper/gradle-wrapper.jar`) — already present in the repo.

---

## 0. Check network (this sandbox blocks it)

```bash
curl -I -m 10 https://maven.fabricmc.net/
```

- **Times out** → build cannot run here. Allowlist the hosts above in your proxy/firewall, or build on a networked machine.
- **Returns HTTP headers** → continue.

---

## 1. Build

```bash
cd /home/neo/cortisol/orbital-railgun
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build --console=plain
```

First run downloads Minecraft + mappings and decompiles (~3–8 min). Output jar:

```
build/libs/orbital_railgun-1.0.jar
```

---

## Other useful commands

```bash
# Compile only (faster feedback than full build)
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew compileJava compileClientJava --console=plain

# Run the client in a dev instance
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runClient

# Run a dedicated dev server
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew runServer

# Clean rebuild
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew clean build --console=plain

# Full stack trace on failure
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build --stacktrace --console=plain
```

---

## If the gradle wrapper is missing

The wrapper jar/script are committed, but if they ever go missing regenerate them
(system Gradle 4.4.1 cannot evaluate this project because loom won't resolve, so
generate in an empty dir and copy the files in):

```bash
mkdir -p /tmp/wrap && cd /tmp/wrap
gradle wrapper --gradle-version 8.14.1 --distribution-type bin
cp gradlew gradlew.bat /home/neo/cortisol/orbital-railgun/
cp gradle/wrapper/gradle-wrapper.jar /home/neo/cortisol/orbital-railgun/gradle/wrapper/
chmod +x /home/neo/cortisol/orbital-railgun/gradlew
```

---

## Set JAVA_HOME once (skip prefixing every command)

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
cd /home/neo/cortisol/orbital-railgun
./gradlew build --console=plain
```
