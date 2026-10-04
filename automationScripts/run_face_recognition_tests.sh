#!/usr/bin/env bash
# Face recognition tests: every run listed in the test protocol.
#
# Run from the repository root (Git Bash on Windows, or any bash), with one
# Android device or emulator connected:
#
#     export JAVA_HOME=/path/to/jdk-21
#     export ANDROID_HOME=/path/to/android/sdk        # or sdk.dir in local.properties
#     bash automationScripts/run_face_recognition_tests.sh            # everything
#     bash automationScripts/run_face_recognition_tests.sh green      # current commit
#     bash automationScripts/run_face_recognition_tests.sh red-sensor # sensor tests before FaceNameDetector
#     bash automationScripts/run_face_recognition_tests.sh red-alias  # without xstream aliases
#
# Results go to build/face_recognition_evidence/ (override with EVIDENCE=...):
# one log per Gradle call with its exit code, the JUnit XML/HTML reports, and
# a *_summary.txt per run read from the JUnit XML. The instrumented task has
# ignoreFailures = true, so its exit code says nothing about the tests.
#
# Nothing here edits a file. The red runs run the tests of an earlier commit of
# this branch, checked out in a separate worktree next to this repository.
# Needs: git, bash, python 3 (for the XML summary), JDK 21, Android SDK.

set -u

ROOT="$(git rev-parse --show-toplevel)" || exit 1
cd "$ROOT" || exit 1
EVIDENCE="${EVIDENCE:-$ROOT/build/face_recognition_evidence}"
mkdir -p "$EVIDENCE"
EVIDENCE="$(cd "$EVIDENCE" && pwd)"

# The commit just before "[fix] XStream aliases for the face bricks" on this branch;
# it differs from that fix commit only by the aliases.
BEFORE_ALIAS=68eeae484
ALIAS_FIX=30607b1f5
# "[test] FaceNameSensorStageTest" and "[feature] Continuous face name detection":
# the sensor stage tests on the commit that adds them, the parent of the feature.
SENSOR_TESTS=5945c7c56
SENSOR_FEATURE=0682ba62e
SENSOR_TEST_CLASS=org.catrobat.catroid.uiespresso.facerecognizer.FaceNameSensorStageTest

UNIT_TESTS=(
  --tests "org.catrobat.catroid.FaceRecognizer.FaceDatabaseTest"
  --tests "org.catrobat.catroid.FaceRecognizer.RecognizerSessionTest"
  --tests "org.catrobat.catroid.FaceRecognizer.FaceNameWindowTest"
  --tests "org.catrobat.catroid.camera.mlkitdetectors.FaceNameDetectorTest"
  --tests "org.catrobat.catroid.content.actions.FaceNameTrainActionStateTest"
  --tests "org.catrobat.catroid.formulaeditor.SensorHandlerFaceNameTest"
  --tests "org.catrobat.catroid.formulaeditor.common.FormulaElementResourcesTest"
  --tests "org.catrobat.catroid.stage.BrickDialogManagerTest"
  --tests "org.catrobat.catroid.io.RecentBrickListSerializerTest"
  --tests "org.catrobat.catroid.FaceRecognizer.env.FileUtilsWriteLinesTest"
  --tests "org.catrobat.catroid.FaceRecognizer.env.FileUtilsWriteFilesTest"
  --tests "org.catrobat.catroid.FaceRecognizer.RecognizerPersonNameTest"
  --tests "org.catrobat.catroid.camera.VisualDetectionHandlerAfterStageTest"
  --tests "org.catrobat.catroid.test.xmlformat.BricksXmlSerializerTest"
  --tests "org.catrobat.catroid.FaceRecognizer.ml.MobileFaceNetInputTest"
)
DEVICE_TESTS="org.catrobat.catroid.FaceRecognizer.FaceRecognizerLifecycleTest,\
org.catrobat.catroid.FaceRecognizer.RecognizerTrainingOrderTest,\
org.catrobat.catroid.FaceRecognizer.FaceRecognitionFalsePositiveTest,\
org.catrobat.catroid.FaceRecognizer.FaceDetectionCacheTest,\
org.catrobat.catroid.FaceRecognizer.MobileFaceNetTest,\
org.catrobat.catroid.uiespresso.facerecognizer.FaceTrainingUiTest,\
$SENSOR_TEST_CLASS,\
org.catrobat.catroid.test.content.bricks.FaceNameBrickCategoryTest,\
org.catrobat.catroid.test.content.bricks.BrickCategoryTest"

# ---------------- environment ----------------

die() { echo "ERROR: $*" >&2; exit 1; }

