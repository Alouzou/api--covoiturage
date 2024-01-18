package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.modèles.Voiture
import org.springframework.http.ResponseEntity

interface VoitureDAO : DAO<Voiture> {

    override fun chercherTous(): List<Voiture>
    override fun ajouter(element: Voiture): Voiture?
    override fun chercherParCode(code: Int): Voiture?
    override fun modifier(code: Int, element: Voiture): Voiture?
    override fun supprimer(code: Int): Voiture?
    override fun modifierEntité(code: Int, element: Voiture): ResponseEntity<Voiture>?


    fun chercherTousVoituresDunConducteur(code: Int): List<Voiture>

    fun ajouterAUtilisateur(element: Voiture,code: Int) : Voiture?
    fun vérifierExistanceUtilisateur(code: Int): Boolean
    fun vérifierExistanceVoiture(code_utilisateur: Int, code_voiture: Int): Boolean
    fun supprimerAvecCodeUtilisateur(code_voiture: Int, code_utilisateur: Int): Voiture?
}