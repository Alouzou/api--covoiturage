package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.Modèle.Écocourse

interface ÉcocourseDAO : DAO<Écocourse> {
    override fun chercherTous(): List<Écocourse>
    override fun ajouter(element: Écocourse): Écocourse?
    override fun chercherParCode(code: Int): Écocourse?
    override fun modifier(code: Int, element: Écocourse): Écocourse?
    override fun supprimer(code: Int): Écocourse?

    fun vérifierExistanceÉcocourse(écocourse: Écocourse): Boolean

    fun vérifierCourseParCode(code: Int) :Boolean

    fun chercherCoursesDisponibles(code: Int) : List<Écocourse>

    fun chercherCoursesPassager(code: Int) : List<Écocourse>

    fun chercherCoursesConducteur(code: Int) : List<Écocourse>

    fun validerConducteur(codeÉcocourse : Int, codeConducteur: Int): Boolean
    fun rejoindre(codeÉcocourse: Int, codUtilisateur: Int): Écocourse?
    fun quitter(codeÉcocourse: Int, codeUtilisateur: Int)
}