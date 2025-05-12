package be.helha.projets.projetdarktower.Model;

public class Etage {
    private int etage;

    public Etage(int etage) {
        this.etage = etage;
    }

    public int getEtage() {
        return etage;
    }

    public void incrementer() {
        this.etage++;
    }

    public void resetEtage() {
        this.etage = 1;
    }
}
