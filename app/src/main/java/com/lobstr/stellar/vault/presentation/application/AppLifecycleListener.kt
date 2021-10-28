package com.lobstr.stellar.vault.presentation.application

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * Tracks the Lifecycle of the whole application thanks to {@link LifecycleObserver}.
 * This is registered via {@link ProcessLifecycleOwner#get()} ()}. The events are designed
 * to be dispatched with delay (by design) so activity rotations (or screens transitions) don't trigger these calls.
 */
class AppLifecycleListener : DefaultLifecycleObserver {

    // Move To Foreground.
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    // Move To Background.
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        LVApplication.checkPinAppearance = true
    }
}