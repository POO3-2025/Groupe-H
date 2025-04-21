package be.helha.projets.projetdarktower.Model;

public class WaterWa extends Personnage {

    public WaterWa(String id) {
        super(id, "WaterWa", 120, 5);
    }

    @Override
    public void attaquer(Personnage cible, String typeAttaque) {
        int degats = (typeAttaque.equals("magique")) ? attaque + 10 : attaque;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
    }
}
