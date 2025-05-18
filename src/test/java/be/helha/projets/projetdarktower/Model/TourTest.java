package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class TourTest {

    private Tour tour;

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test constructeur et getter")
    void testConstructeurEtGetter() {
        tour = new Tour(5);
        assertEquals(5, tour.getTour());
    }

    @Test
    @DisplayName("2: Test méthode incrementer")
    void testIncrementer() {
        tour = new Tour(1);
        tour.incrementer();
        assertEquals(2, tour.getTour());
    }

    @Test
    @DisplayName("3: Test méthode resetTour")
    void testResetTour() {
        tour = new Tour(10);
        tour.resetTour();
        assertEquals(1, tour.getTour());
    }
}
