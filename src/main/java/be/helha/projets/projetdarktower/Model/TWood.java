package be.helha.projets.projetdarktower.Model;

public class TWood extends Personnage {

    public TWood(String id) {
        super(id, "TWood", 90, 35);
    }

    @Override
    public void attaquer(Personnage cible) {
        int degats = 5;
        int vieRestante = cible.getPointsDeVie()-degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
    }
}

