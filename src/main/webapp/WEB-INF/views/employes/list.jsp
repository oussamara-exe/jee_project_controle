<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Liste des employés" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="employes" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-users"></i> Liste des employés</h1>
            <a href="${pageContext.request.contextPath}/app/employes/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouvel employé
            </a>
        </div>
        
        <!-- Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'created'}">Employé créé avec succès !</c:when>
                    <c:when test="${param.success == 'updated'}">Employé mis à jour avec succès !</c:when>
                    <c:when test="${param.success == 'archived'}">Employé archivé avec succès !</c:when>
                    <c:when test="${param.success == 'deleted'}">Employé supprimé avec succès !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Recherche et filtres -->
        <div class="card mb-4">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/app/employes/search" method="get" class="row g-3">
                    <div class="col-md-8">
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-search"></i></span>
                            <input type="text" class="form-control" name="q" 
                                   placeholder="Rechercher par nom, prénom, matricule ou email..."
                                   value="${searchTerm}">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-search"></i> Rechercher
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Tableau des employés -->
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Matricule</th>
                                <th>Nom & Prénom</th>
                                <th>Email</th>
                                <th>Département</th>
                                <th>Poste</th>
                                <th>Date embauche</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${employes}" var="employe">
                                <tr>
                                    <td><strong>${employe.matricule}</strong></td>
                                    <td>
                                        <i class="fas fa-user-circle"></i>
                                        ${employe.nom} ${employe.prenom}
                                    </td>
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
                                        ${employe.dateEmbauche}
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
                                    <td>
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/app/employes/view?id=${employe.id}" 
                                               class="btn btn-sm btn-info" title="Voir">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/app/employes/edit?id=${employe.id}" 
                                               class="btn btn-sm btn-warning" title="Modifier">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <c:if test="${employe.actif}">
                                                <form action="${pageContext.request.contextPath}/app/employes/archive" 
                                                      method="post" style="display: inline;">
                                                    <input type="hidden" name="id" value="${employe.id}">
                                                    <button type="submit" class="btn btn-sm btn-secondary" 
                                                            title="Archiver" 
                                                            onclick="return confirmDelete('Archiver cet employé ?')">
                                                        <i class="fas fa-archive"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            
                            <c:if test="${empty employes}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted py-4">
                                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                        Aucun employé trouvé
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Navigation des pages">
                        <ul class="pagination justify-content-center">
                            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" 
                                       href="${pageContext.request.contextPath}/app/employes/list?page=${i}&pageSize=${pageSize}">
                                        ${i + 1}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

