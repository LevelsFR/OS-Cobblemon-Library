#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
runner="$root/scripts/retry-gradle-transient.sh"
tmp_dir="$(mktemp -d)"
trap 'rm -rf "$tmp_dir"' EXIT
mock_bin="$tmp_dir/bin"
mkdir -p "$mock_bin"

cat > "$mock_bin/gradle-mock" <<'MOCK'
#!/usr/bin/env bash
count=0
[[ -f "$RETRY_TEST_COUNTER" ]] && read -r count < "$RETRY_TEST_COUNTER"
count=$((count + 1))
printf '%s\n' "$count" > "$RETRY_TEST_COUNTER"
case "$RETRY_TEST_MODE" in
  transient)
    if (( count == 1 )); then
      echo "Received status code 503 from server"
      exit 1
    fi
    exit 0
    ;;
  transient429)
    if (( count == 1 )); then
      echo "Received status code 429 from server"
      exit 1
    fi
    exit 0
    ;;
  not_found)
    echo "Received status code 404 from server"
    exit 1
    ;;
  compile)
    echo "Compilation failed"
    exit 1
    ;;
  persistent)
    echo "Received status code 503 from server"
    exit 1
    ;;
  *)
    echo "Unexpected test mode: $RETRY_TEST_MODE" >&2
    exit 2
    ;;
esac
MOCK
cat > "$mock_bin/sleep" <<'MOCK'
#!/usr/bin/env bash
exit 0
MOCK
chmod +x "$mock_bin/gradle-mock" "$mock_bin/sleep"

run_case() {
  local mode="$1" expected_status="$2" expected_attempts="$3" status=0 actual_attempts
  local counter="$tmp_dir/$mode.count" output="$tmp_dir/$mode.log"
  rm -f "$counter"
  RETRY_TEST_MODE="$mode" RETRY_TEST_COUNTER="$counter" PATH="$mock_bin:$PATH" \
    bash "$runner" gradle-mock > "$output" 2>&1 || status=$?
  actual_attempts="$(cat "$counter")"
  if [[ "$status" -ne "$expected_status" || "$actual_attempts" -ne "$expected_attempts" ]]; then
    cat "$output"
    echo "Retry test failed for $mode: status=$status attempts=$actual_attempts" >&2
    exit 1
  fi
}

run_case transient 0 2
run_case transient429 0 2
run_case not_found 1 1
run_case compile 1 1
run_case persistent 1 3
echo "Gradle retry checks passed (transient, permanent build failure, retry limit)."
