# Publishing to Maven Central

Maintainer: Timur Kokishev (AlreadyBetter). Namespace: `io.github.alreadybetter`.
The project groupId is `io.github.alreadybetter.wordinflexer`. The license is MIT.

The `central-release` profile prepares the parent POM and both modules for a single reactor deployment. It attaches source and Javadoc JARs, signs artifacts and uploads them to Central Portal for validation. `autoPublish=false` leaves the final publication decision in the Portal.

The configuration has been prepared but no release build or upload has been performed. The project remains at `0.1.0-SNAPSHOT` until a release is intentionally prepared.

## Prerequisites

- Maven 3.6.3 or later and a full JDK 17 or later for release tooling. The library still targets Java 8 through `release=8`.
- A verified Central Portal namespace (already confirmed for this account).
- A Central Portal user token: its username and password are separate from GitHub login credentials.
- GnuPG with a signing key whose public key is available to Central validators.

GnuPG was found and its version command worked at `C:\Program Files\Git\usr\bin\gpg.exe` on the current development machine. If it is not on PATH, add its directory for the current PowerShell session:

```powershell
$env:PATH = 'C:\Program Files\Git\usr\bin;' + $env:PATH
```

Check available signing keys locally with `gpg --list-secret-keys --keyid-format LONG`. If none is suitable, create one interactively with `gpg --full-generate-key`, using your identity and an email address you control. Keep the private key and passphrase outside the repository and back them up securely. Publish only the public key, following the [Sonatype GPG guide](https://central.sonatype.org/publish/requirements/gpg/).

## Local credentials

Generate a publishing token in your Central Portal account. Set these variables in your local terminal or IDE environment:

- `CENTRAL_TOKEN_USERNAME`: token username.
- `CENTRAL_TOKEN_PASSWORD`: token password.
- `MAVEN_GPG_PASSPHRASE`: signing-key passphrase, if not supplied by a configured GPG agent.

The committed `publishing/settings.xml` contains only environment-variable references. Use it with Maven's `-s` option. If you need existing mirrors or proxy settings, merge its `central` server entry into your personal Maven settings instead; do not overwrite existing settings blindly.

Do not paste secret values into source files, documentation, command-line arguments or chat. Maven GPG best-practice checks are enabled; use the environment or GPG agent for the passphrase, not `-Dgpg.passphrase`.

## Prepare the release version

Run all commands below from the repository root. They are manual instructions, not steps already executed.

Inspect the working tree and stage the intended changes, including renamed files. Select an unused release version and update the entire reactor. For the first release:

```shell
mvn org.codehaus.mojo:versions-maven-plugin:2.18.0:set -DnewVersion=0.1.0 -DprocessAllModules=true -DgenerateBackupPoms=false
```

Review the parent version and both child parent references. The dependency on core uses `${project.version}` and follows the reactor version. The release profile rejects SNAPSHOT project versions and dependencies.

Commit the reviewed release state and create an appropriate Git tag, such as `v0.1.0`. The POM currently identifies SCM as `HEAD`; update its tag to the actual release tag when preparing that release. Do not publish until the release sources are available in the GitHub repository.

## Verify locally

After the version and signing key are ready, this command builds modules, executes tests, generates documentation and signs the artifacts without uploading them:

```shell
mvn -Pcentral-release clean verify -Dgpg.keyname=YOUR_SIGNING_KEY_FINGERPRINT
```

Replace the fingerprint placeholder with your public key fingerprint. For the two JAR modules, inspect the main JAR, `-sources.jar`, `-javadoc.jar` and signatures in their target directories. Main JARs include the root MIT license as `META-INF/LICENSE`. The parent POM also needs its signature; it does not need source or Javadoc JARs.

## Upload for validation

When local verification succeeds and you intend to upload, run the full reactor from its root:

```shell
mvn -s publishing/settings.xml -Pcentral-release clean deploy -Dgpg.keyname=YOUR_SIGNING_KEY_FINGERPRINT
```

This command executes the checks again and uploads the parent and both modules together. It waits for Central validation. It does not automatically make the release public.

Open [Central Portal deployments](https://central.sonatype.com/publishing/deployments), inspect validation results and click Publish only when ready. Once published, that version cannot be replaced; corrections require a new version.

After publication, move development to the next SNAPSHOT version and reset the SCM tag to `HEAD`.

## Consumer dependency

After version `0.1.0` has actually been published, consumers can use:

```xml
<dependency>
    <groupId>io.github.alreadybetter.wordinflexer</groupId>
    <artifactId>word-inflexer-kazakh</artifactId>
    <version>0.1.0</version>
</dependency>
```

Core is resolved transitively. JUnit remains test-scoped and is not a consumer dependency. No custom repository entry is needed for Maven Central.

## References

- [Central Maven publishing plugin](https://central.sonatype.org/publish/publish-portal-maven/)
- [Central publication requirements](https://central.sonatype.org/publish/requirements/)
- [Maven GPG signing configuration](https://maven.apache.org/plugins/maven-gpg-plugin/sign-mojo.html)
