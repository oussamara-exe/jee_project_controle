package ma.projet.rh.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre pour ajouter des en-têtes de sécurité HTTP
 * Protection contre XSS, Clickjacking, etc.
 */
@WebFilter(filterName = "SecurityHeadersFilter", urlPatterns = {"/*"})
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Protection XSS
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // Protection Clickjacking
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");

        // Protection contre le MIME sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // Content Security Policy (CSP) - Autoriser les CDN pour Bootstrap et Font Awesome
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://code.jquery.com; " +
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data: https://cdnjs.cloudflare.com; " +
            "connect-src 'self';");

        // Strict Transport Security (HSTS) - À activer en production avec HTTPS
        // httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // Référer Policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions Policy
        httpResponse.setHeader("Permissions-Policy", 
            "geolocation=(), microphone=(), camera=()");

        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}

