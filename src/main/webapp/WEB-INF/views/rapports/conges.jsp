<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Rapport Congés" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="rapports" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-calendar-alt"></i> Rapport Congés</h1>
            <div>
                <form method="get" action="${pageContext.request.contextPath}/app/rapports/conges" class="d-inline">
                    <input type="number" name="annee" value="${annee}" min="2020" max="2030" class="form-control d-inline-block" style="width: 100px;">
                    <button type="submit" class="btn btn-primary">Filtrer</button>
                </form>
                <a href="${pageContext.request.contextPath}/app/rapports" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Retour
                </a>
            </div>
        </div>

        <!-- Statistiques générales -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-primary">
                    <div class="card-body text-center">
                        <h5 class="card-title">Total congés</h5>
                        <h2>${totalConges}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-warning">
                    <div class="card-body text-center">
                        <h5 class="card-title">En attente</h5>
                        <h2>${congesEnAttente}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info">
                    <div class="card-body text-center">
                        <h5 class="card-title">Validés Manager</h5>
                        <h2>${congesValides}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-success">
                    <div class="card-body text-center">
                        <h5 class="card-title">Approuvés</h5>
                        <h2>${congesApprouves}</h2>
                    </div>
                </div>
            </div>
        </div>

        <!-- Congés par type -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-chart-pie"></i> Répartition par type de congé</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty congesParType}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Type de congé</th>
                                    <th>Nombre</th>
                                    <th>Pourcentage</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="type" items="${congesParType}">
                                    <tr>
                                        <td><strong>${type[0].libelle}</strong></td>
                                        <td>${type[1]}</td>
                                        <td>
                                            <div class="progress">
                                                <div class="progress-bar bg-success" role="progressbar" 
                                                     style="width: ${totalConges > 0 ? (type[1] * 100.0) / totalConges : 0}%"
                                                     aria-valuenow="${totalConges > 0 ? (type[1] * 100.0) / totalConges : 0}" 
                                                     aria-valuemin="0" aria-valuemax="100">
                                                    <fmt:formatNumber value="${totalConges > 0 ? (type[1] * 100.0) / totalConges : 0}" maxFractionDigits="1" />%
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty congesParType}">
                    <div class="alert alert-info">Aucune donnée disponible</div>
                </c:if>
            </div>
        </div>

        <!-- Top employés avec congés -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-trophy"></i> Top 5 employés avec le plus de congés</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty topConges}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Rang</th>
                                    <th>Employé</th>
                                    <th>Nombre de congés</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="top" items="${topConges}" varStatus="status">
                                    <tr>
                                        <td><strong>#${status.index + 1}</strong></td>
                                        <td>${top[0]} ${top[1]}</td>
                                        <td><span class="badge bg-primary">${top[2]}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty topConges}">
                    <div class="alert alert-info">Aucune donnée disponible</div>
                </c:if>
            </div>
        </div>

        <!-- Liste des congés -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-list"></i> Tous les congés</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty tousConges}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Employé</th>
                                    <th>Type</th>
                                    <th>Date début</th>
                                    <th>Date fin</th>
                                    <th>Jours</th>
                                    <th>Statut</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="conge" items="${tousConges}">
                                    <tr>
                                        <td>${conge.employe.nom} ${conge.employe.prenom}</td>
                                        <td>${conge.typeConge.libelle}</td>
                                        <td>${conge.dateDebut}</td>
                                        <td>${conge.dateFin}</td>
                                        <td>${conge.nombreJours}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${conge.statut == 'APPROUVE'}">
                                                    <span class="badge bg-success">Approuvé</span>
                                                </c:when>
                                                <c:when test="${conge.statut == 'VALIDE_MANAGER'}">
                                                    <span class="badge bg-info">Validé Manager</span>
                                                </c:when>
                                                <c:when test="${conge.statut == 'VALIDE_RH'}">
                                                    <span class="badge bg-info">Validé RH</span>
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
                <c:if test="${empty tousConges}">
                    <div class="alert alert-info">Aucun congé trouvé</div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

