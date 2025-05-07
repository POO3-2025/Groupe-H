package be.helha.projets.projetdarktower.Model;

public class Minotaurus extends Personnage {

    private int niveau; // représente l'étage ou le niveau de puissance

    public Minotaurus(String id, int niveau) {
        super(id, "Minotaurus", 80 + (niveau * 20), 20 + (niveau * 5)); // augmentation PV & attaque
        this.niveau = niveau;
    }

    @Override
    public void attaquer(Personnage cible) {
        int degats = 20;
        int vieRestante = cible.getPointsDeVie()-degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
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
