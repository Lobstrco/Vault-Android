package com.lobstr.stellar.vault.presentation.application

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent


/**
 * Tracks the Lifecycle of the whole application thanks to {@link LifecycleObserver}.
 * This is registered via {@link ProcessLifecycleOwner#get()} ()}. The events are designed
 * to be dispatched with delay (by design) so activity rotations (or screens transitions) don't trigger these calls.
 */
class AppLifecycleListener : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // Add action if needed.
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        LVApplication.checkPinAppearance = true
    }
}