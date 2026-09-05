# 세턴쿠지 V9 APK 빌드 안내

## 가장 쉬운 방법: Android Studio
1. PC에 Android Studio 설치
2. 이 폴더를 Open
3. SDK Platform 35 설치
4. Build > Build APK(s)
5. `app/build/outputs/apk/debug/app-debug.apk` 생성

## 휴대폰만 있는 경우: GitHub Actions
1. GitHub 앱/웹에서 새 Repository 생성
2. 이 ZIP을 압축 해제한 뒤 전체 파일 업로드
3. Actions 탭에서 `Build SaturnKuji V9 APK` 실행
4. 완료 후 Artifacts에서 `SaturnKuji_V9_Debug_APK` 다운로드
5. ZIP 압축 해제 → `app-debug.apk` 설치

## Gradle Wrapper
`gradle-wrapper.properties`, `gradlew`, `gradlew.bat`가 포함되어 있습니다.
현재 배포 환경에서는 공식 `gradle-wrapper.jar` 바이너리를 외부에서 내려받을 수 없어 포함하지 못했습니다. Android Studio에서 프로젝트를 열면 Gradle을 동기화할 수 있으며, PC 터미널에서는 `gradle wrapper --gradle-version 8.9`로 jar까지 생성할 수 있습니다.
