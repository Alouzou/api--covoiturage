package com.écovoiturage.écovoiturage.contrôleurs

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.modèles.Permis
import com.écovoiturage.écovoiturage.services.PermisService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:}")
class PermisContrôleur (val service: PermisService) {


    //possible de chercher un permis par son numéro de permis si voulu
    @Operation(summary = "Obtenir le permis de conduire par le code de l'utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Permis trouvé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas de permis/Utilisateur inexistant")
    ])
    @GetMapping("/utilisateurs/{code_utilisateur}/permis")
    fun obtenirPermisParCodeUtilisateur(@PathVariable code_utilisateur: Int): Permis? {
        var unPermis = service.chercherParCode(code_utilisateur)

        if (unPermis == null){
            throw RessourceInexistanteException("L'utilisateur n'a pas de permis de conduire")
        }
        return unPermis
    }

    @Operation(summary = "Ajouter une un permis de conduire à un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Permis ajouté"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant"),
        ApiResponse(responseCode = "409", description = "L'utilisateur à déja un permis/ Un permis avec ce code existe déjà")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/utilisateurs/{code_utilisateur}/permis")
    fun ajouterPermisUtilisateur(@RequestBody permis: Permis, @PathVariable code_utilisateur: Int): Permis? {
        var unPermis = service.ajouter(permis,code_utilisateur)
        if (unPermis == null){
            throw ErreurRequêteException("Erreur dans la requête")
        }
        return unPermis
    }

    @Operation(summary = "Modifier le permis de conduire d'un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Permis modifié"),
        ApiResponse(responseCode = "201", description = "Permis ajouté car l'utilisateur n'avait pas de permis de conduire"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant")
    ])
    @PutMapping("/utilisateurs/{code_utilisateur}/permis")
    fun modifierPermisUtilisateur(@RequestBody permis: Permis, @PathVariable code_utilisateur: Int): ResponseEntity<Permis> {
        var existancePermis = service.vérifierExistancePermis(code_utilisateur)
        var unPermis = service.modifier(code_utilisateur,permis)
        if (existancePermis){
            return ResponseEntity.status(HttpStatus.OK).body(unPermis)
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(unPermis)
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Enlever un permis de conduire à un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Permis supprimé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas de permis/Utilisateur inexistant")
    ])
    @DeleteMapping("/utilisateurs/{code_utilisateur}/permis")
    fun supprimerPermisDunUtilisateur(@PathVariable code_utilisateur: Int) {
        service.supprimer(code_utilisateur)
    }





}