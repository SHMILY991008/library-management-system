#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

for command in npm java sudo nginx; do
  if ! command -v "$command" >/dev/null 2>&1; then
    printf 'Missing command: %s\n' "$command" >&2
    exit 1
  fi
done

if [[ ! -f /etc/library-management/api.env ]]; then
  printf 'Create /etc/library-management/api.env from deploy/api.env.example first.\n' >&2
  exit 1
fi

if [[ -e /etc/nginx/sites-enabled/default ]]; then
  printf 'The default Nginx site owns port 80. Review and disable it before deployment.\n' >&2
  exit 1
fi

cd "$repo_root/frontend"
npm ci
npm run build

cd "$repo_root/backend"
./mvnw package

if ! id libraryapp >/dev/null 2>&1; then
  sudo useradd --system --home /opt/library-management --shell /usr/sbin/nologin libraryapp
fi

sudo install -d -o libraryapp -g libraryapp /opt/library-management
sudo install -d /var/www/library-management
sudo cp -a "$repo_root/frontend/dist/." /var/www/library-management/
sudo install -o libraryapp -g libraryapp -m 644 "$repo_root/backend/target/library-management-system-0.0.1-SNAPSHOT.jar" /opt/library-management/library-api.jar
sudo install -m 644 "$repo_root/deploy/systemd/library-api.service" /etc/systemd/system/library-api.service
sudo install -m 644 "$repo_root/deploy/nginx/library.conf" /etc/nginx/sites-available/library-management
sudo ln -sfn /etc/nginx/sites-available/library-management /etc/nginx/sites-enabled/library-management
sudo nginx -t
sudo systemctl daemon-reload
sudo systemctl enable --now library-api
sudo systemctl reload nginx
printf 'Deployment files installed. Check systemctl status library-api and visit the host address.\n'
