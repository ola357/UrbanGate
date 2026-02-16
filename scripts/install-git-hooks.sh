#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HOOKS_DIR="${ROOT_DIR}/.githooks"
HOOK_FILE="${HOOKS_DIR}/commit-msg"

mkdir -p "${HOOKS_DIR}"

cat > "${HOOK_FILE}" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail

MSG_FILE="${1:-}"
if [[ -z "${MSG_FILE}" || ! -f "${MSG_FILE}" ]]; then
  echo "commit-msg hook: message file not found"
  exit 1
fi

SUBJECT="$(head -n 1 "${MSG_FILE}")"

# Require OLAM-<digits> at the start of the subject
if [[ ! "${SUBJECT}" =~ ^OLAM-[0-9]+ ]]; then
  echo "❌ Invalid commit message subject:"
  echo "   ${SUBJECT}"
  echo ""
  echo "Expected format:"
  echo "   OLAM-123 Short description"
  exit 1
fi

exit 0
EOF

chmod +x "${HOOK_FILE}"

# Configure this repo to use .githooks
git -C "${ROOT_DIR}" config core.hooksPath .githooks

echo "✅ Installed commit-msg hook at .githooks/commit-msg"
echo "✅ Configured git core.hooksPath=.githooks for this repository"
