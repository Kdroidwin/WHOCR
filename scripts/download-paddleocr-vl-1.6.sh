#!/usr/bin/env bash
# Downloads only from the official PaddlePaddle Hugging Face repository.
# The official client verifies Xet content before writing the completed model.
set -euo pipefail

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
TARGET_DIR="$ROOT_DIR/app/src/main/assets/paddleocr-vl-1.6"
TOOLS_DIR="${TMPDIR:-/tmp}/whocr-hf-tools"
EXPECTED_SHA256="85a479d506a11e724e7285d395c551be69f41dbc16b6342d3cacfb189aed71db"

mkdir -p "$TOOLS_DIR" "$TARGET_DIR"
python3 -m pip install --quiet --target "$TOOLS_DIR" 'huggingface_hub[hf_xet]'

PYTHONPATH="$TOOLS_DIR" HF_HUB_ENABLE_HF_XET=1 HF_XET_HIGH_PERFORMANCE=1 \
python3 - <<PYTHON
from huggingface_hub import snapshot_download
snapshot_download(
    repo_id="PaddlePaddle/PaddleOCR-VL-1.6",
    local_dir="$TARGET_DIR",
    force_download=True,
)
PYTHON

actual="$(sha256sum "$TARGET_DIR/model.safetensors" | awk '{print $1}')"
test "$actual" = "$EXPECTED_SHA256"
printf 'Verified PaddleOCR-VL-1.6: %s\n' "$actual"
