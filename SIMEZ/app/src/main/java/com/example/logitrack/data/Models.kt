package com.example.logitrack.data

data class Aeroport(val id: Int, val codi: String, val nom: String, val ciutatId: Int)
data class Ciutat(val id: Int, val nom: String, val paisId: Int)
data class Client(val id: Int)
data class RebutjarRequest(val rao: String)
data class EstatsOferte(val id: Int, val estat: String)
data class Incoterm(val id: Int, val tipusIncontermId: Int, val trackingStepsId: Int)
data class LiniesTransportMaritim(val id: Int, val nom: String, val ciutatId: Int)
data class Oferte(
    val id: Int, val tipusTransportId: Int, val tipusFluxeId: Int,
    val tipusCarregaId: Int, val incotermId: Int, val clientId: Int,
    val comentaris: String?, val agentComercialId: Int?, val transportistaId: Int?,
    val pesBrut: Double?, val volum: Double?, val tipusValidacioId: Int,
    val portOrigenId: Int?, val portDestiId: Int?, val aeroportOrigenId: Int?,
    val aeroportDestiId: Int?, val liniaTransportMaritimId: Int?,
    val estatOfertaId: Int, val operadorId: Int, val dataCreacio: String,
    val dataValidesaInicial: String?, val dataValidesaFinal: String?,
    val raoRebuig: String?, val tipusContenidorId: Int?
)
data class Paisso(val id: Int, val nom: String)
data class Port(val id: Int, val nom: String, val ciutatId: Int)
data class Rol(val id: Int, val rol: String)
data class TipusCarrega(val id: Int, val tipus: String)
data class TipusContenidor(val id: Int, val tipus: String)
data class TipusFlux(val id: Int, val tipus: String)
data class TipusIncoterm(val id: Int, val codi: String?, val nom: String?)
data class TipusTransport(val id: Int, val tipus: String)
data class TipusValidacion(val id: Int, val tipus: String)

data class LoginRequest(val correu: String, val contrasenya: String)
data class TrackingStep(val id: Int, val ordre: Int?, val nom: String?)
data class Transportiste(val id: Int, val nom: String, val ciutatId: Int)
data class Usuari( val id: Int, val correu: String, val contrasenya: String, val nom: String, val cognoms: String, @com.google.gson.annotations.SerializedName("rol_id") val rolId: Int )data class Document(
    val id: Int,
    val ofertaId: Int,
    val nom: String,
    val tipus: String,
    val url: String


)