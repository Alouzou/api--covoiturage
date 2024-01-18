package com.écovoiturage.écovoiturage.contrôleurs

import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Écocourse
import com.écovoiturage.écovoiturage.services.ÉcocourseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:}")
class ÉcocourseContrôleur(val service: ÉcocourseService){

    @Operation(summary = "Obtenir la liste de toutes les écocourses")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Liste des écocourses trouvée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Liste inexistante ou vide"),
        ApiResponse(responseCode = "500", description = "Erreur dans la requete")
    ])
    @GetMapping("/écocourses")
    fun obtenirÉcocourses() : List<Écocourse>? {
        var listeÉcocourses = service.chercherTous()
        if (listeÉcocourses.isEmpty()){
            throw RessourceInexistanteException("Liste inexistante ou vide")
        }
        return listeÉcocourses
    }



    @Operation(summary = "Obtenir une écocourse à partir de son code")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourse trouvée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Écocourse non trouvée"),
    ])
    @GetMapping("/écocourses/{codeÉcocourse}")
    fun obtenirÉcocourseParCode(@PathVariable codeÉcocourse : Int) : Écocourse? {
        var écocourse = service.chercherParCode(codeÉcocourse)
        if (écocourse != null) {
            return écocourse
        } else {
            throw RessourceInexistanteException("Écocourse inexistante")
        }
    }



    @Operation(summary = "Obtenir les écocourses disponibles pour un utilisateur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourses trouvées"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a aucune écocourse")
    ])
    @GetMapping("/écocourses/utilisateurs/{code_utilisateur}/disponibles")
    fun obtenirÉcocoursesDisponibles(@PathVariable code_utilisateur : Int) : List<Écocourse>{
        var listeÉcocourses = service.chercherCoursesDisponibles(code_utilisateur)
        if (listeÉcocourses.isEmpty()){
            throw RessourceInexistanteException("Liste inexistante ou vide")
        }
        return listeÉcocourses
    }

    @Operation(summary = "Obtenir les écocourses d'un utilisateur en tant que passager")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourses trouvées"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a aucune écocourse")
    ])
    @GetMapping("/écocourses/utilisateurs/{code_utilisateur}/passager")
    fun obtenirÉcocoursesPassager(@PathVariable code_utilisateur : Int) : List<Écocourse>{
        var listeÉcocourses = service.chercherCoursesPassager(code_utilisateur)
        if (listeÉcocourses.isEmpty()){
            throw RessourceInexistanteException("Liste inexistante ou vide")
        }
        return listeÉcocourses
    }

    @Operation(summary = "Obtenir les écocourses d'un utilisateur en tant que conducteur")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourses trouvées"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "L'utilisateur n'a aucune écocourse")
    ])
    @GetMapping("/écocourses/utilisateurs/{code_utilisateur}/conducteur")
    fun obtenirÉcocoursesConducteur(@PathVariable code_utilisateur : Int) : List<Écocourse> {
        var listeÉcocourses = service.chercherCoursesConducteur(code_utilisateur)
        if (listeÉcocourses.isEmpty()) {
            throw RessourceInexistanteException("Liste inexistante ou vide")
        }
        return listeÉcocourses
    }


    @Operation(summary = "Créer une écocourse")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Écocourse créée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "409", description = "Le code de cette Écocourse existe déja")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/écocourses")
    fun ajouterÉcocourse(@RequestBody écocourse : Écocourse):Écocourse{

        var result = service.ajouter(écocourse)
        if (result == null){
            throw ErreurRequêteException("Écocourse non ajoutée !")
        } else {
            return result
        }

    }

    @Operation(summary = "Rejoindre une écocourse")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourse rejoint"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Écocourse non-trouvée"),
        ApiResponse(responseCode = "409", description = "L'utilisateur est déja dans cette écocourse")
    ])
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/écocourses/{codeÉcocourse}/rejoindre/{codeUtilisateur}")
    fun rejoindreÉcocourse(@PathVariable codeÉcocourse : Int,@PathVariable codeUtilisateur : Int):Écocourse{
        var result = service.rejoindre(codeÉcocourse,codeUtilisateur)
        if (result == null){
            throw ErreurRequêteException("L'utilisateur n'a pas pu joindre l'écocourse")
        } else {
            return result
        }

    }

    @Operation(summary = "Annuler sa réservation dans une écocourse")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourse quitée"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Écocourse non-trouvée/L'utilisateur n'est pas dans la course"),
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/écocourses/{codeÉcocourse}/retirer/{codeUtilisateur}")
    fun quitterÉcocourse(@PathVariable codeÉcocourse : Int,@PathVariable codeUtilisateur : Int){
        service.quitter(codeÉcocourse,codeUtilisateur)

    }


    @Operation(summary = "Modifier une écocourse")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Écocourse modifiée"),
        ApiResponse(responseCode = "201", description = "Écocourse crée car elle n'existait pas"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
    ])
    @PutMapping("/écocourses/{codeÉcocourse}")
    fun modifierÉcocourse(@PathVariable codeÉcocourse : Int, @RequestBody écocourse : Écocourse): ResponseEntity<Écocourse> {
        val existanceCourse = service.vérifierCourseParCode(codeÉcocourse)
        val uneCourse = service.modifier(codeÉcocourse, écocourse)
        if (existanceCourse){
            return ResponseEntity.status(HttpStatus.OK).body(uneCourse)
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(uneCourse)
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une écocourse")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Écocourse supprimé"),
        ApiResponse(responseCode = "400", description = "Erreur dans la requête"),
        ApiResponse(responseCode = "404", description = "Écocourse inexistante")
    ])
    @DeleteMapping("/écocourses/{codeÉcocourse}")
    fun supprimerÉcocourse(@PathVariable codeÉcocourse: Int){
        service.supprimer(codeÉcocourse)
    }

}