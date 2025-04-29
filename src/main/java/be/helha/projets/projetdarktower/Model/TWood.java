package be.helha.projets.projetdarktower.Model;

public class TWood extends Personnage {

    public TWood(String id) {
        super(id, "TWood", 90, 35);
    }

    @Override
    public void attaquer(Personnage cible, String typeAttaque) {
        int degats = (typeAttaque.equals("magique")) ? attaque + 10 : attaque;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
    }
}

