package ma.projet.rh.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utilitaire pour le hashage et la vérification des mots de passe avec BCrypt
 */
public class PasswordUtil {

    private static final int BCRYPT_COST = 10; // Coût de l'algorithme BCrypt

    /**
     * Hash un mot de passe avec BCrypt
     *
     * @param plainPassword Mot de passe en clair
     * @return Hash BCrypt du mot de passe
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Vérifie si un mot de passe correspond à un hash BCrypt
     *
     * @param plainPassword Mot de passe en clair
     * @param hashedPassword Hash BCrypt à vérifier
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }

    /**
     * Valide la force d'un mot de passe
     * - Au moins 8 caractères
     * - Au moins une majuscule
     * - Au moins une minuscule
     * - Au moins un chiffre
     *
     * @param password Mot de passe à valider
     * @return true si le mot de passe est valide, false sinon
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasUppercase && hasLowercase && hasDigit) {
                return true;
            }
        }

        return false;
    }

    /**
     * Génère un mot de passe temporaire aléatoire
     *
     * @return Mot de passe temporaire
     */
    public static String generateTemporaryPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String all = upper + lower + digits;
        
        StringBuilder password = new StringBuilder();
        
        // Au moins une majuscule
        password.append(upper.charAt((int) (Math.random() * upper.length())));
        // Au moins une minuscule
        password.append(lower.charAt((int) (Math.random() * lower.length())));
        // Au moins un chiffre
        password.append(digits.charAt((int) (Math.random() * digits.length())));
        
        // Compléter jusqu'à 12 caractères
        for (int i = 0; i < 9; i++) {
            password.append(all.charAt((int) (Math.random() * all.length())));
        }
        
        // Mélanger les caractères
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
}

