package be.helha.projets.projetdarktower.Model;

public class WaterWa extends Personnage {

    public WaterWa(String id) {
        super(id, "WaterWa", 120, 5);
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = 40;
        int vieRestante = cible.getPointsDeVie()-degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }
    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(120);
    }
}
