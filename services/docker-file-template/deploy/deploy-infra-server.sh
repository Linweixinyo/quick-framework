#!/usr/bin/env bash
set -euo pipefail

# 适用于只部署数据库与缓存的基础设施服务器。
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=deploy-common.sh
source "${SCRIPT_DIR}/deploy-common.sh"

deploy_infra

log "基础设施部署命令已执行完成"
