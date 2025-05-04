package be.helha.projets.projetdarktower.Model;

public class Minotaurus extends Personnage {

    private int niveau; // représente l'étage ou le niveau de puissance

    public Minotaurus(String id, int niveau) {
        super(id, "Minotaurus", 80 + (niveau * 20), 20 + (niveau * 5)); // augmentation PV & attaque
        this.niveau = niveau;
    }

    @Override
    public void attaquer(Personnage cible, String typeAttaque) {
        int degats = typeAttaque.equals("physique") ? attaque : attaque + 5;
        cible.pointsDeVie -= degats;
        System.out.println(nom + " (Niveau " + niveau + ") inflige " + degats + " dégâts à " + cible.getNom());
    }

    public int getNiveau() {
        return niveau;
    }

    public void monterNiveau() {
        this.niveau++;
        this.pointsDeVie += 20;
        this.attaque += 5;
    }
}
