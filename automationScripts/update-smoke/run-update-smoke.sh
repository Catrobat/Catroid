#!/usr/bin/env bash

set -euo pipefail

export MSYS2_ARG_CONV_EXCL='*'
export MSYS_NO_PATHCONV=1

if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
  echo "Usage: $0 <baseline-apk> <pr-apk> [package-name]" >&2
  exit 2
fi

BASELINE_APK="$1"
PR_APK="$2"
PACKAGE_NAME="${3:-org.catrobat.catroid}"
ADB="${ADB:-adb}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
ARTIFACT_DIR="${UPDATE_SMOKE_ARTIFACT_DIR:-$REPO_ROOT/build/update-smoke}"
LEGACY_DB_SQL="${UPDATE_SMOKE_LEGACY_DB_SQL:-$SCRIPT_DIR/fixtures/legacy-room-v2-app-database.sql}"
GOOD_PROJECT_ARCHIVE="${UPDATE_SMOKE_GOOD_PROJECT_ARCHIVE:-$REPO_ROOT/catroid/src/androidTest/assets/Pong_Starter.catrobat}"
GOOD_PROJECT_NAME="${UPDATE_SMOKE_GOOD_PROJECT_NAME:-Pong Starter}"
BROKEN_PROJECT_NAME="${UPDATE_SMOKE_BROKEN_PROJECT_NAME:-UpdateSmokeBrokenProject}"
STRICT_UI="${UPDATE_SMOKE_STRICT_UI:-true}"
SEED_LEGACY_DB="${UPDATE_SMOKE_SEED_LEGACY_DB:-true}"

APP_ROOT="/data/data/$PACKAGE_NAME"
APP_FILES="$APP_ROOT/files"
APP_DATABASES="$APP_ROOT/databases"
APP_DATABASE="$APP_DATABASES/app_database"
TMP_DIR="$(mktemp -d)"

cleanup() {
  rm -rf "$TMP_DIR"
}

mkdir -p "$ARTIFACT_DIR"

log() {
  echo "[update-smoke] $*"
}

capture_logcat() {
  local name="$1"
  "$ADB" logcat -d > "$ARTIFACT_DIR/$name-logcat.txt" || true
}

capture_failure_artifacts() {
  local exit_code="$1"
  if [ "$exit_code" -ne 0 ]; then
    log "Capturing failure artifacts after exit code $exit_code"
    capture_logcat "failure"
    "$ADB" shell uiautomator dump /sdcard/update-smoke-failure-window.xml \
      > "$ARTIFACT_DIR/failure-uiautomator.txt" 2>&1 || true
    "$ADB" pull /sdcard/update-smoke-failure-window.xml "$ARTIFACT_DIR/failure-window.xml" \
      > "$ARTIFACT_DIR/failure-adb-pull-window.txt" 2>&1 || true
  fi
}

finish() {
  local exit_code="$?"
  capture_failure_artifacts "$exit_code"
  cleanup
  exit "$exit_code"
}

trap finish EXIT

assert_no_crash() {
  local name="$1"
  capture_logcat "$name"

  if grep -E "FATAL EXCEPTION|ANR in $PACKAGE_NAME|Process: $PACKAGE_NAME|BaseExceptionHandler: uncaughtException|Process $PACKAGE_NAME .*has died|SIG: 9" "$ARTIFACT_DIR/$name-logcat.txt"; then
    log "Crash or ANR found in $name logcat"
    exit 1
  fi
}

prepare_seed_data() {
  local seed_root="$TMP_DIR/app-files"
  mkdir -p "$seed_root/$GOOD_PROJECT_NAME"
  unzip -q "$GOOD_PROJECT_ARCHIVE" -d "$seed_root/$GOOD_PROJECT_NAME"

  mkdir -p "$seed_root/$BROKEN_PROJECT_NAME/images"
  mkdir -p "$seed_root/$BROKEN_PROJECT_NAME/sounds"
  touch "$seed_root/$BROKEN_PROJECT_NAME/.nomedia"
  touch "$seed_root/$BROKEN_PROJECT_NAME/images/.nomedia"
  touch "$seed_root/$BROKEN_PROJECT_NAME/sounds/.nomedia"

  cat > "$seed_root/$BROKEN_PROJECT_NAME/code.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<program>
  <header>
    <programName>$BROKEN_PROJECT_NAME</programName>
  </header>
  <broken>
EOF

  printf '%s\n' 'not a png file' > "$seed_root/$BROKEN_PROJECT_NAME/automatic_screenshot.png"

  echo "$seed_root"
}

