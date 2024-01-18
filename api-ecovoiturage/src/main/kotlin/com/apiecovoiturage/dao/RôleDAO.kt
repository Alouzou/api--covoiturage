package com.écovoiturage.écovoiturage.dao

import Rôle

interface RôleDAO : DAO<Rôle>{

    override fun chercherTous(): List<Rôle>

    override fun chercherParCode(code: Int): Rôle?

    override fun ajouter(element: Rôle): Rôle?

    override fun modifier(code: Int, element: Rôle): Rôle?

    override fun supprimer(code: Int): Rôle?

    fun ajouterAUtilisateur(element: Rôle,code:Int): Rôle?

    fun supprimerAUtilisateur(element: Rôle,code:Int): Rôle?

    fun vérifierExistanceRôle(code: Int,rôle:Rôle):Boolean

    fun chercherRôlesParCode(code: Int): List<Rôle>

    fun vérifierExistanceUtilisateur(code: Int): Boolean
}