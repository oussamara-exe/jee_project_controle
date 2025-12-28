<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Tableau de bord" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="dashboard" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-tachometer-alt"></i> Tableau de bord</h1>
            <p class="text-muted mb-0">
                <i class="fas fa-calendar-alt"></i> 
                <fmt:formatDate value="<%= new java.util.Date() %>" pattern="EEEE dd MMMM yyyy" />
            </p>
        </div>
        
        <!-- Carte de bienvenue -->
        <div class="alert alert-info" role="alert">
            <h4 class="alert-heading">
                <i class="fas fa-user-circle"></i> 
                Bienvenue, ${sessionScope.currentUser.employePrenom} ${sessionScope.currentUser.employeNom} !
            </h4>
            <p class="mb-0">Vous êtes connecté en tant qu'<strong>Employé</strong>.</p>
        </div>
        
        <!-- Cartes statistiques -->
        <div class="row">
            <!-- Solde de congés -->
            <div class="col-md-4 mb-4">
                <div class="card text-white" style="background: linear-gradient(135deg, #00c6ff 0%, #0072ff 100%);">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title mb-2">Congés restants</h6>
                                <h2 class="mb-0">
                                    <c:choose>
                                        <c:when test="${not empty soldeConge}">
                                            ${soldeConge.joursRestants} jours
                                        </c:when>
                                        <c:otherwise>
                                            -- jours
                                        </c:otherwise>
                                    </c:choose>
                                </h2>
                            </div>
                            <div>
                                <i class="fas fa-calendar-check fa-3x opacity-50"></i>
                            </div>
                        </div>
                        <hr class="bg-white opacity-25">
                        <small>
                            <c:if test="${not empty soldeConge}">
                                ${soldeConge.joursPris} jours pris sur ${soldeConge.joursAcquis}
                            </c:if>
                        </small>
                    </div>
                </div>
            </div>
            
            <!-- Demandes de congés en cours -->
            <div class="col-md-4 mb-4">
                <div class="card text-white" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title mb-2">Congés en attente</h6>
                                <h2 class="mb-0">${congesEnAttente != null ? congesEnAttente : 0}</h2>
                            </div>
                            <div>
                                <i class="fas fa-clock fa-3x opacity-50"></i>
                            </div>
                        </div>
                        <hr class="bg-white opacity-25">
                        <small>En attente de validation</small>
                    </div>
                </div>
            </div>
            
            <!-- Notifications -->
            <div class="col-md-4 mb-4">
                <div class="card text-white" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title mb-2">Notifications</h6>
                                <h2 class="mb-0">${notificationsNonLues != null ? notificationsNonLues : 0}</h2>
                            </div>
                            <div>
                                <i class="fas fa-bell fa-3x opacity-50"></i>
                            </div>
                        </div>
                        <hr class="bg-white opacity-25">
                        <small>Messages non lus</small>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Actions rapides -->
        <div class="row">
            <div class="col-md-12 mb-4">
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-bolt"></i> Actions rapides
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/app/conges/create" 
                                   class="btn btn-primary w-100 py-3">
                                    <i class="fas fa-plus-circle fa-2x d-block mb-2"></i>
                                    Demander un congé
                                </a>
                            </div>
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/app/conges/list" 
                                   class="btn btn-info w-100 py-3">
                                    <i class="fas fa-list fa-2x d-block mb-2"></i>
                                    Mes congés
                                </a>
                            </div>
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/app/temps/list" 
                                   class="btn btn-success w-100 py-3">
                                    <i class="fas fa-clock fa-2x d-block mb-2"></i>
                                    Feuilles de temps
                                </a>
                            </div>
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/app/paie/mes-fiches" 
                                   class="btn btn-warning w-100 py-3">
                                    <i class="fas fa-file-invoice-dollar fa-2x d-block mb-2"></i>
                                    Mes fiches de paie
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Notifications récentes -->
        <c:if test="${not empty notifications}">
            <div class="row">
                <div class="col-md-12">
                    <div class="card">
                        <div class="card-header">
                            <i class="fas fa-bell"></i> Notifications récentes
                        </div>
                        <div class="card-body">
                            <div class="list-group">
                                <c:forEach items="${notifications}" var="notif" end="4">
                                    <a href="${notif.lien}" class="list-group-item list-group-item-action ${notif.lu ? '' : 'list-group-item-info'}">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h6 class="mb-1">
                                                <c:choose>
                                                    <c:when test="${notif.type == 'CONGE_APPROUVE'}">
                                                        <i class="fas fa-check-circle text-success"></i>
                                                    </c:when>
                                                    <c:when test="${notif.type == 'CONGE_REFUSE'}">
                                                        <i class="fas fa-times-circle text-danger"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-info-circle text-info"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                                ${notif.message}
                                            </h6>
                                            <small class="text-muted">
                                                ${notif.dateCreation}
                                            </small>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/app/notifications" class="btn btn-sm btn-outline-primary">
                                    Voir toutes les notifications
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

