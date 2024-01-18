package com.écovoiturage.écovoiturage.contrôleurs

import com.fasterxml.jackson.databind.ObjectMapper
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.dao.SourceDonnées
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.services.UtilisateurService
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class UtilisateurContrôleurUtilisateursAuthentifiésTest {
    @Autowired
    private lateinit var mapper: ObjectMapper
    @MockBean
    lateinit var service: UtilisateurService
    @Autowired
    private lateinit var mockMvc: MockMvc
    @WithMockUser
    @Test
    // @PostMapping("/utilisateurs")
    fun `Étant donné un utilisateur authentifié et l'utilisateur dont le code est 60 et qui n'est pas dans la BD lorsque l'utilisateur effectue une requête POST pour l'ajouter alors il obtient un JSON qui contient un utilisateur dont le code est 60 et un code de retour 201` (){
        val utilisateur = Utilisateur(60,"Lewis", "Ethan", "ethan@example.com", "654-987-3210",33,null, Adresse(10,"Appt 210", "210", "Champs-Élysées", "Paris", "FR", "75008", "FR"))

        Mockito.`when`(service.ajouter(utilisateur)).thenReturn(utilisateur)

        mockMvc.perform(
                MockMvcRequestBuilders.post("/utilisateurs").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }
    @WithMockUser
    @Test
    // @PostMapping("/utilisateurs")
    fun `Étant donné un utilisateur authentifié et l'utilisateur dont le code est 12, qui n'est pas dans la BD et qui n'entre pas les infos nécessaires pour l'adresse, lorsque l'utilisateur effectue une requête POST pour l'ajouter alors il obtient un code de retour 400` (){
        val utilisateurStr = "{\"code\": 12," +
                "\"nom\": \"John\"," +
                "\"prénom\": \"Doe\"," +
                "\"courriel\": \"john@example.com\"," +
                "\"téléphone\": \"123-456-7890\"," +
                "\"nombreCourse\": 44," +
                "\"urlPhoto\": null," +
                "\"adresse\": {}" +
                "}"

        mockMvc.perform(
                MockMvcRequestBuilders.post("/utilisateurs").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(utilisateurStr))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
    @WithMockUser
    @Test
    // @PutMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur authentifié et l'utilisateur dont le code est 1 lorsque l'utilisateur effectue une requête PUT pour modifier son adresse alors il obtient un JSON qui contient un utilisateur avec l'adresse mise à jour et un code de retour 200` (){
        val utilisateur = Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "10", "Spring Street", "New York", "NY", "10001", "US"))
        Mockito.`when`(service.vérifierExistanceUtilisateur(ArgumentMatchers.anyInt())).thenReturn(true)
        Mockito.`when`(service.modifier(1, utilisateur)).thenReturn(utilisateur)

        mockMvc.perform(
                MockMvcRequestBuilders.put("/utilisateurs/1").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(utilisateur)))
                        .andExpect(MockMvcResultMatchers.status().isOk)
    }
}