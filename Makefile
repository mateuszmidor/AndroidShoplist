SHELL := /bin/bash

# Gradle wrapper is the source of truth for all build/test tasks.
GRADLE := ./gradlew

# Location of your Android SDK (used only for adb, which lives in platform-tools).
ADB := $(HOME)/Android/Sdk/platform-tools/adb
# Fall back to adb on PATH if the SDK path above is not present.
ifeq ($(shell test -x $(ADB) && echo yes),)
  ADB := adb
endif

.PHONY: help build assembleDebug test unitTest connectedTest devices install installDebug installRelease run clean

# Show help — the default target.
help:
	@echo "Usage:"
	@echo "  make help            Show this help"
	@echo "  make build           Assemble a debug APK"
	@echo "  make assembleDebug   Same as build"
	@echo "  make test            Run local (JVM) unit tests"
	@echo "  make unitTest        Same as test"
	@echo "  make connectedTest   Run instrumented tests on a connected device/emulator"
	@echo "  make devices         List connected devices/emulators"
	@echo "  make install         Build and install the debug APK on a connected device"
	@echo "  make installDebug    Same as install"
	@echo "  make installRelease  Build and install the release APK on a connected device"
	@echo "  make run             Install debug APK and launch the app"
	@echo "  make clean           Clean Gradle build outputs"

# Build a debug APK. This is the fastest full build.
build assembleDebug:
	$(GRADLE) assembleDebug

# Local (JVM) unit tests under app/src/test.
test unitTest:
	$(GRADLE) testDebugUnitTest

# Instrumented tests under app/src/androidTest (requires connected device/emulator).
connectedTest:
	$(GRADLE) connectedDebugAndroidTest

# List connected devices/emulators (phones, tablets, emulators).
devices:
	$(ADB) devices -l

# Build and install the debug APK on the first connected device.
install installDebug:
	$(ADB) devices -l | grep -qE 'device (usb|product|emulator|wifi|tcpip|transport)' || { echo "No device connected"; exit 1; }
	$(GRADLE) installDebug

# Build and install the release APK on the first connected device.
installRelease:
	$(ADB) devices -l | grep -qE 'device (usb|product|emulator|wifi|tcpip|transport)' || { echo "No device connected"; exit 1; }
	$(GRADLE) installRelease

# Install the debug APK and launch the app's main activity.
run: install
	$(ADB) shell am start -n org.mateuszmidor.shoplist/.MainActivity

# Clean all build artifacts.
clean:
	$(GRADLE) clean
