<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Créer une feuille de temps" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="temps" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-clock"></i> Créer une feuille de temps</h1>
            <a href="${pageContext.request.contextPath}/app/temps/list" class="btn btn-secondary">
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
                <form action="${pageContext.request.contextPath}/app/temps/create" method="post">
                    <div class="mb-3">
                        <label for="dateSemaine" class="form-label">Semaine (lundi) <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="dateSemaine" name="dateSemaine" 
                               value="${dateSemaine}" required>
                        <small class="form-text text-muted">Sélectionnez le lundi de la semaine</small>
                    </div>
                    
                    <div class="mb-3">
                        <label for="heuresNormales" class="form-label">Heures normales <span class="text-danger">*</span></label>
                        <input type="number" step="0.5" class="form-control" id="heuresNormales" 
                               name="heuresNormales" min="0" max="60" required>
                        <small class="form-text text-muted">Nombre total d'heures travaillées cette semaine</small>
                    </div>
                    
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> 
                        Les heures supplémentaires seront calculées automatiquement en fonction de vos heures contractuelles.
                    </div>
                    
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/app/temps/list" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Enregistrer en brouillon
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

