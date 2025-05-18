package be.helha.projets.projetdarktower.Model;

import java.util.Random;

public class FistFire extends Personnage {

    private static final double CHANCE_CRITIQUE = 0.4; // 40% crit

    public FistFire(String id) {
        super(id, "Fist Fire", 150, 60); // PV à 150, ATK à 60
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = this.attaque;

        Random random = new Random();
        if (random.nextDouble() < CHANCE_CRITIQUE) {
            degats *= 2; // coup critique
            System.out.println(nom + " fait un coup critique !");
        }

        int vieRestante = cible.getPointsDeVie() - degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }

    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(150);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        if (pointsDeVie > 150) {
            super.setPointsDeVie(150);
        } else {
            super.setPointsDeVie(pointsDeVie);
        }
    }
}

