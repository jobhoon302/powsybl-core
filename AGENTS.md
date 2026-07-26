# AGENTS.md

## Cursor Cloud specific instructions

PowSyBl Core is a **Java 21 / Maven multi-module monorepo** (~94 modules) that builds
library JARs plus the `iTools` command-line distribution. It is **not** a long-running
service: there is no server, database, or web app to start. Development is entirely
build / test / run-CLI cycles. See `README.md` for the canonical build docs.

### Toolchain
- JDK 21 and the Maven wrapper `./mvnw` (Maven 3.9.3) are preinstalled. There is **no**
  system `mvn` on the PATH, so always use `./mvnw` (or pass `--mvn ./mvnw` to `install.sh`).

### Build / test / lint / run
- Build everything (also installs artifacts to `~/.m2`, needed before running iTools or
  offline test runs): `./mvnw install -DskipTests`
- Run tests for a module (online, so Surefire's JUnit-platform provider can be fetched):
  `./mvnw test -pl <module>` (e.g. `-pl ucte/ucte-converter`). Full suite: `./mvnw package`.
- Lint = **Checkstyle** + Maven **Enforcer**, inherited from `powsybl-parent`. They run
  automatically during any `compile`/`test`/`install`; there is no separate lint command.
- Run the app (iTools CLI) from the build output without reinstalling:
  `./distribution-core/target/powsybl/bin/itools --help`
  Example end-to-end task (convert a grid file):
  `./distribution-core/target/powsybl/bin/itools convert-network --input-file <case>.uct --output-format XIIDM --output-file <out>`

### Non-obvious gotchas
- `./install.sh` defaults to the `mvn` command which does not exist here; run
  `./install.sh --mvn ./mvnw install` if you need the copied distribution under `~/powsybl`.
  Otherwise just use the CLI directly from `distribution-core/target/powsybl/bin/itools`.
- `./mvnw dependency:go-offline` FAILS on this repo (it tries to resolve the optional
  artifact `com.bea.xml:jsr173-ri:1.0`, which is absent from Maven Central). The normal
  build does not need it, so prime dependencies with `./mvnw install -DskipTests`, not
  `go-offline`.
- Running tests with `-o` (offline) right after an `-DskipTests` build fails because the
  Surefire JUnit-platform provider was never downloaded; run tests online at least once.
- `convert-network --input-file` needs a real filesystem path (the datasource resolves by
  directory + basename); copy test-resource cases out to a working dir if paths confuse it.
- Docker-tagged tests are skipped by default; enable with
  `-Dpowsybl.docker-unit-tests.skip=false` (requires a Docker daemon).
