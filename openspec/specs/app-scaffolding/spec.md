# app-scaffolding Spec

## Purpose

Scaffold the ShopList Android application so that it launches on a compatible device and presents a placeholder screen. This establishes the initial app skeleton and verifies the application identity.

## Requirements

### Requirement: App launches to placeholder screen

The ShopList app SHALL launch on a compatible device and render the placeholder
screen without crashing.

#### Scenario: Launch app on compatible device

- **GIVEN** a compatible device (Android 11+ / API 30+)
- **WHEN** the user launches the app
- **THEN** the app starts and shows the placeholder screen
- **AND** the app does not crash

#### Scenario: App identity uses real package

- **GIVEN** the app is built
- **WHEN** the application package is inspected
- **THEN** the package/applicationId is `org.mateuszmidor.shoplist`
