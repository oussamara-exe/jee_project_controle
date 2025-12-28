<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Validation des congés" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="conges-validation" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-check-circle"></i> Validation des congés</h1>
        </div>

        <!-- Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i> 
                <c:choose>
                    <c:when test="${param.success == 'validated'}">Congé validé avec succès.</c:when>
                    <c:when test="${param.success == 'refused'}">Congé refusé.</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Liste des congés à valider -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <c:choose>
                        <c:when test="${isRH}">Congés en attente de validation RH</c:when>
                        <c:when test="${isManager}">Congés en attente de validation Manager</c:when>
                        <c:otherwise>Congés à valider</c:otherwise>
                    </c:choose>
                </h5>
            </div>
            <div class="card-body">
                <c:if test="${empty conges}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucun congé en attente de validation.
                    </div>
                </c:if>

                <c:if test="${not empty conges}">
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
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="conge" items="${conges}">
                                    <tr>
                                        <td>${conge.employe.nom} ${conge.employe.prenom}</td>
                                        <td>${conge.typeConge.libelle}</td>
                                        <td>${conge.dateDebut}</td>
                                        <td>${conge.dateFin}</td>
                                        <td>${conge.nombreJours}</td>
                                        <td>
                                            <span class="badge bg-warning">${conge.statut}</span>
                                        </td>
                                        <td>
                                            <form method="post" action="${pageContext.request.contextPath}/app/conges/valider" style="display:inline;">
                                                <input type="hidden" name="id" value="${conge.id}">
                                                <button type="submit" class="btn btn-sm btn-success">
                                                    <i class="fas fa-check"></i> Valider
                                                </button>
                                            </form>
                                            <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" 
                                                    data-bs-target="#refuseModal${conge.id}">
                                                <i class="fas fa-times"></i> Refuser
                                            </button>
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

<!-- Modals pour refus -->
<c:forEach var="conge" items="${conges}">
    <div class="modal fade" id="refuseModal${conge.id}" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Refuser le congé</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/app/conges/refuser">
                    <div class="modal-body">
                        <input type="hidden" name="id" value="${conge.id}">
                        <div class="mb-3">
                            <label for="commentaire${conge.id}" class="form-label">Commentaire</label>
                            <textarea class="form-control" id="commentaire${conge.id}" name="commentaire" rows="3" required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                        <button type="submit" class="btn btn-danger">Refuser</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:forEach>

<jsp:include page="../../fragments/footer.jsp" />

