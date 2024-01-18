package com.écovoiturage.écovoiturage.contrôleurs

import com.fasterxml.jackson.databind.ObjectMapper
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.Modèle.Écocourse
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Permis
import com.écovoiturage.écovoiturage.modèles.Voiture
import com.écovoiturage.écovoiturage.services.AdresseService
import com.écovoiturage.écovoiturage.services.VoitureService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

@SpringBootTest
@AutoConfigureMockMvc
class VoitureContrôleurTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var service: VoitureService

    @Autowired
    private lateinit var mockMvc: MockMvc



    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné une liste de voitures associées à l'utilisateur dont le code est 1 lorsqu'on effectue une requête GET alors on obtient un JSON qui contient une liste de voitures dont le code d'utilisateur est 1 et un code de retour 200`(){
        val listeVoitures = mutableListOf(
                Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),
                Voiture(2,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        )
        Mockito.`when`(service.chercherToutVoituresUtilisateur(1)).thenReturn(listeVoitures)

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1/voitures")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].conducteur.code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Int>(listeVoitures.size))) //ca c'est pour comparer la taille de la liste retournée avec la taille de la listeÉcocourses
    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné une liste de voitures associées à l'utilisateur dont le code est 1 qui est vide lorsqu'on effectue une requête GET alors on obtient un un code de retour 404 et le message d'erreur « L'utilisateur n'a pas de voiture »`(){
        val listeVoitures = emptyList<Voiture>()
        Mockito.`when`(service.chercherToutVoituresUtilisateur(1)).thenReturn(listeVoitures)

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1/voitures")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas de voitures", résultat.resolvedException?.message)
                }
    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné une liste de voitures associées à un utilisateur inexistant dont le code est 54 lorsqu'on effectue une requête GET alors on obtient un un code de retour 404 et le message d'erreur « L'utilisateur n'existe pas »`(){
        val listeVoitures = mutableListOf(
                Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),
                Voiture(2,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        )
        Mockito.`when`(service.chercherToutVoituresUtilisateur(54)).thenThrow(RessourceInexistanteException("L'utilisateur n'existe pas"))

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/54/voitures")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'existe pas", résultat.resolvedException?.message)
                }
    }


    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné une liste de voitures associées à l'utilisateur dont le code est invalide comme -1 lorsqu'on effectue une requête GET alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`(){
        val listeVoitures = mutableListOf(
                Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),
                Voiture(2,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        )
        Mockito.`when`(service.chercherToutVoituresUtilisateur(-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/-1/voitures")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur à qui on veut ajouter une voiture lorsqu'on effectue une requête POST avec une voiture alors on obtient un code de retour 201 et un JSON qui contient la voiture ajoutée et le code de l'utilisateur`(){
        val uneVoiture = Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.ajouterAUtilisateur(uneVoiture,1)).thenReturn(uneVoiture)

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/1/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.conducteur.code").value(1))
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur avec un code invalide comme -1 à qui on veut ajouter une voiture lorsqu'on effectue une requête POST alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`(){
        val uneVoiture = Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.ajouterAUtilisateur(uneVoiture,-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/-1/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur avec une voiture avec le code 2 lorsqu'on effectue une requête POST avec une voiture avec le même code de voiture alors on obtient un code de retour 409 et le message d'erreur « L'utilisateur a déjà cette voiture »`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.ajouterAUtilisateur(uneVoiture,1)).thenThrow(ProduitExisteDéjàException("L'utilisateur a déjà cette voiture"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/1/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isConflict)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    Assertions.assertEquals("L'utilisateur a déjà cette voiture", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur avec une voiture avec le code 2 lorsqu'on effectue une requête PUT pour modifier la couleur pour orange alors on obtient un code de retour 200 et un JSON qui contient la voiture modifiée avec la nouvelle information`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.vérifierExistanceVoiture(1,uneVoiture.code)).thenReturn(true)
        Mockito.`when`(service.modifier(1,uneVoiture)).thenReturn(uneVoiture)

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/1/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.conducteur.code").value(1))
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur sans voiture lorsqu'on effectue une requête PUT pour obtient un code de retour 201 et un JSON qui contient la nouvelle voiture ajoutée`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.vérifierExistanceVoiture(1,uneVoiture.code)).thenReturn(false)
        Mockito.`when`(service.modifier(1,uneVoiture)).thenReturn(uneVoiture)

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/1/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.conducteur.code").value(1))
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur avec un code invalide comme -1 lorsqu'on effectue une requête PUT alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.vérifierExistanceVoiture(1,uneVoiture.code)).thenReturn(false)
        Mockito.`when`(service.modifier(-1,uneVoiture)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/-1/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/voitures")
    fun `Étant donné un utilisateur avec un code inexistant comme 180 lorsqu'on effectue une requête PUT alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'existe pas »`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.vérifierExistanceVoiture(1,uneVoiture.code)).thenReturn(false)
        Mockito.`when`(service.modifier(180,uneVoiture)).thenThrow(RessourceInexistanteException("L'utilisateur n'existe pas"))

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/180/voitures").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'existe pas", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/voitures/{code_voiture}")
    fun `Étant donné un utilisateur avec le code 2 et une voiture avec le code 1 lorsqu'on effectue une requête DELETE avec le code d'utilisateur 2 et le code de voiture 1 alors on obtient un code de retour 204`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.supprimerAvecCodeUtilisateur(uneVoiture.code,1)).thenReturn(uneVoiture)

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/1/voitures/2").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isNoContent)
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/voitures/{code_voiture}")
    fun `Étant donné un utilisateur avec le code invalide comme -1 et une voiture avec le code 1 lorsqu'on effectue une requête DELETE alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.supprimerAvecCodeUtilisateur(uneVoiture.code,-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/-1/voitures/2").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/voitures/{code_voiture}")
    fun `Étant donné un utilisateur avec le code 1 et une voiture avec un code inexistant comme 78 lorsqu'on effectue une requête DELETE alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'a pas de voitures avec ce code »`(){
        val uneVoiture = Voiture(2,"Toyota", "2019", "Corolla", "Orange", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")))
        Mockito.`when`(service.supprimerAvecCodeUtilisateur(78,1)).thenThrow(RessourceInexistanteException("L'utilisateur n'a pas de voitures avec ce code"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/1/voitures/78").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(uneVoiture)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas de voitures avec ce code", résultat.resolvedException?.message)
                }
    }






}