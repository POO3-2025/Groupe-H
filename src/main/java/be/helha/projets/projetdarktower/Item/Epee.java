package be.helha.projets.projetdarktower.Item;

public class Epee extends Item {
    private int degat;

    public Epee(String nom, int degat,double chanceDeDrop) {
        super(nom,chanceDeDrop);
        this.type = "Épée";
        this.degat = degat;
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
