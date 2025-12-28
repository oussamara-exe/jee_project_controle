<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Sidebar -->
<div class="col-md-2 sidebar">
    <nav class="nav flex-column">
        <!-- Dashboard -->
        <a class="nav-link ${param.activePage == 'dashboard' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/app/dashboard">
            <i class="fas fa-tachometer-alt"></i> Tableau de bord
        </a>
        
        <!-- Employés -->
        <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'RH'}">
            <a class="nav-link ${param.activePage == 'employes' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/employes/list">
                <i class="fas fa-users"></i> Employés
            </a>
            
            <a class="nav-link ${param.activePage == 'departements' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/departements/list">
                <i class="fas fa-building"></i> Départements
            </a>
            
            <a class="nav-link ${param.activePage == 'postes' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/postes/list">
                <i class="fas fa-briefcase"></i> Postes
            </a>
        </c:if>
        
        <!-- Congés -->
        <a class="nav-link ${param.activePage == 'conges' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/app/conges/list">
            <i class="fas fa-calendar-alt"></i> Mes congés
        </a>
        
        <c:if test="${sessionScope.currentUser.role == 'MANAGER' || sessionScope.currentUser.role == 'RH' || sessionScope.currentUser.role == 'ADMIN'}">
            <a class="nav-link ${param.activePage == 'conges-validation' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/conges/validation">
                <i class="fas fa-check-circle"></i> Valider congés
                <c:if test="${congesEnAttente > 0 || congesEnAttenteRH > 0}">
                    <span class="badge bg-danger ms-auto">${congesEnAttente}${congesEnAttenteRH}</span>
                </c:if>
            </a>
        </c:if>
        
        <!-- Feuilles de temps -->
        <a class="nav-link ${param.activePage == 'temps' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/app/temps/list">
            <i class="fas fa-clock"></i> Feuilles de temps
        </a>
        
        <c:if test="${sessionScope.currentUser.role == 'MANAGER' || sessionScope.currentUser.role == 'RH' || sessionScope.currentUser.role == 'ADMIN'}">
            <a class="nav-link ${param.activePage == 'temps-validation' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/temps/validation">
                <i class="fas fa-check-circle"></i> Valider feuilles de temps
            </a>
        </c:if>
        
        <!-- Paie -->
        <c:if test="${sessionScope.currentUser.role == 'RH' || sessionScope.currentUser.role == 'ADMIN'}">
            <a class="nav-link ${param.activePage == 'paie' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/paie/list">
                <i class="fas fa-money-check-alt"></i> Gestion paie
            </a>
        </c:if>
        
        <a class="nav-link ${param.activePage == 'mes-paies' ? 'active' : ''}" 
           href="${pageContext.request.contextPath}/app/paie/mes-fiches">
            <i class="fas fa-file-invoice-dollar"></i> Mes fiches de paie
        </a>
        
        <!-- Rapports -->
        <c:if test="${sessionScope.currentUser.role != 'EMPLOYE'}">
            <a class="nav-link ${param.activePage == 'rapports' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/rapports">
                <i class="fas fa-chart-bar"></i> Rapports
            </a>
        </c:if>
        
        <!-- Administration -->
        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
            <hr>
            <h6 class="text-muted px-3 mb-2">Administration</h6>
            
            <a class="nav-link ${param.activePage == 'utilisateurs' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/admin/utilisateurs">
                <i class="fas fa-user-shield"></i> Utilisateurs
            </a>
            
            <a class="nav-link ${param.activePage == 'historique' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/admin/historique">
                <i class="fas fa-history"></i> Historique
            </a>
            
            <a class="nav-link ${param.activePage == 'parametres' ? 'active' : ''}" 
               href="${pageContext.request.contextPath}/app/admin/parametres">
                <i class="fas fa-cogs"></i> Paramètres
            </a>
        </c:if>
    </nav>
</div>

