package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatResultTest {

    @Test
    void testConstructeur() {
        String message = "Le joueur attaque le minotaure !";
        int pvJoueur = 80;
        int pvMinotaure = 50;
        int degatsMinotaure = 12;
        int tour = 3;

        CombatResult result = new CombatResult(message, pvJoueur, pvMinotaure, degatsMinotaure, tour);

        assertEquals(message, result.message, "Le message doit être correctement initialisé");
        assertEquals(pvJoueur, result.pvJoueur, "Les PV du joueur doivent être correctement initialisés");
        assertEquals(pvMinotaure, result.pvMinotaure, "Les PV du minotaure doivent être correctement initialisés");
        assertEquals(degatsMinotaure, result.degatsMinotaure, "Les dégâts du minotaure doivent être correctement initialisés");
        assertEquals(tour, result.tour, "Le tour doit être correctement initialisé");
    }
}
