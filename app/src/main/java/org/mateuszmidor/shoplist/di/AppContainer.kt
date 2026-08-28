package org.mateuszmidor.shoplist.di

/**
 * Manual dependency injection container.
 *
 * Owns the long-lived dependencies of the app. Currently empty; the Room
 * database and repositories will be added here by the 01b data foundation
 * change. A single instance is created in [org.mateuszmidor.shoplist.ShopListApp]
 * and shared across the app.
 */
class AppContainer
