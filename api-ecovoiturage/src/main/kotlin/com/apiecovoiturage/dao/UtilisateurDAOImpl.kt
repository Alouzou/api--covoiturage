package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import org.springframework.http.ResponseEntity
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.query
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement




@Repository
class UtilisateurDAOImpl(val db: JdbcTemplate,val adresseRepo:AdresseDAO): UtilisateurDAO {
    var keyHolder = GeneratedKeyHolder()

    override fun chercherTous(): List<Utilisateur> {
        val utilisateurs = db.query("select * from utilisateur") { response, _ -> Utilisateur(
            response.getInt("code"),
            response.getString("prénom"),
            response.getString("nom"),
            response.getString("courriel"),
            response.getString("téléphone"),
            response.getInt("nombreCourse"),
            response.getString("urlPhoto"),
            adresseRepo.chercherParCode(response.getInt("code"))
            )
        }
        if (utilisateurs.size < 1){
            throw RessourceInexistanteException("Liste d'utilisateurs inexistante ou vide")
        }
        return utilisateurs

    }

    override fun chercherParCode(code: Int): Utilisateur? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        val utilisateur = db.query("select * from utilisateur where code = ?", code) {
            response, _ -> Utilisateur(
                response.getInt("code"),
                response.getString("nom"),
                response.getString("prénom"),
                response.getString("courriel"),
                response.getString("téléphone"),
                response.getInt("nombreCourse"),
                response.getString("urlPhoto"),
                adresseRepo.chercherParCode(response.getInt("code"))
                )
        }
        return utilisateur.firstOrNull()
    }

    override fun ajouter(element: Utilisateur): Utilisateur? {
        if (element.code != 0){
            if (element.code < 1 ) {
                throw ErreurRequêteException("Le code d'utilisateur est invalide")
            }
            if (vérifierExistanceUtilisateur(element.code)){
                throw ProduitExisteDéjàException("Un utilisateur avec ce code existe déjà")
            }
        }

        val résultat : Int
        if (element.code != 0){
            résultat = db.update("insert into utilisateur (`code`, `prénom`, `nom`, `courriel`, `téléphone`, `nombreCourse`, `urlPhoto`, `adresse_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", element.code, element.prénom, element.nom, element.courriel, element.téléphone, element.nombreCourse, element.urlPhoto, element.adresse?.id)
        } else {
            résultat = db.update(
                    PreparedStatementCreator { connection ->
                        val ps = connection.prepareStatement("insert into utilisateur (`prénom`, `nom`, `courriel`, `téléphone`, `nombreCourse`, `urlPhoto`, `adresse_id`) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
                        ps.setString(1,element.prénom)
                        ps.setString(2,element.nom)
                        ps.setString(3,element.courriel)
                        ps.setString(4,element.téléphone)
                        ps.setInt(5,element.nombreCourse)
                        ps.setString(6,element.urlPhoto)
                        ps.setInt(7, element.adresse?.id ?: 0)
                        ps
                    },
                    keyHolder)
            element.code = keyHolder.key!!.toInt()
        }

        return if (résultat == 1) {
            element
        } else {
            null
        }
    }
    override fun modifier(code: Int, element: Utilisateur): Utilisateur? {
        var adresseLue : Adresse? = null
        if (element.adresse !=null){
            var adresseUtilisateur = vérifierAdresse(element.adresse!!)
            if(adresseUtilisateur != null){
               adresseLue = adresseUtilisateur
            } else {
                 adresseLue = adresseRepo.ajouter(element.adresse!!)!!
            }
        }

        var résultat = db.update("update utilisateur set `prénom` = ?, `nom` = ?, `courriel` = ?, `téléphone` = ?, `nombreCourse` = ?, `urlPhoto` = ?, `adresse_id` = ? where `code` = ?", element.prénom, element.nom, element.courriel, element.téléphone, element.nombreCourse, element.urlPhoto, adresseLue?.id
                ?: 0, code)
        return if (résultat == 1) {
            element
        } else {
            null
        }


    }
    override fun modifierEntité(code: Int, element: Utilisateur): ResponseEntity<Utilisateur>? {
        TODO("Not yet implemented")
    }

    override fun supprimer(code: Int): Utilisateur? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        var utilisateur = chercherParCode(code)
        var résultat = db.update("delete from utilisateur where `code` = ?", code)
        return if (résultat == 1) {
            utilisateur
        } else {
            null
        }
    }

    override fun vérifierExistanceUtilisateur(code: Int): Boolean {
        val utilisateur = chercherParCode(code)
        if (utilisateur != null){
            return true
        } else {
            return false
        }
    }

    fun vérifierAdresse(adresse: Adresse) :Adresse?{
        var uneAdresse : List<Adresse>
        if (adresse.appartement != null){
            uneAdresse = db.query("SELECT * FROM adresse where appartement  = ? and numéro_principal = ? and rue = ? and ville = ? and province = ? and code_postal = ? and pays = ?;", adresse.appartement!!,adresse.numéro_principal,adresse.rue,adresse.ville,adresse.province,adresse.code_postal,adresse.pays) { response, _ ->
                Adresse(response.getInt("id"), response.getString("appartement"),response.getString("numéro_principal"),response.getString("rue"),response.getString("ville"),response.getString("province"),response.getString("code_postal"),response.getString("pays"))
            }
        } else {
            uneAdresse = db.query("SELECT * FROM adresse where appartement is null  and numéro_principal = ? and rue = ? and ville = ? and province = ? and code_postal = ? and pays = ?;",adresse.numéro_principal,adresse.rue,adresse.ville,adresse.province,adresse.code_postal,adresse.pays) { response, _ ->
                Adresse(response.getInt("id"), response.getString("appartement"),response.getString("numéro_principal"),response.getString("rue"),response.getString("ville"),response.getString("province"),response.getString("code_postal"),response.getString("pays"))
            }
        }
        return uneAdresse.firstOrNull()

    }

}