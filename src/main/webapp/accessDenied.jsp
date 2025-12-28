<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accès refusé - Gestion RH</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .error-container {
            background: white;
            border-radius: 20px;
            padding: 60px 40px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            text-align: center;
            max-width: 600px;
        }
        
        .error-icon {
            font-size: 6rem;
            color: #dc3545;
            margin-bottom: 20px;
        }
        
        .error-code {
            font-size: 3rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
        }
        
        .error-message {
            font-size: 1.2rem;
            color: #666;
            margin-bottom: 30px;
        }
        
        .btn-home {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px 30px;
            font-weight: 600;
            border-radius: 25px;
            transition: all 0.3s;
        }
        
        .btn-home:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
            color: white;
        }
    </style>
</head>
<body>

<div class="error-container">
    <i class="fas fa-ban error-icon"></i>
    <div class="error-code">403</div>
    <div class="error-message">Accès refusé</div>
    <p class="text-muted mb-4">
        Vous n'avez pas les permissions nécessaires pour accéder à cette page.
        <br>
        Si vous pensez qu'il s'agit d'une erreur, veuillez contacter votre administrateur.
    </p>
    
    <div class="d-flex justify-content-center gap-3">
        <a href="javascript:history.back()" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour
        </a>
        <a href="${pageContext.request.contextPath}/app/dashboard" class="btn btn-home">
            <i class="fas fa-home"></i> Accueil
        </a>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>

