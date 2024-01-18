package com.écovoiturage.écovoiturage.contrôleurs

import Rôle
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Permis
import com.écovoiturage.écovoiturage.services.RôleService
import com.écovoiturage.écovoiturage.services.UtilisateurService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.base-path:}")
class UtilisateurContrôleur(val service: UtilisateurService,val serviceRôle:RôleService) {
    @Operation(summary = "Obtenir la liste des utilisateurs")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Liste des utilisateurs trouvée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Liste inexistante ou vide")
    ])
    @GetMapping("/utilisateurs")
    fun chercherTousUtilisateurs() = service.chercherTous()


    @Operation(summary = "Obtenir un utilisateur à partir de son code")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant")
    ])
    @GetMapping("/utilisateurs/{code_utilisateur}")
    fun chercherUtilisateurParCode(@PathVariable code_utilisateur: Int) = service.chercherParCode(code_utilisateur)?: throw RessourceInexistanteException("Utilisateur inexistant")


    @Operation(summary = "Ajouter un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Utilisateur créé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "409", description = "Un utilisateur avec ce code existe déjà")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/utilisateurs")
    fun ajouterUtilisateur(@RequestBody utilisateur: Utilisateur) = service.ajouter(utilisateur)?: throw ErreurRequêteException("Erreur dans la requête")


    @Operation(summary = "Modifier un utilisateur à partir de son code")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Utilisateur modifié"),
        ApiResponse(responseCode = "201", description = "Utilisateur créé car il n'existait pas"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête")
    ])
    @PutMapping("/utilisateurs/{code_utilisateur}")
    fun modifierUtilisateur(@RequestBody utilisateur: Utilisateur, @PathVariable code_utilisateur: Int): ResponseEntity<Utilisateur> {
        var vérificationUtilisateur = service.vérifierExistanceUtilisateur(code_utilisateur)
        var resultat : Utilisateur?
        if (vérificationUtilisateur){
            resultat = service.modifier(code_utilisateur, utilisateur)
            if (resultat == null){
                throw ErreurRequêteException("Erreur dans la requête")
            }
            return ResponseEntity.status(HttpStatus.OK).body(resultat)
        } else {
            resultat = service.ajouter(utilisateur)
            if (resultat == null){
                throw ErreurRequêteException("Erreur dans la requête")
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(resultat)
        }
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un utilisateur à partir de son code")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant")
    ])
    @DeleteMapping("/utilisateurs/{code_utilisateur}")
    fun supprimerUtilisateur(@PathVariable code_utilisateur: Int) = service.supprimer(code_utilisateur)?: throw RessourceInexistanteException("Utilisateur inexistant")




    // partie pour les roles
    @Operation(summary = "Obtenir le rôle d'un utilisateur par son code")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Rôle trouvé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas de rôle")
    ])
    @GetMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun obtenirRôleParCodeUtilisateur(@PathVariable code_utilisateur: Int):List<Rôle>{
        var listeRôles = serviceRôle.chercherParCode(code_utilisateur)

        if (listeRôles.isEmpty()){
            throw RessourceInexistanteException("L'utilisateur n'a pas de rôle")
        }
        return listeRôles
    }

    @Operation(summary = "Ajouter un rôle à un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Rôle ajouté"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant"),
        ApiResponse(responseCode = "409", description = "L'utilisateur à déja ce rôle")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun ajouterRôleUtilisateur(@RequestBody rôle: Rôle, @PathVariable code_utilisateur: Int):Rôle? {
        var unRôle = serviceRôle.ajouter(rôle,code_utilisateur)

        if (unRôle == null){
            throw ErreurRequêteException("Erreur dans la requête")
        }
        return unRôle
    }



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Enlever un rôle à un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Rôle supprimé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas ce rôle")
    ])
    @DeleteMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun supprimerRôleDunUtilisateur(@RequestBody rôle: Rôle, @PathVariable code_utilisateur: Int) {
        serviceRôle.supprimer(rôle,code_utilisateur)
    }


























}