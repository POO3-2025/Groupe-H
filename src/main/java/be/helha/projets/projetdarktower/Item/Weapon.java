package be.helha.projets.projetdarktower.Item;

public class Weapon extends Item {
    private int degats;

    public Weapon(String nom, int degats, double chanceDeDrop) {
        super(nom, chanceDeDrop);
        this.type = "Weapon"; // cohérence avec le nom de la classe
        this.degats = degats;
    }

    public int getDegats() {
        return degats;
    }

    public void setDegats(int degats) {
        this.degats = degats;
    }

    @Override
    public String toString() {
        return "Weapon {" + super.toString() + ", dégâts=" + degats + '}';
    }
}
