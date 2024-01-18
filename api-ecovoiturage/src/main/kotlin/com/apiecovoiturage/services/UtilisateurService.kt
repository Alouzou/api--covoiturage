package com.écovoiturage.écovoiturage.services

import com.écovoiturage.écovoiturage.dao.UtilisateurDAO
import org.springframework.http.ResponseEntity
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import org.springframework.stereotype.Service

@Service
class UtilisateurService(val dao: UtilisateurDAO) {
    fun chercherTous(): List<Utilisateur> = dao.chercherTous()
    fun chercherParCode(code: Int): Utilisateur? = dao.chercherParCode(code)
    fun ajouter(utilisateur: Utilisateur): Utilisateur? = dao.ajouter(utilisateur)
    fun modifier(code: Int, utilisateur: Utilisateur): Utilisateur? = dao.modifier(code, utilisateur)
    fun supprimer(code: Int): Utilisateur? = dao.supprimer(code)

    fun vérifierExistanceUtilisateur(code: Int) : Boolean = dao.vérifierExistanceUtilisateur(code)
}