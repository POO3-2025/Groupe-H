package be.helha.projets.projetdarktower.Model;

public class JoWind extends Personnage {

    public JoWind(String id) {
        super(id, "JoWind", 110, 15);
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = 50;
        int vieRestante = cible.getPointsDeVie()-degats;
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
        if (pointsDeVie > 110){
            super.setPointsDeVie(110);
        }
        else {
            super.setPointsDeVie(pointsDeVie);
        }
    }
}

