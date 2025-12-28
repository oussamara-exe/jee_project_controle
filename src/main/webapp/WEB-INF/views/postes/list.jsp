<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Postes" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="postes" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-briefcase"></i> Postes</h1>
            <a href="${pageContext.request.contextPath}/app/postes/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouveau poste
            </a>
        </div>
        
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'created'}">Poste créé avec succès !</c:when>
                    <c:when test="${param.success == 'updated'}">Poste mis à jour avec succès !</c:when>
                    <c:when test="${param.success == 'deleted'}">Poste supprimé avec succès !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i>
                <c:if test="${param.error == 'hasEmployees'}">
                    Impossible de supprimer : des employés sont associés à ce poste.
                </c:if>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card">
            <div class="card-body">
                <c:if test="${not empty postes}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Titre</th>
                                    <th>Description</th>
                                    <th>Salaire min</th>
                                    <th>Salaire max</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="poste" items="${postes}">
                                    <tr>
                                        <td><strong>${poste.titre}</strong></td>
                                        <td>${poste.description}</td>
                                        <td>
                                            <c:if test="${not empty poste.salaireMin}">
                                                <fmt:formatNumber value="${poste.salaireMin}" type="currency" currencyCode="MAD" />
                                            </c:if>
                                            <c:if test="${empty poste.salaireMin}">
                                                <span class="text-muted">-</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty poste.salaireMax}">
                                                <fmt:formatNumber value="${poste.salaireMax}" type="currency" currencyCode="MAD" />
                                            </c:if>
                                            <c:if test="${empty poste.salaireMax}">
                                                <span class="text-muted">-</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/app/postes/edit?id=${poste.id}" 
                                                   class="btn btn-sm btn-warning" title="Modifier">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/app/postes/delete" 
                                                      method="post" style="display: inline;">
                                                    <input type="hidden" name="id" value="${poste.id}">
                                                    <button type="submit" class="btn btn-sm btn-danger" 
                                                            title="Supprimer"
                                                            onclick="return confirm('Supprimer ce poste ?')">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                
                <c:if test="${empty postes}">
                    <div class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                        Aucun poste enregistré
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

