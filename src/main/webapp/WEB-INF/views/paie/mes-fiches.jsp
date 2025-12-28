<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Mes fiches de paie" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="mes-paies" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-file-invoice-dollar"></i> Mes fiches de paie</h1>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Historique des fiches de paie</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty fiches}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucune fiche de paie disponible.
                    </div>
                </c:if>

                <c:if test="${not empty fiches}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Période</th>
                                    <th>Salaire brut</th>
                                    <th>Déductions</th>
                                    <th>Net à payer</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="fiche" items="${fiches}">
                                    <tr>
                                        <td>${fiche.mois}/${fiche.annee}</td>
                                        <td><fmt:formatNumber value="${fiche.totalBrut}" type="currency" currencySymbol="MAD" /></td>
                                        <td><fmt:formatNumber value="${fiche.totalDeductions}" type="currency" currencySymbol="MAD" /></td>
                                        <td><strong><fmt:formatNumber value="${fiche.netAPayer}" type="currency" currencySymbol="MAD" /></strong></td>
                                        <td>
                                            <span class="badge bg-${fiche.statut == 'PAYE' ? 'success' : fiche.statut == 'VALIDE' ? 'info' : 'warning'}">
                                                ${fiche.statut}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/app/paie/view?id=${fiche.id}" 
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i> Voir
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
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

