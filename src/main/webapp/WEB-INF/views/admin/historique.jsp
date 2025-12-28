<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Historique des actions" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="historique" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-history"></i> Historique des actions</h1>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Journal des actions</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty historique}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucune action enregistrée.
                    </div>
                </c:if>

                <c:if test="${not empty historique}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Utilisateur</th>
                                    <th>Type</th>
                                    <th>Description</th>
                                    <th>Entité</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="action" items="${historique}">
                                    <tr>
                                        <td><fmt:formatDate value="${action.dateAction}" pattern="dd/MM/yyyy HH:mm" /></td>
                                        <td>${action.utilisateur.username}</td>
                                        <td><span class="badge bg-info">${action.typeAction}</span></td>
                                        <td>${action.description}</td>
                                        <td>${action.entiteType} #${action.entiteId}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

