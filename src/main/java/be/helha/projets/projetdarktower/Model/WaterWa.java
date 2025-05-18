package be.helha.projets.projetdarktower.Model;

public class WaterWa extends Personnage {

    public WaterWa(String id) {
        super(id, "WaterWa", 220, 30); // PV 220, ATK 30
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = this.attaque;
        int vieRestante = cible.getPointsDeVie() - degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }

    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(220);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 220) {
            super.setPointsDeVie(220);
        } else {
            super.setPointsDeVie(pointsDeVie);
        }
    }

}


