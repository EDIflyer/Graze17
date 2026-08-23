# Graze17 Migration Log

## Scope
- Baseline reference: commit `75f0859a6197d06acf3b8bedb69cc31bb4fd40c1`
- Goal: Android 17 (API 37) readiness and stability
- Rename plan in progress: phase 1 (label/branding) and phase 2 (internal package/class names)
- Explicitly unchanged: `applicationId` remains `com.graze16`

## Changes

| ID | Area | Change | Risk | Verification | Status |
|---|---|---|---|---|---|
| M01 | SDK Level | Set `compileSdk=37` and `targetSdkVersion=37` | Medium | `./gradlew :app:assembleDebug -x lint` success | Done |
| M02 | Android 17 Service Start Limits | Switched sync service launch call sites to foreground-safe startup (`startForegroundService` on API 26+) | High | Build success; runtime behavior still needs device validation | Done (code), runtime check pending |
| M03 | Foreground Service Compliance | Added foreground notification channel + `onStartCommand` + foreground enter/exit in sync service | High | Build success; runtime verification pending on Android 17 | Done (code), runtime check pending |
| M04 | Manifest Service Policy | Added `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_DATA_SYNC`, `foregroundServiceType=dataSync`, explicit `exported=false` on services | Medium | Build success | Done |
| M05 | Notification Runtime Permission | Added POST_NOTIFICATIONS runtime request in dashboard activity for API 33+ | Medium | Build success; user permission-flow test pending | Done (code), runtime check pending |
| M06 | Widget Update Path | Avoided service start from widget update callback; update widgets directly | Medium | Build success; widget refresh behavior test pending | Done (code), runtime check pending |
| M07 | Rename Phase 1 | Updated launcher-visible app label strings to Graze17 and widget label branding | Low | Build success | Done |
| M08 | Rename Phase 2 (Start) | Renamed internal Java/manifest/resource package references from `com.graze16` to `com.graze17`; set namespace to `com.graze17` | High | Build success | Started and compiling |
| M09 | Tooling Workaround | Local SDK alias created from `platforms/android-37.0` to `platforms/android-37` for AGP 8.13 compatibility | Medium | API 37 build now resolves and succeeds | Applied locally |
| M10 | Dashboard Stability | Removed regression-prone dashboard reinitialization/click interception debug flow, restored stable list click path, and added defensive cursor bounds/null checks | Medium | Build success; runtime navigation validation pending on Android 17 | Done (code), runtime check pending |

## Open Verification Matrix

| Check | Environment | Result |
|---|---|---|
| Manual sync trigger starts and completes | Android 17 emulator | Pending |
| Scheduled/background sync trigger | Android 17 emulator | Pending |
| Notification display and tap actions | Android 17 emulator | Pending |
| Notification permission grant/deny behavior | Android 17 emulator + physical | Pending |
| Dashboard navigation stability | Android 17 physical | Pending |
| Widget update flow | Android 17 physical | Pending |

## Notes
- AGP warns that 8.13.0 is tested up to compile SDK 36.1. Build currently succeeds on 37 with a local SDK directory workaround.
- A future cleanup should remove the SDK alias once AGP/SDK toolchain fully supports API 37 naming conventions in this environment.
- Commit splitting plan prepared in `RENAME_SPLIT_PLAN.md` for non-interactive, low-risk reviewable commits.
