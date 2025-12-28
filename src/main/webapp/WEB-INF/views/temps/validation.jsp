<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Validation des feuilles de temps" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="temps" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-check-circle"></i> Validation des feuilles de temps</h1>
            <a href="${pageContext.request.contextPath}/app/temps/list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'validated'}">Feuille de temps validée avec succès !</c:when>
                    <c:when test="${param.success == 'rejected'}">Feuille de temps rejetée.</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> Une erreur est survenue.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="card">
            <div class="card-body">
                <c:if test="${empty feuilles}">
                    <div class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                        Aucune feuille de temps en attente de validation
                    </div>
                </c:if>
                
                <c:if test="${not empty feuilles}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Employé</th>
                                    <th>Semaine</th>
                                    <th>Heures normales</th>
                                    <th>Heures supplémentaires</th>
                                    <th>Total</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="feuille" items="${feuilles}">
                                    <tr>
                                        <td>
                                            <strong>${feuille.employe.nom} ${feuille.employe.prenom}</strong><br>
                                            <small class="text-muted">${feuille.employe.matricule}</small>
                                        </td>
                                        <td>
                                            ${feuille.dateSemaine}
                                        </td>
                                        <td>${feuille.heuresNormales}h</td>
                                        <td>${feuille.heuresSupplementaires}h</td>
                                        <td><strong>${feuille.totalHeures}h</strong></td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <button type="button" class="btn btn-sm btn-success" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#validerModal${feuille.id}">
                                                    <i class="fas fa-check"></i> Valider
                                                </button>
                                                <button type="button" class="btn btn-sm btn-danger" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#rejeterModal${feuille.id}">
                                                    <i class="fas fa-times"></i> Rejeter
                                                </button>
                                            </div>
                                            
                                            <!-- Modal Valider -->
                                            <div class="modal fade" id="validerModal${feuille.id}" tabindex="-1">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <form action="${pageContext.request.contextPath}/app/temps/valider" method="post">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title">Valider la feuille de temps</h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <p>Valider la feuille de temps de <strong>${feuille.employe.nom} ${feuille.employe.prenom}</strong> ?</p>
                                                                <div class="mb-3">
                                                                    <label for="commentaireVal${feuille.id}" class="form-label">Commentaire (optionnel)</label>
                                                                    <textarea class="form-control" id="commentaireVal${feuille.id}" 
                                                                              name="commentaire" rows="3"></textarea>
                                                                </div>
                                                                <input type="hidden" name="id" value="${feuille.id}">
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                                                                <button type="submit" class="btn btn-success">Valider</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            <!-- Modal Rejeter -->
                                            <div class="modal fade" id="rejeterModal${feuille.id}" tabindex="-1">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <form action="${pageContext.request.contextPath}/app/temps/rejeter" method="post">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title">Rejeter la feuille de temps</h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <p>Rejeter la feuille de temps de <strong>${feuille.employe.nom} ${feuille.employe.prenom}</strong> ?</p>
                                                                <div class="mb-3">
                                                                    <label for="commentaireRej${feuille.id}" class="form-label">Commentaire <span class="text-danger">*</span></label>
                                                                    <textarea class="form-control" id="commentaireRej${feuille.id}" 
                                                                              name="commentaire" rows="3" required></textarea>
                                                                </div>
                                                                <input type="hidden" name="id" value="${feuille.id}">
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                                                                <button type="submit" class="btn btn-danger">Rejeter</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
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

