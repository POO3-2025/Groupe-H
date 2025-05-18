package be.helha.projets.projetdarktower.Model;

public abstract class Personnage {

    protected String id;
    protected String nom;
    protected int pointsDeVie;
    protected int attaque;

    public Personnage(String id, String nom, int pointsDeVie, int attaque) {
        this.id = id;
        this.nom = nom;
        this.pointsDeVie = pointsDeVie;
        this.attaque = attaque;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getPointsDeVie() {
        if (this.pointsDeVie < 0) {
            this.pointsDeVie = 0;
        }
        return pointsDeVie;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPointsDeVie(int pointsDeVie) {
        this.pointsDeVie = pointsDeVie;
    }

    public void setAttaque(int attaque) {
        this.attaque = attaque;
    }

    public int getAttaque() {
        return attaque;
    }

    public abstract int attaquer(Personnage cible);
    public void resetPointDeVie() {
    }
}

