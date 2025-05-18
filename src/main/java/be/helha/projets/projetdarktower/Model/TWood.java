package be.helha.projets.projetdarktower.Model;

public class TWood extends Personnage {

    public TWood(String id) {
        super(id, "TWood", 90, 25);  // PV 90 (corrigé), ATK 25
    }

    @Override
    public int attaquer(Personnage cible) {
        int degats = this.attaque;
        int vieRestante = cible.getPointsDeVie() - degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(vieRestante);
        return degats;
    }

    @Override
    public void resetPointDeVie() {
        this.setPointsDeVie(90);
    }

    @Override
    public void setPointsDeVie(int pointsDeVie) {
        super.setPointsDeVie(Math.min(pointsDeVie, 90)); // Limite à 90
    }

    public void regenererPV() {
        int vieActuelle = this.getPointsDeVie();
        int nouvelleVie = Math.min(vieActuelle + 15, 90); // Régénère 15 PV max 90
        this.setPointsDeVie(nouvelleVie);
        System.out.println(this.getNom() + " se régénère de 15 PV, nouvelle vie : " + this.getPointsDeVie());
    }
}