check_environment() {
  [ -n "${JAVA_HOME:-}" ] || die "JAVA_HOME is not set; point it to a JDK 21."
  local version
  version="$("$JAVA_HOME/bin/java" -version 2>&1 | head -1)"
  echo "$version" | grep -q '"21' || die "JAVA_HOME must be a JDK 21 (the project uses Java 21); found: $version"

  if [ -z "${ANDROID_HOME:-}" ] && [ -f local.properties ]; then
    ANDROID_HOME="$(sed -n 's/^sdk\.dir=//p' local.properties | sed 's/\\:/:/g; s/\\\\/\//g')"
  fi
  [ -n "${ANDROID_HOME:-}" ] || die "Set ANDROID_HOME or sdk.dir in local.properties."
  export ANDROID_HOME
  export PATH="$ANDROID_HOME/platform-tools:$PATH"

  # The first candidate that really runs Python 3 (on Windows "python3" can be
  # the Microsoft Store placeholder).
  PYTHON=""
  for candidate in "${PYTHON_BIN:-}" python3 python py; do
    [ -n "$candidate" ] || continue
    if "$candidate" -c 'import sys; sys.exit(sys.version_info[0] != 3)' >/dev/null 2>&1; then
      PYTHON="$candidate"
      break
    fi
  done
  [ -n "$PYTHON" ] || die "python 3 is needed for the XML summary (set PYTHON_BIN)."
}

check_device() {
  local devices
  devices="$(adb devices | sed -n '2,$p' | grep -c 'device$')"
  [ "$devices" = "1" ] || die "Connect exactly one device or emulator (adb devices shows $devices)."
}

# ---------------- helpers ----------------

log_run() {   # log_run <name> <dir> <command...>
  local name="$1" dir="$2"; shift 2
  local log="$EVIDENCE/$name.log"
  {
    echo "# $(date -Iseconds)"
    echo "# dir: $dir"
    echo "# commit: $(git -C "$dir" rev-parse HEAD)"
    echo "# device: $(adb devices -l | sed -n 2p)"
    echo "\$ $*"
  } > "$log"
  ( cd "$dir" && "$@" ) 2>&1 | tee -a "$log"
  local code=${PIPESTATUS[0]}
  echo "EXIT_CODE=$code" | tee -a "$log"
}

clear_results() {   # a run can never pick up XML from an earlier run
  rm -rf "$1/catroid/build/test-results" "$1/catroid/build/outputs/androidTest-results"
}

copy_reports() {   # copy_reports <name> <dir>
  local out="$EVIDENCE/${1}_reports"
  rm -rf "$out"
  mkdir -p "$out"
  cp -r "$2/catroid/build/test-results" "$out/" 2>/dev/null
  cp -r "$2/catroid/build/reports/tests" "$out/unit-html" 2>/dev/null
  cp -r "$2/catroid/build/outputs/androidTest-results" "$out/" 2>/dev/null
  cp -r "$2/catroid/build/reports/androidTests" "$out/device-html" 2>/dev/null
}

summarise_xml() {   # summarise_xml <name>
  "$PYTHON" - "$EVIDENCE/${1}_reports" <<'EOF' | tee "$EVIDENCE/${1}_summary.txt"
import glob, sys, xml.etree.ElementTree as ET
total = failed = 0
for path in sorted(glob.glob(sys.argv[1] + "/**/TEST-*.xml", recursive=True)):
    for tc in ET.parse(path).getroot().iter("testcase"):
        bad = tc.find("failure") is not None or tc.find("error") is not None
        total += 1
        failed += bad
        if bad:
            print("FAIL", tc.get("classname"), tc.get("name"))
print(f"TOTAL={total} PASSED={total - failed} FAILED={failed}")
if total == 0:
    print("NO TESTS RAN")
EOF
}

device_info() {
  {
    echo "serial: $(adb get-serialno)"
    echo "model: $(adb shell getprop ro.product.manufacturer | tr -d '\r') $(adb shell getprop ro.product.model | tr -d '\r')"
    echo "android: $(adb shell getprop ro.build.version.release | tr -d '\r') (API $(adb shell getprop ro.build.version.sdk | tr -d '\r'))"
    echo "build: $(adb shell getprop ro.build.display.id | tr -d '\r')"
  } > "$EVIDENCE/device.txt"
  cat "$EVIDENCE/device.txt"
}

# ---------------- runs ----------------

