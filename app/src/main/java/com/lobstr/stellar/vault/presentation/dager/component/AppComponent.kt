package com.lobstr.stellar.vault.presentation.dager.component

import android.content.Context
import com.lobstr.stellar.vault.presentation.dager.component.confirm_mnemonics.ConfirmMnemonicsComponent
import com.lobstr.stellar.vault.presentation.dager.component.dashboard.DashboardComponent
import com.lobstr.stellar.vault.presentation.dager.component.fcm.FcmInternalComponent
import com.lobstr.stellar.vault.presentation.dager.component.fcm.FcmServiceComponent
import com.lobstr.stellar.vault.presentation.dager.component.pin.PinComponent
import com.lobstr.stellar.vault.presentation.dager.component.recovery_key.RecoveryKeyComponent
import com.lobstr.stellar.vault.presentation.dager.component.settings.SettingsComponent
import com.lobstr.stellar.vault.presentation.dager.component.splash.SplashComponent
import com.lobstr.stellar.vault.presentation.dager.component.transaction.TransactionComponent
import com.lobstr.stellar.vault.presentation.dager.component.transaction_details.TransactionDetailsComponent
import com.lobstr.stellar.vault.presentation.dager.component.vault_auth.VaultAuthComponent
import com.lobstr.stellar.vault.presentation.dager.module.ApiModule
import com.lobstr.stellar.vault.presentation.dager.module.AppModule
import com.lobstr.stellar.vault.presentation.dager.module.RepositoryModule
import com.lobstr.stellar.vault.presentation.dager.module.confirm_mnemonics.ConfirmMnemonicsModule
import com.lobstr.stellar.vault.presentation.dager.module.dashboard.DashboardModule
import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.dager.module.fcm.FcmServiceModule
import com.lobstr.stellar.vault.presentation.dager.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dager.module.recovery_key.RecoveryKeyModule
import com.lobstr.stellar.vault.presentation.dager.module.settings.SettingsModule
import com.lobstr.stellar.vault.presentation.dager.module.splash.SplashModule
import com.lobstr.stellar.vault.presentation.dager.module.transaction.TransactionModule
import com.lobstr.stellar.vault.presentation.dager.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.dager.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.util.manager.network.NetworkWorker
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, RepositoryModule::class])
interface AppComponent {

    val context: Context

    fun plusSplashComponent(module: SplashModule): SplashComponent

    fun plusRecoveryKeyComponent(module: RecoveryKeyModule): RecoveryKeyComponent

    fun plusConfirmMnemonicsComponent(module: ConfirmMnemonicsModule): ConfirmMnemonicsComponent

    fun plusPinComponent(module: PinModule): PinComponent

    fun plusVaultAuthComponent(module: VaultAuthModule): VaultAuthComponent

    fun plusSettingsComponent(module: SettingsModule): SettingsComponent

    fun plusFcmServiceComponent(module: FcmServiceModule): FcmServiceComponent

    fun plusFcmInternalComponent(module: FcmInternalModule): FcmInternalComponent

    fun plusTransactionDetailsComponent(module: TransactionDetailsModule): TransactionDetailsComponent

    fun plusTransactionComponent(module: TransactionModule): TransactionComponent

    fun plusDashboardComponent(module: DashboardModule): DashboardComponent

    fun inject(networkWorker: NetworkWorker)
}