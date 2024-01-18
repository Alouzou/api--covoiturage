package com.écovoiturage.écovoiturage.contrôleurs

import Rôle
import com.fasterxml.jackson.databind.ObjectMapper
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.services.RôleService
import com.écovoiturage.écovoiturage.services.UtilisateurService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

@SpringBootTest
@AutoConfigureMockMvc
class UtilisateurContrôleurTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var service: UtilisateurService

    @MockBean
    lateinit var serviceRôle: RôleService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    //@GetMapping("/utilisateurs")
    fun `Étant donné une liste d'utilisateurs lorsqu'on effectue une requête GET alors on obtient un JSON qui contient une liste d'utilisateurs et un code de retour 200`(){
        val utilisateurs = mutableListOf(
            Utilisateur(1, "Tremblay", "Pierre", "pierre@test.ca", "514-123-4567", 1, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")),
            Utilisateur(2, "Doe", "John", "john@test.ca", "514-987-6543", 5, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(2, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")),
            Utilisateur(3, "L'éponge", "Bob", "bob@test.ca", "514-555-5555", 2, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(3, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")),
        )
        Mockito.`when`(service.chercherTous()).thenReturn(utilisateurs)
        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }
    @Test
    //@GetMapping("/utilisateurs")
    fun `Étant donné une liste d'utilisateurs vide lorsqu'on effectue une requête GET alors on obtient un code de retour 404 et le message d'erreur « Liste inexistante ou vide »`(){
        val utilisateurs = emptyList<Utilisateur>()
        Mockito.`when`(service.chercherTous()).thenThrow(RessourceInexistanteException("Liste inexistante ou vide"))
        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Liste inexistante ou vide", résultat.resolvedException?.message)
                }
    }
    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur dont le code est 1 lorsqu'on effectue une requête GET alors on obtient un JSON qui contient cet utilisateur et un code de retour 200`(){
        val utilisateur = Utilisateur(1, "Tremblay", "Pierre", "pierre@test.ca", "514-123-4567", 1, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.chercherParCode(1)).thenReturn(utilisateur)
        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1))
    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur inexistant dont le code est 123456 lorsqu'on effectue une requête GET alors on obtient un code de retour 404 et le message d'erreur « Utilisateur inexistant »`(){
        Mockito.`when`(service.chercherTous()).thenReturn(null)
        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/123456")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                Assertions.assertEquals("Utilisateur inexistant", résultat.resolvedException?.message)
            }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs")
    fun `Étant donné un nouvel utilisateur avec le code 9 lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un JSON qui contient l'utilisateur avec le code 9 et un code de retour 201`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-444-4444", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.ajouter(utilisateur)).thenReturn(utilisateur)
        mockMvc.perform(post("/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(utilisateur)))
            .andExpect(status().isCreated)
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs")
    fun `Étant donné un utilisateur avec le code 9 et qu'il existe déjà un utilisateur avec le code 9 lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 409 et le message d'erreur « Un utilisateur avec ce code existe déjà »`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-444-4444", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.ajouter(utilisateur)).thenThrow(ProduitExisteDéjàException("Un utilisateur avec ce code existe déjà"))
        mockMvc.perform(post("/utilisateurs").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isConflict)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    Assertions.assertEquals("Un utilisateur avec ce code existe déjà", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs")
    fun `Étant donné un nouvel utilisateur avec le code 9 lorsqu'on effectue une requête POST pour l'ajouter et que le champ nom est manquant dans le JSON envoyé alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-444-4444", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.ajouter(utilisateur)).thenThrow(ErreurRequêteException("Erreur dans la requête"))
        mockMvc.perform(post("/utilisateurs").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Erreur dans la requête", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur avec le code 9 dont le téléphone est « 5145555555 » lorsqu'on effectue une requête PUT pour modifier le téléphone pour « 5144444444 » alors on obtient un JSON qui contient l'utilisateur avec le code 9 dont le téléphone a été modifié ainsi qu'un code de retour 200`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-555-5555", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.vérifierExistanceUtilisateur(ArgumentMatchers.anyInt())).thenReturn(true)
        Mockito.`when`(service.modifier(9,utilisateur)).thenReturn(utilisateur)


        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/9").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.téléphone").value("514-555-5555"))
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur avec le code 9 et qu'il n'existe pas déjà un utilisateur avec le code 9 lorsqu'on effectue une requête PUT pour saisir les données de l'utilisateur alors on obtient un JSON qui contient l'utilisateur avec le code 9 et un code de retour 201`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-555-5555", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.vérifierExistanceUtilisateur(ArgumentMatchers.anyInt())).thenReturn(false)
        Mockito.`when`(service.modifier(9,utilisateur)).thenReturn(utilisateur)
        Mockito.`when`(service.ajouter(utilisateur)).thenReturn(utilisateur)


        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/9").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.téléphone").value("514-555-5555"))
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur avec le code 9 et qu'il n'existe pas déjà un utilisateur avec le code 9 lorsqu'on effectue une requête PUT pour saisir les données de l'utilisateur et que le champ courriel est manquant dans le JSON envoyé alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-555-5555", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.vérifierExistanceUtilisateur(ArgumentMatchers.anyInt())).thenReturn(false)
        Mockito.`when`(service.modifier(9,utilisateur)).thenReturn(null)



        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/9").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Erreur dans la requête", résultat.resolvedException?.message)
                }

    }


    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur avec le code 9 lorsqu'on effectue une requête DELETE alors on obtient un code de retour 204`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-555-5555", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.supprimer(9)).thenReturn(utilisateur)

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/9").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isNoContent)
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur avec le code 11 et qu'il n'existe pas d'utilisateur avec ce code lorsqu'on effectue une requête DELETE alors on obtient un code de retour 404 avec le message d'erreur « Utilisateur inexistant »`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-555-5555", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.supprimer(11)).thenThrow(RessourceInexistanteException("Utilisateur inexistant"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/11").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Utilisateur inexistant", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}")
    fun `Étant donné un utilisateur avec le code 9 lorsqu'on effectue une requête DELETE et que le code d'utilisateur est invalide comme -1 alors on obtient un code de retour 400 avec le message d'erreur « Erreur dans la requête »`(){
        val utilisateur = Utilisateur(9, "Toto", "Tata", "toto@tata.ca", "514-555-5555", 0, "https://images.mubicdn.net/images/cast_member/9020/cache-148043-1465730065/image-w856.jpg?size=800x", Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada"))
        Mockito.`when`(service.supprimer(-1)).thenThrow(ErreurRequêteException("Erreur dans la requête"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/-1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(utilisateur)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Erreur dans la requête", résultat.resolvedException?.message)
                }
    }








    // partie pour rôles

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 qui a le rôle de passager lorsqu'on effectue une requête GET alors on obtient un JSON qui contient un rôle passager associé à l'utilisateur avec le code 1 ainsi qu'un code de retour 200`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        val rôles = listOf(Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager"))

        Mockito.`when`(serviceRôle.chercherParCode(1)).thenReturn(rôles)

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1/rôles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rôle").value("passager"))

    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 qui a le rôle de passager lorsqu'on effectue une requête GET avec le code d'utilisateur non valide comme -1 alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        val rôles = listOf(Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager"))
        Mockito.`when`(serviceRôle.chercherParCode(-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/-1/rôles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de rôle lorsqu'on effectue une requête GET avec le code d'utilisateur 1 alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'a pas de rôle »`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        val rôles = emptyList<Rôle>()
        Mockito.`when`(serviceRôle.chercherParCode(1)).thenThrow(RessourceInexistanteException("L'utilisateur n'a pas de rôle"))

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1/rôles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas de rôle", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de rôle lorsqu'on effectue une requête POST avec le code d'utilisateur 1 et le rôle passager alors on obtient un JSON avec le rôle passager associé à l'utilisateur 1 et un code de retour 201`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        Mockito.`when`(serviceRôle.ajouter(unRôle,1)).thenReturn(unRôle)

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/1/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.utilisateur.code").value(1))
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 qui n'a pas de rôle lorsqu'on effectue une requête POST avec le code d'utilisateur invalide comm -1 alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        Mockito.`when`(serviceRôle.ajouter(unRôle,-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/-1/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur lorsqu'on effectue une requête POST avec le code d'utilisateur qui n'existe pas comme 18 et le rôle passager alors on obtient un code de retour 404 et le message d'erreur « Utilisateur inexistant »`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        Mockito.`when`(serviceRôle.ajouter(unRôle,18)).thenThrow(RessourceInexistanteException("L'utilisateur n'existe pas"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/18/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'existe pas", résultat.resolvedException?.message)
                }


    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 et le rôle passager lorsqu'on effectue une requête POST avec le code d'utilisateur 1 et le rôle passager alors on obtient un code de retour 409 et le message d'erreur « L'utilisateur à déja ce rôle »`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        Mockito.`when`(serviceRôle.ajouter(unRôle,1)).thenThrow(ProduitExisteDéjàException("L'utilisateur à déja ce rôle"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/1/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    Assertions.assertEquals("L'utilisateur à déja ce rôle", résultat.resolvedException?.message)
                }

    }


    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 et le rôle passager lorsqu'on effectue une requête DELETE avec le code d'utilisateur 1 et le rôle passager alors on obtient un code de retour 204`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        Mockito.`when`(serviceRôle.supprimer(unRôle,1)).thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/1/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 et le rôle passager lorsqu'on effectue une requête DELETE avec le code d'utilisateur non valide comme -1 alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"passager")
        Mockito.`when`(serviceRôle.supprimer(unRôle,-1)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/-1/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/rôles")
    fun `Étant donné un utilisateur avec le code 1 et le rôle passager lorsqu'on effectue une requête DELETE avec le code d'utilisateur 1 et le rôle conducteur alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'a pas ce rôle»`() {
        val unRôle = Rôle(Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")),"conducteur")
        Mockito.`when`(serviceRôle.supprimer(unRôle,1)).thenThrow(RessourceInexistanteException("L'utilisateur n'a pas ce rôle"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/1/rôles").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(unRôle)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas ce rôle", résultat.resolvedException?.message)
                }

    }
}