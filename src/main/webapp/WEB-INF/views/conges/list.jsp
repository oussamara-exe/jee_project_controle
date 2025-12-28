<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Mes congés" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="conges" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-calendar-alt"></i> Mes congés</h1>
            <a href="${pageContext.request.contextPath}/app/conges/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouvelle demande
            </a>
        </div>

        <!-- Solde de congés -->
        <c:if test="${not empty solde}">
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="card text-white bg-info">
                        <div class="card-body">
                            <h6 class="card-title">Jours acquis</h6>
                            <h2>${solde.joursAcquis}</h2>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card text-white bg-warning">
                        <div class="card-body">
                            <h6 class="card-title">Jours pris</h6>
                            <h2>${solde.joursPris}</h2>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card text-white bg-success">
                        <div class="card-body">
                            <h6 class="card-title">Jours restants</h6>
                            <h2>${solde.joursRestants}</h2>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Liste des congés -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Historique des demandes</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty conges}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucune demande de congé enregistrée.
                    </div>
                </c:if>

                <c:if test="${not empty conges}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Type</th>
                                    <th>Date début</th>
                                    <th>Date fin</th>
                                    <th>Nombre de jours</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="conge" items="${conges}">
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
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/app/conges/view?id=${conge.id}" 
                                               class="btn btn-sm btn-info" title="Voir">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </div>
        
        <div class="mt-3">
            <a href="${pageContext.request.contextPath}/app/conges/solde" class="btn btn-outline-primary">
                <i class="fas fa-chart-pie"></i> Voir mon solde détaillé
            </a>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />
