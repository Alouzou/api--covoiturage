package com.écovoiturage.écovoiturage.dao


import Rôle
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Repository

@Repository
class RôleDAOImpl(val db : JdbcTemplate,val utilisateurRepo :UtilisateurDAO) :RôleDAO {


    override fun chercherRôlesParCode(code: Int): List<Rôle>{
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        var rôles = db.query("select * from role_utilisateur where utilisateur_code = ?",code) { response, _ ->
            Rôle(utilisateurRepo.chercherParCode(response.getInt("utilisateur_code"))!!, response.getString("rôle"))
        }
        return rôles
    }


    override fun ajouterAUtilisateur(element: Rôle, code: Int): Rôle? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if (vérifierExistanceRôle(code,element)){
            throw ProduitExisteDéjàException("L'utilisateur possède déjà ce rôle")
        }
        val résultat = db.update("INSERT INTO role_utilisateur (`utilisateur_code`, `rôle`) VALUES (?, ?)",code,element.rôle)
        if (résultat == 1){
            element.utilisateur.code = code
            return element
        } else {
            return null
        }
    }

    override fun supprimerAUtilisateur(element: Rôle, code: Int): Rôle? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if (!vérifierExistanceRôle(code,element)){
            throw RessourceInexistanteException("L'utilisateur n'a pas ce rôle")
        }
        val résultat =  db.update("DELETE FROM role_utilisateur WHERE (`utilisateur_code` = ?) and (`rôle` = ?)",code,element.rôle)

        if (résultat == 1){
            return element
        } else {
            return null
        }
    }

    override fun vérifierExistanceRôle(code: Int,rôle:Rôle): Boolean {
        val listesRôles = chercherRôlesParCode(code)
        val existanceRôle =  listesRôles.find { it.rôle == rôle.rôle }
        if(existanceRôle!=null){
            return true
        } else {
            return false
        }
    }

    override fun vérifierExistanceUtilisateur(code: Int) :Boolean{
        val utilisateur = db.query("select * from utilisateur where code = ?", code) {
            response, _ -> Utilisateur(
                response.getInt("code"),
                response.getString("prénom"),
                response.getString("nom"),
                response.getString("courriel"),
                response.getString("téléphone"),
                response.getInt("nombreCourse"),
                response.getString("urlPhoto"),
                Adresse(0,"","","","","","","")
        )
        }
        var vérification = utilisateur.firstOrNull()
        if (vérification !=null){
            return true
        } else return false
    }


    override fun chercherTous(): List<Rôle> {
        TODO("Not yet implemented")
    }

    override fun chercherParCode(code: Int): Rôle? {
        TODO("Not yet implemented")
    }

    override fun ajouter(element: Rôle): Rôle? {
        TODO("Not yet implemented")
    }

    override fun modifier(code: Int, element: Rôle): Rôle? {
        TODO("Not yet implemented")
    }

    override fun modifierEntité(code: Int, element: Rôle): ResponseEntity<Rôle>? {
        TODO("Not yet implemented")
    }

    override fun supprimer(code: Int): Rôle? {
        TODO("Not yet implemented")
    }


}