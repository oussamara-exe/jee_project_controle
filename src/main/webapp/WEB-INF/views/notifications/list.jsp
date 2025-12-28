<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../fragments/header.jsp">
    <jsp:param name="pageTitle" value="Mes notifications" />
</jsp:include>

<jsp:include page="../../fragments/sidebar.jsp">
    <jsp:param name="activePage" value="notifications" />
</jsp:include>

<!-- Contenu principal -->
<div class="col-md-10">
    <div class="content-wrapper">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1><i class="fas fa-bell"></i> Mes notifications</h1>
            <c:if test="${notificationsNonLues > 0}">
                <form action="${pageContext.request.contextPath}/app/notifications" method="post" style="display: inline;">
                    <input type="hidden" name="action" value="marquer-toutes-lues">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-check-double"></i> Marquer toutes comme lues
                    </button>
                </form>
            </c:if>
        </div>
        
        <!-- Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'read'}">Notification marquée comme lue !</c:when>
                    <c:when test="${param.success == 'allRead'}">Toutes les notifications ont été marquées comme lues !</c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Statistiques -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h3 class="text-primary">${not empty notifications ? notifications.size() : 0}</h3>
                        <p class="text-muted mb-0">Total</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h3 class="text-warning">${notificationsNonLues}</h3>
                        <p class="text-muted mb-0">Non lues</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h3 class="text-success">${not empty notifications ? notifications.size() - notificationsNonLues : 0}</h3>
                        <p class="text-muted mb-0">Lues</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Liste des notifications -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-list"></i> Liste des notifications</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty notifications}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i> Vous n'avez aucune notification.
                    </div>
                </c:if>

                <c:if test="${not empty notifications}">
                    <div class="list-group">
                        <c:forEach items="${notifications}" var="notif">
                            <div class="list-group-item ${not notif.lu ? 'list-group-item-warning' : ''}">
                                <div class="d-flex w-100 justify-content-between align-items-start">
                                    <div class="flex-grow-1">
                                        <div class="d-flex align-items-center mb-2">
                                            <h6 class="mb-0 me-2">
                                                <c:choose>
                                                    <c:when test="${notif.type == 'CONGE_A_VALIDER'}">
                                                        <i class="fas fa-calendar-check text-warning"></i>
                                                    </c:when>
                                                    <c:when test="${notif.type == 'CONGE_APPROUVE'}">
                                                        <i class="fas fa-check-circle text-success"></i>
                                                    </c:when>
                                                    <c:when test="${notif.type == 'CONGE_REFUSE'}">
                                                        <i class="fas fa-times-circle text-danger"></i>
                                                    </c:when>
                                                    <c:when test="${notif.type == 'PAIE_GENEREE'}">
                                                        <i class="fas fa-money-check-alt text-info"></i>
                                                    </c:when>
                                                    <c:when test="${notif.type == 'TEMPS_A_VALIDER'}">
                                                        <i class="fas fa-clock text-primary"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-info-circle text-secondary"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                                ${notif.message}
                                            </h6>
                                            <c:if test="${not notif.lu}">
                                                <span class="badge bg-warning text-dark">Non lue</span>
                                            </c:if>
                                        </div>
                                        <c:if test="${not empty notif.dateCreation}">
                                            <small class="text-muted">
                                                <i class="fas fa-clock"></i> ${notif.dateCreation}
                                            </small>
                                        </c:if>
                                        <c:if test="${notif.lu && not empty notif.dateLecture}">
                                            <small class="text-muted ms-2">
                                                <i class="fas fa-eye"></i> Lu le ${notif.dateLecture}
                                            </small>
                                        </c:if>
                                    </div>
                                    <div class="ms-3">
                                        <c:if test="${not empty notif.lien}">
                                            <a href="${notif.lien}" class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-external-link-alt"></i> Voir
                                            </a>
                                        </c:if>
                                        <c:if test="${not notif.lu}">
                                            <form action="${pageContext.request.contextPath}/app/notifications" 
                                                  method="post" style="display: inline;" class="ms-2">
                                                <input type="hidden" name="action" value="marquer-lue">
                                                <input type="hidden" name="id" value="${notif.id}">
                                                <button type="submit" class="btn btn-sm btn-outline-success" 
                                                        title="Marquer comme lue">
                                                    <i class="fas fa-check"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../../fragments/footer.jsp" />