green() {
  # Tracked files must match the commit, so the results belong to it.
  if [ -n "$(git status --porcelain --untracked-files=no)" ]; then
    git status --short --untracked-files=no
    die "Tracked files differ from HEAD; commit first."
  fi
  git rev-parse HEAD > "$EVIDENCE/green_commit.txt"
  git log -1 --format="%H %s" >> "$EVIDENCE/green_commit.txt"
  git log --oneline -8 > "$EVIDENCE/green_log.txt"
  check_device
  device_info

  clear_results "$ROOT"
  log_run green_unit "$ROOT" ./gradlew :catroid:testCatroidDebugUnitTest \
      "${UNIT_TESTS[@]}" --continue --console=plain
  copy_reports green_unit "$ROOT"
  summarise_xml green_unit

  clear_results "$ROOT"
  log_run green_device "$ROOT" ./gradlew :catroid:connectedCatroidDebugAndroidTest \
      "-Pandroid.testInstrumentationRunnerArguments.class=$DEVICE_TESTS" \
      --continue --console=plain
  copy_reports green_device "$ROOT"
  summarise_xml green_device

  # The fixture task in a full build graph: undeclared dependencies fail validation.
  log_run green_gradle_validation "$ROOT" ./gradlew :catroid:assembleCatroidDebugAndroidTest \
      :catroid:testCatroidDebugUnitTest --tests "org.catrobat.catroid.FaceRecognizer.FaceDatabaseTest" \
      --warning-mode all --console=plain
}

worktree_at() {   # worktree_at <dir> <commit>: a worktree of this repository at <commit>
  local dir="$1" commit="$2"
  if [ ! -d "$dir" ]; then
    git worktree add --detach "$dir" "$commit" || die "Could not create the worktree at $dir."
  fi
  # An existing worktree may be at another commit; move it to <commit>.
  git -C "$dir" checkout -q --detach "$commit" || die "Could not check out $commit in $dir."
  [ "$(git -C "$dir" rev-parse HEAD)" = "$(git rev-parse "$commit")" ] || die "$dir is not at $commit."
  [ -f "$ROOT/local.properties" ] && cp "$ROOT/local.properties" "$dir/local.properties"
  clear_results "$dir"
}

red_sensor() {
  # FaceNameSensorStageTest on the commit that adds it, the parent of the feature.
  [ "$(git rev-parse "$SENSOR_FEATURE^")" = "$(git rev-parse "$SENSOR_TESTS")" ] ||
    die "$SENSOR_TESTS is not the parent of the feature commit $SENSOR_FEATURE."
  git diff --name-only "$SENSOR_TESTS^" "$SENSOR_TESTS" > "$EVIDENCE/red_sensor_changed_files.txt"
  [ "$(cat "$EVIDENCE/red_sensor_changed_files.txt")" = "catroid/src/androidTest/java/org/catrobat/catroid/uiespresso/facerecognizer/FaceNameSensorStageTest.kt
catroid/src/main/java/org/catrobat/catroid/camera/mlkitdetectors/FaceNameTestFrames.kt" ] ||
    die "$SENSOR_TESTS changes more than the stage test and its test-only frame source."
  check_device

  local before="$ROOT/../Catroid-red-sensor"
  worktree_at "$before" "$SENSOR_TESTS"
  log_run red_sensor_before_feature "$before" ./gradlew :catroid:connectedCatroidDebugAndroidTest \
      "-Pandroid.testInstrumentationRunnerArguments.class=$SENSOR_TEST_CLASS" \
      --continue --console=plain
  copy_reports red_sensor_before_feature "$before"
  summarise_xml red_sensor_before_feature
}

red_alias() {
  # The existing BricksXmlSerializerTest on the commit just before the alias fix.
  [ "$(git rev-parse "$ALIAS_FIX^")" = "$(git rev-parse "$BEFORE_ALIAS")" ] ||
    die "$BEFORE_ALIAS is not the parent of the alias fix $ALIAS_FIX."
  git diff --name-only "$BEFORE_ALIAS" "$ALIAS_FIX" > "$EVIDENCE/red_before_alias_changed_files.txt"
  [ "$(cat "$EVIDENCE/red_before_alias_changed_files.txt")" = "catroid/src/main/java/org/catrobat/catroid/io/XstreamSerializer.java" ] ||
    die "The alias fix changes more than XstreamSerializer.java."
  git diff --no-color "$BEFORE_ALIAS" "$ALIAS_FIX" > "$EVIDENCE/red_before_alias.diff"

  local before="$ROOT/../Catroid-before-alias"
  worktree_at "$before" "$BEFORE_ALIAS"
  log_run red_before_alias "$before" ./gradlew :catroid:testCatroidDebugUnitTest \
      --tests "org.catrobat.catroid.test.xmlformat.BricksXmlSerializerTest" \
      --continue --console=plain
  copy_reports red_before_alias "$before"
  summarise_xml red_before_alias
}

check_environment
case "${1:-all}" in
  green)      green ;;
  red-sensor) red_sensor ;;
  red-alias)  red_alias ;;
  all)        green; red_sensor; red_alias ;;
  *) echo "usage: $0 [green|red-sensor|red-alias|all]"; exit 2 ;;
esac

echo
echo "Logs and reports: $EVIDENCE"
grep -H "EXIT_CODE" "$EVIDENCE"/*.log
for summary in "$EVIDENCE"/*_summary.txt; do
  echo "== $(basename "$summary")"
  cat "$summary"
done
