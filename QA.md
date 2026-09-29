# Minecraft 26.3 port validation

Base: upstream `26.2-fabric`, commit
`b1db442b4a48bde173aba5bad2abf58a24375465` (the newest Minecraft-version branch
available when this port was prepared).

- Production build: `JAVA_HOME=/tmp/sso-jdk25 ./gradlew build --no-daemon` passed
  with Gradle 9.6.0 and Fabric Loom 1.17.20.
- Target: Minecraft 26.3, Java 25, Fabric Loader 0.19.5, Fabric API
  0.161.0+26.3, Cloth Config 26.3.159; optional Mod Menu 21.0.0-beta.1.
- Dedicated-server test of the production JAR: startup, 108 placement checks,
  `/reload`, another 108 placement checks, and graceful shutdown all passed.
  No ERROR/FATAL log entries; process exit 0. Both crafting recipes and
  server-side configuration initialization passed.
- Isolated production-JAR client launch passed: resources loaded, the Mod Menu
  factory opened the Cloth Config screen, it rendered for 40 client ticks, and
  the client stopped with exit 0. Offline-account Realms authentication errors
  occurred; no Chalk/configuration/rendering failures were observed.
- Archive integrity, Java 25 class version, expanded metadata, and exclusion of
  QA fixture classes from the production JAR verified.
- `git diff --check` and Python QA script compilation passed.

The source changes preserve the destruction-effects override using the new
`spawnDestroyByEntityParticles` hook, remove a redundant `playerDestroy`
override whose server-only signature changed, and update the piston reaction
from `DESTROY` to `POPPED`. Minecraft compatibility is restricted to 26.3
patch releases instead of claiming compatibility with every future version.

See [qa/README.md](qa/README.md) for repeatable checks and coverage. In-world
particles/glow, multiplayer, and the optional colorful addon were not manually
play-tested. Configuration-screen rendering was automated, not a manual UI test.
