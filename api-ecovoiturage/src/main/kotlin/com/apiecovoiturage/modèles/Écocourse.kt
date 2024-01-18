package com.écovoiturage.écovoiturage.Modèle

import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Voiture


data class Écocourse(var codeÉcocourse: Int,
                     val conducteur: Utilisateur,
                     val date: Long,
                     var adresseDépart: Adresse,
                     var adresseDestination: Adresse,
                     val voiture: Voiture,
                     val prixTotal: Double,
                     val nombrePassagers: Int

    ){
}