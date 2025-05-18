package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.CharacterSelectionRequest;
import be.helha.projets.projetdarktower.Model.Personnage;
import be.helha.projets.projetdarktower.Service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CharacterController.class)
@Import(CharacterControllerTest.TestConfig.class)
public class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharacterService characterService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        Mockito.reset(characterService);
    }

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Sélectionner un personnage avec succès")
    void testSelectCharacter_Success() throws Exception {
        Personnage personnage = new PersonnageConcret();
        personnage.setNom("Chevalier");

        CharacterSelectionRequest request = new CharacterSelectionRequest();
        request.setCharacterId("1");
        request.setUserId("user123");

        Mockito.when(characterService.selectCharacter("1")).thenReturn(personnage);

        mockMvc.perform(post("/characters/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Personnage Chevalier sélectionné !"));
    }

    @Test
    @DisplayName("2: Échec de sélection - personnage non trouvé")
    void testSelectCharacter_NotFound() throws Exception {
        CharacterSelectionRequest request = new CharacterSelectionRequest();
        request.setCharacterId("999");
        request.setUserId("user456");

        Mockito.when(characterService.selectCharacter("999")).thenReturn(null);

        mockMvc.perform(post("/characters/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Personnage non trouvé."));
    }

    // Classe concrète pour Personnage abstrait
    private static class PersonnageConcret extends Personnage {
        public PersonnageConcret() {
            super("id", "nom", 100, 10);
        }

        @Override
        public int attaquer(Personnage cible) {
            return 0;
        }
    }

    // Config test pour mock manuel
    static class TestConfig {
        @Bean
        public CharacterService characterService() {
            return Mockito.mock(CharacterService.class);
        }
    }
}
