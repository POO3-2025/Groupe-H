package be.helha.projets.projetdarktower.Item;

public class Potion extends Item {
    private int pointsDeVieRecuperes;
    private int usages;

    public Potion(String nom,int PointsDeVieRecuperes,double chanceDeDrop,int usages) {
        super(nom,chanceDeDrop);
        this.type = "Potion";
        this.pointsDeVieRecuperes = PointsDeVieRecuperes;
        this.usages = usages;
    }
    public Potion() {
        super();
        this.type = "Potion";
    }

    public int getPointsDeVieRecuperes() {
        return pointsDeVieRecuperes;
    }

    public int getUsages() {
        return usages;
    }

    public void setUsages(int usages) {
        this.usages = usages;
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
