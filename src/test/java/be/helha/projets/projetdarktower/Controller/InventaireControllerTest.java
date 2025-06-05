package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(InventaireController.class)
@Import(InventaireControllerTest.TestConfig.class)  // Injecte le mock dans le contexte Spring
public class InventaireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    private ObjectMapper objectMapper;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();

        item = new Item();
        item.setId("item123");
        item.setNom("Épée magique");
    }

    @Test
    @DisplayName("1: Vider l'inventaire retourne OK")
    void testViderInventaire() throws Exception {
        mockMvc.perform(post("/inventaire/1/vider"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventaire vidé pour le personnage 1"));
    }

    @Test
    @DisplayName("2: Initialiser un inventaire retourne OK")
    void testInitialiserInventaire() throws Exception {
        mockMvc.perform(post("/inventaire/1/initialiser"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventaire initialisé pour le personnage 1"));
    }

    @Test
    @DisplayName("3: Ajouter un item avec succès")
    void testAjouterItem_Success() throws Exception {
        Mockito.when(itemService.ajouterItem(item, 1)).thenReturn(true);

        mockMvc.perform(post("/inventaire/1/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string("Item ajouté avec succès."));
    }

    @Test
    @DisplayName("4: Ajouter un item échoue si inventaire plein")
    void testAjouterItem_Failure() throws Exception {
        Mockito.when(itemService.ajouterItem(item, 1)).thenReturn(false);

        mockMvc.perform(post("/inventaire/1/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Impossible d'ajouter l'item (inventaire plein ou autre problème)."));
    }

    @Test
    @DisplayName("5: Charger inventaire retourne la liste des items")
    void testChargerInventaire() throws Exception {
        Mockito.when(itemService.chargerInventaire(1)).thenReturn(List.of(item));

        mockMvc.perform(get("/inventaire/1/charger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Épée magique"));
    }

    @Test
    @DisplayName("6: Récupérer contenu coffre retourne la liste des items")
    void testRecupererContenuCoffre() throws Exception {
        Mockito.when(itemService.recupererContenuCoffre(1)).thenReturn(List.of(item));

        mockMvc.perform(get("/inventaire/1/coffre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item123"));
    }

    @Test
    @DisplayName("7: Ajouter item dans coffre avec succès")
    void testAjouterItemDansCoffre_Success() throws Exception {
        Mockito.when(itemService.ajouterItemDansCoffre(item, 1)).thenReturn(true);

        mockMvc.perform(post("/inventaire/1/coffre/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("8: Échec ajout item dans coffre")
    void testAjouterItemDansCoffre_Failure() throws Exception {
        Mockito.when(itemService.ajouterItemDansCoffre(item, 1)).thenReturn(false);

        mockMvc.perform(post("/inventaire/1/coffre/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("9: Supprimer item du coffre succès")
    void testSupprimerItemDuCoffre_Success() throws Exception {
        Mockito.when(itemService.supprimerItemDuCoffre("item123", 1)).thenReturn(true);

        mockMvc.perform(delete("/inventaire/1/coffre/item123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item supprimé du coffre."));
    }

    @Test
    @DisplayName("10: Échec suppression item du coffre")
    void testSupprimerItemDuCoffre_Failure() throws Exception {
        Mockito.when(itemService.supprimerItemDuCoffre("item123", 1)).thenReturn(false);

        mockMvc.perform(delete("/inventaire/1/coffre/item123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Item introuvable dans le coffre."));
    }

    @Test
    @DisplayName("11: Vérifier la possession d'un coffre")
    void testPossedeCoffre() throws Exception {
        Mockito.when(itemService.possedeCoffre(1)).thenReturn(true);

        mockMvc.perform(get("/inventaire/coffre/existe/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("12: Récupérer item par ID trouvé")
    void testRecupererItemParId_Found() throws Exception {
        Mockito.when(itemService.recupererItemParId("item123", 1)).thenReturn(item);

        mockMvc.perform(get("/inventaire/item/item123/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("item123"));
    }

    @Test
    @DisplayName("13: Récupérer item par ID non trouvé")
    void testRecupererItemParId_NotFound() throws Exception {
        Mockito.when(itemService.recupererItemParId("item123", 1)).thenReturn(null);

        mockMvc.perform(get("/inventaire/item/item123/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("14: Supprimer un item par ID")
    void testSupprimerItem() throws Exception {
        Mockito.when(itemService.supprimerItem("item123")).thenReturn("Item supprimé");

        mockMvc.perform(delete("/inventaire/item/item123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item supprimé"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public ItemService itemService() {
            return Mockito.mock(ItemService.class);
        }
    }
}
