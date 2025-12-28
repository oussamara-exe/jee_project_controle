<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Détails du congé" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="conges" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-calendar-alt"></i> Détails du congé</h1>
            <a href="${pageContext.request.contextPath}/app/conges/list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'created'}">Demande de congé créée avec succès !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="row">
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Informations du congé</h5>
                    </div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong>Type de congé :</strong><br>
                                <span class="badge bg-info">${conge.typeConge.libelle}</span>
                            </div>
                            <div class="col-md-6">
                                <strong>Statut :</strong><br>
                                <c:choose>
                                    <c:when test="${conge.statut == 'APPROUVE'}">
                                        <span class="badge bg-success">Approuvé</span>
                                    </c:when>
                                    <c:when test="${conge.statut == 'VALIDE_RH'}">
                                        <span class="badge bg-info">Validé RH</span>
                                    </c:when>
                                    <c:when test="${conge.statut == 'VALIDE_MANAGER'}">
                                        <span class="badge bg-warning">Validé Manager</span>
                                    </c:when>
                                    <c:when test="${conge.statut == 'REFUSE'}">
                                        <span class="badge bg-danger">Refusé</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">En attente</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong>Date de début :</strong><br>
                                ${conge.dateDebut}
                            </div>
                            <div class="col-md-6">
                                <strong>Date de fin :</strong><br>
                                ${conge.dateFin}
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong>Nombre de jours :</strong><br>
                                ${conge.nombreJours} jour(s)
                            </div>
                            <div class="col-md-6">
                                <strong>Date de demande :</strong><br>
                                ${conge.dateDemande}
                            </div>
                        </div>
                        
                        <c:if test="${not empty conge.commentaireEmploye}">
                            <div class="mb-3">
                                <strong>Commentaire employé :</strong><br>
                                ${conge.commentaireEmploye}
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty conge.commentaireManager}">
                            <div class="mb-3">
                                <strong>Commentaire manager :</strong><br>
                                ${conge.commentaireManager}
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty conge.commentaireRH}">
                            <div class="mb-3">
                                <strong>Commentaire RH :</strong><br>
                                ${conge.commentaireRH}
                            </div>
                        </c:if>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">Employé</h5>
                    </div>
                    <div class="card-body">
                        <strong>Nom complet :</strong> ${conge.employe.nom} ${conge.employe.prenom}<br>
                        <strong>Matricule :</strong> ${conge.employe.matricule}<br>
                        <strong>Email :</strong> ${conge.employe.email}
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0">Actions</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/app/conges/list" 
                               class="btn btn-secondary">
                                <i class="fas fa-list"></i> Retour à la liste
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

