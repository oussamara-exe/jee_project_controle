<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Départements" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="departements" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-building"></i> Départements</h1>
            <a href="${pageContext.request.contextPath}/app/departements/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouveau département
            </a>
        </div>
        
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'created'}">Département créé avec succès !</c:when>
                    <c:when test="${param.success == 'updated'}">Département mis à jour avec succès !</c:when>
                    <c:when test="${param.success == 'deleted'}">Département supprimé avec succès !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i>
                <c:if test="${param.error == 'hasEmployees'}">
                    Impossible de supprimer : des employés sont associés à ce département.
                </c:if>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Liste des départements</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty departements}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucun département enregistré.
                    </div>
                </c:if>

                <c:if test="${not empty departements}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Nom</th>
                                    <th>Description</th>
                                    <th>Responsable</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="dept" items="${departements}">
                                    <tr>
                                        <td><strong>${dept.nom}</strong></td>
                                        <td>${dept.description}</td>
                                        <td>
                                            <c:if test="${not empty dept.responsable}">
                                                ${dept.responsable.prenom} ${dept.responsable.nom}
                                            </c:if>
                                            <c:if test="${empty dept.responsable}">
                                                <span class="text-muted">Aucun</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/app/departements/edit?id=${dept.id}" 
                                                   class="btn btn-sm btn-warning" title="Modifier">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/app/departements/delete" 
                                                      method="post" style="display: inline;">
                                                    <input type="hidden" name="id" value="${dept.id}">
                                                    <button type="submit" class="btn btn-sm btn-danger" 
                                                            title="Supprimer"
                                                            onclick="return confirm('Supprimer ce département ?')">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                
                                <c:if test="${empty departements}">
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-4">
                                            <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                            Aucun département enregistré
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

