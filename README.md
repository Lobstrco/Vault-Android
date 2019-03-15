[![LOBSTR-Vault](/images/top-logo.png)](https://play.google.com/store/apps/details?id=com.lobstr.stellar.vault)

## LOBSTR Vault - Multi-signature security on the Stellar network.

This repository contains the source code of the LOBSTR Vault transaction signer app with local key storage for Android mobile platform. 

LOBSTR Vault uses multisignature technology of the Stellar Network and allows you to create a local signer account and verify transactions from multiple Stellar accounts.

## Main features

- Create new signer account on the Stellar Network or recover existing account
- Local key storage - private keys never leave your device
- Recover existing account. Mnemonic (BIP39) support for account backups
- Fully integrated with LOBSTR wallet and StellarTerm.com
- LOBSTR Vault signer account can sign transactions of one or more Stellar accounts
- Install LOBSTR Vault on multiple devices to protect one Stellar account with N of N multisig setup
- View and sign transactions on the Stellar Network
- Import transaction XDRs for signatures
- Receive signature requests with PUSH notifications
- Protect your account with PIN and biometric authentication
- Available for iOS and Android devices

## Developers: Integrate LOBSTR Vault with your service

Are you a representative or a developer of a wallet, exchange or service on the Stellar Network and looking to integrate the support of the LOBSTR Vault signer app with your solution?

To integrate LOBSTR Vault with your service, please follow the simple steps below:

- **Check if a Stellar account is protected by LOBSTR Vault**

To confirm that LOBSTR Vault is used to protect a certain Stellar account, check the account.signers property of Stellar account and look for signer with the following public key:

```
GA2T6GR7VXXXBETTERSAFETHANSORRYXXXPROTECTEDBYLOBSTRVAULT
```

Example of signers property:

```
"signers": [
    {
      "weight": 10,
      "key": "GDXRCYMVIZ76WWEJ23Q5UVB2ICDIVVPWD2STXNHLPOIY5EXHBLO5U2GJ",
      "type": "ed25519_public_key"
    },
    {
      "weight": 1,
      "key": "GA2T6GR7VXXXBETTERSAFETHANSORRYXXXPROTECTEDBYLOBSTRVAULT",
      "type": "ed25519_public_key"
    },
    {
      "weight": 10,
      "key": "GCG5XRQF7KOVT47RNWBHOPP2QJCNRCWDRXQ64SC7T7K5AGC5WHR4B4IK",
      "type": "ed25519_public_key"
    }
  ],
```

- **Make a POST request to https://vault.lobstr.co/api/transactions/**

In order to submit users' signed transactions to LOBSTR Vault for additional signatures, please make a POST request to https://vault.lobstr.co/api/transactions/ with XDR data of transaction sent in the body of request. 

No authentication required.

Please see the cURL example below:

```
curl 'https://vault.lobstr.co/api/transactions/'  -d '{"xdr": "AAAAADHO3Y+T41Lpy8prDeQ5yOs1nQHE5u9IsLeaYnKwA7XkAAAAZAD8XysAAACnAAAAAAAAAAAAAAABAAAAAAAAAAsAAAAAAAAAAQAAAAAAAAAA"}' -H "Content-Type: application/json"

```

## Support

Have any questions or encountered an issue? 

Visit the [LOBSTR Vault Help Center](https://lobstr.zendesk.com/hc/en-us/categories/360001534333-LOBSTR-Vault) for a list of frequently asked questions or [Contact Support](https://lobstr.zendesk.com/hc/en-us/requests/new/) if the issue persists.

## Links

- Visit [LOBSTR Vault web page](https://lobstr.co/vault/)
- Get LOBSTR Vault on [Google Play](https://play.google.com/store/apps/details?id=com.lobstr.stellar.vault)
- Download LOBSTR Vault on the [App Store](https://itunes.apple.com/app/lobstr-vault/id1452248529)
- Sign up in [LOBSTR Wallet](https://lobstr.co/)
- Trade with [StellarTerm](https://stellarterm.com/)
- Follow [LOBSTR wallet on Twitter](https://twitter.com/Lobstrco)

## License

LOBSTR Vault is an open source software and is licensed under the [GNU General Public License v3.0](/LICENSE). Please understand the license carefully before using LOBSTR Vault.
