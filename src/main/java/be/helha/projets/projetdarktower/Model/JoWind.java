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
}

