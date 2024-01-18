package com.écovoiturage.écovoiturage.services

import Rôle
import com.écovoiturage.écovoiturage.dao.RôleDAO
import org.springframework.stereotype.Service

@Service
class RôleService(val dao: RôleDAO) {

    fun chercherParCode(code:Int): List<Rôle> = dao.chercherRôlesParCode(code)

    fun ajouter(rôle: Rôle,code: Int): Rôle? = dao.ajouterAUtilisateur(rôle,code)

    fun supprimer(rôle: Rôle,code: Int) : Rôle? = dao.supprimerAUtilisateur(rôle,code)

    fun vérifierExistanceRôle(code: Int,rôle:Rôle):Boolean = dao.vérifierExistanceRôle(code,rôle)

    fun vérifierExistanceUtilisateur(code:Int):Boolean = dao.vérifierExistanceUtilisateur(code)

}