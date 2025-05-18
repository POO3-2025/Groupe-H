package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class EtageTest {

    private Etage etage;

    @BeforeEach
    void setUp() {
        etage = new Etage(1); // On commence à l'étage 1
    }

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test du constructeur")
    void testConstructeur() {
        Etage e = new Etage(3);
        assertEquals(3, e.getEtage(), "L'étage doit être initialisé à 3");
    }

    @Test
    @DisplayName("2: Test du getter getEtage()")
    void testGetEtage() {
        assertEquals(1, etage.getEtage(), "L'étage doit être 1 après l'initialisation");
    }

    @Test
    @DisplayName("3: Test de l'incrémentation de l'étage")
    void testIncrementer() {
        etage.incrementer();
        assertEquals(2, etage.getEtage(), "L'étage doit être 2 après incrémentation");
        etage.incrementer();
        assertEquals(3, etage.getEtage(), "L'étage doit être 3 après une deuxième incrémentation");
    }

    @Test
    @DisplayName("4: Test de la réinitialisation de l'étage")
    void testResetEtage() {
        etage.incrementer(); // Étape à 2
        etage.resetEtage();  // Doit revenir à 1
        assertEquals(1, etage.getEtage(), "L'étage doit être réinitialisé à 1");
    }
}
