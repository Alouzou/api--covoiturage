package com.écovoiturage.écovoiturage.services

import com.écovoiturage.écovoiturage.modèles.Voiture
import com.écovoiturage.écovoiturage.dao.VoitureDAO
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class VoitureService(val dao: VoitureDAO) {
    fun chercherTous(): List<Voiture> = dao.chercherTous()
    fun chercherParCode(code: Int): Voiture? = dao.chercherParCode(code)
    fun ajouter(voiture: Voiture): Voiture? = dao.ajouter(voiture)
    fun modifier(code: Int, voiture: Voiture): Voiture? = dao.modifier(code, voiture)
    fun supprimer(code: Int): Voiture? = dao.supprimer(code)

    fun supprimerAvecCodeUtilisateur(code_voiture: Int,code_utilisateur: Int):Voiture?  = dao.supprimerAvecCodeUtilisateur(code_voiture,code_utilisateur)
    fun ajouterAUtilisateur(voiture: Voiture,code: Int) : Voiture? = dao.ajouterAUtilisateur(voiture,code)
    fun chercherToutVoituresUtilisateur(code: Int) : List<Voiture> = dao.chercherTousVoituresDunConducteur(code)

    fun vérifierExistanceVoiture(code_utilisateur: Int,code_voiture: Int) :Boolean = dao.vérifierExistanceVoiture(code_utilisateur,code_voiture)
}