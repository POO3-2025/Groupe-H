package be.helha.projets.projetdarktower.Item;

public class Weapon extends Item {
    private int degats;
    private int usages;

    public Weapon(String nom, int degats, double chanceDeDrop,int usages) {
        super(nom, chanceDeDrop);
        this.type = "Weapon"; // cohérence avec le nom de la classe
        this.degats = degats;
        this.usages = usages;
    }
    public Weapon() {
        super();
        this.type = "Weapon";
    }

    public int getDegats() {
        return degats;
    }

    public void setDegats(int degats) {
        this.degats = degats;
    }

    public int getUsages() {
        return usages;
    }

    public void setUsages(int usages) {
        this.usages = usages;
    }

    @Override
    public String toString() {
        return "Weapon {" + super.toString() + ", dégâts=" + degats + '}';
    }
}
