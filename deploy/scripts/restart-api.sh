#!/usr/bin/env bash
set -euo pipefail

app_dir="/opt/library-management"
pid_file="$app_dir/library-api.pid"
log_file="$app_dir/library-api.log"
env_file="/etc/library-management/api.env"

if [[ ! -f "$env_file" ]]; then
  printf 'Missing %s\n' "$env_file" >&2
  exit 1
fi

if [[ -f "$pid_file" ]]; then
  old_pid="$(cat "$pid_file")"
  if kill -0 "$old_pid" >/dev/null 2>&1; then
    kill "$old_pid"
    for _ in {1..20}; do
      kill -0 "$old_pid" >/dev/null 2>&1 || break
      sleep 1
    done
  fi
  rm -f "$pid_file"
fi

set -a
source "$env_file"
set +a
nohup java -jar "$app_dir/library-api.jar" >> "$log_file" 2>&1 &
echo $! > "$pid_file"
printf 'Library API started with PID %s\n' "$(cat "$pid_file")"
