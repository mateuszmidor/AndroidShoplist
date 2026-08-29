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

## Running the app in android studio emulator

Pixel 5a and 6a can be used.

## Deploying the app to the phone (Samsung Galaxy A52)

One-time phone setup:

1. Settings → About phone → tap "Build number" 7 times → Developer mode is enabled.
2. Settings → Developer options → enable **USB debugging**.
3. Connect the phone with a USB cable. In the phone's USB notification choose **"File transfer" (MTP)** and accept the "Allow USB debugging?" RSA fingerprint prompt on the screen.

### Deploy from the terminal (requires the Gradle wrapper, present after scaffolding)

```sh
make install   # builds and installs the debug APK; run 'make run' to install and launch
```

### Deploy from Android Studio

From Android Studio: open the project, pick the A52 from the device dropdown at the top, and click **Run (▶)**. Gradle builds, installs, and launches the app for you.

## Handy commands via Makefile

The root `Makefile` wraps the most common Gradle/adb tasks. Run `make help` for a full list.

```sh
make help             # list all targets
make build            # assemble the debug APK (alias: assembleDebug)
make test             # run local (JVM) unit tests (alias: unitTest)
make connectedTest    # run instrumented tests on a connected device/emulator
make devices          # list connected phones/emulators
make install          # build + install the debug APK on a connected device (alias: installDebug)
make installRelease   # build + install the release APK
make run              # install debug APK and launch the app
make clean            # clean Gradle build outputs
```

The `install*`/`run` targets error out with "No device connected" when no authorized device is detected, so run `make devices` first if in doubt.

## Troubleshooting

- `adb devices` reports `unauthorized` → unlock the phone and tap "Allow" on the RSA prompt.
- The phone does not appear at all → replug the cable; run `adb kill-server && adb start-server`; make sure the USB mode is "File transfer" (Samsung won't expose adb in "Charging only"); verify `groups` includes `adbusers` (re-login after adding the group).
- Wireless alternative (A52 runs Android 11+): Settings → Developer options → **Wireless debugging** → `adb pair <ip-and-port>` then `adb connect <ip-and-port>`; no cable needed after pairing.
