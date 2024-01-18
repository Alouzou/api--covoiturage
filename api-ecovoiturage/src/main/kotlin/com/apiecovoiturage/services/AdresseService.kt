package com.écovoiturage.écovoiturage.services

import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.dao.AdresseDAO
import org.springframework.stereotype.Service

@Service
class AdresseService(val dao : AdresseDAO) {

    fun chercherParCode(code:Int): Adresse? = dao.chercherParCode(code)

    fun ajouter(code:Int,adresse: Adresse): Adresse? = dao.ajouterAUtilisateur(code,adresse)

    fun modifier(code:Int,adresse: Adresse) : Adresse?= dao.modifier(code,adresse)

    fun supprimer(code: Int) : Adresse? = dao.supprimer(code)

    fun vérifierExistanceAdresse(code: Int):Boolean = dao.vérifierExistanceAdresse(code)

    fun vérifierExistanceUtilisateur(code: Int):Boolean = dao.vérifierExistanceUtilisateur(code)

}