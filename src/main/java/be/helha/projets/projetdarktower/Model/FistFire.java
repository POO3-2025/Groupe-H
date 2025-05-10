package be.helha.projets.projetdarktower.Model;

public class FistFire extends Personnage {

    public FistFire(String id) {
        super(id, "Fist Fire", 100, 25);
    }

    @Override
    public int attaquer(Personnage cible){
        int degats = 15;
        int vieRestante = cible.getPointsDeVie()-degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }

    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(100);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 100){
            super.setPointsDeVie(100);
        }
        else {
            super.setPointsDeVie(pointsDeVie);
        }
    }
}
