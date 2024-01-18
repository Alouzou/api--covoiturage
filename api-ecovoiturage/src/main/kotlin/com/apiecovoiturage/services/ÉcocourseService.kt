package com.écovoiturage.écovoiturage.services

import com.écovoiturage.écovoiturage.Modèle.Écocourse
import com.écovoiturage.écovoiturage.dao.ÉcocourseDAO
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service

@Service
class ÉcocourseService(val dao : ÉcocourseDAO) {

    fun chercherTous(): List<Écocourse> = dao.chercherTous()
    fun chercherParCode(code: Int): Écocourse? = dao.chercherParCode(code)
    fun ajouter(écocourse : Écocourse): Écocourse? = dao.ajouter(écocourse)

    fun modifier(code: Int, écocourse: Écocourse): Écocourse? = dao.modifier(code, écocourse)
    fun supprimer(code: Int): Écocourse? = dao.supprimer(code)

    fun vérifierCourseParCode(code: Int) :Boolean = dao.vérifierCourseParCode(code)

    fun chercherCoursesDisponibles(code: Int) : List<Écocourse> = dao.chercherCoursesDisponibles(code)

    fun chercherCoursesPassager(code: Int) : List<Écocourse> = dao.chercherCoursesPassager(code)

    fun chercherCoursesConducteur(code: Int) : List<Écocourse> = dao.chercherCoursesConducteur(code)

    fun rejoindre(codeÉcocourse : Int, codeUtilisateur : Int) :Écocourse? = dao.rejoindre(codeÉcocourse,codeUtilisateur)

    fun quitter(codeÉcocourse : Int, codeUtilisateur : Int) : Unit = dao.quitter(codeÉcocourse,codeUtilisateur)

}