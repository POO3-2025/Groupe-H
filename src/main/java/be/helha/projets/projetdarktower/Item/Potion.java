package be.helha.projets.projetdarktower.Item;

public class Potion extends Item {
    private int pointsDeVieRecuperes;

    public Potion(String nom) {
        super(nom);
        this.type = "Potion";
        this.pointsDeVieRecuperes = 100;
    }

    public int getPointsDeVieRecuperes() {
        return pointsDeVieRecuperes;
    }

    public void setPointsDeVieRecuperes(int pointsDeVieRecuperes) {
        this.pointsDeVieRecuperes = pointsDeVieRecuperes;
    }

    @Override
    public String toString() {
        return "Potion {" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", pointsDeVieRecuperes=" + pointsDeVieRecuperes +
                '}';
    }
}
