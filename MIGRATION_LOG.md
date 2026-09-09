# Graze17 Migration Log

## Scope
- Baseline reference: commit `75f0859a6197d06acf3b8bedb69cc31bb4fd40c1`
- Goal: Android 17 (API 37) readiness and stability
- Rename plan in progress: phase 1 (label/branding) and phase 2 (internal package/class names)
- Application identity migration approved: `applicationId` is now `com.graze17`; no Play Store upgrade path currently needs to be preserved.

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
| M08 | Rename Phase 2 (Start) | Renamed internal Java/manifest/resource package references from `com.graze17` to `com.graze17`; set namespace to `com.graze17` | High | Build success | Started and compiling |
| M09 | Tooling Workaround | Local SDK alias created from `platforms/android-37.0` to `platforms/android-37` for AGP 8.13 compatibility | Medium | API 37 build now resolves and succeeds | Applied locally |
| M10 | Dashboard Stability | Removed regression-prone dashboard reinitialization/click interception debug flow, restored stable list click path, and added defensive cursor bounds/null checks | Medium | Build success; runtime navigation validation pending on Android 17 | Done (code), runtime check pending |
| M11 | Theme Resource Resolution | Replaced hardcoded `com.graze17` package in dynamic style/drawable lookups with runtime package (`Context.getPackageName()`) to keep custom theme attrs resolvable while `applicationId` remains `com.graze17` | High | `./gradlew :app:assembleDebug -x lint` success; dashboard cold-start rerun pending | Done (code), runtime check pending |
| M12 | Sync Progress UX Stability | Added per-sync manual-hide state for the in-progress panel so tapping sync during active synchronization reliably toggles panel visibility without auto re-show from background status refreshes | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime tap-toggle validation pending | Done (code), runtime check pending |
| M13 | Dashboard Progress/Empty-State Cleanup | Prevented centered fallback sync spinner when inline progress panel is present, and bound list empty view (`@android:id/empty`) so "No articles yet" only appears when adapter is empty | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime dashboard validation pending | Done (code), runtime check pending |
| M14 | Toolbar Sync Icon State | Added active/idle toolbar sync icon switching (`ic_popup_sync` while syncing, default icon when idle) and menu invalidation on status refresh so users can see sync state while keeping tap-to-toggle behavior | Low | `./gradlew :app:assembleDebug -x lint` success; runtime visual validation pending | Done (code), runtime check pending |
| M15 | Toolbar Sync Icon Visibility | Replaced near-identical system active icon with custom `ic_sync_active_32dp` (sync glyph plus busy badge) and applied immediate icon switch on sync start for clearer visual feedback | Low | `./gradlew :app:assembleDebug -x lint` success; runtime visual validation pending | Done (code), runtime check pending |
| M16 | Toolbar Sync Spinner Restoration | Restored an actual animated spinner in the sync action slot via custom menu action view using `progress_small_white`, with idle icon fallback and preserved tap behavior | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime visual parity check pending | Done (code), runtime check pending |
| M17 | Dashboard Navigation Regression Recovery | Restored `ListView` item click wiring in dashboard initialization so tapping labels (e.g., "all articles", "my recently starred") again opens feed/article lists after AppCompat migration | High | `./gradlew :app:assembleDebug -x lint` success; runtime tap-through validation pending | Done (code), runtime check pending |
| M18 | Sync Action State Reliability | Added explicit UI sync-state tracking and forced sync menu action-view inflation at runtime to avoid stale/static toolbar sync visuals when backend state updates lag or action layout inflation is skipped | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime spinner visibility validation pending | Done (code), runtime check pending |
| M19 | Sync Icon Reset + Locale Date Format | Ensured menu invalidation still runs in menu-only mode so sync icon/spinner reverts after completion; added stale-sync-state auto-clear and switched article timestamp rendering to Android locale/time-format APIs (`getDateFormat` + `getTimeFormat`) | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime sync-reset/date-display validation pending | Done (code), runtime check pending |
| M20 | Sync Completion Count Accuracy | Extended sync result payload to include fetched/new article count and changed completion toast to report `X new articles downloaded` rather than total state updates | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime toast validation pending | Done (code), runtime check pending |
| M21 | Repeated Download Loop Mitigation | Marked permanent missing-content download failures (`FileNotFoundException`) as `STATE_DOWNLOAD_ERROR` instead of `STATE_NOT_DOWNLOADED` to stop endless requeue of the same old items each sync | Medium | `./gradlew :app:assembleDebug -x lint` success; runtime repeated-download validation pending | Done (code), runtime check pending |
| M22 | Automatic Sync Download Churn Guard | Skipped the article-download phase during automatic sync runs when zero new entries were fetched in that run, preventing repeated “new articles downloading” churn on unchanged timelines while preserving manual-sync retries | Medium | `./gradlew assembleDebug -x lint` success; runtime validation pending | Done (code), runtime check pending |
| M23 | Theme-Aware Article Header Drawable | Replaced non-themed drawable loads in article header rendering with `ContextCompat.getDrawable(...)` to resolve theme-attribute warnings and improve Android 17 compatibility | Low | `./gradlew assembleDebug -x lint` success; runtime warning check pending | Done (code), runtime check pending |
| M24 | Starred Retention Fix | Protected starred entries from automatic capacity cleanup so they are not deleted and re-downloaded on each sync cycle; this preserves the “my recently starred” list and prevents repeated churn | High | `./gradlew assembleDebug -x lint` success; runtime starred-retention validation pending on Android 17 | Done (code), runtime check pending |
| M24 | Starred Retention Fix | Protected starred entries from automatic capacity-pruning so they are not deleted and re-downloaded on each sync cycle; this preserves the “recently starred” list without repeated churn | High | `./gradlew assembleDebug -x lint` success; runtime starred-retention check pending on Android 17 | Done (code), runtime check pending |
| M25 | Preference Store Alignment | Unified SettingsActivity, EntryManager, and SyncInterfaceFactory on `com.graze17_preferences`; migrated missing values from the package-default store so capacity and sort preferences control sync and article ordering | High | `./gradlew :app:compileDebugJavaWithJavac` success; device preference migration check pending | Done (code), runtime validation pending |
| M26 | Application Identity Alignment | Changed the Gradle application ID from `com.graze16` to `com.graze17` to match the namespace, packages, manifest, and branding after confirming there is no Play Store upgrade path to preserve | High | `./gradlew :app:assembleDebug -x lint` pending; fresh-install validation pending | Done (code), runtime validation pending |

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
