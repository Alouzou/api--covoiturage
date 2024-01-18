package com.écovoiturage.écovoiturage.Modèle

import com.écovoiturage.écovoiturage.modèles.Adresse

data class Utilisateur(var code: Int,
                       val nom: String,
                       val prénom: String,
                       val courriel: String,
                       val téléphone: String,
                       val nombreCourse : Int,
                       val urlPhoto : String?,
                       var adresse: Adresse?) {
}