package be.helha.projets.projetdarktower.Configuration;

public class Credentials {
    /**
     * Constructeur par défaut.
     */
    public Credentials() {
        // Constructeur vide
    }
    /** Le nom d'hôte du serveur de base de données (par exemple, "localhost" ou une adresse IP). */
    public String HostName;

    /** Le nom d'utilisateur pour se connecter à la base de données. */
    public String UserName;

    /** Le mot de passe pour se connecter à la base de données. */
    public String Password;

    /** Le nom de la base de données à laquelle se connecter. */
    public String DBName;

    /** Le port utilisé pour la connexion à la base de données . */
    public int port;
}
