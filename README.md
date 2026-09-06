# Toronto Lickalotapus LogoBot (Android prototype)

This is the first functional stage of the mobile-only MLB The Show 26 logo automation plan.

## What this build does now

- Runs as an Android AccessibilityService capable of dispatching tap/swipe gestures.
- Shows a transparent accessibility overlay over PS Remote Play for one-time controller calibration.
- Records the centers of X, Circle, Square, Triangle, D-pad, shoulders/triggers, and both virtual sticks.
- Performs a deliberately safe end-to-end test: **Triangle → wait → Triangle**, which should toggle Fine Control on and back off in the MLB The Show 26 logo editor.
- Stores the 30-layer logical Toronto Lickalotapus v1 design recipe.

## Why this stage matters

PS Remote Play protects its video surface from Android screenshots. LogoBot does not need to capture that surface. Calibration records the locations of the visible virtual controls by asking the user to tap each location through a transparent accessibility overlay. After calibration, `dispatchGesture()` can send those controller touches to Remote Play.

## First run

1. Install the debug APK.
2. Open **LogoBot**.
3. Tap **Enable LogoBot Accessibility Service** and enable it in Android Settings.
4. Open PS Remote Play, connect to the PS5, open MLB The Show 26's Logo Editor and make the on-screen controller visible.
5. Return to LogoBot and tap **Start calibration in 7 seconds**.
6. Switch straight back to Remote Play before the seven seconds expire.
7. When the overlay appears, tap the center of each controller control requested at the top of the screen.
8. Return to LogoBot. Calibration should say COMPLETE.
9. Open Remote Play on the Logo Editor again.
10. From LogoBot tap **Safe test in 5 seconds**, return to Remote Play, and watch Triangle toggle Fine Control on and off.

The test is intentionally non-destructive. Do **not** run automated logo-building commands until this test works consistently.

## Next stage

The next stage adds a small command palette and maps MLB The Show 26's exact **Menu → shape selection → stamp → fine-control → move/scale/rotate** navigation. Once those menu routes are confirmed, the 30 logical layers in `LickalotapusRecipe.kt` can be translated into an executable recipe.

## Build APK with GitHub Actions

The included `.github/workflows/build-apk.yml` can build a debug APK entirely in GitHub Actions, which is useful when working only from a phone. Push this project to a GitHub repository and run **Build LogoBot APK** from the Actions tab. The resulting `LogoBot-debug-apk` artifact contains `app-debug.apk`.

## Safety / controls

This prototype only sends controller touches after an explicit user command and delay. It does not read Remote Play video, credentials, messages, or other app contents. Disable the LogoBot Accessibility Service when you are finished using it.
