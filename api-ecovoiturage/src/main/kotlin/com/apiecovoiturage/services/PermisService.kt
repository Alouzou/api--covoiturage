package com.écovoiturage.écovoiturage.services

import com.écovoiturage.écovoiturage.dao.PermisDAO
import com.écovoiturage.écovoiturage.modèles.Permis
import org.springframework.stereotype.Service

@Service
class PermisService (val dao : PermisDAO){

    fun chercherParCode(code:Int): Permis? = dao.chercherParCode(code)

    fun ajouter(element: Permis,code: Int): Permis? = dao.ajouterAUtilisateur(element,code)

    fun modifier(code: Int, element: Permis): Permis? = dao.modifier(code, element)

    fun supprimer(code: Int): Permis? = dao.supprimer(code)

    fun vérifierExistancePermis(code: Int):Boolean = dao.vérifierExistancePermis(code)

    fun vérifierExistanceUtilisateur(code: Int):Boolean = dao.vérifierExistanceUtilisateur(code)
}