<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Fiche de paie" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="paie" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-file-invoice-dollar"></i> Fiche de paie</h1>
            <a href="${pageContext.request.contextPath}/app/paie/list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">Fiche de paie - ${fiche.employe.nom} ${fiche.employe.prenom}</h5>
            </div>
            <div class="card-body">
                <div class="row mb-4">
                    <div class="col-md-6">
                        <strong>Période :</strong> ${fiche.mois}/${fiche.annee}<br>
                        <strong>Matricule :</strong> ${fiche.employe.matricule}<br>
                        <strong>Poste :</strong> ${fiche.employe.poste != null ? fiche.employe.poste.titre : 'N/A'}
                    </div>
                    <div class="col-md-6 text-end">
                        <strong>Statut :</strong>
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
                    </div>
                </div>
                
                <hr>
                
                <h6 class="mb-3">Gains</h6>
                <table class="table table-sm">
                    <tr>
                        <td>Salaire de base</td>
                        <td class="text-end"><fmt:formatNumber value="${fiche.salaireBase}" type="currency" currencyCode="MAD" /></td>
                    </tr>
                    <c:if test="${fiche.heuresSupplementaires > 0}">
                        <tr>
                            <td>Heures supplémentaires (${fiche.heuresSupplementaires}h)</td>
                            <td class="text-end"><fmt:formatNumber value="${fiche.montantHeuresSup}" type="currency" currencyCode="MAD" /></td>
                        </tr>
                    </c:if>
                    <c:if test="${fiche.primes > 0}">
                        <tr>
                            <td>Primes</td>
                            <td class="text-end"><fmt:formatNumber value="${fiche.primes}" type="currency" currencyCode="MAD" /></td>
                        </tr>
                    </c:if>
                    <c:if test="${fiche.indemnites > 0}">
                        <tr>
                            <td>Indemnités</td>
                            <td class="text-end"><fmt:formatNumber value="${fiche.indemnites}" type="currency" currencyCode="MAD" /></td>
                        </tr>
                    </c:if>
                    <tr class="table-primary">
                        <td><strong>Total brut</strong></td>
                        <td class="text-end"><strong><fmt:formatNumber value="${fiche.totalBrut}" type="currency" currencyCode="MAD" /></strong></td>
                    </tr>
                </table>
                
                <hr>
                
                <h6 class="mb-3">Déductions</h6>
                <table class="table table-sm">
                    <tr>
                        <td>Cotisations sociales</td>
                        <td class="text-end"><fmt:formatNumber value="${fiche.cotisationsSociales}" type="currency" currencyCode="MAD" /></td>
                    </tr>
                    <tr>
                        <td>Retenue IR</td>
                        <td class="text-end"><fmt:formatNumber value="${fiche.retenueIR}" type="currency" currencyCode="MAD" /></td>
                    </tr>
                    <c:if test="${fiche.autresDeductions > 0}">
                        <tr>
                            <td>Autres déductions</td>
                            <td class="text-end"><fmt:formatNumber value="${fiche.autresDeductions}" type="currency" currencyCode="MAD" /></td>
                        </tr>
                    </c:if>
                    <tr class="table-danger">
                        <td><strong>Total déductions</strong></td>
                        <td class="text-end"><strong><fmt:formatNumber value="${fiche.totalDeductions}" type="currency" currencyCode="MAD" /></strong></td>
                    </tr>
                </table>
                
                <hr>
                
                <div class="row">
                    <div class="col-md-12">
                        <div class="alert alert-success text-center">
                            <h4 class="mb-0">
                                Net à payer : 
                                <fmt:formatNumber value="${fiche.netAPayer}" type="currency" currencyCode="MAD" />
                            </h4>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

