#!/usr/bin/env bash
set -euo pipefail

COLIMA=/Users/kevin.horner/brew/opt/colima/bin/colima
COMPOSE_DIR="$(cd "$(dirname "$0")" && pwd)"
export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"

colima_running() {
  "$COLIMA" status 2>/dev/null | grep -q "Running"
}

start() {
  if ! colima_running; then
    echo "Starting Colima..."
    "$COLIMA" start --memory 6 --cpu 4
  else
    echo "Colima already running."
  fi
  echo "Starting services..."
  docker-compose -f "$COMPOSE_DIR/docker-compose.yml" up -d
  echo "SonarQube will be available at http://localhost:9000 (may take ~60s to initialize)"
}

stop() {
  if colima_running; then
    echo "Stopping services..."
    docker-compose -f "$COMPOSE_DIR/docker-compose.yml" down
    echo "Stopping Colima..."
    "$COLIMA" stop
  else
    echo "Colima is not running — nothing to stop."
  fi
  echo "All stopped."
}

restart() {
  stop
  start
}

case "${1:-}" in
  start)   start ;;
  stop)    stop ;;
  restart) restart ;;
  *)
    echo "Usage: $0 {start|stop|restart}"
    exit 1
    ;;
esac
