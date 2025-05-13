package be.helha.projets.projetdarktower.Model;

public class CombatResult {
    public String message;
    public int pvJoueur;
    public int pvMinotaure;
    public int degatsMinotaure;
    public int tour;

    public CombatResult(String message, int pvJoueur, int pvMinotaure, int degatsMinotaure, int tour) {
        this.message = message;
        this.pvJoueur = pvJoueur;
        this.pvMinotaure = pvMinotaure;
        this.degatsMinotaure = degatsMinotaure;
        this.tour = tour;
    }
}

