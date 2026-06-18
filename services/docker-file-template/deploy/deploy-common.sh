#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_FILE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
COMMON_ENV_FILE="${COMMON_ENV_FILE:-${ENV_FILE:-${DOCKER_FILE_DIR}/.env.infra}}"
SERVICES_CONF="${SERVICES_CONF:-${SCRIPT_DIR}/services.conf}"

log() {
  printf '[deploy] %s\n' "$*"
}

fail() {
  printf '[deploy][error] %s\n' "$*" >&2
  exit 1
}

check_file() {
  local file_path="$1"
  local message="$2"

  if [[ ! -f "${file_path}" ]]; then
    fail "${message}: ${file_path}"
  fi
}

check_runtime() {
  if ! command -v docker >/dev/null 2>&1; then
    fail "未找到 docker 命令，请先在服务器安装并启动 Docker"
  fi

  if docker compose version >/dev/null 2>&1; then
    COMPOSE_BIN=(docker compose)
    return
  fi

  if command -v docker-compose >/dev/null 2>&1; then
    COMPOSE_BIN=(docker-compose)
    return
  fi

  fail "未找到 Docker Compose，请安装 docker compose 插件或 docker-compose"
}

run_compose() {
  local work_dir="$1"
  local compose_file="$2"
  local service_env_file="$3"
  shift 3
  local env_args=()

  check_file "${work_dir}/${compose_file}" "缺少 Compose 文件"

  if [[ -f "${COMMON_ENV_FILE}" ]]; then
    log "加载公共环境文件：${COMMON_ENV_FILE}"
    env_args+=(--env-file "${COMMON_ENV_FILE}")
  else
    log "未找到公共环境文件：${COMMON_ENV_FILE}"
  fi

  if [[ -n "${service_env_file}" && -f "${service_env_file}" ]]; then
    log "加载服务环境文件：${service_env_file}"
    env_args+=(--env-file "${service_env_file}")
  elif [[ -n "${service_env_file}" ]]; then
    log "未找到服务环境文件：${service_env_file}"
  fi

  (cd "${work_dir}" && "${COMPOSE_BIN[@]}" "${env_args[@]}" -f "${compose_file}" "$@")
}

deploy_infra() {
  check_runtime
  log "部署基础设施：MySQL、Redis、app-network"
  run_compose "${DOCKER_FILE_DIR}/infra" "docker-compose-datasource.yml" "" up -d
}

deploy_service() {
  local service_dir="${1:-}"
  local service_path="${DOCKER_FILE_DIR}/${service_dir}"

  if [[ -z "${service_dir}" ]]; then
    fail "缺少服务目录参数，例如：bash deploy/deploy-service-server.sh api-service"
  fi

  check_runtime
  check_file "${service_path}/Dockerfile-backend" "缺少服务 Dockerfile"
  check_file "${service_path}/docker-compose.yml" "缺少服务 Compose 文件"
  check_file "${service_path}/app.jar" "缺少手动打包产物，请将 Jar 命名为 app.jar"

  log "部署服务目录：${service_dir}"
  run_compose "${service_path}" "docker-compose.yml" "${service_path}/.env" up -d --build
}
