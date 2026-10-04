# Catroid Update Smoke Test

This smoke test exercises the update path users take from the currently deployed
Play Store branch to the APK under test.

The workflow builds a release-style baseline APK from `main`, and a
release-style `updateTest` APK for the PR with the same package
name and a higher version code. Both APKs are built locally and signed with the
same debug key so Android accepts the in-place update. The `updateTest` build
type is not suitable for updating over an APK actually downloaded from Google
Play, because Play-signed and debug-signed APKs cannot update each other.

## Local Run

Build or provide two APKs with the same package name, where the second APK has a
higher version code:

```sh
ADB=/path/to/adb \
automationScripts/update-smoke/run-update-smoke.sh \
  /path/to/baseline.apk \
  /path/to/pr.apk \
  org.catrobat.catroid
```

The emulator must allow `adb root`, because the test seeds private app storage
under `/data/data/org.catrobat.catroid` before launching the updated APK.

## Seeded Fixtures

The script seeds:

- `Pong Starter` as a known-good project.
- `UpdateSmokeBrokenProject` with malformed `code.xml` and a corrupt thumbnail.
- `fixtures/legacy-room-v2-app-database.sql`, a Room schema version 2 database
  fixture whose `project_response` table intentionally lacks the version 3
  `private` column.

## Environment Variables

- `ADB`: path to `adb`; defaults to `adb`.
- `UPDATE_SMOKE_ARTIFACT_DIR`: directory for logcat/UI artifacts.
- `UPDATE_SMOKE_GOOD_PROJECT_ARCHIVE`: override the good `.catrobat` fixture.
- `UPDATE_SMOKE_GOOD_PROJECT_NAME`: expected display name for the good project.
- `UPDATE_SMOKE_BROKEN_PROJECT_NAME`: expected display name for the broken project.
- `UPDATE_SMOKE_LEGACY_DB_SQL`: override the legacy Room SQL fixture.
- `UPDATE_SMOKE_SEED_LEGACY_DB`: set to `false` to skip DB seeding.
- `UPDATE_SMOKE_STRICT_UI`: defaults to `true`; set to `false` for local
  debugging when UI matching should not fail the run.
