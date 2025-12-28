<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Redirection automatique vers la page de login ou le dashboard
    if (session != null && session.getAttribute("currentUser") != null) {
        response.sendRedirect(request.getContextPath() + "/app/dashboard");
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
%>

