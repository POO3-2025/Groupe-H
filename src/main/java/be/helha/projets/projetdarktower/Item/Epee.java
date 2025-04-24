package be.helha.projets.projetdarktower.Item;

public class Epee extends Item {
    private int degat;

    public Epee(String nom) {
        super(nom);
        this.type = "Épée";
        this.degat = 30;
    }

    public int getDegat() {
        return degat;
    }

    public void setDegat(int degat) {
        this.degat = degat;
    }

    @Override
    public String toString() {
        return "Épée {" + super.toString() + ", dégâts=" + degat + '}';
    }
}
