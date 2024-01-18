package com.écovoiturage.écovoiturage.dao

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ErreurServeurInterne
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Écocourse
import com.écovoiturage.écovoiturage.modèles.Adresse
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.query
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Statement

@Repository
class ÉcocourseDAOImpl(val db: JdbcTemplate,val utilisateurRepo :UtilisateurDAO,val adresseRepo :AdresseDAO,val voitureRepo :VoitureDAO) : ÉcocourseDAO {

    var keyHolder = GeneratedKeyHolder()
    // override fun chercherTous(): List<Écocourse> = SourceDonnées.écocourses
    override fun chercherTous(): List<Écocourse> {
        val requete = ("SELECT * FROM écocourse")
        return db.query(requete) { resultSet: ResultSet, _: Int ->
            Écocourse(
                resultSet.getInt("codeÉcocourse"),
                utilisateurRepo.chercherParCode(resultSet.getInt("codeConducteur"))!!,
                resultSet.getLong("date"),
                adresseRepo.chercherParCodeAdresse(resultSet.getInt("adresseDépart_id"))!!,
                adresseRepo.chercherParCodeAdresse(resultSet.getInt("adresseDestination_id"))!!,
                voitureRepo.chercherParCode(resultSet.getInt("codeVoiture"))!!,
                resultSet.getDouble("prixTotal"),
                resultSet.getInt("nombrePassagers")
            )

        }

        }


    override fun chercherParCode(code: Int): Écocourse? {
        if (code < 0){
            throw ErreurRequêteException("Le code de l'écocourse est invalide")
        }

        val requete =("SELECT * FROM écocourse WHERE codeÉcocourse = ?")
        var écocourse = db.query(requete, arrayOf(code)){ resultSet, _ -> Écocourse(
            resultSet.getInt("codeÉcocourse"),
            utilisateurRepo.chercherParCode(resultSet.getInt("codeConducteur"))!!,
            resultSet.getLong("date"),
            adresseRepo.chercherParCodeAdresse(resultSet.getInt("adresseDépart_id"))!!,
            adresseRepo.chercherParCodeAdresse(resultSet.getInt("adresseDestination_id"))!!,
            voitureRepo.chercherParCode(resultSet.getInt("codeVoiture"))!!,
            resultSet.getDouble("prixTotal"),
            resultSet.getInt("nombrePassagers")
        )
        }
        return écocourse.firstOrNull()
    }

