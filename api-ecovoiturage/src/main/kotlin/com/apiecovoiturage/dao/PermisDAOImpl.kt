package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Permis
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Repository

@Repository
class PermisDAOImpl(val db: JdbcTemplate,val utilisateurRepo :UtilisateurDAO) : PermisDAO {

    //erreur 500 = bad sql grammar

    override fun chercherParCode(code: Int): Permis?{
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        var permis = db.query("select * from permis where utilisateur_code = ?",code) { response, _ ->
            Permis(response.getString("numéro_permis"), utilisateurRepo.chercherParCode(response.getInt("utilisateur_code"))!!,response.getLong("date_émission"),response.getLong("date_expiration"),response.getString("classe"))
        }
        return  permis.firstOrNull()
    }

    override fun ajouterAUtilisateur(element: Permis, code: Int): Permis?  {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if(vérifierExistancePermis(code)){
            throw ProduitExisteDéjàException("L'utilisateur à déja un permis de conduire")
        }
        if (vérifierExistanceNuméroPermis(element.numéro_permis)){
            throw ProduitExisteDéjàException("Un permis de conduire avec ce numéro existe déjà")
        }

        val résultat = db.update("INSERT INTO permis (`numéro_permis`, `utilisateur_code`, `date_émission`, `date_expiration`, `classe`) VALUES (?, ?, ?, ?, ?)",element.numéro_permis,code,element.date_émission,element.date_expiration,element.classe)
        if (résultat == 1){
            element.utilisateur.code = code
            return element
        } else {
            return null
        }
    }

    override fun vérifierExistancePermis(code: Int): Boolean {
        var permis = chercherParCode(code)
        if (permis == null) {
            return false
        } else {
            return true
        }
    }

    fun vérifierExistanceNuméroPermis(numéro: String): Boolean {
        var permis = db.query("select * from permis where numéro_permis = ?",numéro) { response, _ ->
            Permis(response.getString("numéro_permis"), utilisateurRepo.chercherParCode(response.getInt("utilisateur_code"))!!,response.getLong("date_émission"),response.getLong("date_expiration"),response.getString("classe"))
        }
        var testNuméro = permis.firstOrNull()
        if (testNuméro !=null){
            return true
        } else {
            return false
        }

    }


    override fun modifier(code: Int, element: Permis): Permis?{
        var résultat = 0
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if(vérifierExistancePermis(code)){
            résultat =  db.update("UPDATE permis SET `numéro_permis` = ?, `utilisateur_code` = ?, `date_émission` = ?, `date_expiration` = ?, `classe` = ? WHERE (`utilisateur_code` = ?)",element.numéro_permis,code,element.date_émission,element.date_expiration,element.classe,code)
        } else {
            var résultatAjouter = ajouterAUtilisateur(element,code)
            if (résultatAjouter != null){
                résultat = 1
            } else {
                résultat = 0
            }
        }
        if (résultat == 1 ){
            return element
        } else {
            return null
        }
    }

    override fun supprimer(code: Int): Permis? {
        if (code < 1){
            throw ErreurRequêteException("Le code d'utilisateur est invalide")
        }
        if (!vérifierExistanceUtilisateur(code)){
            throw  RessourceInexistanteException("L'utilisateur n'existe pas")
        }
        if(!vérifierExistancePermis(code)){
            throw RessourceInexistanteException("L'utilisateur n'a pas de permis")
        }
        var existancePermis  = chercherParCode(code)
        val résultat =  db.update("DELETE FROM permis WHERE (`utilisateur_code` = ?)",code)

        if (résultat == 1){
            return existancePermis
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
                Adresse(0,"","","","","","","")
        )
        }
        var vérification = utilisateur.firstOrNull()
        if (vérification !=null){
            return true
        } else return false

    }

    override fun chercherTous(): List<Permis> {
        TODO("Non nécessaire")
    }

    override fun ajouter(element: Permis): Permis? {
        TODO("use ajouterAUtilisateur instead")
    }

    override fun modifierEntité(code: Int, element: Permis): ResponseEntity<Permis>? {
        TODO("Use modifier Instead")
    }

}