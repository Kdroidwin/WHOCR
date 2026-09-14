# WHOCR: 次セッション用 APK ビルド指示書

以下を新しい Codex セッションにそのまま貼り付ける。

```text
WHOCR の APK をビルドして。作業ディレクトリは
/home/owner/ダウンロード/WHOCR/EasyOCR-main。

必ず以下の既存ローカル環境を使うこと:
- JDK: /home/owner/ダウンロード/CTCLRC-main/.toolchain/jdk-17.0.20.1+1
- Android SDK: /home/owner/ダウンロード/CTCLRC-main/.android-sdk
- Gradle cache: /home/owner/ダウンロード/WHOCR/.gradle-cache
- local.properties には sdk.dir が設定済み。

WHOCR は公式 PP-OCRv6_medium を完全オフラインで使う。
- 検出モデル: ppocr-sdk/src/main/assets/models/det/inference.onnx
- 認識モデル: ppocr-sdk/src/main/assets/models/rec/inference.onnx
- 認識設定: ppocr-sdk/src/main/assets/models/rec/inference.yml
- Android SDKソース: ppocr-sdk/src/main/java/com/paddle/ocr/
全OCRモードで PP-OCRv6_medium を使用する。縦書き日本語モードは内部で
画像を回転し、検出座標を正しく元画像へ戻す。
モデルを削除・置換・実行時ダウンロードしてはならない。

ビルド時は次を実行すること。ネットワークアクセスは Maven 依存取得の
ためだけに使ってよいが、APK の INTERNET / ACCESS_NETWORK_STATE 権限は
絶対に追加しないこと。

env JAVA_HOME=/home/owner/ダウンロード/CTCLRC-main/.toolchain/jdk-17.0.20.1+1 \
PATH=/home/owner/ダウンロード/CTCLRC-main/.toolchain/jdk-17.0.20.1+1/bin:$PATH \
GRADLE_USER_HOME=/home/owner/ダウンロード/WHOCR/.gradle-cache \
./gradlew :app:assembleDebug --no-daemon --console=plain \
-Dorg.gradle.vfs.watch=false -Dorg.gradle.workers.max=1 \
-Dorg.gradle.jvmargs='-Xmx1536m -Dfile.encoding=UTF-8'

失敗時の確認順:
1. Android Build Tools 36 の aapt2 が存在するか確認:
   /home/owner/ダウンロード/CTCLRC-main/.android-sdk/build-tools/36.0.0/aapt2 version
2. 依存関係が未取得なら --offline を付けずに同じビルドコマンドを再実行する。
3. Gradle の一時キャッシュではなく、必ず上記 GRADLE_USER_HOME を使う。
4. Build Tools 36 が壊れている場合だけ、公式 build-tools_r36_linux.zip を
   取得して SDK の build-tools/36.0.0 に展開する。35.0.0 へ固定しない。

成功後は次を実行して、APK の場所、サイズ、SHA-256、権限を報告すること:

APK=app/build/outputs/apk/debug/app-debug.apk
ls -lh "$APK"
sha256sum "$APK"
/home/owner/ダウンロード/CTCLRC-main/.android-sdk/build-tools/36.0.0/aapt dump permissions "$APK"

検査結果に INTERNET または ACCESS_NETWORK_STATE が出たら、APK を渡さず
Manifest を修正して再ビルドすること。
```

## 前提・安全条件

- デバッグ APK の applicationId は `com.whocr.debug`。
- 正式配布用には、リリース署名鍵をユーザーが用意してから
  `assembleRelease` を実行する。デバッグ鍵の APK を配布版と呼ばない。
- Android の既定アシスタント選択は、インストール後に端末設定でユーザーが
  明示的に行う。APK は勝手に既定アシスタントへ変更できない。
- アシスタント由来スクリーンショットはメモリのみで扱い、保存・共有は無効。
- PP-OCRv6_medium 統合版は arm64-v8a 専用で、APKは約236MBになる。
