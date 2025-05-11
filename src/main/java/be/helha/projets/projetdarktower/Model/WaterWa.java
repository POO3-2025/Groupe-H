package be.helha.projets.projetdarktower.Model;

public class WaterWa extends Personnage {

    public WaterWa(String id) {
        super(id, "WaterWa", 180, 20); // 180 PV, 20 ATK
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = 20;
        int vieRestante = cible.getPointsDeVie() - degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }

    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(180);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 180){
            super.setPointsDeVie(180);
        }
        else {
            super.setPointsDeVie(pointsDeVie);
        }
    }
}

