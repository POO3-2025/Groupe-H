package be.helha.projets.projetdarktower.Model;

public class Minotaurus extends Personnage {

    private int niveau; // représente l'étage ou le niveau de puissance

    public Minotaurus(String id, int niveau) {
        super(id, "Minotaurus",
                50 + (int)(Math.pow(niveau, 1.5) * 2.0),
                10 + (int)(Math.pow(niveau, 1.3) * 1.5));
        this.niveau = niveau;
    }


    @Override
    public int attaquer(Personnage cible) {
        int degats = this.attaque;

        // Passif de WaterWa : réduit les dégâts reçus de moitié
        if (cible instanceof WaterWa) {
            degats /= 2;
        }

        // Passif de JoWind : 20% de chance d’esquiver complètement
        if (cible instanceof JoWind) {
            if (Math.random() < 0.30) {
                System.out.println(cible.getNom() + " esquive l'attaque !");
                degats = 0;
            }
        }

        int vieRestante = cible.getPointsDeVie() - degats;
        System.out.println(nom + " inflige " + degats + " dégâts à " + cible.getNom());
        cible.setPointsDeVie(Math.max(vieRestante, 0));

        return degats;
    }


    public void resetPointsDeVie() {
        this.setPointsDeVie(50 + (int)(Math.pow(niveau, 1.5) * 2.0));
    }


    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
}
