<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Feuilles de temps" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="temps" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-clock"></i> Feuilles de temps</h1>
            <a href="${pageContext.request.contextPath}/app/temps/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouvelle feuille
            </a>
        </div>
        
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'created'}">Feuille de temps créée avec succès !</c:when>
                    <c:when test="${param.success == 'submitted'}">Feuille de temps soumise pour validation !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Mes feuilles de temps</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty feuilles}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucune feuille de temps enregistrée.
                    </div>
                </c:if>

                <c:if test="${not empty feuilles}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Semaine</th>
                                    <th>Heures normales</th>
                                    <th>Heures supplémentaires</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="feuille" items="${feuilles}">
                                    <tr>
                                        <td>${feuille.dateSemaine}</td>
                                        <td>${feuille.heuresNormales}h</td>
                                        <td>${feuille.heuresSupplementaires}h</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${feuille.statut == 'VALIDE'}">
                                                    <span class="badge bg-success">Validé</span>
                                                </c:when>
                                                <c:when test="${feuille.statut == 'SOUMIS'}">
                                                    <span class="badge bg-warning">En attente</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Brouillon</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <c:if test="${feuille.statut == 'BROUILLON'}">
                                                    <form action="${pageContext.request.contextPath}/app/temps/soumettre" 
                                                          method="post" style="display: inline;">
                                                        <input type="hidden" name="id" value="${feuille.id}">
                                                        <button type="submit" class="btn btn-sm btn-primary" 
                                                                onclick="return confirm('Soumettre cette feuille pour validation ?')">
                                                            <i class="fas fa-paper-plane"></i> Soumettre
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

