<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Modifier le département" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="departements" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-edit"></i> Modifier le département</h1>
            <a href="${pageContext.request.contextPath}/app/departements/list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="card">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/app/departements/edit" method="post">
                    <input type="hidden" name="id" value="${departement.id}">
                    
                    <div class="mb-3">
                        <label for="nom" class="form-label">Nom <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="nom" name="nom" 
                               value="${departement.nom}" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="description" class="form-label">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3">${departement.description}</textarea>
                    </div>
                    
                    <div class="mb-3">
                        <label for="responsableId" class="form-label">Responsable</label>
                        <select class="form-select" id="responsableId" name="responsableId">
                            <option value="">-- Aucun --</option>
                            <c:forEach items="${managers}" var="manager">
                                <option value="${manager.id}" 
                                        ${departement.responsable != null && departement.responsable.id == manager.id ? 'selected' : ''}>
                                    ${manager.nom} ${manager.prenom}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/app/departements/list" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Enregistrer
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

