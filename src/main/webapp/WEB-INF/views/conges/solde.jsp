<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Solde de congés" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="conges" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-calendar-check"></i> Mon solde de congés</h1>
            <a href="${pageContext.request.contextPath}/app/conges/list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <c:if test="${not empty solde}">
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="card text-white bg-primary">
                        <div class="card-body text-center">
                            <h5 class="card-title">Jours acquis</h5>
                            <h2 class="mb-0">${solde.joursAcquis}</h2>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card text-white bg-warning">
                        <div class="card-body text-center">
                            <h5 class="card-title">Jours pris</h5>
                            <h2 class="mb-0">${solde.joursPris}</h2>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card text-white bg-success">
                        <div class="card-body text-center">
                            <h5 class="card-title">Jours restants</h5>
                            <h2 class="mb-0">${solde.joursRestants}</h2>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Historique des congés (${solde.annee})</h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty historique}">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>Type</th>
                                        <th>Date début</th>
                                        <th>Date fin</th>
                                        <th>Jours</th>
                                        <th>Statut</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="conge" items="${historique}">
                                        <tr>
                                            <td>${conge.typeConge.libelle}</td>
                                            <td>${conge.dateDebut}</td>
                                            <td>${conge.dateFin}</td>
                                            <td>${conge.nombreJours}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${conge.statut == 'APPROUVE'}">
                                                        <span class="badge bg-success">Approuvé</span>
                                                    </c:when>
                                                    <c:when test="${conge.statut == 'REFUSE'}">
                                                        <span class="badge bg-danger">Refusé</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning">En attente</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                    <c:if test="${empty historique}">
                        <div class="text-center text-muted py-4">
                            <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                            Aucun congé enregistré
                        </div>
                    </c:if>
                </div>
            </div>
        </c:if>
        
        <c:if test="${empty solde}">
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i> Aucun solde de congé disponible pour l'année en cours.
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

