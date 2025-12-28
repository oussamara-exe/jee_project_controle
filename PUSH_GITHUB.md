# 🚀 Guide pour publier sur GitHub

## ✅ Étape 1 : Créer un dépôt sur GitHub

1. Connectez-vous à [GitHub.com](https://github.com)
2. Cliquez sur le bouton **"+"** en haut à droite → **"New repository"**
3. Remplissez les informations :
   - **Repository name** : `jee_project_controle` (ou un autre nom)
   - **Description** : "Application JEE complète de gestion des ressources humaines"
   - **Visibilité** : ✅ **Public** (pour un dépôt public)
   - ⚠️ **Ne cochez PAS** "Add a README file" (nous en avons déjà un)
   - ⚠️ **Ne cochez PAS** "Add .gitignore" (nous en avons déjà un)
4. Cliquez sur **"Create repository"**

## ✅ Étape 2 : Connecter votre dépôt local à GitHub

Ouvrez un terminal dans le dossier du projet et exécutez :

```bash
# Remplacez VOTRE_USERNAME par votre nom d'utilisateur GitHub
git remote add origin https://github.com/VOTRE_USERNAME/jee_project_controle.git

# Vérifier que le remote est bien ajouté
git remote -v
```

## ✅ Étape 3 : Pousser le code sur GitHub

```bash
# Renommer la branche en 'main' (standard GitHub)
git branch -M main

# Pousser le code
git push -u origin main
```

Si GitHub vous demande vos identifiants :
- **Username** : Votre nom d'utilisateur GitHub
- **Password** : Utilisez un **Personal Access Token** (pas votre mot de passe)
  - Pour créer un token : GitHub → Settings → Developer settings → Personal access tokens → Generate new token
  - Cochez au minimum : `repo` (accès complet aux dépôts)

## ✅ Étape 4 : Vérifier

Allez sur votre dépôt GitHub et vérifiez que tous les fichiers sont bien présents !

## 🔐 Alternative : Utiliser SSH (recommandé)

Si vous préférez utiliser SSH au lieu de HTTPS :

```bash
# Ajouter le remote SSH
git remote set-url origin git@github.com:VOTRE_USERNAME/jee_project_controle.git

# Pousser
git push -u origin main
```

## 📝 Commandes utiles pour la suite

```bash
# Voir l'état des fichiers
git status

# Ajouter des modifications
git add .
git commit -m "Description des modifications"

# Pousser les modifications
git push

# Voir l'historique
git log --oneline
```

---

**🎉 Félicitations ! Votre projet est maintenant public sur GitHub !**

