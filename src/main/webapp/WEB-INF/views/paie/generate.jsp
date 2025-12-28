<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Générer des fiches de paie" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="paie" />
</jsp:include>

<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-calculator"></i> Générer des fiches de paie</h1>
            <a href="${pageContext.request.contextPath}/app/paie/list" class="btn btn-secondary">
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
                <form action="${pageContext.request.contextPath}/app/paie/generate" method="post">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="mois" class="form-label">Mois <span class="text-danger">*</span></label>
                            <select class="form-select" id="mois" name="mois" required>
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
                        </div>
                        <div class="col-md-6">
                            <label for="annee" class="form-label">Année <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="annee" name="annee" 
                                   value="${annee}" min="2020" max="2030" required>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="employeId" class="form-label">Employé</label>
                        <select class="form-select" id="employeId" name="employeId">
                            <option value="">-- Tous les employés actifs --</option>
                            <c:forEach items="${employes}" var="emp">
                                <option value="${emp.id}">${emp.nom} ${emp.prenom} (${emp.matricule})</option>
                            </c:forEach>
                        </select>
                        <small class="form-text text-muted">Laissez vide pour générer pour tous les employés actifs</small>
                    </div>
                    
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> 
                        Les fiches de paie seront générées avec les calculs automatiques basés sur les salaires de base.
                    </div>
                    
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/app/paie/list" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-calculator"></i> Générer
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

