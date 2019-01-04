package com.lobstr.stellar.vault.presentation.dagger.component

import android.content.Context
import com.lobstr.stellar.vault.presentation.dagger.component.confirm_mnemonics.ConfirmMnemonicsComponent
import com.lobstr.stellar.vault.presentation.dagger.component.dashboard.DashboardComponent
import com.lobstr.stellar.vault.presentation.dagger.component.fcm.FcmInternalComponent
import com.lobstr.stellar.vault.presentation.dagger.component.fcm.FcmServiceComponent
import com.lobstr.stellar.vault.presentation.dagger.component.mnemonics.MnemonicsComponent
import com.lobstr.stellar.vault.presentation.dagger.component.operation_details.OperationDetailsComponent
import com.lobstr.stellar.vault.presentation.dagger.component.pin.PinComponent
import com.lobstr.stellar.vault.presentation.dagger.component.recovery_key.RecoveryKeyComponent
import com.lobstr.stellar.vault.presentation.dagger.component.settings.SettingsComponent
import com.lobstr.stellar.vault.presentation.dagger.component.splash.SplashComponent
import com.lobstr.stellar.vault.presentation.dagger.component.transaction.TransactionComponent
import com.lobstr.stellar.vault.presentation.dagger.component.transaction_details.TransactionDetailsComponent
import com.lobstr.stellar.vault.presentation.dagger.component.vault_auth.VaultAuthComponent
import com.lobstr.stellar.vault.presentation.dagger.module.ApiModule
import com.lobstr.stellar.vault.presentation.dagger.module.AppModule
import com.lobstr.stellar.vault.presentation.dagger.module.RepositoryModule
import com.lobstr.stellar.vault.presentation.dagger.module.confirm_mnemonics.ConfirmMnemonicsModule
import com.lobstr.stellar.vault.presentation.dagger.module.dashboard.DashboardModule
import com.lobstr.stellar.vault.presentation.dagger.module.fcm.FcmInternalModule
import com.lobstr.stellar.vault.presentation.dagger.module.fcm.FcmServiceModule
import com.lobstr.stellar.vault.presentation.dagger.module.mnemonics.MnemonicsModule
import com.lobstr.stellar.vault.presentation.dagger.module.operation_details.OperationDetailsModule
import com.lobstr.stellar.vault.presentation.dagger.module.pin.PinModule
import com.lobstr.stellar.vault.presentation.dagger.module.recovery_key.RecoveryKeyModule
import com.lobstr.stellar.vault.presentation.dagger.module.settings.SettingsModule
import com.lobstr.stellar.vault.presentation.dagger.module.splash.SplashModule
import com.lobstr.stellar.vault.presentation.dagger.module.transaction.TransactionModule
import com.lobstr.stellar.vault.presentation.dagger.module.transaction_details.TransactionDetailsModule
import com.lobstr.stellar.vault.presentation.dagger.module.vault_auth.VaultAuthModule
import com.lobstr.stellar.vault.presentation.util.manager.network.NetworkWorker
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, RepositoryModule::class])
interface AppComponent {

    val context: Context

    fun plusSplashComponent(module: SplashModule): SplashComponent

    fun plusRecoveryKeyComponent(module: RecoveryKeyModule): RecoveryKeyComponent

    fun plusMnemonicsComponent(module: MnemonicsModule): MnemonicsComponent

    fun plusConfirmMnemonicsComponent(module: ConfirmMnemonicsModule): ConfirmMnemonicsComponent

    fun plusPinComponent(module: PinModule): PinComponent

    fun plusVaultAuthComponent(module: VaultAuthModule): VaultAuthComponent

    fun plusSettingsComponent(module: SettingsModule): SettingsComponent

    fun plusFcmServiceComponent(module: FcmServiceModule): FcmServiceComponent

    fun plusFcmInternalComponent(module: FcmInternalModule): FcmInternalComponent

    fun plusTransactionDetailsComponent(module: TransactionDetailsModule): TransactionDetailsComponent

    fun plusTransactionComponent(module: TransactionModule): TransactionComponent

    fun plusDashboardComponent(module: DashboardModule): DashboardComponent

    fun plusOperationDetailsModule(module: OperationDetailsModule): OperationDetailsComponent

    fun inject(networkWorker: NetworkWorker)
}