package com.écovoiturage.écovoiturage.contrôleurs

import com.fasterxml.jackson.databind.ObjectMapper
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse

import com.écovoiturage.écovoiturage.modèles.Permis
import com.écovoiturage.écovoiturage.services.PermisService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

import org.springframework.http.MediaType

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

import org.mockito.ArgumentMatchers.anyInt
import org.springframework.security.test.context.support.WithMockUser
import java.util.Date


@SpringBootTest
@AutoConfigureMockMvc
class PermisContrôleurTest {

    @Autowired
    private lateinit var mapper: ObjectMapper


    @MockBean
    lateinit var service: PermisService

    @Autowired
    private lateinit var mockMvc: MockMvc


    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 qui a un permis de conduire lorsqu'on effectue une requête GET avec le code 1 alors on obtient un JSON qui contient un permis de conduire qui est associé à l'utilisateur avec le code 1 ainsi qu'un code de retour 200`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.chercherParCode(1)).thenReturn(permis)

        mockMvc.perform(get("/utilisateurs/1/permis").with(csrf()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.utilisateur.code").value(1))

    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 qui a un permis de conduire lorsqu'on effectue une requête GET avec le code d'utilisateur non valide comme 0 alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`() {
        Mockito.`when`(service.chercherParCode(0)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(get("/utilisateurs/0/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is ErreurRequêteException)
                    assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de permis de conduire lorsqu'on effectue une requête GET avec le code d'utilisateur 1 alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'a pas de permis »`() {
        Mockito.`when`(service.chercherParCode(anyInt())).thenReturn(null)

        mockMvc.perform(get("/utilisateurs/1/permis")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    assertEquals("L'utilisateur n'a pas de permis de conduire", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de permis de conduire lorsqu'on effectue une requête POST avec le code d'utilisateur 1 et  un permis de conduire alors on obtient un JSON avec le permis associé à l'utilisateur 1 et un code de retour 201`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101 ,20240101,"5")
        Mockito.`when`(service.ajouter(permis, 1)).thenReturn(permis)


        mockMvc.perform(post("/utilisateurs/1/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.utilisateur.code").value(1))

    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de permis de conduire lorsqu'on effectue une requête POST avec le code d'utilisateur invalide et un permis de conduire alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.ajouter(permis,-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))


        mockMvc.perform(post("/utilisateurs/-1/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is ErreurRequêteException)
                    assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur lorsqu'on effectue une requête POST avec le code d'utilisateur qui n'existe pas et un permis de conduire alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'existe pas »`() {
        val permis = Permis("PPPPPPP", Utilisateur(18,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101 ,20240101,"5")
        Mockito.`when`(service.ajouter(permis,18)).thenThrow(RessourceInexistanteException("L'utilisateur n'existe pas"))

        mockMvc.perform(post("/utilisateurs/18/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    assertEquals("L'utilisateur n'existe pas", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 et un permis de conduire lorsqu'on effectue une requête POST avec le code d'utilisateur 1 et un permis de conduire alors on obtient un code de retour 409 et le message d'erreur « L'utilisateur à déja un permis de conduire »`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.ajouter(permis,18)).thenThrow(ProduitExisteDéjàException("L'utilisateur à déja un permis de conduire"))

        mockMvc.perform(post("/utilisateurs/18/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isConflict)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    assertEquals("L'utilisateur à déja un permis de conduire", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 et qui a un permis de conduire lorsqu'on effectue une requête PUT avec le code d'utilisateur 1 et un permis de conduire avec une date d'expiration qui est 2025-01-01 alors on obtient un JSON avec le permis de conduire associé à l'utilisateur avec le code 1 et un code de retour 200`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.vérifierExistancePermis(anyInt())).thenReturn(true)
        Mockito.`when`(service.modifier(1,permis)).thenReturn(permis)

        mockMvc.perform(put("/utilisateurs/1/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.utilisateur.code").value(1))
                .andExpect(jsonPath("$.date_expiration").value(20240101))

    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de permis de conduire lorsqu'on effectue une requête PUT avec le code d'utilisateur 1 et un permis de conduire alors on obtient un JSON avec le permis de conduire associé à l'utilisateur avec le code 1 et un code de retour 201`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.vérifierExistancePermis(anyInt())).thenReturn(false)
        Mockito.`when`(service.modifier(1,permis)).thenReturn(permis)

        mockMvc.perform(put("/utilisateurs/1/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.utilisateur.code").value(1))

    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 et un permis de conduire lorsqu'on effectue une requête PUT avec le code d'utilisateur non valide et un permis de conduire alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.vérifierExistancePermis(anyInt())).thenReturn(false)
        Mockito.`when`(service.modifier(0,permis)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(put("/utilisateurs/0/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is ErreurRequêteException)
                    assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec un code qui n'est pas dans la liste des utilisateurs et un permis de conduire lorsqu'on effectue une requête PUT avec le code de l'utilisateur qui n'est pas dans la liste et un permis de conduire alors on obtient un code de retour 404 et le message d'erreur « Utilisateur inexistant »`() {
        val permis = Permis("PPPPPPP", Utilisateur(18,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.vérifierExistancePermis(anyInt())).thenReturn(false)
        Mockito.`when`(service.modifier(18,permis)).thenThrow(RessourceInexistanteException("Utilisateur inexistant"))

        mockMvc.perform(put("/utilisateurs/18/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    assertEquals("Utilisateur inexistant", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 et un permis de conduire lorsqu'on effectue une requête DELETE avec le code d'utilisateur 1 alors on obtient un code de retour 204`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.supprimer( 1)).thenReturn(permis)

        mockMvc.perform(delete("/utilisateurs/1/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isNoContent)


    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 et un permis de conduire lorsqu'on effectue une requête DELETE avec le code d'utilisateur invalide alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.supprimer( 0)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))


        mockMvc.perform(delete("/utilisateurs/0/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is ErreurRequêteException)
                    assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/permis")
    fun `Étant donné un utilisateur avec le code 1 et qui n'a pas de permis de conduire lorsqu'on effectue une requête DELETE avec le code d'utilisateur 1 alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'a pas de permis»`() {
        val permis = Permis("PPPPPPP", Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101,20240101,"5")
        Mockito.`when`(service.supprimer( 1)).thenThrow(RessourceInexistanteException("L'utilisateur n'a pas de permis"))


        mockMvc.perform(delete("/utilisateurs/1/permis").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(permis)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    assertEquals("L'utilisateur n'a pas de permis", résultat.resolvedException?.message)
                }

    }

}