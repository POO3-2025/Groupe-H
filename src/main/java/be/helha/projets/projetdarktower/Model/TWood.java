package be.helha.projets.projetdarktower.Model;

public class TWood extends Personnage {

    public TWood(String id) {
        super(id, "TWood", 90, 35);
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = 5;
        int vieRestante = cible.getPointsDeVie()-degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }
    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(90);
    }
    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 90){
            super.setPointsDeVie(90);
        }
        else {
            super.setPointsDeVie(pointsDeVie);
        }
    }
}

