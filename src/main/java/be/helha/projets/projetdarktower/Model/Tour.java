package be.helha.projets.projetdarktower.Model;

public class Tour {
    private int tour;
    public Tour(int tour) { this.tour = tour; }
    public int getTour() { return tour; }
    public void incrementer() { this.tour++; }
    public void resetTour() { this.tour = 1; }
}
