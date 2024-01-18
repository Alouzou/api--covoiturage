package com.écovoiturage.écovoiturage.contrôleurs

import com.fasterxml.jackson.databind.ObjectMapper
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Permis
import com.écovoiturage.écovoiturage.services.AdresseService
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
class AdresseContrôleurTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var service: AdresseService

    @Autowired
    private lateinit var mockMvc: MockMvc



    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné une adresse associée à l'utilisateur dont le code est 1 lorsqu'on effectue une requête GET alors on obtient un JSON qui contient l'adresse associée et un code de retour 200`(){
        val adresse = Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.chercherParCode(1)).thenReturn(adresse)

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1/adresse"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))

    }

    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné une adresse manquante à l'utilisateur dont le code est 2 lorsqu'on effectue une requête GET alors on obtient un code de retour 404 et le message d'erreur « L'utilisateur n'a pas d'adresse associé »`(){
        val adresse = Adresse(1, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.chercherParCode(2)).thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas d'adresse associé", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@GetMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné une adresse associée à l'utilisateur dont le code est 1 lorsqu'on effectue une requête GET et que le code de l'utilisateur est invalide comme 0 alors on obtient de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`(){
        Mockito.`when`(service.vérifierExistanceAdresse(ArgumentMatchers.anyInt())).thenReturn(true)
        Mockito.`when`(service.vérifierExistanceUtilisateur(ArgumentMatchers.anyInt())).thenReturn(true)
        Mockito.`when`(service.chercherParCode(anyInt())).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.get("/utilisateurs/0/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur dont le code est 1 qui n'a pas d'adresse associée et une nouvelle adresse avec le code 4 lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un JSON qui contient une adresse avec le code 4 et un code de retour 201`(){
        val adresse = Adresse(4, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.ajouter(1,adresse )).thenReturn(adresse)


        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur inexistant lorsqu'on effectue une requête POST pour lui ajouter une adresse alors obtient un code de retour 404 et le message d'erreur « Utilisateur inexistant »`(){
        val adresse = Adresse(4, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.ajouter(18,adresse )).thenThrow(RessourceInexistanteException("L'utilisateur n'existe pas"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/18/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'existe pas", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur dont le code est 1 qui a déjà une adresse associée et une nouvelle adresse avec le code 5 lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 409 et le message d'erreur « L'utilisateur à déja une adresse »`(){
        val adresse = Adresse(4, "22", "54646","de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.ajouter(1,adresse )).thenThrow(ProduitExisteDéjàException("L'utilisateur à déja une adresse définie"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isConflict)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    Assertions.assertEquals("L'utilisateur à déja une adresse définie", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PostMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur dont le code est 1 qui n'a pas d'adresse associée et une nouvelle adresse avec le code 4 lorsqu'on effectue une requête POST pour l'ajouter et que le code utilisateur est non valide comme -1 alors on obtient un code de retour 400 et le message d'erreur « Le code d'utilisateur est invalide »`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.ajouter(-1,adresse )).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.post("/utilisateurs/-1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur dont le code est 1 et qui a une adresse associée dont le nom de la rue est « du château » lorsqu'on effectue une requête PUT pour modifier le nom de la rue pour « du château modifié » alors on obtient un JSON qui contient une adresse associé à l'utilisateur avec le code 1 et qui a pour nom de la rue « du château modifié » ainsi qu'un code de retour 200`(){
        val adresse = Adresse(4, null,"6450" ,"du château modifié","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.vérifierExistanceAdresse(anyInt())).thenReturn(true)
        Mockito.`when`(service.vérifierExistanceUtilisateur(anyInt())).thenReturn(true)
        Mockito.`when`(service.modifier(1,adresse )).thenReturn(adresse)

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.rue").value("du château modifié"))
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur dont le code est 1 et qui n'a pas une adresse associée lorsqu'on effectue une requête PUT alors on obtient un JSON qui contient une adresse associé à l'utilisateur avec le code 1 et un code de retour 201`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.vérifierExistanceAdresse(anyInt())).thenReturn(false)
        Mockito.`when`(service.vérifierExistanceUtilisateur(anyInt())).thenReturn(true)
        Mockito.`when`(service.modifier(1,adresse )).thenReturn(adresse)

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
    }

    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur inexistant comme avec le code 18 lorsqu'on effectue une requête PUT pour modifier son adresse alors on obtient un code de retour 404 et le message d'erreur « Utilisateur inexistant »`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.vérifierExistanceAdresse(anyInt())).thenReturn(false)
        Mockito.`when`(service.vérifierExistanceUtilisateur(anyInt())).thenReturn(false)
        Mockito.`when`(service.modifier(18,adresse )).thenThrow(RessourceInexistanteException("Utilisateur inexistant"))

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/18/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Utilisateur inexistant", résultat.resolvedException?.message)
                }
    }


    @WithMockUser
    @Test
    //@PutMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur dont le code est 1 et qui n'a pas une adresse associée lorsqu'on effectue une requête PUT et que le champ nom de la rue est non valide comme 0 alors on obtient un code de retour 400 et le message d'erreur « Erreur dans la requête »`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.vérifierExistanceAdresse(anyInt())).thenReturn(false)
        Mockito.`when`(service.vérifierExistanceUtilisateur(anyInt())).thenReturn(true)
        Mockito.`when`(service.modifier(0,adresse )).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.put("/utilisateurs/0/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }


    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur avec le code 8 et qui a une adresse associé lorsqu'on effectue une requête DELETE alors on obtient un code de retour 204`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.supprimer(1)).thenReturn(adresse)

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isNoContent)
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur avec le code 8 et qui n'a pas d'adresse associée lorsqu'on effectue une requête DELETE alors on obtient un code de retour 404 avec le message d'erreur « L'utilisateur n'a pas d'adresse définie »`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.supprimer(1)).thenThrow(RessourceInexistanteException("L'utilisateur n'a pas d'adresse définie"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/1/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas d'adresse définie", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/utilisateurs/{code_utilisateur}/adresse")
    fun `Étant donné un utilisateur avec le code 8 et qui a une adresse associé lorsqu'on effectue une requête DELETE et qu'il envoie un code invalide comme 0 alors on obtient un code de retour 400 avec le message d'erreur « Erreur dans la requête »`(){
        val adresse = Adresse(4, null,"6450" ,"de la maison","montreal","QC","AAAAAA","Canada")
        Mockito.`when`(service.supprimer(0)).thenThrow(ErreurRequêteException("Le code d'utilisateur est invalide"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/utilisateurs/0/adresse").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adresse)))
                .andExpect(status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code d'utilisateur est invalide", résultat.resolvedException?.message)
                }
    }





}