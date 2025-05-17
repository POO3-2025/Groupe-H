package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EtageTest {

    private Etage etage;

    @BeforeEach
    void setUp() {
        etage = new Etage(1); // On commence à l'étage 1
    }

    @Test
    void testConstructeur() {
        Etage e = new Etage(3);
        assertEquals(3, e.getEtage(), "L'étage doit être initialisé à 3");
    }

    @Test
    void testGetEtage() {
        assertEquals(1, etage.getEtage(), "L'étage doit être 1 après l'initialisation");
    }

    @Test
    void testIncrementer() {
        etage.incrementer();
        assertEquals(2, etage.getEtage(), "L'étage doit être 2 après incrémentation");
        etage.incrementer();
        assertEquals(3, etage.getEtage(), "L'étage doit être 3 après une deuxième incrémentation");
    }

    @Test
    void testResetEtage() {
        etage.incrementer(); // Étape à 2
        etage.resetEtage();  // Doit revenir à 1
        assertEquals(1, etage.getEtage(), "L'étage doit être réinitialisé à 1");
    }
}
