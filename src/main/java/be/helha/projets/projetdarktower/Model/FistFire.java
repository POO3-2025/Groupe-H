package be.helha.projets.projetdarktower.Model;

import java.util.Random;

public class FistFire extends Personnage {

    private static final double CHANCE_CRITIQUE = 0.4; // 40% de chance de faire un coup critique

    public FistFire(String id) {
        super(id, "Fist Fire", 70, 40); // 70 PV et 40 ATK
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = this.attaque;

        // Générer un nombre aléatoire entre 0 et 1
        Random random = new Random();
        if (random.nextDouble() < CHANCE_CRITIQUE) {
            degats *= 2; // Si le nombre est inférieur à 0.4, coup critique
            System.out.println(nom + " fait un coup critique !");
        }

        int vieRestante = cible.getPointsDeVie() - degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }

    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(70);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 70) {
            super.setPointsDeVie(70);
        } else {
            super.setPointsDeVie(pointsDeVie);
        }
    }
}
