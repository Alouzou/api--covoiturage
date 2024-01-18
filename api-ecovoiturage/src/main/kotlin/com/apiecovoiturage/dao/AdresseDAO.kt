package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.modèles.Adresse

interface AdresseDAO : DAO<Adresse> {
    override fun chercherTous(): List<Adresse>
    override fun chercherParCode(code: Int): Adresse?
    override fun ajouter(uneAdresse: Adresse): Adresse?
    override fun modifier(code: Int, uneAdresse: Adresse): Adresse?
    override fun supprimer(code: Int): Adresse?

    fun ajouterAUtilisateur(code:Int,uneAdresse: Adresse): Adresse?

    fun vérifierExistanceAdresse(code: Int):Boolean

    fun vérifierExistanceUtilisateur(code: Int): Boolean
    fun chercherParCodeAdresse(code: Int): Adresse?
}