# Phase 2 Rename Split Plan (Low-Risk)

This plan breaks the current broad rename change-set into reviewable commits without using interactive git.

## Goal
- Set `applicationId` to `com.graze17`; the identity migration was approved because the app is not currently distributed through the Play Store.
- Keep internal namespace/package migration to `com.graze17`.
- Isolate risk by area so each commit can be validated independently.

## Pre-Check
```bash
git status --short
git diff --stat
```

## Commit Buckets

### 1) Build and Manifest policy updates
Files:
- app/build.gradle
- app/src/main/AndroidManifest.xml
- app/src/main/res/values/strings.xml

Commands:
```bash
git add app/build.gradle app/src/main/AndroidManifest.xml app/src/main/res/values/strings.xml
./gradlew :app:assembleDebug -x lint
git commit -m "Android 17: API 37 config, foreground service policy, and Graze17 branding"
```

### 2) Sync/notification runtime behavior hardening
Files:
- app/src/main/java/com/grazeten/SynchronizationService.java
- app/src/main/java/com/grazeten/WakeupAndSynchronizeReceiver.java
- app/src/main/java/com/grazeten/EntryManager.java
- app/src/main/java/com/grazeten/appwidget/UnreadWidgetProvider.java

Commands:
```bash
git add app/src/main/java/com/grazeten/SynchronizationService.java \
        app/src/main/java/com/grazeten/WakeupAndSynchronizeReceiver.java \
        app/src/main/java/com/grazeten/EntryManager.java \
        app/src/main/java/com/grazeten/appwidget/UnreadWidgetProvider.java
./gradlew :app:assembleDebug -x lint
git commit -m "Android 17: harden sync startup, foreground execution, and widget update path"
```

### 3) Dashboard/navigation crash-hardening
Files:
- app/src/main/java/com/grazeten/DashboardListActivity.java

Commands:
```bash
git add app/src/main/java/com/grazeten/DashboardListActivity.java
./gradlew :app:assembleDebug -x lint
git commit -m "Dashboard: restore stable list navigation flow and add defensive cursor handling"
```

### 4) Internal package/class rename in Java sources
Files:
- app/src/main/java/**

Commands:
```bash
git add app/src/main/java
./gradlew :app:assembleDebug -x lint
git commit -m "Rename internals: migrate Java packages/imports to com.graze17"
```

### 5) Resource XML rename alignment
Files:
- app/src/main/res/**

Commands:
```bash
git add app/src/main/res
./gradlew :app:assembleDebug -x lint
git commit -m "Rename internals: align resource XML references with com.graze17"
```

### 6) Documentation
Files:
- MIGRATION_LOG.md
- RENAME_SPLIT_PLAN.md
- .github/agents/graze17-modernizer.agent.md

Commands:
```bash
git add MIGRATION_LOG.md RENAME_SPLIT_PLAN.md .github/agents/graze17-modernizer.agent.md
git commit -m "Docs: migration log, split strategy, and modernization agent"
```

## Final Check
```bash
git status --short
git log --oneline -n 10
```

## Safety Notes
- Existing `com.graze16` installations must be uninstalled before installing the `com.graze17` package.
- If one bucket fails at runtime, revert only that commit later instead of rolling back everything.
