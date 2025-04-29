package be.helha.projets.projetdarktower.Model;

public class JoWind extends Personnage {

    public JoWind(String id) {
        super(id, "JoWind", 110, 15);
    }

    @Override
    public void attaquer(Personnage cible, String typeAttaque) {
        int degats = (typeAttaque.equals("magique")) ? attaque + 10 : attaque;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
    }
}

