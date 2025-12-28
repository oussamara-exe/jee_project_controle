<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Rapport Paie" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="rapports" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-money-check-alt"></i> Rapport Paie</h1>
            <div>
                <form method="get" action="${pageContext.request.contextPath}/app/rapports/paie" class="d-inline">
                    <select name="mois" class="form-control d-inline-block" style="width: 150px;">
                        <option value="1" ${mois == 1 ? 'selected' : ''}>Janvier</option>
                        <option value="2" ${mois == 2 ? 'selected' : ''}>Février</option>
                        <option value="3" ${mois == 3 ? 'selected' : ''}>Mars</option>
                        <option value="4" ${mois == 4 ? 'selected' : ''}>Avril</option>
                        <option value="5" ${mois == 5 ? 'selected' : ''}>Mai</option>
                        <option value="6" ${mois == 6 ? 'selected' : ''}>Juin</option>
                        <option value="7" ${mois == 7 ? 'selected' : ''}>Juillet</option>
                        <option value="8" ${mois == 8 ? 'selected' : ''}>Août</option>
                        <option value="9" ${mois == 9 ? 'selected' : ''}>Septembre</option>
                        <option value="10" ${mois == 10 ? 'selected' : ''}>Octobre</option>
                        <option value="11" ${mois == 11 ? 'selected' : ''}>Novembre</option>
                        <option value="12" ${mois == 12 ? 'selected' : ''}>Décembre</option>
                    </select>
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
                        <h5 class="card-title">Total fiches</h5>
                        <h2>${totalFiches}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-warning">
                    <div class="card-body text-center">
                        <h5 class="card-title">Calculées</h5>
                        <h2>${fichesCalculees}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info">
                    <div class="card-body text-center">
                        <h5 class="card-title">Validées</h5>
                        <h2>${fichesValidees}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-success">
                    <div class="card-body text-center">
                        <h5 class="card-title">Payées</h5>
                        <h2>${fichesPayees}</h2>
                    </div>
                </div>
            </div>
        </div>

        <!-- Résumé financier -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card border-primary">
                    <div class="card-body text-center">
                        <h6 class="card-title text-primary">Total Brut</h6>
                        <h3 class="text-primary"><fmt:formatNumber value="${totalBrut}" type="number" maxFractionDigits="2" /> MAD</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border-danger">
                    <div class="card-body text-center">
                        <h6 class="card-title text-danger">Total Déductions</h6>
                        <h3 class="text-danger"><fmt:formatNumber value="${totalDeductions}" type="number" maxFractionDigits="2" /> MAD</h3>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border-success">
                    <div class="card-body text-center">
                        <h6 class="card-title text-success">Net à Payer</h6>
                        <h3 class="text-success"><fmt:formatNumber value="${totalNet}" type="number" maxFractionDigits="2" /> MAD</h3>
                    </div>
                </div>
            </div>
        </div>

        <!-- Répartition par département -->
        <c:if test="${not empty repartitionDept}">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="fas fa-building"></i> Répartition par département</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Département</th>
                                    <th>Nombre de fiches</th>
                                    <th>Total Brut</th>
                                    <th>Total Net</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="dept" items="${repartitionDept}">
                                    <tr>
                                        <td><strong>${dept[0]}</strong></td>
                                        <td>${dept[4]}</td>
                                        <td><fmt:formatNumber value="${dept[1]}" type="number" maxFractionDigits="2" /> MAD</td>
                                        <td><strong><fmt:formatNumber value="${dept[3]}" type="number" maxFractionDigits="2" /> MAD</strong></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Liste des fiches de paie -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-list"></i> Fiches de paie - ${mois}/${annee}</h5>
            </div>
            <div class="card-body">
                <c:if test="${not empty fichesMois}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Employé</th>
                                    <th>Salaire Base</th>
                                    <th>Total Brut</th>
                                    <th>Déductions</th>
                                    <th>Net à Payer</th>
                                    <th>Statut</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="fiche" items="${fichesMois}">
                                    <tr>
                                        <td><strong>${fiche.employe.nom} ${fiche.employe.prenom}</strong></td>
                                        <td><fmt:formatNumber value="${fiche.salaireBase}" type="number" maxFractionDigits="2" /> MAD</td>
                                        <td><fmt:formatNumber value="${fiche.totalBrut}" type="number" maxFractionDigits="2" /> MAD</td>
                                        <td><fmt:formatNumber value="${fiche.totalDeductions}" type="number" maxFractionDigits="2" /> MAD</td>
                                        <td><strong><fmt:formatNumber value="${fiche.netAPayer}" type="number" maxFractionDigits="2" /> MAD</strong></td>
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
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty fichesMois}">
                    <div class="alert alert-info">Aucune fiche de paie trouvée pour cette période</div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

