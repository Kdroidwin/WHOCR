# WHOCR

WHOCR is an offline Android OCR app for Japanese and English. It can open an
image from Android's screenshot editor or, after the user selects it in system
settings, act as the default assistant and receive Android's temporary
assistant screenshot.

## Privacy and security

- The merged manifest explicitly removes `INTERNET` and `ACCESS_NETWORK_STATE`.
  No OCR request, analytics, upload, update check, or search is implemented.
- PP-OCRv6_medium detection and 50-language recognition ONNX models are
  packaged with the APK and run locally through ONNX Runtime. This includes
  Japanese and English; no model is fetched after installation.
- Assistant screenshots only travel through a one-shot in-memory reference. They
  are never put in MediaStore, a cache directory, a database, an intent URI, or
  logs. The handoff reference is cleared as soon as the editor consumes it.
- Screens are protected with `FLAG_SECURE`, preventing other apps from capturing
  the OCR view. Backups and device-transfer exclude all app data.
- Save and share are explicit user actions. Ordinary images chosen by the user
  remain under the user's control; only assistant captures have the no-disk
  guarantee.

## Default assistant setup

1. Install WHOCR.
2. In WHOCR, use the top-right settings button, then select WHOCR as the device
   assistant. The exact Android settings screen differs by device.
3. Invoke the assistant gesture/button. Android supplies a one-time screenshot
   to WHOCR, which runs Japanese + English OCR locally.

Android and device policy control whether a screenshot can be supplied (for
example, protected apps may return no image). WHOCR does not bypass those
protections.

## AMOLED black

The app, bottom control bar, bottom sheets, and settings entry point all use an
AMOLED-black Material color scheme (`#000000`) with high-contrast text.

## PP-OCRv6_medium

WHOCR uses the official `PP-OCRv6_medium_det_onnx` and
`PP-OCRv6_medium_rec_onnx` models. Their assets are located in
`ppocr-sdk/src/main/assets/models/`; the project contains the corresponding
official Android SDK source under `ppocr-sdk/`. The arm64-v8a APK includes only
the device ABI, ONNX Runtime, OpenCV, and both model files.

All Japanese/English modes use PP-OCRv6_medium. The dedicated vertical Japanese
mode rotates the input internally and maps recognition locations back onto the
original image.

## Build

Requires Android SDK Platform 36 and JDK 17:

```bash
./gradlew test assembleDebug
```

### GitHub 配布用リリース APK

このリポジトリには署名鍵・パスワードを含めていません。自分の署名鍵を用意したうえで、次の環境変数を指定してビルドしてください。

```bash
export WHOCR_RELEASE_STORE_FILE=/absolute/path/to/your-release-key.p12
export WHOCR_RELEASE_STORE_PASSWORD='your-store-password'
export WHOCR_RELEASE_KEY_ALIAS='your-key-alias'
export WHOCR_RELEASE_KEY_PASSWORD='your-key-password'
./gradlew assembleRelease
```

出力先は `app/build/outputs/apk/release/app-release.apk` です。モデルは
`ppocr-sdk/src/main/assets/models/` に同梱され、OCR 実行時にネットワークを使用しません。
