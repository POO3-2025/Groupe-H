package be.helha.projets.projetdarktower.Model;

public class FistFire extends Personnage {

    public FistFire(String id) {
        super(id, "Fist Fire", 100, 25);
    }

    @Override
    public void attaquer(Personnage cible){
        int degats = 15;
        int vieRestante = cible.getPointsDeVie()-degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
    }
}
