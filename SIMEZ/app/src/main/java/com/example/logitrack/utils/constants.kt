package com.example.logitrack.utils

object Constants {

    // Rols
    const val ROL_ADMIN  = 22
    const val ROL_AGENT  = 23
    const val ROL_CLIENT = 24

    // Estats ofertes
    const val ESTAT_PENDENT    = 11
    const val ESTAT_ACCEPTADA  = 12
    const val ESTAT_REBUTJADA  = 13
    const val ESTAT_EN_TRANSIT = 14
    const val ESTAT_LLIURADA   = 15

    // Socket
    const val SOCKET_HOST = "10.0.2.2"
    const val SOCKET_PORT = 9000

    // SharedPreferences
    const val PREFS_NAME   = "logitrack"
    const val PREF_USER_ID = "userId"
    const val PREF_USER_NOM = "userName"
    const val PREF_COGNOMS  = "userCognoms"
    const val PREF_CORREU   = "userCorreu"
    const val PREF_ROL_ID   = "rolId"

    // Seguretat - En producció aquestes claus haurien d'estar al Android Keystore
    const val AES_KEY_ENCRYPTION = "1234567890123456"
    const val AES_IV_ENCRYPTION  = "1234567890123456"

    // Tipus transport
    const val TRANSPORT_MARITIM    = 1
    const val TRANSPORT_AERI       = 2
    const val TRANSPORT_TERRESTRE  = 3

    // Validació per defecte
    const val TIPUS_VALIDACIO_DEFAULT = 9
}