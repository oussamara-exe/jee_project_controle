<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Gestion des utilisateurs" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="utilisateurs" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-user-shield"></i> Gestion des utilisateurs</h1>
        </div>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Liste des utilisateurs</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty utilisateurs}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Aucun utilisateur enregistré.
                    </div>
                </c:if>

                <c:if test="${not empty utilisateurs}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Username</th>
                                    <th>Rôle</th>
                                    <th>Actif</th>
                                    <th>Dernière connexion</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="user" items="${utilisateurs}">
                                    <tr>
                                        <td>${user.username}</td>
                                        <td><span class="badge bg-primary">${user.role}</span></td>
                                        <td>
                                            <span class="badge bg-${user.actif ? 'success' : 'danger'}">
                                                ${user.actif ? 'Oui' : 'Non'}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${not empty user.derniereConnexion}">
                                                ${user.derniereConnexion}
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="#" class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-edit"></i> Modifier
                                            </a>
                                        </td>
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

