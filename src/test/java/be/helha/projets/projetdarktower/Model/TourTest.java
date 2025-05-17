package be.helha.projets.projetdarktower.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TourTest {

    @Test
    void testConstructeurEtGetter() {
        Tour tour = new Tour(5);
        assertEquals(5, tour.getTour());
    }

    @Test
    void testIncrementer() {
        Tour tour = new Tour(1);
        tour.incrementer();
        assertEquals(2, tour.getTour());
    }

    @Test
    void testResetTour() {
        Tour tour = new Tour(10);
        tour.resetTour();
        assertEquals(1, tour.getTour());
    }
}
