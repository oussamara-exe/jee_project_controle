<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Rapport Employés" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="rapports" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-users"></i> Rapport Employés</h1>
            <a href="${pageContext.request.contextPath}/app/rapports" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>

        <!-- Statistiques générales -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-primary">
                    <div class="card-body text-center">
                        <h5 class="card-title">Total employés</h5>
                        <h2>${totalEmployes}</h2>
                        <small>Actifs</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info">
                    <div class="card-body text-center">
                        <h5 class="card-title">Départements</h5>
                        <h2>${totalDepartements}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-success">
                    <div class="card-body text-center">
                        <h5 class="card-title">Postes</h5>
                        <h2>${totalPostes}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-warning">
                    <div class="card-body text-center">
                        <h5 class="card-title">Taux d'activité</h5>
                        <h2><fmt:formatNumber value="${tauxActif}" maxFractionDigits="1" />%</h2>
                        <small>${totalInactifs} inactif(s)</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Répartition par département -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-building"></i> Répartition par département</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty repartitionDept}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Département</th>
                                    <th>Nombre d'employés</th>
                                    <th>Pourcentage</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="dept" items="${repartitionDept}">
                                    <tr>
                                        <td><strong>${dept[0]}</strong></td>
                                        <td>${dept[1]}</td>
                                        <td>
                                            <div class="progress">
                                                <div class="progress-bar" role="progressbar" 
                                                     style="width: ${(dept[1] * 100.0) / totalEmployes}%"
                                                     aria-valuenow="${(dept[1] * 100.0) / totalEmployes}" 
                                                     aria-valuemin="0" aria-valuemax="100">
                                                    <fmt:formatNumber value="${(dept[1] * 100.0) / totalEmployes}" maxFractionDigits="1" />%
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty repartitionDept}">
                    <div class="alert alert-info">Aucune donnée disponible</div>
                </c:if>
            </div>
        </div>

        <!-- Répartition par poste -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-briefcase"></i> Répartition par poste</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty repartitionPoste}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Poste</th>
                                    <th>Nombre d'employés</th>
                                    <th>Pourcentage</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="poste" items="${repartitionPoste}">
                                    <tr>
                                        <td><strong>${poste[0]}</strong></td>
                                        <td>${poste[1]}</td>
                                        <td>
                                            <div class="progress">
                                                <div class="progress-bar bg-success" role="progressbar" 
                                                     style="width: ${(poste[1] * 100.0) / totalEmployes}%"
                                                     aria-valuenow="${(poste[1] * 100.0) / totalEmployes}" 
                                                     aria-valuemin="0" aria-valuemax="100">
                                                    <fmt:formatNumber value="${(poste[1] * 100.0) / totalEmployes}" maxFractionDigits="1" />%
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty repartitionPoste}">
                    <div class="alert alert-info">Aucune donnée disponible</div>
                </c:if>
            </div>
        </div>

        <!-- Liste des employés -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-list"></i> Liste complète des employés</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty tousEmployes}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Matricule</th>
                                    <th>Nom & Prénom</th>
                                    <th>Email</th>
                                    <th>Département</th>
                                    <th>Poste</th>
                                    <th>Statut</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="employe" items="${tousEmployes}">
                                    <tr>
                                        <td><strong>${employe.matricule}</strong></td>
                                        <td>${employe.nom} ${employe.prenom}</td>
                                        <td>${employe.email}</td>
                                        <td>
                                            <c:if test="${not empty employe.departement}">
                                                <span class="badge bg-info">${employe.departement.nom}</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty employe.poste}">
                                                ${employe.poste.titre}
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${employe.actif}">
                                                    <span class="badge bg-success">Actif</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Inactif</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty tousEmployes}">
                    <div class="alert alert-info">Aucun employé trouvé</div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

