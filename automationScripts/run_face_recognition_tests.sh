#!/usr/bin/env bash
# Face recognition tests: the local and device tests of the current commit.
#
# Run from the repository root (Git Bash on Windows, or any bash), with one
# Android device or emulator connected:
#
#     export JAVA_HOME=/path/to/jdk-21
#     export ANDROID_HOME=/path/to/android/sdk        # or sdk.dir in local.properties
#     bash automationScripts/run_face_recognition_tests.sh
#
# Results go to build/face_recognition_evidence/ (override with EVIDENCE=...):
# one log per Gradle call with its exit code, the JUnit XML/HTML reports, and
# a *_summary.txt per run read from the JUnit XML. The instrumented task has
# ignoreFailures = true, so its exit code says nothing about the tests.
#
# Nothing here edits a file. The device tests delete the trained faces on the
# device; the app itself stays installed, with its projects.
# Needs: git, bash, python 3 (for the XML summary), JDK 21, Android SDK.

set -u

ROOT="$(git rev-parse --show-toplevel)" || exit 1
cd "$ROOT" || exit 1
EVIDENCE="${EVIDENCE:-$ROOT/build/face_recognition_evidence}"
mkdir -p "$EVIDENCE"
EVIDENCE="$(cd "$EVIDENCE" && pwd)"

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
  --tests "org.catrobat.catroid.FaceRecognizer.ml.BlazeFaceSuppressionTest"
  --tests "org.catrobat.catroid.camera.CameraAnalyzersTest"
  --tests "org.catrobat.catroid.camera.DetectorsCompleteListenerTest"
  --tests "org.catrobat.catroid.FaceRecognizer.RecognizerPersonNameTest"
  --tests "org.catrobat.catroid.camera.VisualDetectionHandlerAfterStageTest"
  --tests "org.catrobat.catroid.test.xmlformat.BricksXmlSerializerTest"
  --tests "org.catrobat.catroid.FaceRecognizer.ml.MobileFaceNetInputTest"
  --tests "org.catrobat.catroid.test.robolectric.SpriteActivityRecreationTest"
  --tests "org.catrobat.catroid.test.robolectric.StageActivityRecreationTest"
  --tests "org.catrobat.catroid.stage.FaceNameWatcherTest"
  --tests "org.catrobat.catroid.content.bricks.FaceNameTrainBrickTest"
  --tests "org.catrobat.catroid.io.RenamedBrickTypesTest"
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
  # In PATH a Windows drive letter ("C:/...") splits at its colon; Git Bash needs /c/...
  local sdk_in_path="$ANDROID_HOME"
  if command -v cygpath >/dev/null 2>&1; then
    sdk_in_path="$(cygpath -u "$ANDROID_HOME")"
  fi
  export PATH="$sdk_in_path/platform-tools:$PATH"
  command -v adb >/dev/null 2>&1 || die "adb not found in $sdk_in_path/platform-tools."

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

# Every run that failed: a Gradle exit code, failed tests, no tests or unreadable results.
FAILED_RUNS=()

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
  [ "$code" = "0" ] || FAILED_RUNS+=("$name: Gradle exit code $code")
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

summarise_xml() {   # summarise_xml <name>; exits 1 with failed tests, no tests or an unreadable report
  "$PYTHON" - "$EVIDENCE/${1}_reports" <<'EOF' | tee "$EVIDENCE/${1}_summary.txt"
import glob, sys, xml.etree.ElementTree as ET
total = failed = skipped = unreadable = 0
for path in sorted(glob.glob(sys.argv[1] + "/**/TEST-*.xml", recursive=True)):
    try:
        root = ET.parse(path).getroot()
    except (ET.ParseError, OSError) as error:
        unreadable += 1
        print("UNREADABLE", path, error)
        continue
    for tc in root.iter("testcase"):
        total += 1
        if tc.find("failure") is not None or tc.find("error") is not None:
            failed += 1
            print("FAIL", tc.get("classname"), tc.get("name"))
        elif tc.find("skipped") is not None:
            skipped += 1
            print("SKIPPED", tc.get("classname"), tc.get("name"))
print(f"TOTAL={total} PASSED={total - failed - skipped} FAILED={failed} SKIPPED={skipped}")
if total == 0:
    print("NO TESTS RAN")
if unreadable:
    print(f"UNREADABLE REPORTS={unreadable}")
sys.exit(1 if failed or total == 0 or unreadable else 0)
EOF
  local code=${PIPESTATUS[0]}
  [ "$code" = "0" ] || FAILED_RUNS+=("$1: failed tests, no tests or unreadable results")
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
  # Gradle uninstalls the app after the run by default, and with it every project
  # saved on the device; the app and the test app stay installed instead.
  log_run green_device "$ROOT" ./gradlew :catroid:connectedCatroidDebugAndroidTest \
      "-Pandroid.testInstrumentationRunnerArguments.class=$DEVICE_TESTS" \
      -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true \
      --continue --console=plain
  copy_reports green_device "$ROOT"
  summarise_xml green_device

  # The fixture task in a full build graph: undeclared dependencies fail validation.
  log_run green_gradle_validation "$ROOT" ./gradlew :catroid:assembleCatroidDebugAndroidTest \
      :catroid:testCatroidDebugUnitTest --tests "org.catrobat.catroid.FaceRecognizer.FaceDatabaseTest" \
      --warning-mode all --console=plain
}

report() {   # the logs and summaries; returns 1 when any run failed
  echo
  echo "Logs and reports: $EVIDENCE"
  grep -H "EXIT_CODE" "$EVIDENCE"/*.log
  for summary in "$EVIDENCE"/*_summary.txt; do
    echo "== $(basename "$summary")"
    cat "$summary"
  done
  echo
  if [ "${#FAILED_RUNS[@]}" -gt 0 ]; then
    echo "FAILED:"
    printf '  %s\n' "${FAILED_RUNS[@]}"
    return 1
  fi
  echo "ALL RUNS PASSED"
}

check_environment
case "${1:-green}" in
  green) green ;;
  *) echo "usage: $0 [green]"; exit 2 ;;
esac
report || exit 1
