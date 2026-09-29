# Minecraft 26.3 verification

The production mod is built with Java 25 using `./gradlew clean build`.
`build/libs/chalk-3.2.1+26.3.jar` is the distributable; the `-sources.jar` and
QA fixtures are not installed with the mod.

## Reproduce the dedicated-server checks

These scripts require Python 3, a populated Gradle cache from the build, and
Java 25. Create a directory containing only the production dependency JARs:
Fabric API `0.161.0+26.3` and Cloth Config `26.3.159`. Pass that directory as
`--mods`. The default Java location is `/tmp/sso-jdk25/bin`; override it with
`--javac` and `--java` respectively on other machines.

```sh
python3 qa/build_fixture.py --jar build/libs/chalk-3.2.1+26.3.jar --mods /path/to/dependencies --javac /path/to/java25/bin/javac
python3 qa/server_smoke.py --jar build/libs/chalk-3.2.1+26.3.jar --mods /path/to/dependencies --fixture build/qa-fixture/chalk-qa-checks.jar --java /path/to/java25/bin/java
```

The server harness creates a fresh disposable world under `build/qa-run/`,
accepts the Minecraft EULA for that local test instance, binds only to localhost
on a free port, and writes an artifact/checksum audit, console log, and JSON
result. It starts the production JAR with Fabric Loader 0.19.5, runs the checks,
reloads datapacks, repeats the checks, and shuts down normally. No existing game
world or launcher installation is used.

The fixture verifies both normal and glow chalk crafting, the white-only
registration gate without the optional colorful addon, configuration loading,
and 108 placements per phase (two variants × six faces × nine click regions).
Each placement verifies its orientation, empty collision shape, targetable
outline, piston `POPPED` reaction, surviving support, automatic removal when
support disappears, and execution of the 26.3 destruction-effects hook. It also
checks the 64-use durability setting. These checks exercise server behavior;
they do not visually inspect particles/glow, click the Mod Menu screen, test
multiplayer clients, or validate the separately distributed colorful addon.

## Client configuration smoke fixture

The client fixture waits for the title screen, opens the screen supplied by
Chalk's Mod Menu integration, lets it render for 40 ticks, and stops the client.
Build it with `qa/build_fixture.py --client` and the same arguments above, using
a dependency directory that additionally contains Mod Menu 21.0.0-beta.1. Install
the resulting `build/qa-fixture/chalk-client-qa.jar` alongside the production mod
and dependencies in a separate Minecraft 26.3 Fabric test profile. A passing run
logs `CHALK_CLIENT_QA_CONFIG_OPEN` and `CHALK_CLIENT_QA_PASS` and exits normally.
The fixture must never be distributed with the production mod.
