package com.écovoiturage.écovoiturage.contrôleurs

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.services.AdresseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:}")
class AdresseContrôleur(val service: AdresseService) {

    @Operation(summary = "Obtenir l'adresse d'un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Adresse trouvée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Adresse inexistante/Utilisateur inexistant")
    ])
    @GetMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun obtenirAdresseUtilisateur(@PathVariable code_utilisateur: Int) :Adresse {
        var uneAdresse = service.chercherParCode(code_utilisateur)
        if (uneAdresse == null){
            throw RessourceInexistanteException("L'utilisateur n'a pas d'adresse associé")
        }
        return uneAdresse
    }

    @Operation(summary = "Ajouter une adresse à un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Adresse crée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant"),
        ApiResponse(responseCode = "409", description = "L'utilisateur à déja une adresse")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun ajouterAdresseUtilisateur(@RequestBody adresse: Adresse, @PathVariable code_utilisateur: Int):Adresse? {
        var uneAdresse = service.ajouter(code_utilisateur,adresse)
        if (uneAdresse == null){
            throw ErreurRequêteException("Erreur dans la requête")
        }
        return uneAdresse
    }

    @Operation(summary = "Modifier une adresse à partir de son utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Adresse modifiée"),
        ApiResponse(responseCode = "201", description = "Adresse crée car elle n'existait pas"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant")
    ])
    @PutMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun modifierAdresseUtilisateur(@RequestBody adresse: Adresse, @PathVariable code_utilisateur: Int): ResponseEntity<Adresse> {
        var existanceAdresse = service.vérifierExistanceAdresse(code_utilisateur)
        var uneAdresse = service.modifier(code_utilisateur,adresse)


        if (existanceAdresse){
            return ResponseEntity.status(HttpStatus.OK).body(uneAdresse)
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(uneAdresse)
        }
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une adresse à partir de son utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Adresse supprimée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Adresse inexistante/Utilisateur inexistant")
    ])
    @DeleteMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun supprimerAdresseDunUtilisateur(@PathVariable code_utilisateur: Int) {
        service.supprimer(code_utilisateur)
    }

}