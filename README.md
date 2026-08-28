# AndroidShoplist

ad-free shopping list for android to replace Listonic which is full of ads and overly complex.

## opencode initial session

- I will provide you with a bunch of information regarding current project. Don't do anything just remember the information and confirm you understood every time I give you new information. We will continue this information feeding until I tell you. Confirm you understood

- About me: I'm a professional software developer with 15 years of experience in embedded (C, C++) and backend(Go, python) development 

- Project context: I'm using "Listonic" android app on my phone every day; it is a handy shopping list but full of ads, in-app payments and other nuisances. I think this is a great opportunity for me to learn a new niche: android/kotlin development. At this moment I know nothing about android/kotlin development 

- Initially the project should allow creating/renaming/deleting shopping lists. Each list should allow adding/renaming/deleting items. Lists should be stored in phone memory. It should run on samsung galaxy A52 

- This project is a study project - purpose is educational and to replace the shopping application on my phone, not intended to be published to wide audience, so no need for production-readiness

- Now before we start implementing anything, let's prepare ARCHITECTURE.md file that gathers requirements (functional, quality), constraints, conventions, like described in https://github.com/mateuszmidor/ArchStudy/tree/master/Drivers. So now you must read the linked document, and confirm you  understand it

- Now you will ask me all questions relevant to this project based on the web document, one by one, I will answer them, and you will populate ARCHITECTURE.md along the way. Lets go!

`then happened a series of 22 question-answer pingpong`

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