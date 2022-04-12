package com.lobstr.stellar.vault.domain.home

interface HomeInteractor {

    fun checkFcmRegistration()

    fun getRateUsState(): Int
}