enable_adb_root() {
  "$ADB" wait-for-device
  "$ADB" root > "$ARTIFACT_DIR/adb-root.txt" 2>&1 || true
  "$ADB" wait-for-device
}

seed_private_storage() {
  local seed_root="$1"
  local seed_archive="$TMP_DIR/update-smoke-fixtures.tar.gz"
  local seed_archive_for_adb
  local device_seed_archive="/data/local/tmp/update-smoke-fixtures.tar.gz"
  local app_uid

  app_uid="$("$ADB" shell "stat -c %u '$APP_ROOT'" | tr -d '\r')"

  tar -czf "$seed_archive" -C "$seed_root" .
  seed_archive_for_adb="$seed_archive"
  if command -v cygpath >/dev/null 2>&1; then
    seed_archive_for_adb="$(cygpath -w "$seed_archive")"
  fi
  "$ADB" push "$seed_archive_for_adb" "$device_seed_archive" > "$ARTIFACT_DIR/adb-push-fixtures.txt"
  "$ADB" shell "rm -rf '$APP_FILES' && mkdir -p '$APP_FILES'"
  "$ADB" shell "tar -xzf '$device_seed_archive' -C '$APP_FILES'"
  "$ADB" shell "rm -f '$device_seed_archive'"
  "$ADB" shell "chown -R $app_uid:$app_uid '$APP_FILES'"
  "$ADB" shell "restorecon -R '$APP_ROOT'" > "$ARTIFACT_DIR/restorecon.txt" 2>&1 || true
}

seed_legacy_room_database() {
  local sql_file="$TMP_DIR/update-smoke-legacy-db.sql"
  local sql_file_for_adb
  local device_sql_file="/data/local/tmp/update-smoke-legacy-db.sql"
  local app_uid

  [ "$SEED_LEGACY_DB" = "true" ] || return 0

  app_uid="$("$ADB" shell "stat -c %u '$APP_ROOT'" | tr -d '\r')"

  cp "$LEGACY_DB_SQL" "$sql_file"

  sql_file_for_adb="$sql_file"
  if command -v cygpath >/dev/null 2>&1; then
    sql_file_for_adb="$(cygpath -w "$sql_file")"
  fi

  "$ADB" push "$sql_file_for_adb" "$device_sql_file" > "$ARTIFACT_DIR/adb-push-legacy-db-sql.txt"
  "$ADB" shell "rm -rf '$APP_DATABASES' && mkdir -p '$APP_DATABASES'"
  "$ADB" shell "sqlite3 '$APP_DATABASE' < '$device_sql_file'" > "$ARTIFACT_DIR/sqlite-seed-legacy-db.txt" 2>&1
  "$ADB" shell "rm -f '$device_sql_file'"
  "$ADB" shell "chown -R $app_uid:$app_uid '$APP_DATABASES'"
  "$ADB" shell "restorecon -R '$APP_DATABASES'" > "$ARTIFACT_DIR/restorecon-db.txt" 2>&1 || true
}

launch_app() {
  "$ADB" shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1 \
    > "$ARTIFACT_DIR/monkey-launch.txt"
}

wait_for_process() {
  local timeout_seconds="$1"
  local elapsed=0

  while [ "$elapsed" -lt "$timeout_seconds" ]; do
    if "$ADB" shell pidof "$PACKAGE_NAME" >/dev/null 2>&1; then
      return 0
    fi
    sleep 1
    elapsed=$((elapsed + 1))
  done

  log "Package $PACKAGE_NAME is not running after $timeout_seconds seconds"
  capture_logcat "missing-process"
  exit 1
}

dump_window() {
  local name="$1"

  "$ADB" shell uiautomator dump /sdcard/update-smoke-window.xml \
    > "$ARTIFACT_DIR/$name-uiautomator.txt" 2>&1 || true
  "$ADB" pull /sdcard/update-smoke-window.xml "$ARTIFACT_DIR/$name-window.xml" \
    > "$ARTIFACT_DIR/$name-adb-pull-window.txt" 2>&1 || true
}

