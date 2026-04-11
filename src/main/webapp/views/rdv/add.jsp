<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nouveau rendez-vous" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />

<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-calendar-plus me-2"></i>Planifier un rendez-vous</h4>
        <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour
        </a>
    </div>

    <div class="card" style="max-width:700px;">
        <div class="card-header" style="background:var(--teal-dark);color:#fff;">
            <i class="bi bi-calendar3 me-1"></i>Informations du rendez-vous
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/rdv">
                <input type="hidden" name="action" value="save">

                <div class="mb-3">
                    <label class="form-label fw-semibold">Patient <span class="text-danger">*</span></label>
                    <select name="idPatient" class="form-select" required>
                        <option value="">-- Sélectionner un patient --</option>
                        <c:forEach var="p" items="${patients}">
                            <option value="${p.idPatient}" ${param.idPatient eq p.idPatient ? 'selected' : ''}>
                                    ${p.nomComplet} (${p.dateNaissance})
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Dentiste <span class="text-danger">*</span></label>
                    <select name="idDentiste" id="idDentiste" class="form-select" required>
                        <option value="">-- Sélectionner un dentiste --</option>
                        <c:forEach var="d" items="${dentistes}">
                            <option value="${d.idUtilisateur}">Dr. ${d.nomComplet}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Date <span class="text-danger">*</span></label>
                        <input type="date" name="date" id="dateRdv" class="form-control" required
                               min="<%= java.time.LocalDate.now() %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Heure <span class="text-danger">*</span></label>
                        <select name="heure" id="heureRdv" class="form-select" required>
                            <option value="">-- Sélectionnez un dentiste et une date --</option>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mt-1">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Motif <span class="text-danger">*</span></label>
                        <select name="motif" class="form-select" required>
                            <c:forEach var="m" items="${motifs}">
                                <option value="${m.name()}">${m.libelle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Durée (minutes)</label>
                        <select name="duree" id="duree" class="form-select">
                            <option value="15">15 min</option>
                            <option value="30" selected>30 min</option>
                            <option value="45">45 min</option>
                            <option value="60">60 min</option>
                            <option value="90">90 min</option>
                        </select>
                    </div>
                </div>

                <div class="mb-3 mt-3">
                    <label class="form-label fw-semibold">Notes</label>
                    <textarea name="notes" class="form-control" rows="2"
                              placeholder="Observations ou informations complémentaires…"></textarea>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-teal px-4">
                        <i class="bi bi-calendar-check me-1"></i>Confirmer le rendez-vous
                    </button>
                    <a href="${pageContext.request.contextPath}/rdv" class="btn btn-outline-secondary px-4">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const dateInput = document.getElementById("dateRdv");
        const dentisteSelect = document.getElementById("idDentiste");
        const heureSelect = document.getElementById("heureRdv");
        const dureeInput = document.getElementById("duree");

        // 1. Bloquer les dates passées
        if (dateInput) {
            const today = new Date().toISOString().split('T')[0];
            dateInput.setAttribute('min', today);
        }

        // 2. Fonction AJAX pour charger les créneaux disponibles
        function mettreAJourHeures() {
            const dateVal = dateInput.value;
            const dentisteVal = dentisteSelect.value;
            const dureeVal = dureeInput ? dureeInput.value : 30;

            if (dateVal && dentisteVal && dureeVal) {
                heureSelect.innerHTML = '<option value="">Recherche des dispo...</option>';
                heureSelect.disabled = true;

                // Appel vers le Servlet avec la durée
                fetch('${pageContext.request.contextPath}/rdv?action=getDispo&date=' + dateVal + '&idDentiste=' + dentisteVal + '&duree=' + dureeVal)
                    .then(response => response.json())
                    .then(creneaux => {
                        heureSelect.innerHTML = '<option value="">-- Sélectionnez une heure --</option>';
                        heureSelect.disabled = false;

                        if (creneaux.length === 0) {
                            heureSelect.innerHTML = '<option value="">Aucun créneau libre suffisant</option>';
                            heureSelect.disabled = true;
                        } else {
                            creneaux.forEach(heure => {
                                heureSelect.innerHTML += '<option value="' + heure + '">' + heure + '</option>';
                            });
                        }
                    })
                    .catch(err => {
                        console.error("Erreur AJAX", err);
                        heureSelect.innerHTML = '<option value="">Erreur de chargement</option>';
                    });
            } else {
                heureSelect.innerHTML = '<option value="">Veuillez remplir Date et Dentiste</option>';
                heureSelect.disabled = true;
            }
        }

        // 3. Écouteurs d'événements
        if(dateInput) dateInput.addEventListener("change", mettreAJourHeures);
        if(dentisteSelect) dentisteSelect.addEventListener("change", mettreAJourHeures);
        if(dureeInput) dureeInput.addEventListener("change", mettreAJourHeures);
    });
</script>

<jsp:include page="/views/common/footer.jsp"/>