<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

        </div> <!-- Fermeture content -->
    </div> <!-- Fermeture row -->
</div> <!-- Fermeture container-fluid -->

<!-- Footer -->
<footer class="bg-light text-center text-muted py-3 mt-5" style="border-top: 1px solid #dee2e6;">
    <div class="container">
        <p class="mb-0">
            &copy; 2024 Système de Gestion RH | 
            <a href="#" class="text-decoration-none">Mentions légales</a> | 
            <a href="#" class="text-decoration-none">Politique de confidentialité</a>
        </p>
        <small class="text-muted">Version 1.0.0</small>
    </div>
</footer>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- jQuery (optionnel mais utile) -->
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>

<!-- Custom JS -->
<script>
    // Confirmation de suppression
    function confirmDelete(message) {
        return confirm(message || 'Êtes-vous sûr de vouloir supprimer cet élément ?');
    }
    
    // Auto-hide alerts après 5 secondes
    document.addEventListener('DOMContentLoaded', function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            setTimeout(function() {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        });
    });
    
    // Formatage des dates
    document.querySelectorAll('.format-date').forEach(function(el) {
        const date = new Date(el.textContent);
        el.textContent = date.toLocaleDateString('fr-FR');
    });
</script>

</body>
</html>

