#!/usr/bin/env bash
set -uo pipefail

if (( $# == 0 )); then
  echo "Usage: retry-gradle-transient.sh <command> [args...]" >&2
  exit 2
fi

attempt=1
max_attempts=3
delay=5
log_file="$(mktemp)"
trap 'rm -f "$log_file"' EXIT

while (( attempt <= max_attempts )); do
  : > "$log_file"
  "$@" 2>&1 | tee "$log_file"
  status=${PIPESTATUS[0]}
  if (( status == 0 )); then
    exit 0
  fi

  if (( attempt == max_attempts )) || ! grep -Eiq '(status code|response code)[^0-9]*(429|5[0-9][0-9])|HTTP[^0-9]*(429|5[0-9][0-9])' "$log_file"; then
    exit "$status"
  fi

  printf 'Gradle hit a transient HTTP error; retrying (%d/%d)\n' "$attempt" "$max_attempts" >&2
  sleep "$delay"
  attempt=$((attempt + 1))
  delay=$((delay * 2))
done
