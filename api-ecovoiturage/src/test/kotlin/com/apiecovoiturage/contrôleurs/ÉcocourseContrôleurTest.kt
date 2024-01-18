package com.écovoiturage.écovoiturage.contrôleurs

import com.fasterxml.jackson.databind.ObjectMapper
import com.écovoiturage.écovoiturage.Exceptions.ErreurRequêteException
import com.écovoiturage.écovoiturage.Exceptions.ProduitExisteDéjàException
import com.écovoiturage.écovoiturage.Exceptions.RessourceInexistanteException
import com.écovoiturage.écovoiturage.Modèle.Utilisateur
import com.écovoiturage.écovoiturage.Modèle.Écocourse
import com.écovoiturage.écovoiturage.modèles.Adresse
import com.écovoiturage.écovoiturage.modèles.Voiture
import com.écovoiturage.écovoiturage.services.AdresseService
import com.écovoiturage.écovoiturage.services.ÉcocourseService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mock
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
import java.util.*
import javax.print.attribute.standard.Media
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

@SpringBootTest
@AutoConfigureMockMvc
class ÉcocourseContrôleurTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var serviceÉcocourse : ÉcocourseService

    @Autowired
    private lateinit var mockMvc: MockMvc



    @Test
    //@GetMapping("/écocourses")
    fun `Étant donné une liste d'écocourses non vide, lorsqu'on effectue une requête GET alors on obtient un JSON qui contient une liste d'écocourses et un code de retour 200`(){
        val listeÉcocourses = mutableListOf(
            Écocourse(1, Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),55.0,2),
            Écocourse(2, Utilisateur(2,"Smith", "Alice", "alice@example.com", "987-654-3210",1,null, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR")), 20240101, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Voiture(2,"Honda", "2020", "Civic", "Blue", "XYZ789", 5,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),15.0,2)
        )
        Mockito.`when`(serviceÉcocourse.chercherTous()).thenReturn(listeÉcocourses)

        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Int>(listeÉcocourses.size))) //ca c'est pour comparer la taille de la liste retournée avec la taille de la listeÉcocourses

    }

    @Test
    //@GetMapping("/écocourses")
    fun `Étant donné une liste d'écocourses vide, lorsqu'on effectue une requête GET alors on obtient un message d'erreur « Liste inexistante ou vide » et un code de retour 404`(){
        val écocourses = emptyList<Écocourse>()
        Mockito.`when`(serviceÉcocourse.chercherTous()).thenReturn(écocourses)
        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect { résultat ->
                Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                Assertions.assertEquals("Liste inexistante ou vide", résultat.resolvedException?.message)
            }

    }

    @Test
    //@GetMapping("/écocourses/utilisateurs/{code_utilisateur}/disponibles")
    fun `Étant donné une liste d'écocourses disponibles non vide, lorsqu'on effectue une requête GET pour obtenir la liste des écocourses disponibles alors on obtient un JSON qui contient une liste d'écocourses disponibles et un code de retour 200`(){
        val listeÉcocourses = mutableListOf(
                Écocourse(1, Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),55.0,2),
                Écocourse(2, Utilisateur(2,"Smith", "Alice", "alice@example.com", "987-654-3210",1,null, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR")), 20240101, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Voiture(2,"Honda", "2020", "Civic", "Blue", "XYZ789", 5,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),15.0,2)
        )
        Mockito.`when`(serviceÉcocourse.chercherCoursesDisponibles(1)).thenReturn(listeÉcocourses)

        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/utilisateurs/1/disponibles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Int>(listeÉcocourses.size))) //ca c'est pour comparer la taille de la liste retournée avec la taille de la listeÉcocourses

    }

    @Test
    //@GetMapping("/écocourses/utilisateurs/{code_utilisateur}/disponibles")
    fun `Étant donné une listes d'écocourses disponibles vide, lorsqu'on effectue une requête GET alors on obtient un message d'erreur « Liste inexistante ou vide » et un code de retour 404`(){
        val écocourses = emptyList<Écocourse>()
        Mockito.`when`(serviceÉcocourse.chercherCoursesDisponibles(1)).thenReturn(écocourses)
        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/utilisateurs/1/disponibles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Liste inexistante ou vide", résultat.resolvedException?.message)
                }

    }

    @Test
    //@GetMapping("/écocourses/utilisateurs/{code_utilisateur}/passager")
    fun `Étant donné une liste d'écocourses où on est le passager non vide, lorsqu'on effectue une requête GET pour obtenir la liste des écocourses où on est le passager  alors on obtient un JSON qui contient une liste d'écocourses où nous sommes un passager et un code de retour 200`(){
        val listeÉcocourses = mutableListOf(
                Écocourse(1, Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),55.0,2),
                Écocourse(2, Utilisateur(2,"Smith", "Alice", "alice@example.com", "987-654-3210",1,null, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR")), 20240101, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Voiture(2,"Honda", "2020", "Civic", "Blue", "XYZ789", 5,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),15.0,2)
        )
        Mockito.`when`(serviceÉcocourse.chercherCoursesPassager(1)).thenReturn(listeÉcocourses)

        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/utilisateurs/1/passager")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Int>(listeÉcocourses.size))) //ca c'est pour comparer la taille de la liste retournée avec la taille de la listeÉcocourses

    }

    @Test
    //@GetMapping("/écocourses/utilisateurs/{code_utilisateur}/passager")
    fun `Étant donné une listes d'écocourses où on est passager vide, lorsqu'on effectue une requête GET alors on obtient un message d'erreur « Liste inexistante ou vide » et un code de retour 404`(){
        val écocourses = emptyList<Écocourse>()
        Mockito.`when`(serviceÉcocourse.chercherCoursesPassager(1)).thenReturn(écocourses)
        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/utilisateurs/1/passager")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Liste inexistante ou vide", résultat.resolvedException?.message)
                }

    }


    @Test
    //@GetMapping("/écocourses/utilisateurs/{code_utilisateur}/conducteur")
    fun `Étant donné une liste d'écocourses où on est le conducteur non vide, lorsqu'on effectue une requête GET pour obtenir la liste des écocourses où on est le conducteur  alors on obtient un JSON qui contient une liste d'écocourses où nous sommes un conducteur et un code de retour 200`(){
        val listeÉcocourses = mutableListOf(
                Écocourse(1, Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US")), 20230101, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019", "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),55.0,2),
                Écocourse(2, Utilisateur(2,"Smith", "Alice", "alice@example.com", "987-654-3210",1,null, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR")), 20240101, Adresse(2,"Apt 456", "456", "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"), Voiture(2,"Honda", "2020", "Civic", "Blue", "XYZ789", 5,Utilisateur(1,"Doe", "John", "john@example.com", "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street", "New York", "NY", "10001", "US"))),15.0,2)
        )
        Mockito.`when`(serviceÉcocourse.chercherCoursesConducteur(1)).thenReturn(listeÉcocourses)

        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/utilisateurs/1/conducteur")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Int>(listeÉcocourses.size))) //ca c'est pour comparer la taille de la liste retournée avec la taille de la listeÉcocourses

    }

    @Test
    //@GetMapping("/écocourses/utilisateurs/{code_utilisateur}/conducteur")
    fun `Étant donné une listes d'écocourses où on est conducteur vide, lorsqu'on effectue une requête GET alors on obtient un message d'erreur « Liste inexistante ou vide » et un code de retour 404`(){
        val écocourses = emptyList<Écocourse>()
        Mockito.`when`(serviceÉcocourse.chercherCoursesConducteur(1)).thenReturn(écocourses)
        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/utilisateurs/1/conducteur")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Liste inexistante ou vide", résultat.resolvedException?.message)
                }

    }


    @Test
    //@GetMapping("/écocourses/{code})
    fun `Étant donnée l'écocourse dont le code est 2 qui existe déjà lorsqu'on effectue une requête GET alors on obtient un JSON qui contient l'écocourse et un code de retour 200` (){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.chercherParCode(2)).thenReturn(écocourse)
        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    // @GetMapping("/écocourses/{code}")
    fun `Étant donné l'écocourse dont le code est 4 et qui n'existe pas, lorsqu'on effectue une requête GET alors on obtient un code de retour 404 et le message d'erreur « Écocourse inexistante »`() {
        Mockito.`when`(serviceÉcocourse.chercherParCode(4)).thenReturn(null)
        mockMvc.perform(MockMvcRequestBuilders.get("/écocourses/4")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect { résultat ->
                Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                Assertions.assertEquals("Écocourse inexistante", résultat.resolvedException?.message)
            }

    }

    @WithMockUser
    @Test
    // @PostMapping("/écocourses/{codeÉcocourse}/rejoindre/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut rejoindre une écocourse avec le code 2 qu'il n'a pas déjà rejoint, lorsqu'on effectue une requête post pour l'ajouter a la course alors on obtient un code de retour 200 avec un Json qui contient les informations de l'écocourse 1`() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.rejoindre(2,1)).thenReturn(écocourse)
        mockMvc.perform(MockMvcRequestBuilders.post("/écocourses/2/rejoindre/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.codeÉcocourse").value(2))
    }

    @WithMockUser
    @Test
    // @PostMapping("/écocourses/{codeÉcocourse}/rejoindre/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut rejoindre une écocourse avec un code invalide comme -1 , lorsqu'on effectue une requête post pour l'ajouter a la course alors on obtient un code de retour 400 avec le message d'erreur « Le code de l'écocourse est invalide » `() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.rejoindre(-1,1)).thenThrow(ErreurRequêteException("Le code de l'écocourse est invalide"))
        mockMvc.perform(MockMvcRequestBuilders.post("/écocourses/-1/rejoindre/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code de l'écocourse est invalide", résultat.resolvedException?.message)
                }

    }

    @WithMockUser
    @Test
    // @PostMapping("/écocourses/{codeÉcocourse}/rejoindre/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut rejoindre une écocourse inexistante avec le code 54 , lorsqu'on effectue une requête post pour l'ajouter a la course alors on obtient un code de retour 404 avec le message d'erreur « Écocourse inexistante » `() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.rejoindre(54,1)).thenThrow(RessourceInexistanteException("Écocourse inexistante"))
        mockMvc.perform(MockMvcRequestBuilders.post("/écocourses/54/rejoindre/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Écocourse inexistante", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    // @PostMapping("/écocourses/{codeÉcocourse}/rejoindre/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut rejoindre une écocourse avec le code 1 dont il fait déja parti, lorsqu'on effectue une requête post pour l'ajouter a la course alors on obtient un code de retour 409 avec le message d'erreur « L'utilisateur fait déja parti de l'écocourse » `() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.rejoindre(1,1)).thenThrow(ProduitExisteDéjàException("L'utilisateur fait déja parti de l'écocourse"))
        mockMvc.perform(MockMvcRequestBuilders.post("/écocourses/1/rejoindre/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    Assertions.assertEquals("L'utilisateur fait déja parti de l'écocourse", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    // @DeleteMapping("/écocourses/{codeÉcocourse}/retirer/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut quitter une écocourse avec le code 2 dont il fait déja parti, lorsqu'on effectue une requête delete pour l'enlever a la course alors on obtient un code de retour 204`() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.rejoindre(2,1)).thenReturn(écocourse)
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/2/retirer/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @WithMockUser
    @Test
    // @DeleteMapping("/écocourses/{codeÉcocourse}/retirer/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut quitter une écocourse un code invalide comme -1 dont il fait déja parti, lorsqu'on effectue une requête delete pour l'enlever a la course alors on obtient un code de retour 400 avec le message d'erreur « Le code de l'écocourse est invalide » `() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.quitter(-1,1)).thenThrow(ErreurRequêteException("Le code de l'écocourse est invalide"))
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/-1/retirer/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Le code de l'écocourse est invalide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    // @DeleteMapping("/écocourses/{codeÉcocourse}/retirer/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec le code 1 qui veut quitter une écocourse un code inexistant comme 89 , lorsqu'on effectue une requête delete pour l'enlever a la course alors on obtient un code de retour 404 avec le message d'erreur « Écocourse inexistante » `() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.quitter(89,1)).thenThrow(RessourceInexistanteException("Écocourse inexistante"))
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/89/retirer/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Écocourse inexistante", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    // @DeleteMapping("/écocourses/{codeÉcocourse}/retirer/{codeUtilisateur}")
    fun `Étant donné un utilisateur avec un code inexistant comme 75 qui veut quitter une écocourse , lorsqu'on effectue une requête delete pour l'enlever a la course alors on obtient un code de retour 404 avec le message d'erreur « L'utilisateur n'a pas été trouvé » `() {
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.quitter(89,1)).thenThrow(RessourceInexistanteException("L'utilisateur n'a pas été trouvé "))
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/89/retirer/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("L'utilisateur n'a pas été trouvé ", résultat.resolvedException?.message)
                }
    }


    @WithMockUser
    @Test
    //@PostMapping("/écocourses")
    fun `Étant donné un écocourse avec le code 2 losrqu'on effectue une requête POST pour l'ajouter alors on obtient un JSON qui contient l'écocourse avec le code 2 et un code de retour 201`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.ajouter(écocourse)).thenReturn(écocourse)
        mockMvc.perform(MockMvcRequestBuilders.post("/écocourses").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @WithMockUser
    @Test
    //@PostMapping("/écocourses")
    fun `Étant donné une écocourse avec le code 3 et qu'il existe déjà une écocourse avec ce code losrqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 409 et le message d'erreur « Cette écocourse existe déjà »`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),44.0,3)
        Mockito.`when`(serviceÉcocourse.ajouter(écocourse)).thenThrow(ProduitExisteDéjàException("Cette écocourse existe déjà"))

        mockMvc.perform(MockMvcRequestBuilders.post("/écocourses").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ProduitExisteDéjàException)
                    Assertions.assertEquals("Cette écocourse existe déjà", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@PutMapping("/écocourses/{codeÉcocourse}")
    fun `Étant donné une écocourse avec le code 2 et qu'il déjà une écocourse avec le code 2 lorsqu'on effectue une requête PUT pour modifier le prix total à 50$ alors on obtient un JSON qui contient l'écocourse avec les informations modifiées ainsi qu'un code de retour 200`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),50.0,3)
        Mockito.`when`(serviceÉcocourse.modifier(2,écocourse)).thenReturn(écocourse)
        Mockito.`when`(serviceÉcocourse.vérifierCourseParCode(2)).thenReturn(true)
        mockMvc.perform(MockMvcRequestBuilders.put("/écocourses/2").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.prixTotal").value(50.0))

    }

    @WithMockUser
    @Test
    //@PutMapping("/écocourses/{codeÉcocourse}")
    fun `Étant donné une écocourse avec le code 2 et qu'il n'existe pas déjà une écocourse avec le code 2 lorsqu'on effectue une requête PUT alors on obtient un JSON qui contient l'écocourse avec le code 2 ainsi qu'un code de retour 201`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),50.0,3)
        Mockito.`when`(serviceÉcocourse.modifier(2,écocourse)).thenReturn(écocourse)
        Mockito.`when`(serviceÉcocourse.vérifierCourseParCode(2)).thenReturn(false)
        mockMvc.perform(MockMvcRequestBuilders.put("/écocourses/2").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.prixTotal").value(50.0))
    }

    @WithMockUser
    @Test
    //@PutMapping("/écocourses/{codeÉcocourse}")
    fun `Étant donné une écocourse avec le code 2 et qu'il n'existe pas déjà une écocourse avec le code 2 lorsqu'on effectue une requête PUT avec un code d'écocourse non valide comme -1 alors on obtient un code de retour 400 avec le message d'erreur « Code d'écocourse non valide » `(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),50.0,3)
        Mockito.`when`(serviceÉcocourse.modifier(2,écocourse)).thenReturn(écocourse)
        Mockito.`when`(serviceÉcocourse.vérifierCourseParCode(2)).thenThrow(ErreurRequêteException("Code d'écocourse non valide"))
        mockMvc.perform(MockMvcRequestBuilders.put("/écocourses/2").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Code d'écocourse non valide", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/écocourses/{codeÉcocourse}")
    fun `Étant donné une écocourse avec le code 43 lorsqu'on effectue une requête DELETE alors on obtient un code de retour 204`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),50.0,3)
        Mockito.`when`(serviceÉcocourse.supprimer(2)).thenReturn(écocourse)
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/2").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/écocourses/{codeÉcocourse}")
    fun `Étant donné une écocourse avec le code 9 et qu'il n'existe pas d'écocourse avec ce code lorsqu'on effectue une requête DELETE alors on obtient un code de retour 404 avec le message d'erreur « Écocourse inexistante »`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),50.0,3)
        Mockito.`when`(serviceÉcocourse.supprimer(9)).thenThrow(RessourceInexistanteException("Écocourse inexistante"))
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/9").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is RessourceInexistanteException)
                    Assertions.assertEquals("Écocourse inexistante", résultat.resolvedException?.message)
                }
    }

    @WithMockUser
    @Test
    //@DeleteMapping("/écocourses/{codeÉcocourse}")
    fun `Étant donné une écocourse qu'on veut supprimer avec un code invalide comme -1 lorsqu'on effectue une requête DELETE alors on obtient un code de retour 400 avec le message d'erreur « Code écocourse invalide »`(){
        val écocourse = Écocourse(2, Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US")), 20230405, Adresse(1, "Appartement A", "123",
                "First Street", "New York", "NY", "10001", "US"), Adresse(2,"Apt 456", "456",
                "Avenue des Fleurs", "Marseille", "FR", "13001", "FR"), Voiture(1,"Toyota", "2019",
                "Corolla", "Red", "ABC123", 4,Utilisateur(1,"Doe", "John", "john@example.com",
                "123-456-7890",44,null, Adresse(1, "Appartement A", "123", "First Street",
                "New York", "NY", "10001", "US"))),50.0,3)
        Mockito.`when`(serviceÉcocourse.supprimer(-1)).thenThrow(ErreurRequêteException("Code écocourse invalide"))
        mockMvc.perform(MockMvcRequestBuilders.delete("/écocourses/-1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(écocourse)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect { résultat ->
                    Assertions.assertTrue(résultat.resolvedException is ErreurRequêteException)
                    Assertions.assertEquals("Code écocourse invalide", résultat.resolvedException?.message)
                }
    }
}