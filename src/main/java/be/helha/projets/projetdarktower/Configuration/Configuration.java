package be.helha.projets.projetdarktower.Configuration;

public class Configuration {
    /**
     * Constructeur par défaut.
     */
    public Configuration() {
        // Constructeur vide
    }
    /** Le type de connexion à utiliser (par exemple, "local" ou "remote"). */
    public String ConnectionType;

    /** Le type de base de données (par exemple, "sqlserver", "mysql", etc.). */
    public String DBType;

    /** Les informations d'identification pour accéder à la base de données. */
    public Credentials BDCredentials;
}