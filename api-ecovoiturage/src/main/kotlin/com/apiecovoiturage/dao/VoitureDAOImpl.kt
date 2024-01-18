package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Voiture
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.query
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

@Repository
class VoitureDAOImpl(val db: JdbcTemplate,val utilisateurRepo :UtilisateurDAO): VoitureDAO {
    var keyHolder = GeneratedKeyHolder()

    override fun chercherTousVoituresDunConducteur(code: Int): List<Voiture> {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        return db.query("select * from voiture where conducteur_code = ?",code) { response, _ -> Voiture(
            response.getInt("code"),
            response.getString("marque"),
            response.getString("année"),
            response.getString("modèle"),
            response.getString("couleur"),
            response.getString("noImmatriculation"),
            response.getInt("capacité"),
            utilisateurRepo.chercherParCode(response.getInt("conducteur_code"))!!
        )
        }
    }

    override fun ajouterAUtilisateur(element: Voiture,code: Int): Voiture? {
        val résultat : Int
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        var existanceVoiture = chercherParCode(element.code)
        if (existanceVoiture != null) {
            if (existanceVoiture.conducteur.code == code){
                throw ProduitExisteDéjàException("L'utilisateur possède déjà une voiture avec ce code")
            }
        }
        if (element.code == 0){
            résultat = db.update(
                    PreparedStatementCreator { connection ->
                        val ps = connection.prepareStatement("insert into voiture (`marque`, `année`, `modèle`, `couleur`, `noImmatriculation`, `capacité`,`conducteur_code`) VALUES (?, ?, ?, ?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS)
                        ps.setString(1,element.marque)
                        ps.setString(2,element.année)
                        ps.setString(3,element.modèle)
                        ps.setString(4,element.couleur)
                        ps.setString(5,element.noImmatriculation)
                        ps.setInt(6,element.capacité)
                        ps.setInt(7, code)
                        ps
                    },
                    keyHolder)
            element.code = keyHolder.key!!.toInt()
        } else {
            résultat = db.update("insert into voiture (`code`,`marque`, `année`, `modèle`, `couleur`, `noImmatriculation`, `capacité`,`conducteur_code`) VALUES (?,?, ?, ?, ?, ?, ?,?)",
                element.code,
                element.marque,
                element.année,
                element.modèle,
                element.couleur,
                element.noImmatriculation,
                element.capacité,
                code )
        }

        return if (résultat == 1) {
            element
        } else {
            null
        }
    }

    override fun chercherParCode(code: Int): Voiture? {
        val voiture = db.query("select * from voiture join écocourse on codeVoiture = code where code = ?", code) {
                response, _ -> Voiture(
            response.getInt("code"),
            response.getString("marque"),
            response.getString("année"),
            response.getString("modèle"),
            response.getString("couleur"),
            response.getString("noImmatriculation"),
            response.getInt("capacité"),
            utilisateurRepo.chercherParCode(response.getInt("codeConducteur"))!!
        )
        }
        return voiture.firstOrNull()
    }

    override fun modifier(code: Int, element: Voiture): Voiture? {
        val résultat : Int
        var voiture : Voiture?
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if(vérifierExistanceVoiture(code,element.code)){
            résultat = db.update("UPDATE voiture SET `marque` = ?, `année` = ?, `modèle` = ?, `couleur` = ?, `noImmatriculation` = ?,`capacité` = ? WHERE (`code` = ? and `conducteur_code` = ? )", element.marque, element.année, element.modèle, element.couleur, element.noImmatriculation, element.capacité,element.code,code)
            voiture = element
        } else {
           val test = ajouterAUtilisateur(element,code)
            if (test == null){
                résultat = 0
                voiture = null
            } else {
                voiture = test
                résultat = 1
            }
        }

        return if (résultat == 1) {
            voiture
        } else {
            null
        }
    }

    override fun supprimer(code: Int): Voiture? {
        var voiture = chercherParCode(code)
        var résultat = db.update("delete from voiture where `code` = ?", code)
        return if (résultat == 1) {
            voiture
        } else {
            null
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

    override fun vérifierExistanceVoiture(code_utilisateur:Int,code_voiture: Int) : Boolean{
        var voiture = db.query("SELECT * FROM voiture where code = ? and conducteur_code = ?",code_voiture,code_utilisateur) { response, _ ->
            Voiture(response.getInt("code"),response.getString("marque"),response.getString("année"),response.getString("modèle"),response.getString("couleur"),response.getString("noImmatriculation"),response.getInt("capacité"), utilisateurRepo.chercherParCode(response.getInt("conducteur_code"))!!)
        }
        if (voiture.firstOrNull() != null){
            return true
        } else {
            return false
        }
    }

    override fun supprimerAvecCodeUtilisateur(code_voiture :Int,code_utilisateur:Int):Voiture?{
        if (code_utilisateur < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code_utilisateur)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if (!vérifierExistanceVoiture(code_utilisateur,code_voiture)){
            throw  RessourceInexistanteException("L'utilisateur n'a pas de voitures avec ce code")
        }
         return supprimer(code_voiture)

    }

    override fun modifierEntité(code: Int, element: Voiture): ResponseEntity<Voiture>? {
        TODO("Not yet implemented")
    }


    override fun chercherTous(): List<Voiture> {
        val voitures = db.query("select * from voiture") { response, _ -> Voiture(
            response.getInt("code"),
            response.getString("marque"),
            response.getString("année"),
            response.getString("modéle"),
            response.getString("couleur"),
            response.getString("noImmatriculation"),
            response.getInt("capacité"),
            utilisateurRepo.chercherParCode(response.getInt("conducteur_code"))!!
        )
        }
        if (voitures.size < 1){
            throw RessourceInexistanteException("Liste de voitures inexistante ou vide")
        }
        return voitures
    }

    override fun ajouter(element: Voiture): Voiture? {
        TODO("Not yet implemented")
    }
}