# Hadramout Exchange Platform — Android

Native Kotlin Android app translated from the provided Flutter entry point.

## Included

- Arabic/Yemen-first interface with RTL layout
- Home balance overview
- Deposit request flow
- Withdrawal request flow
- Persistent local transaction history
- Admin dashboard summary
- Supabase REST boundary with explicit demo mode

## Supabase configuration

The first release intentionally uses:

- `https://placeholder.supabase.co`
- `demo-anon-key-not-for-production`

These values keep the app safe and runnable without project credentials. Before production, replace the two `buildConfigField` values in `app/build.gradle.kts` through a secure build configuration. Never commit a real Supabase key to source control.

## Build

```bash
gradle :app:assembleRelease
```

The APK is generated at `app/build/outputs/apk/release/app-release.apk`.