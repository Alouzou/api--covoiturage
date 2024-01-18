package com.écovoiturage.écovoiturage.modèles

import com.écovoiturage.écovoiturage.Modèle.Utilisateur

data class Voiture(
        var code: Int,
        val marque: String,
        val année: String,
        val modèle: String,
        val couleur: String,
        val noImmatriculation: String,
        val capacité: Int,
        val conducteur :Utilisateur
)
