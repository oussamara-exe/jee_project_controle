<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Modifier l'employé" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="employes" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-user-edit"></i> Modifier l'employé</h1>
            <a href="${pageContext.request.contextPath}/app/employes/view?id=${employe.id}" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <!-- Messages d'erreur -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Formulaire -->
        <div class="card">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/app/employes/edit" method="post">
                    <input type="hidden" name="id" value="${employe.id}">
                    <input type="hidden" name="version" value="${employe.version}">
                    
                    <!-- Informations personnelles -->
                    <h5 class="mb-3"><i class="fas fa-id-card"></i> Informations personnelles</h5>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="matricule" class="form-label">Matricule <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="matricule" name="matricule" 
                                   value="${employe.matricule}" required>
                        </div>
                        <div class="col-md-6">
                            <label for="nom" class="form-label">Nom <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="nom" name="nom" 
                                   value="${employe.nom}" required>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="prenom" class="form-label">Prénom <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="prenom" name="prenom" 
                                   value="${employe.prenom}" required>
                        </div>
                        <div class="col-md-6">
                            <label for="dateNaissance" class="form-label">Date de naissance</label>
                            <input type="date" class="form-control" id="dateNaissance" name="dateNaissance"
                                   value="${employe.dateNaissance}">
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" id="email" name="email" 
                                   value="${employe.email}" required>
                        </div>
                        <div class="col-md-6">
                            <label for="telephone" class="form-label">Téléphone</label>
                            <input type="tel" class="form-control" id="telephone" name="telephone" 
                                   value="${employe.telephone}">
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="adresse" class="form-label">Adresse</label>
                        <textarea class="form-control" id="adresse" name="adresse" rows="2">${employe.adresse}</textarea>
                    </div>
                    
                    <hr>
                    
                    <!-- Informations professionnelles -->
                    <h5 class="mb-3"><i class="fas fa-briefcase"></i> Informations professionnelles</h5>
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label for="departementId" class="form-label">Département</label>
                            <select class="form-select" id="departementId" name="departementId">
                                <option value="">-- Sélectionner --</option>
                                <c:forEach items="${departements}" var="dept">
                                    <option value="${dept.id}" ${employe.departement != null && employe.departement.id == dept.id ? 'selected' : ''}>
                                        ${dept.nom}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label for="posteId" class="form-label">Poste</label>
                            <select class="form-select" id="posteId" name="posteId">
                                <option value="">-- Sélectionner --</option>
                                <c:forEach items="${postes}" var="poste">
                                    <option value="${poste.id}" ${employe.poste != null && employe.poste.id == poste.id ? 'selected' : ''}>
                                        ${poste.titre}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label for="managerId" class="form-label">Manager</label>
                            <select class="form-select" id="managerId" name="managerId">
                                <option value="">-- Aucun --</option>
                                <c:forEach items="${managers}" var="manager">
                                    <option value="${manager.id}" ${employe.manager != null && employe.manager.id == manager.id ? 'selected' : ''}>
                                        ${manager.nom} ${manager.prenom}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label for="dateEmbauche" class="form-label">Date d'embauche <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="dateEmbauche" name="dateEmbauche" 
                                   value="${employe.dateEmbauche}" required>
                        </div>
                        <div class="col-md-4">
                            <label for="salaireBase" class="form-label">Salaire de base</label>
                            <input type="number" step="0.01" class="form-control" id="salaireBase" name="salaireBase" 
                                   value="${employe.salaireBase}" min="0">
                        </div>
                        <div class="col-md-4">
                            <label for="heuresHebdo" class="form-label">Heures hebdomadaires</label>
                            <input type="number" class="form-control" id="heuresHebdo" name="heuresHebdo" 
                                   value="${employe.heuresHebdo}" min="0" max="60">
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="iban" class="form-label">IBAN</label>
                        <input type="text" class="form-control" id="iban" name="iban" 
                               value="${employe.iban}" placeholder="MA64...">
                    </div>
                    
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="actif" name="actif" value="true" 
                               ${employe.actif ? 'checked' : ''}>
                        <label class="form-check-label" for="actif">Compte actif</label>
                    </div>
                    
                    <!-- Boutons -->
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/app/employes/view?id=${employe.id}" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Enregistrer les modifications
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