    override fun ajouter(element: Écocourse): Écocourse? {
        var résultat : Int
        var écocourseAjoutée : Écocourse?
        if (element.codeÉcocourse < 0 ) {
            throw ErreurRequêteException("Le code d'écocourse est invalide")
        }

        if(vérifierCourseParCode(element.codeÉcocourse)){
            throw ProduitExisteDéjàException("Une écocourse avec ce code existe déjà")
        }
        var adresseDépartOriginale = vérifierAdresse(element.adresseDépart)
        var adresseDestinationOriginale = vérifierAdresse(element.adresseDestination)

        if(adresseDépartOriginale != null){
            element.adresseDépart = adresseDépartOriginale
        } else {
            var adresseDépartAjoutée = adresseRepo.ajouter(element.adresseDépart)
            if (adresseDépartAjoutée !=null){
                element.adresseDépart = adresseDépartAjoutée
            } else {
                throw ErreurRequêteException("Erreur dans la requête")
            }
        }

        if (adresseDestinationOriginale != null){
            element.adresseDestination = adresseDestinationOriginale
        } else {
            var adresseDestinationAjoutée = adresseRepo.ajouter(element.adresseDestination)
            if (adresseDestinationAjoutée !=null){
                element.adresseDestination = adresseDestinationAjoutée
            }else {
                throw ErreurRequêteException("Erreur dans la requête")
            }
        }
        if (vérifierExistanceÉcocourse(element)){
            throw ProduitExisteDéjàException("Une écocourse avec ce contenu existe déjà")
        }

        if (element.codeÉcocourse == 0){
            db.update(
                    PreparedStatementCreator { connection ->
                        val ps = connection.prepareStatement("INSERT INTO écocourse(codeConducteur, date, adresseDépart_id, adresseDestination_id, codeVoiture, prixTotal, nombrePassagers) values (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
                        ps.setInt(1,element.conducteur.code)
                        ps.setLong(2,element.date)
                        ps.setInt(3,element.adresseDépart.id)
                        ps.setInt(4,element.adresseDestination.id)
                        ps.setInt(5,element.voiture.code)
                        ps.setDouble(6,element.prixTotal)
                        ps.setInt(7,element.nombrePassagers)
                        ps
                    },
                    keyHolder)
            var codeÉcocourse = keyHolder.key
            écocourseAjoutée = element
            écocourseAjoutée.codeÉcocourse = codeÉcocourse!!.toInt()
        } else {
            résultat = db.update("INSERT INTO écocourse(codeÉcocourse,codeConducteur, date, adresseDépart_id, adresseDestination_id, codeVoiture, prixTotal, nombrePassagers) values (?,?, ?, ?, ?, ?, ?, ?)", element.codeÉcocourse,element.conducteur.code,element.date,element.adresseDépart.id,element.adresseDestination.id,element.voiture.code,element.prixTotal,element.nombrePassagers)
            if (résultat == 1){
                écocourseAjoutée = element
            } else {
                écocourseAjoutée = null
            }
        }

        return  écocourseAjoutée
    }

    override fun modifier(code: Int, element: Écocourse): Écocourse? {
        var résultat : Int
        var courseFinale :Écocourse?
        if (code< 1 ) {
            throw ErreurRequêteException("Le code d'écocourse est invalide'")
        }
        if(vérifierCourseParCode(code)){
            résultat = db.update("UPDATE écocourse SET `date` = ?, `adresseDépart_id` = ?, `adresseDestination_id` = ?, `codeVoiture` = ?,`prixTotal` = ?,`nombrePassagers` = ? WHERE (`codeÉcocourse` = ? )",element.date,element.adresseDépart.id,element.adresseDestination.id,element.voiture.code,element.prixTotal,element.nombrePassagers,code)
            courseFinale = element
        } else {
            var course = ajouter(element)
            if (course !=null){
                courseFinale = course
                résultat = 1
            } else {
                résultat = 0
                courseFinale = null
            }
        }
        if (résultat ==1){
            return courseFinale
        } else {
            return null
        }
    }

    override fun supprimer(code: Int): Écocourse? {
        if (code< 1 ) {
            throw ErreurRequêteException("Le code d'écocourse est invalide'")
        }
        if(!vérifierCourseParCode(code)){
            throw RessourceInexistanteException("Écocourse inexistante")
        }
        val requete = ("DELETE FROM écocourse WHERE codeÉcocourse = ?")
        var result = db.update(requete, code)
        if (result == 1){
            return chercherParCode(code)
        }else{
            return null
        }

    }

    override fun modifierEntité(code: Int, element: Écocourse): ResponseEntity<Écocourse>? {
        TODO("Not yet implemented")
    }

    override fun vérifierExistanceÉcocourse(écocourse: Écocourse) : Boolean {
        val écocourseExistante = db.query("SELECT * FROM écocourse where codeConducteur = ? and date = ? and adresseDépart_id =? and adresseDestination_id = ? and codeVoiture = ? and prixTotal = ? and nombrePassagers = ?",écocourse.conducteur.code,écocourse.date,écocourse.adresseDépart.id,écocourse.adresseDestination.id,écocourse.voiture.code,écocourse.prixTotal,écocourse.nombrePassagers) { response, _ ->
            Écocourse(response.getInt("codeÉcocourse"), utilisateurRepo.chercherParCode(response.getInt("codeConducteur"))!!, response.getLong("date"), adresseRepo.chercherParCodeAdresse(response.getInt("adresseDépart_id"))!!,adresseRepo.chercherParCodeAdresse(response.getInt("adresseDestination_id"))!!, voitureRepo.chercherParCode(response.getInt("codeVoiture"))!!,response.getDouble("prixTotal"),response.getInt("nombrePassagers"))
        }
        if (écocourseExistante.firstOrNull() != null){
            return true
        } else {
            return false
        }
    }

    override fun vérifierCourseParCode(code: Int):Boolean{
        var existance = chercherParCode(code)
        if (existance !=null){
            return true
        } else {
            return false
        }
    }

    override fun chercherCoursesDisponibles(code: Int): List<Écocourse> {
        var écocourses = db.query("SELECT * FROM écocourse where codeÉcocourse not in(select écocourse_code from passagers_écocourses where passager_code = ?) and codeConducteur != ?  and nombrePassagers > 0;",code,code) { response, _ ->
            Écocourse(response.getInt("codeÉcocourse"), utilisateurRepo.chercherParCode(response.getInt("codeConducteur"))!!, response.getLong("date"), adresseRepo.chercherParCodeAdresse(response.getInt("adresseDépart_id"))!!,adresseRepo.chercherParCodeAdresse(response.getInt("adresseDestination_id"))!!, voitureRepo.chercherParCode(response.getInt("codeVoiture"))!!,response.getDouble("prixTotal"),response.getInt("nombrePassagers"))
        }
        return écocourses
    }

    override fun chercherCoursesPassager(code: Int): List<Écocourse> {
        var écocourses = db.query("SELECT * FROM écocourse where codeÉcocourse in(select écocourse_code from passagers_écocourses where passager_code = ?) and codeConducteur != ?;",code,code) { response, _ ->
            Écocourse(response.getInt("codeÉcocourse"), utilisateurRepo.chercherParCode(response.getInt("codeConducteur"))!!, response.getLong("date"), adresseRepo.chercherParCodeAdresse(response.getInt("adresseDépart_id"))!!,adresseRepo.chercherParCodeAdresse(response.getInt("adresseDestination_id"))!!, voitureRepo.chercherParCode(response.getInt("codeVoiture"))!!,response.getDouble("prixTotal"),response.getInt("nombrePassagers"))
        }
        return écocourses
    }

    override fun chercherCoursesConducteur(code: Int): List<Écocourse> {
        var écocourses = db.query("SELECT * FROM écocourse where codeConducteur = ?;",code) { response, _ ->
            Écocourse(response.getInt("codeÉcocourse"), utilisateurRepo.chercherParCode(response.getInt("codeConducteur"))!!, response.getLong("date"), adresseRepo.chercherParCodeAdresse(response.getInt("adresseDépart_id"))!!,adresseRepo.chercherParCodeAdresse(response.getInt("adresseDestination_id"))!!, voitureRepo.chercherParCode(response.getInt("codeVoiture"))!!,response.getDouble("prixTotal"),response.getInt("nombrePassagers"))
        }
        return écocourses
    }

    override fun validerConducteur(codeÉcocourse : Int, codeConducteur: Int): Boolean{
        var écocourse = chercherParCode(codeÉcocourse)
        var conducteur = utilisateurRepo.chercherParCode(codeConducteur)
        if(écocourse != null){
            if(écocourse.conducteur == conducteur){
                return true
            }
        }else {
            throw RessourceInexistanteException("L'utilisateur $conducteur.nom n'est pas conducteur de l'écocourse $codeÉcocourse.")
        }
        return false
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

    override fun rejoindre(codeÉcocourse: Int,codeUtilisateur:Int) : Écocourse?{
        var résultat : Int
        if(codeUtilisateur < 1 || codeÉcocourse < 0){
            throw ErreurRequêteException("Erreur dans la requête code invalide")
        }
        if(chercherParCode(codeÉcocourse) == null){
            throw RessourceInexistanteException("Écocourse non trouvée")
        }
        if(utilisateurRepo.vérifierExistanceUtilisateur(codeUtilisateur) == false){
            throw RessourceInexistanteException("Utilisateur non trouvé")
        }
        if (validerConducteur(codeÉcocourse,codeUtilisateur)){
            throw ProduitExisteDéjàException("L'utilisateur est déja dans cette écocourse en tant que conducteur")
        }
        if (validerPassagerÉcocourse(codeÉcocourse,codeUtilisateur) != null){
            throw ProduitExisteDéjàException("L'utilisateur est déja dans cette écocourse")
        }

        résultat = db.update("INSERT INTO passagers_écocourses (passager_code, écocourse_code) VALUES (?,?);",codeUtilisateur,codeÉcocourse)
        résultat *= db.update("UPDATE écocourse SET nombrePassagers = nombrePassagers - 1  WHERE (codeÉcocourse = ?);",codeÉcocourse)

        if (résultat == 0){
            return null
        } else {
            return chercherParCode(codeÉcocourse)
        }


    }

    override fun quitter(codeÉcocourse: Int,codeUtilisateur:Int){
        if(codeUtilisateur < 1 || codeÉcocourse < 0){
            throw ErreurRequêteException("Erreur dans la requête code invalide")
        }
        if(chercherParCode(codeÉcocourse) == null){
            throw RessourceInexistanteException("Écocourse non trouvée")
        }
        if(utilisateurRepo.vérifierExistanceUtilisateur(codeUtilisateur) == false){
            throw RessourceInexistanteException("Utilisateur non trouvé")
        }
        if (validerPassagerÉcocourse(codeÉcocourse,codeUtilisateur) == null){
            throw ProduitExisteDéjàException("L'utilisateur n'est pas dans cette écocourse")
        }
        var résultat = db.update("DELETE FROM passagers_écocourses WHERE (passager_code = ?) and (écocourse_code = ?) ;",codeUtilisateur,codeÉcocourse)
        résultat *= db.update("UPDATE écocourse SET nombrePassagers = nombrePassagers + 1  WHERE (codeÉcocourse = ?);",codeÉcocourse)
        if (résultat == 0){
            throw ErreurRequêteException("La désinscription n'a pas pu être faite")
        }



    }

    fun validerPassagerÉcocourse(codeÉcocourse: Int,codeUtilisateur: Int) : Écocourse?{
        var validationPassager = db.query("SELECT écocourse.* FROM passagers_écocourses join écocourse on écocourse_code = codeÉcocourse where passager_code = ? and écocourse_code = ?;",codeUtilisateur,codeÉcocourse) { response, _ ->
            Écocourse(response.getInt("codeÉcocourse"), utilisateurRepo.chercherParCode(response.getInt("codeConducteur"))!!, response.getLong("date"), adresseRepo.chercherParCodeAdresse(response.getInt("adresseDépart_id"))!!,adresseRepo.chercherParCodeAdresse(response.getInt("adresseDestination_id"))!!, voitureRepo.chercherParCode(response.getInt("codeVoiture"))!!,response.getDouble("prixTotal"),response.getInt("nombrePassagers"))
        }
        return validationPassager.firstOrNull()
    }


}
