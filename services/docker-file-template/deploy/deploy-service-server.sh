#!/usr/bin/env bash
set -euo pipefail

# 适用于部署单个后端服务的服务器。
# 示例：bash deploy-service-server.sh api-service
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=deploy-common.sh
source "${SCRIPT_DIR}/deploy-common.sh"

SERVICE_DIR="${1:-${SERVICE_DIR:-service-template}}"

deploy_service "${SERVICE_DIR}"

log "${SERVICE_DIR} 部署命令已执行完成"
