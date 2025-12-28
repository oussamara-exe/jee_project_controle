<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Paramètres" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="parametres" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-cogs"></i> Paramètres</h1>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Configuration du système</h5>
            </div>
            <div class="card-body">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i> 
                    Les paramètres de configuration seront disponibles dans une version future.
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

