package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
        MockitoAnnotations.openMocks(this);  // Initialise @Mock Mockito
        objectMapper = new ObjectMapper();

        item = new Item();
        item.setId("item123");
        item.setNom("Épée magique");
    }

    @Test
    void testViderInventaire() throws Exception {
        mockMvc.perform(post("/inventaire/1/vider"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventaire vidé pour le personnage 1"));
    }

    @Test
    void testInitialiserInventaire() throws Exception {
        mockMvc.perform(post("/inventaire/1/initialiser"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventaire initialisé pour le personnage 1"));
    }

    @Test
    void testAjouterItem_Success() throws Exception {
        Mockito.when(itemService.ajouterItem(item, 1)).thenReturn(true);

        mockMvc.perform(post("/inventaire/1/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string("Item ajouté avec succès."));
    }

    @Test
    void testAjouterItem_Failure() throws Exception {
        Mockito.when(itemService.ajouterItem(item, 1)).thenReturn(false);

        mockMvc.perform(post("/inventaire/1/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Impossible d'ajouter l'item (inventaire plein ou autre problème)."));
    }

    @Test
    void testChargerInventaire() throws Exception {
        Mockito.when(itemService.chargerInventaire(1)).thenReturn(List.of(item));

        mockMvc.perform(get("/inventaire/1/charger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Épée magique"));
    }

    @Test
    void testRecupererContenuCoffre() throws Exception {
        Mockito.when(itemService.recupererContenuCoffre(1)).thenReturn(List.of(item));

        mockMvc.perform(get("/inventaire/1/coffre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item123"));
    }

    @Test
    void testAjouterItemDansCoffre_Success() throws Exception {
        Mockito.when(itemService.ajouterItemDansCoffre(item, 1)).thenReturn(true);

        mockMvc.perform(post("/inventaire/1/coffre/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testAjouterItemDansCoffre_Failure() throws Exception {
        Mockito.when(itemService.ajouterItemDansCoffre(item, 1)).thenReturn(false);

        mockMvc.perform(post("/inventaire/1/coffre/ajouter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }

    @Test
    void testSupprimerItemDuCoffre_Success() throws Exception {
        Mockito.when(itemService.supprimerItemDuCoffre("item123", 1)).thenReturn(true);

        mockMvc.perform(delete("/inventaire/1/coffre/item123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item supprimé du coffre."));
    }

    @Test
    void testSupprimerItemDuCoffre_Failure() throws Exception {
        Mockito.when(itemService.supprimerItemDuCoffre("item123", 1)).thenReturn(false);

        mockMvc.perform(delete("/inventaire/1/coffre/item123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Item introuvable dans le coffre."));
    }

    @Test
    void testPossedeCoffre() throws Exception {
        Mockito.when(itemService.possedeCoffre(1)).thenReturn(true);

        mockMvc.perform(get("/inventaire/coffre/existe/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testRecupererItemParId_Found() throws Exception {
        Mockito.when(itemService.recupererItemParId("item123", 1)).thenReturn(item);

        mockMvc.perform(get("/inventaire/item/item123")
                        .param("idPersonnage", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("item123"));
    }

    @Test
    void testRecupererItemParId_NotFound() throws Exception {
        Mockito.when(itemService.recupererItemParId("item123", 1)).thenReturn(null);

        mockMvc.perform(get("/inventaire/item/item123")
                        .param("idPersonnage", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
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