tap_first_matching_node() {
  local pattern="$1"
  local label="$2"
  local xml_file="$ARTIFACT_DIR/tap-window.xml"
  local node
  local bounds
  local x1
  local y1
  local x2
  local y2
  local x
  local y

  "$ADB" shell uiautomator dump /sdcard/update-smoke-window.xml >/dev/null 2>&1 || return 1
  "$ADB" pull /sdcard/update-smoke-window.xml "$xml_file" >/dev/null 2>&1 || return 1

  node="$(tr '<' '\n' < "$xml_file" | grep -E "$pattern" | head -n 1 || true)"
  [ -n "$node" ] || return 1

  bounds="$(printf '%s\n' "$node" | sed -n 's/.*bounds="\[\([0-9]\+\),\([0-9]\+\)\]\[\([0-9]\+\),\([0-9]\+\)\]".*/\1 \2 \3 \4/p')"
  [ -n "$bounds" ] || return 1

  read -r x1 y1 x2 y2 <<< "$bounds"
  x=$(((x1 + x2) / 2))
  y=$(((y1 + y2) / 2))

  log "Tapping $label at $x,$y"
  "$ADB" shell input tap "$x" "$y"
}

accept_first_run_dialog_if_present() {
  sleep 2
  tap_first_matching_node 'text="ACCEPT"' "first-run accept button" || true
}

open_project_list_if_needed() {
  if tap_first_matching_node 'text="Projects"' "projects list"; then
    sleep 3
  fi
}

hide_hints_dialog_if_present() {
  sleep 2
  tap_first_matching_node 'text="HIDE"' "hide hints button" || true
}

run_optional_ui_smoke() {
  accept_first_run_dialog_if_present
  dump_window "after-launch"

  open_project_list_if_needed

  if ! tap_first_matching_node "text=\"$GOOD_PROJECT_NAME\"" "$GOOD_PROJECT_NAME project"; then
    log "Good project node was not found in the UI dump"
    [ "$STRICT_UI" = "true" ] && exit 1
    return 0
  fi

  sleep 5
  hide_hints_dialog_if_present
  assert_no_crash "after-good-project-tap"

  if tap_first_matching_node 'resource-id="org\.catrobat\.catroid:id/button_play"' "play button"; then
    sleep 10
    assert_no_crash "after-good-project-play"
    "$ADB" shell input keyevent KEYCODE_BACK || true
    sleep 2
  else
    log "Play button node was not found after opening the good project"
    [ "$STRICT_UI" = "true" ] && exit 1
  fi

  "$ADB" shell am force-stop "$PACKAGE_NAME"
  "$ADB" logcat -c
  launch_app
  sleep 5
  open_project_list_if_needed || true

  if tap_first_matching_node "text=\"$BROKEN_PROJECT_NAME\"" "$BROKEN_PROJECT_NAME project"; then
    sleep 10
    assert_no_crash "after-broken-project-tap"
  else
    log "Broken project node was not found in the UI dump"
    [ "$STRICT_UI" = "true" ] && exit 1
  fi
}

log "Preparing direct private-storage project fixtures"
SEED_ROOT="$(prepare_seed_data)"

log "Starting rootable emulator adb session"
enable_adb_root
"$ADB" logcat -c

log "Installing baseline APK without launching it"
"$ADB" install "$BASELINE_APK" > "$ARTIFACT_DIR/install-baseline.txt"
if "$ADB" shell pidof "$PACKAGE_NAME" >/dev/null 2>&1; then
  log "Baseline app is already running before the update"
  exit 1
fi

log "Pushing fixtures into $APP_FILES"
seed_private_storage "$SEED_ROOT"
log "Seeding legacy Room app_database"
seed_legacy_room_database

log "Installing PR APK as an in-place update"
"$ADB" install -r "$PR_APK" > "$ARTIFACT_DIR/install-pr.txt"

log "Launching updated app"
"$ADB" logcat -c
launch_app
wait_for_process 30
sleep 15
assert_no_crash "after-launch"

log "Running optional UI smoke interactions"
run_optional_ui_smoke

log "Update smoke test completed"
