#!/usr/bin/env bash
set -euo pipefail

# 单台服务器全套部署脚本。
# 先部署基础设施，再按 deploy/services.conf 中的 SERVICES 顺序部署多个服务。
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=deploy-common.sh
source "${SCRIPT_DIR}/deploy-common.sh"

if [[ ! -f "${SERVICES_CONF}" ]]; then
  fail "缺少多服务声明文件，请先复制 deploy/services.conf.example 为 deploy/services.conf"
fi

# shellcheck source=services.conf.example
source "${SERVICES_CONF}"

if ! declare -p SERVICES >/dev/null 2>&1; then
  fail "deploy/services.conf 中必须声明 SERVICES 数组"
fi

if [[ "${#SERVICES[@]}" -eq 0 ]]; then
  fail "SERVICES 数组不能为空"
fi

deploy_infra

for service_dir in "${SERVICES[@]}"; do
  deploy_service "${service_dir}"
done

log "单台服务器全套部署命令已执行完成"
