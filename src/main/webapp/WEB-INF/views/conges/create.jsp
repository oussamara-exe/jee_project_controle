<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Nouvelle demande de congé" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="conges" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-calendar-plus"></i> Nouvelle demande de congé</h1>
            <a href="${pageContext.request.contextPath}/app/conges/list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour
            </a>
        </div>
        
        <!-- Solde de congés -->
        <c:if test="${not empty solde}">
            <div class="alert alert-info">
                <h5 class="alert-heading"><i class="fas fa-info-circle"></i> Votre solde de congés</h5>
                <div class="row text-center">
                    <div class="col-md-4">
                        <strong>${solde.joursAcquis}</strong>
                        <br><small>Jours acquis</small>
                    </div>
                    <div class="col-md-4">
                        <strong>${solde.joursPris}</strong>
                        <br><small>Jours pris</small>
                    </div>
                    <div class="col-md-4">
                        <strong class="text-primary">${solde.joursRestants}</strong>
                        <br><small>Jours restants</small>
                    </div>
                </div>
            </div>
        </c:if>
        
        <!-- Message d'erreur -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Formulaire -->
        <div class="card">
            <div class="card-header">
                <i class="fas fa-edit"></i> Informations sur le congé
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/app/conges/create" method="post">
                    <div class="row">
                        <!-- Type de congé -->
                        <div class="col-md-6 mb-3">
                            <label for="typeConge" class="form-label">
                                Type de congé <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="typeConge" name="typeConge" required>
                                <option value="">-- Sélectionner --</option>
                                <c:forEach items="${typesConge}" var="type">
                                    <option value="${type}">
                                        <c:choose>
                                            <c:when test="${type == 'ANNUEL'}">Congé annuel</c:when>
                                            <c:when test="${type == 'MALADIE'}">Congé maladie</c:when>
                                            <c:when test="${type == 'MATERNITE'}">Congé maternité</c:when>
                                            <c:when test="${type == 'PATERNITE'}">Congé paternité</c:when>
                                            <c:when test="${type == 'SANS_SOLDE'}">Congé sans solde</c:when>
                                            <c:when test="${type == 'EXCEPTIONNEL'}">Congé exceptionnel</c:when>
                                            <c:otherwise>${type}</c:otherwise>
                                        </c:choose>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <!-- Date de début -->
                        <div class="col-md-3 mb-3">
                            <label for="dateDebut" class="form-label">
                                Date de début <span class="text-danger">*</span>
                            </label>
                            <input type="date" class="form-control" id="dateDebut" name="dateDebut" 
                                   required min="<%= java.time.LocalDate.now() %>">
                        </div>
                        
                        <!-- Date de fin -->
                        <div class="col-md-3 mb-3">
                            <label for="dateFin" class="form-label">
                                Date de fin <span class="text-danger">*</span>
                            </label>
                            <input type="date" class="form-control" id="dateFin" name="dateFin" 
                                   required min="<%= java.time.LocalDate.now() %>">
                        </div>
                        
                        <!-- Commentaire -->
                        <div class="col-md-12 mb-3">
                            <label for="commentaire" class="form-label">
                                Commentaire / Justification
                            </label>
                            <textarea class="form-control" id="commentaire" name="commentaire" 
                                      rows="4" placeholder="Motif de votre demande de congé..."></textarea>
                            <small class="text-muted">
                                Veuillez préciser le motif de votre demande, surtout pour les congés exceptionnels.
                            </small>
                        </div>
                    </div>
                    
                    <div class="alert alert-warning">
                        <i class="fas fa-info-circle"></i>
                        <strong>Important :</strong> Votre demande sera soumise à validation par votre responsable hiérarchique, 
                        puis par le service RH.
                    </div>
                    
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/app/conges/list" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-paper-plane"></i> Soumettre la demande
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Guide d'utilisation -->
        <div class="card mt-4">
            <div class="card-header bg-light">
                <i class="fas fa-question-circle"></i> Guide de demande de congé
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6><i class="fas fa-check-circle text-success"></i> Conseils</h6>
                        <ul>
                            <li>Soumettez votre demande au moins 15 jours à l'avance</li>
                            <li>Vérifiez votre solde de congés avant de faire une demande</li>
                            <li>Assurez-vous qu'il n'y a pas de chevauchement avec d'autres congés</li>
                            <li>Fournissez une justification claire pour les congés exceptionnels</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6><i class="fas fa-info-circle text-info"></i> Processus de validation</h6>
                        <ol>
                            <li><strong>Soumission</strong> : Vous soumettez votre demande</li>
                            <li><strong>Validation Manager</strong> : Votre responsable valide</li>
                            <li><strong>Validation RH</strong> : Le service RH approuve</li>
                            <li><strong>Confirmation</strong> : Vous recevez une notification</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // Validation des dates
    document.getElementById('dateDebut').addEventListener('change', function() {
        const dateDebut = this.value;
        document.getElementById('dateFin').min = dateDebut;
    });
    
    document.getElementById('dateFin').addEventListener('change', function() {
        const dateDebut = document.getElementById('dateDebut').value;
        const dateFin = this.value;
        
        if (dateDebut && dateFin) {
            const debut = new Date(dateDebut);
            const fin = new Date(dateFin);
            const jours = Math.ceil((fin - debut) / (1000 * 60 * 60 * 24)) + 1;
            
            if (jours > 0) {
                alert('Durée du congé : ' + jours + ' jour(s)');
            }
        }
    });
</script>

<jsp:include page="../../fragments/footer.jsp" />

