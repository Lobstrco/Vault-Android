package com.lobstr.stellar.vault.domain.util

import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import io.reactivex.subjects.PublishSubject

class EventProviderModule {

    val notificationEventSubject: PublishSubject<Notification> = PublishSubject.create()

    val networkEventSubject: PublishSubject<Network> = PublishSubject.create()
}