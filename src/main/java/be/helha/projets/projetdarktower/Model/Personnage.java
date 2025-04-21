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
        return pointsDeVie;
    }

    public int getAttaque() {
        return attaque;
    }

    public abstract void attaquer(Personnage cible, String typeAttaque);
}

