package ma.projet.rh.utils;

/**
 * Utilitaire pour générer des hash de mots de passe
 * À utiliser une seule fois pour initialiser les comptes
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        String password = "password123";
        String hash = PasswordUtil.hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("\nSQL pour mettre à jour:");
        System.out.println("UPDATE user_accounts SET password = '" + hash + "' WHERE username = 'admin';");
        System.out.println("UPDATE user_accounts SET password = '" + hash + "' WHERE username = 'rh.manager';");
        System.out.println("UPDATE user_accounts SET password = '" + hash + "' WHERE username = 'it.manager';");
        System.out.println("UPDATE user_accounts SET password = '" + hash + "' WHERE username = 'dev.senior';");
        System.out.println("UPDATE user_accounts SET password = '" + hash + "' WHERE username = 'dev.junior';");
    }
}

