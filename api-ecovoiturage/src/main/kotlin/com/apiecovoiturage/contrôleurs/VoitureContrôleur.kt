package com.écovoiturage.écovoiturage.contrôleurs

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Voiture
import com.écovoiturage.écovoiturage.services.VoitureService
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
class VoitureContrôleur(val service: VoitureService) {


    @Operation(summary = "Obtenir la liste des voitures d'un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Liste des voitures trouvée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Liste inexistante ou vide/ Utilisateur inexistant")
    ])
    @GetMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun chercherToutesVoitures(@PathVariable code_utilisateur: Int) : List<Voiture> {
        var listeVoitures = service.chercherToutVoituresUtilisateur(code_utilisateur)
        if (listeVoitures.isEmpty()){
            throw RessourceInexistanteException("L'utilisateur n'a pas de voitures")
        }
        return listeVoitures
    }


    //temporaire
    @GetMapping("/voitures/{code_voiture}")
    fun chercherVoitureParCode(@PathVariable code_voiture: Int) : Voiture? {
        var listeVoitures = service.chercherParCode(code_voiture)
        return listeVoitures
    }




    @Operation(summary = "Ajouter une voiture à un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Voiture créé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "409", description = "Une voiture avec ce code existe déjà")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun ajouterVoiture(@RequestBody voiture: Voiture,@PathVariable code_utilisateur: Int) = service.ajouterAUtilisateur(voiture,code_utilisateur)?: throw ErreurRequêteException("Erreur dans la requête")


    @Operation(summary = "Modifier une voiture à partir du code de son utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Voiture modifiée"),
        ApiResponse(responseCode = "201", description = "Voiture créé car elle n'existait pas"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Utilisateur inexistant")

    ])
    @PutMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun modifierVoiture(@RequestBody voiture: Voiture, @PathVariable code_utilisateur: Int): ResponseEntity<Voiture>{
        var existanceVoiture = service.vérifierExistanceVoiture(code_utilisateur,voiture.code)
        var uneVoiture = service.modifier(code_utilisateur,voiture)
        if (existanceVoiture){
            return ResponseEntity.status(HttpStatus.OK).body(uneVoiture)
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(uneVoiture)
        }

    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une voiture à partir du code de son utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Voiture supprimée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Voiture inexistante")
    ])
    @DeleteMapping("/utilisateurs/{code_utilisateur}/voitures/{code_voiture}")
    fun supprimerVoiture(@PathVariable code_voiture: Int,@PathVariable code_utilisateur: Int){
        service.supprimerAvecCodeUtilisateur(code_voiture,code_utilisateur)
    }


}