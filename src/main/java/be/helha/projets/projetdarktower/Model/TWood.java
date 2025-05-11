package be.helha.projets.projetdarktower.Model;

public class TWood extends Personnage {

    public TWood(String id) {
        super(id, "TWood", 120, 25);
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
        super.setPointsDeVie(Math.min(pointsDeVie, 90)); // Limite les PV à 90
    }

    // Méthode de régénération pour TWood
    public void regenererPV() {
        int vieActuelle = this.getPointsDeVie();
        int nouvelleVie = vieActuelle + 10; // Régénère 10 PV
        this.setPointsDeVie(nouvelleVie);
        System.out.println(this.getNom() + " se régénère de 10 PV, nouvelle vie : " + this.getPointsDeVie());
    }
}
