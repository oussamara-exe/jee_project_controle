<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Gestion de la paie" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="paie" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-money-check-alt"></i> Gestion de la paie</h1>
            <a href="${pageContext.request.contextPath}/app/paie/generate" class="btn btn-primary">
                <i class="fas fa-plus"></i> Générer fiches
            </a>
        </div>
        
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'generated'}">
                        ${param.count} fiche(s) de paie générée(s) avec succès !
                    </c:when>
                    <c:when test="${param.success == 'validated'}">Fiche de paie validée !</c:when>
                    <c:when test="${param.success == 'paid'}">Fiche de paie marquée comme payée !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Toutes les fiches de paie</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty fiches}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucune fiche de paie générée.
                    </div>
                </c:if>

                <c:if test="${not empty fiches}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Employé</th>
                                    <th>Période</th>
                                    <th>Salaire brut</th>
                                    <th>Net à payer</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="fiche" items="${fiches}">
                                    <tr>
                                        <td>${fiche.employe.prenom} ${fiche.employe.nom}</td>
                                        <td>${fiche.mois}/${fiche.annee}</td>
                                        <td><fmt:formatNumber value="${fiche.totalBrut}" type="currency" currencySymbol="MAD" /></td>
                                        <td><fmt:formatNumber value="${fiche.netAPayer}" type="currency" currencySymbol="MAD" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${fiche.statut == 'PAYE'}">
                                                    <span class="badge bg-success">Payé</span>
                                                </c:when>
                                                <c:when test="${fiche.statut == 'VALIDE'}">
                                                    <span class="badge bg-info">Validé</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning">Calculé</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/app/paie/view?id=${fiche.id}" 
                                                   class="btn btn-sm btn-outline-primary" title="Voir">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${fiche.statut == 'CALCULE'}">
                                                    <form action="${pageContext.request.contextPath}/app/paie/valider" 
                                                          method="post" style="display: inline;">
                                                        <input type="hidden" name="id" value="${fiche.id}">
                                                        <button type="submit" class="btn btn-sm btn-success" 
                                                                title="Valider">
                                                            <i class="fas fa-check"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${fiche.statut == 'VALIDE'}">
                                                    <form action="${pageContext.request.contextPath}/app/paie/payer" 
                                                          method="post" style="display: inline;">
                                                        <input type="hidden" name="id" value="${fiche.id}">
                                                        <button type="submit" class="btn btn-sm btn-primary" 
                                                                title="Marquer comme payé"
                                                                onclick="return confirm('Marquer cette fiche comme payée ?')">
                                                            <i class="fas fa-money-bill-wave"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

