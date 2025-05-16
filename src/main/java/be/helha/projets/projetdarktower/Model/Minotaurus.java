package be.helha.projets.projetdarktower.Model;


public class Minotaurus extends Personnage {

    public Minotaurus(String id) {
        super(id, "Minotaurus", 180, 30); // PV et Attaque personnalisés
    }

    @Override
    public void attaquer(Personnage cible, String typeAttaque) {
        int degats = switch (typeAttaque.toLowerCase()) {
            case "charge" -> this.attaque + 10;  // attaque spéciale
            case "coup de hache" -> this.attaque;
            default -> 25;
        };

        cible.pointsDeVie -= degats;
        if (cible.pointsDeVie < 0) cible.pointsDeVie = 0;

        System.out.println(this.nom + " attaque " + cible.getNom() + " avec " + typeAttaque + " et inflige " + degats + " points de dégâts.");
    }
}
