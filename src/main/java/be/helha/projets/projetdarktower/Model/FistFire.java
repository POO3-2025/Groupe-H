package be.helha.projets.projetdarktower.Model;

public class FistFire extends Personnage {

    public FistFire(String id) {
        super(id, "Fist Fire", 100, 25);
    }

    @Override
    public void attaquer(Personnage cible, String typeAttaque) {
        int degats = (typeAttaque.equals("physique")) ? attaque : attaque + 10;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
    }
}
