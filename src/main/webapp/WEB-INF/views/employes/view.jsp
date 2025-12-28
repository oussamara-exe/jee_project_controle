<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Détails de l'employé" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="employes" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-user"></i> Détails de l'employé</h1>
            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/app/employes/list" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Retour
                </a>
                <a href="${pageContext.request.contextPath}/app/employes/edit?id=${employe.id}" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Modifier
                </a>
            </div>
        </div>
        
        <!-- Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'updated'}">Employé mis à jour avec succès !</c:when>
                    <c:when test="${param.success == 'restored'}">Employé restauré avec succès !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="row">
            <!-- Informations principales -->
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-id-card"></i> Informations personnelles</h5>
                    </div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong>Matricule :</strong><br>
                                <span class="badge bg-info">${employe.matricule}</span>
                            </div>
                            <div class="col-md-6">
                                <strong>Nom complet :</strong><br>
                                ${employe.nom} ${employe.prenom}
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong>Email :</strong><br>
                                <i class="fas fa-envelope"></i> ${employe.email}
                            </div>
                            <div class="col-md-6">
                                <strong>Téléphone :</strong><br>
                                <i class="fas fa-phone"></i> ${employe.telephone}
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <strong>Date de naissance :</strong><br>
                            ${employe.dateNaissance}
                        </div>
                        
                        <div class="mb-3">
                            <strong>Adresse :</strong><br>
                            ${employe.adresse}
                        </div>
                    </div>
                </div>
                
                <div class="card mb-4">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0"><i class="fas fa-briefcase"></i> Informations professionnelles</h5>
                    </div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <strong>Département :</strong><br>
                                <c:if test="${not empty employe.departement}">
                                    <span class="badge bg-info">${employe.departement.nom}</span>
                                </c:if>
                                <c:if test="${empty employe.departement}">
                                    <span class="text-muted">Non assigné</span>
                                </c:if>
                            </div>
                            <div class="col-md-4">
                                <strong>Poste :</strong><br>
                                <c:if test="${not empty employe.poste}">
                                    ${employe.poste.titre}
                                </c:if>
                                <c:if test="${empty employe.poste}">
                                    <span class="text-muted">Non assigné</span>
                                </c:if>
                            </div>
                            <div class="col-md-4">
                                <strong>Manager :</strong><br>
                                <c:if test="${not empty employe.manager}">
                                    ${employe.manager.nom} ${employe.manager.prenom}
                                </c:if>
                                <c:if test="${empty employe.manager}">
                                    <span class="text-muted">Aucun</span>
                                </c:if>
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <strong>Date d'embauche :</strong><br>
                                ${employe.dateEmbauche}
                            </div>
                            <div class="col-md-4">
                                <strong>Salaire de base :</strong><br>
                                <fmt:formatNumber value="${employe.salaireBase}" type="currency" currencyCode="MAD" />
                            </div>
                            <div class="col-md-4">
                                <strong>Heures hebdomadaires :</strong><br>
                                ${employe.heuresHebdo} h/semaine
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <strong>IBAN :</strong><br>
                            ${employe.iban}
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Informations complémentaires -->
            <div class="col-md-4">
                <div class="card mb-4">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0"><i class="fas fa-info-circle"></i> Statut</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <strong>Statut :</strong><br>
                            <c:choose>
                                <c:when test="${employe.actif}">
                                    <span class="badge bg-success">Actif</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Inactif</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <c:if test="${not empty soldeConge}">
                            <div class="mb-3">
                                <strong>Solde de congés :</strong><br>
                                <span class="badge bg-primary">${soldeConge.joursRestants} jours restants</span>
                            </div>
                        </c:if>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0"><i class="fas fa-cog"></i> Actions</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/app/employes/edit?id=${employe.id}" 
                               class="btn btn-warning">
                                <i class="fas fa-edit"></i> Modifier
                            </a>
                            
                            <c:if test="${employe.actif}">
                                <form action="${pageContext.request.contextPath}/app/employes/archive" method="post" 
                                      class="d-grid">
                                    <input type="hidden" name="id" value="${employe.id}">
                                    <button type="submit" class="btn btn-secondary" 
                                            onclick="return confirm('Archiver cet employé ?')">
                                        <i class="fas fa-archive"></i> Archiver
                                    </button>
                                </form>
                            </c:if>
                            
                            <c:if test="${!employe.actif}">
                                <form action="${pageContext.request.contextPath}/app/employes/restore" method="post" 
                                      class="d-grid">
                                    <input type="hidden" name="id" value="${employe.id}">
                                    <button type="submit" class="btn btn-success">
                                        <i class="fas fa-undo"></i> Restaurer
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

