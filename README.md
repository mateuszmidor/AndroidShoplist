# AndroidShoplist

ad-free shopping list for android to replace Listonic which is full of ads and overly complex.

## Tooling setup (Arch Linux)

Install the Android toolchain once:

```sh
# adb/fastboot + udev rules so the phone works over USB without root
sudo pacman -S android-tools android-udev

# add yourself to the adbusers group that android-udev created,
# then log out and back in (or run: newgrp adbusers)
sudo usermod -aG adbusers $USER

# Android Studio - the official IDE (AUR; also: paru -S android-studio)
pamac build android-studio
```

Notes:

- `android-studio` ships its own JDK and bundles the Android SDK Manager; on first launch it offers to download the SDK components the project needs.
- Pacman's regular repos only have the command-line tools (`android-tools`); the IDE itself comes from the AUR.
- If you later want the on-screen emulator, enable the `multilib` repo — the SDK's 32-bit tooling and emulator depend on `lib32-*` packages.

## Running the appp in emulator

Pixel 5a and 6a can be used

## Deploying the app to the phone (Samsung Galaxy A52)

One-time phone setup:

1. Settings → About phone → tap "Build number" 7 times → Developer mode is enabled.
2. Settings → Developer options → enable **USB debugging**.
3. Connect the phone with a USB cable. In the phone's USB notification choose **"File transfer" (MTP)** and accept the "Allow USB debugging?" RSA fingerprint prompt on the screen.

From the terminal (requires the Gradle wrapper, present after scaffolding):

```sh
adb devices            # the A52 must show as "device", not "unauthorized" or empty
./gradlew installDebug # builds the debug APK, installs it and it's ready to launch
adb shell am start -n <applicationId>/.MainActivity   # or just launch it from the launcher
```

Alternatively, from Android Studio: open the project, pick the A52 from the device dropdown at the top, and click **Run (▶)**. Gradle builds, installs, and launches the app for you.

Troubleshooting:

- `adb devices` reports `unauthorized` → unlock the phone and tap "Allow" on the RSA prompt.
- The phone does not appear at all → replug the cable; run `adb kill-server && adb start-server`; make sure the USB mode is "File transfer" (Samsung won't expose adb in "Charging only"); verify `groups` includes `adbusers` (re-login after adding the group).
- Wireless alternative (A52 runs Android 11+): Settings → Developer options → **Wireless debugging** → `adb pair <ip-and-port>` then `adb connect <ip-and-port>`; no cable needed after pairing.
