package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.DTO.UseItemResult;
import be.helha.projets.projetdarktower.Item.Item;
import be.helha.projets.projetdarktower.Item.ItemSelectionRequest;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Service.CharacterService;
import be.helha.projets.projetdarktower.Service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CombatController.class)
@Import(CombatControllerTest.TestConfig.class)
public class CombatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CharacterService characterService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        Mockito.reset(itemService, characterService);
    }

    @Test
    @DisplayName("1: Utilisation d'un item réussie")
    void testUseItem_Success() throws Exception {
        Personnage utilisateur = new PersonnageConcret("idPerso123", "Guerrier", 100, 15);
        Item item = new Item();
        item.setNom("Potion");
        Personnage cible = new PersonnageConcret("cible456", "Dragon", 200, 30);

        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("item123");
        request.setCibleId("cible456");

        UseItemResult result = new UseItemResult("Succès", 10, 20, "Dégâts appliqués");

        Mockito.when(characterService.selectCharacter("idPerso123")).thenReturn(utilisateur);
        Mockito.when(itemService.recupererItemParId("item123", 1)).thenReturn(item);
        Mockito.when(characterService.selectCharacter("cible456")).thenReturn(cible);
        Mockito.when(itemService.utiliserItem(item, utilisateur, cible, 1)).thenReturn(result);

        mockMvc.perform(post("/Combat/1/idPerso123/use-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Succès"))
                .andExpect(jsonPath("$.pointsAvant").value(10))
                .andExpect(jsonPath("$.pointsApres").value(20))
                .andExpect(jsonPath("$.effet").value("Dégâts appliqués"));
    }

    @Test
    @DisplayName("2: Échec si personnage utilisateur non trouvé")
    void testUseItem_PersonnageNotFound() throws Exception {
        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("item123");

        Mockito.when(characterService.selectCharacter("inexistant")).thenReturn(null);

        mockMvc.perform(post("/Combat/1/inexistant/use-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Personnage utilisateur non trouvé."));
    }

    @Test
    @DisplayName("3: Échec si item non trouvé")
    void testUseItem_ItemNotFound() throws Exception {
        Personnage utilisateur = new PersonnageConcret("idPerso", "Guerrier", 100, 15);

        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("invalide");

        Mockito.when(characterService.selectCharacter("idPerso")).thenReturn(utilisateur);
        Mockito.when(itemService.recupererItemParId("invalide", 1)).thenReturn(null);

        mockMvc.perform(post("/Combat/1/idPerso/use-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Objet non trouvé."));
    }

    @Test
    @DisplayName("4: Échec si cible non trouvée")
    void testUseItem_CibleNotFound() throws Exception {
        Personnage utilisateur = new PersonnageConcret("persoOK", "Mage", 80, 12);
        Item item = new Item();
        item.setNom("Bâton magique");

        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("item123");
        request.setCibleId("cibleInvalide");

        Mockito.when(characterService.selectCharacter("persoOK")).thenReturn(utilisateur);
        Mockito.when(itemService.recupererItemParId("item123", 1)).thenReturn(item);
        Mockito.when(characterService.selectCharacter("cibleInvalide")).thenReturn(null);

        mockMvc.perform(post("/Combat/1/persoOK/use-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cible non trouvée."));
    }

    private static class PersonnageConcret extends Personnage {

        public PersonnageConcret(String id, String nom, int pointsDeVie, int attaque) {
            super(id, nom, pointsDeVie, attaque);
        }

        @Override
        public int attaquer(Personnage cible) {
            return 0;
        }
    }

    static class TestConfig {

        @Bean
        public ItemService itemService() {
            return Mockito.mock(ItemService.class);
        }

        @Bean
        public CharacterService characterService() {
            return Mockito.mock(CharacterService.class);
        }
    }
}
