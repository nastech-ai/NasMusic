#!/usr/bin/env bash
# NasMusic GitHub Actions Self-Hosted Runner
# Usage: bash setup-and-run.sh <RUNNER_TOKEN>
# Get a fresh token from: https://github.com/nastech-ai/NasMusic/settings/actions/runners/new

set -e

RUNNER_TOKEN="${1:-$RUNNER_REG_TOKEN}"
REPO_URL="https://github.com/nastech-ai/NasMusic"
RUNNER_NAME="nasmusic-replit-runner"
RUNNER_LABELS="self-hosted,Linux,X64,nasmusic"

if [ -z "$RUNNER_TOKEN" ]; then
  echo "[ERROR] Provide runner registration token as argument or set RUNNER_REG_TOKEN env var."
  echo "  Get a new token: https://github.com/nastech-ai/NasMusic/settings/actions/runners/new"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "[1/3] Configuring runner..."
./config.sh \
  --url "$REPO_URL" \
  --token "$RUNNER_TOKEN" \
  --name "$RUNNER_NAME" \
  --labels "$RUNNER_LABELS" \
  --unattended \
  --replace

echo "[2/3] Installing dependencies for runner..."
./bin/installdependencies.sh 2>/dev/null || true

echo "[3/3] Starting runner (press Ctrl+C to stop)..."
./run.sh
