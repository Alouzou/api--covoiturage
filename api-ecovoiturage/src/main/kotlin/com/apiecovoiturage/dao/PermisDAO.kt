package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.modèles.Permis
import org.springframework.http.ResponseEntity

interface PermisDAO : DAO<Permis> {

    override fun chercherTous(): List<Permis>

    override fun chercherParCode(code: Int): Permis?

    override fun ajouter(element: Permis): Permis?

    override fun modifier(code: Int, element: Permis): Permis?

    override fun modifierEntité(code: Int, element: Permis): ResponseEntity<Permis>?

    override fun supprimer(code: Int): Permis?

    fun ajouterAUtilisateur(element: Permis,code:Int):Permis?

    fun vérifierExistancePermis(code: Int) : Boolean

    fun vérifierExistanceUtilisateur(code: Int): Boolean
}