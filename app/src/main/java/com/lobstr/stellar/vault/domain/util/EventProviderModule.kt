package com.lobstr.stellar.vault.domain.util

import com.lobstr.stellar.vault.domain.util.event.Auth
import com.lobstr.stellar.vault.domain.util.event.Network
import com.lobstr.stellar.vault.domain.util.event.Notification
import com.lobstr.stellar.vault.domain.util.event.Update
import com.lobstr.stellar.vault.presentation.entities.account.AppVersion
import io.reactivex.rxjava3.subjects.PublishSubject

class EventProviderModule {

    val notificationEventSubject: PublishSubject<Notification> = PublishSubject.create()

    val networkEventSubject: PublishSubject<Network> = PublishSubject.create()

    val updateEventSubject: PublishSubject<Update> = PublishSubject.create()

    val authEventSubject: PublishSubject<Auth> = PublishSubject.create()

    val appVersionUpdateSubject: PublishSubject<AppVersion?> = PublishSubject.create()
}