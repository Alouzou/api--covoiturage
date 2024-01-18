package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.query
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.math.BigInteger
import java.sql.Statement


@Repository
class AdresseDAOImpl(val db: JdbcTemplate) : AdresseDAO {

    var keyHolder = GeneratedKeyHolder()

    override fun chercherParCode(code: Int): Adresse? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        /*if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }*/

        var adresse = db.query("SELECT id,appartement,numéro_principal,rue,ville,province,code_postal,pays FROM adresse join utilisateur on adresse_id = id where code = ?",code) { response, _ ->
            Adresse(response.getInt("id"), response.getString("appartement"),response.getString("numéro_principal"),response.getString("rue"),response.getString("ville"),response.getString("province"),response.getString("code_postal"),response.getString("pays"))
        }
        return adresse.firstOrNull()
    }

        override fun chercherParCodeAdresse(code: Int): Adresse? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        /*if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }*/

        var adresse = db.query("SELECT id,appartement,numéro_principal,rue,ville,province,code_postal,pays FROM adresse where id = ?",code) { response, _ ->
            Adresse(response.getInt("id"), response.getString("appartement"),response.getString("numéro_principal"),response.getString("rue"),response.getString("ville"),response.getString("province"),response.getString("code_postal"),response.getString("pays"))
        }
        return adresse.firstOrNull()
    }

    override fun ajouterAUtilisateur(code: Int, uneAdresse: Adresse): Adresse? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if(vérifierExistanceAdresse(code)){
            throw ProduitExisteDéjàException("L'utilisateur à déja une adresse définie")
        }
        db.update(
                PreparedStatementCreator { connection ->
                    val ps = connection.prepareStatement("INSERT INTO adresse (`appartement`, `numéro_principal`, `rue`, `ville`, `province`, `code_postal`, `pays`) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)
                    ps.setString(1,uneAdresse.appartement)
                    ps.setString(2,uneAdresse.numéro_principal)
                    ps.setString(3,uneAdresse.rue)
                    ps.setString(4,uneAdresse.ville)
                    ps.setString(5,uneAdresse.province)
                    ps.setString(6,uneAdresse.code_postal)
                    ps.setString(7,uneAdresse.pays)
                    ps
                },
                keyHolder)

        var codeAdresse = keyHolder.key
        val résultat = db.update("UPDATE utilisateur SET `adresse_id` = ? WHERE (`code` = ?)",codeAdresse,code)


        if (résultat ==1){

            return uneAdresse
        } else {
            return null
        }
    }


    override fun modifier(code: Int, uneAdresse: Adresse): Adresse? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        var résultat :Int = 0
        var existanceAdresse  = chercherParCode(code)
        if (existanceAdresse != null){
            résultat =  db.update("UPDATE adresse join utilisateur on adresse_id = id SET `appartement` = ?, `numéro_principal` = ?, `rue` = ?, `ville` = ?, `province` = ?, `code_postal` = ?, `pays` = ? WHERE (`code` = ?)",uneAdresse.appartement,uneAdresse.numéro_principal,uneAdresse.rue,uneAdresse.ville,uneAdresse.province,uneAdresse.code_postal,uneAdresse.pays,code)
        } else {
            var résultatAjouter = ajouterAUtilisateur(code,uneAdresse)
            if (résultatAjouter != null){
                résultat = 1
            } else {
                résultat = 0
            }
        }

        if (résultat == 1 ){
            return uneAdresse
        } else {
            return null
        }

    }

    override fun supprimer(code: Int): Adresse? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if(!vérifierExistanceAdresse(code)){
            throw RessourceInexistanteException("L'utilisateur n'a pas d'adresse définie")
        }
        var existanceAdresse  = chercherParCode(code)
        db.update("SET foreign_key_checks = 0;")
        val résultat =  db.update("DELETE FROM adresse WHERE id = (SELECT adresse_id FROM utilisateur WHERE code = ?);",code)
        val résultat2 =  db.update("UPDATE utilisateur SET `adresse_id` = NULL WHERE (`code` = ?);",code)
        db.update("SET foreign_key_checks = 1;")
        if (résultat == 1){
            return existanceAdresse
        } else {
            return null
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
                null
        )
        }
        var vérification = utilisateur.firstOrNull()
        if (vérification !=null){
            return true
        } else return false

    }



    override fun vérifierExistanceAdresse(code: Int): Boolean {
        var adresse = chercherParCode(code)
        if (adresse == null) {
            return false
        } else {
            return true
        }
    }

    override fun chercherTous(): List<Adresse> {
        TODO()
    }

    override fun ajouter(uneAdresse: Adresse): Adresse? {
        db.update(
                PreparedStatementCreator { connection ->
                    val ps = connection.prepareStatement("INSERT INTO adresse (`appartement`, `numéro_principal`, `rue`, `ville`, `province`, `code_postal`, `pays`) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)
                    ps.setString(1,uneAdresse.appartement)
                    ps.setString(2,uneAdresse.numéro_principal)
                    ps.setString(3,uneAdresse.rue)
                    ps.setString(4,uneAdresse.ville)
                    ps.setString(5,uneAdresse.province)
                    ps.setString(6,uneAdresse.code_postal)
                    ps.setString(7,uneAdresse.pays)
                    ps
                },
                keyHolder)
        var codeAdresse = keyHolder.key
        uneAdresse.id = codeAdresse!!.toInt()
        return uneAdresse
    }

    override fun modifierEntité(code: Int, element: Adresse): ResponseEntity<Adresse>? {
        TODO("Not yet implemented")
    }
}