package be.helha.projets.projetdarktower.Model;

public class JoWind extends Personnage {

    private static final double CHANCE_ESQUIVE = 0.30;

    public JoWind(String id) {
        super(id, "JoWind", 110, 30);
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
        this.setPointsDeVie(110);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 110) {
            super.setPointsDeVie(110);
        } else {
            super.setPointsDeVie(pointsDeVie);
        }
    }

}


