<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Rapports" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="rapports" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-chart-bar"></i> Rapports</h1>
        </div>

        <div class="row">
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body text-center">
                        <i class="fas fa-users fa-3x text-primary mb-3"></i>
                        <h5>Rapport employés</h5>
                        <p class="text-muted">Statistiques sur les employés</p>
                        <a href="${pageContext.request.contextPath}/app/rapports/employes" class="btn btn-primary">Voir le rapport</a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body text-center">
                        <i class="fas fa-calendar-alt fa-3x text-success mb-3"></i>
                        <h5>Rapport congés</h5>
                        <p class="text-muted">Statistiques sur les congés</p>
                        <a href="${pageContext.request.contextPath}/app/rapports/conges" class="btn btn-success">Voir le rapport</a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body text-center">
                        <i class="fas fa-money-check-alt fa-3x text-info mb-3"></i>
                        <h5>Rapport paie</h5>
                        <p class="text-muted">Statistiques sur la paie</p>
                        <a href="${pageContext.request.contextPath}/app/rapports/paie" class="btn btn-info">Voir le rapport</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

