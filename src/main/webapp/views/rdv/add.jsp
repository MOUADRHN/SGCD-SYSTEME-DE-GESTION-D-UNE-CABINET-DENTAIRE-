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
                            <option value="${p.idPatient}"
                                ${param.idPatient eq p.idPatient ? 'selected' : ''}>
                                ${p.nomComplet} (${p.dateNaissance})
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Dentiste <span class="text-danger">*</span></label>
                    <select name="idDentiste" class="form-select" required>
                        <option value="">-- Sélectionner un dentiste --</option>
                        <c:forEach var="d" items="${dentistes}">
                            <option value="${d.idUtilisateur}">Dr. ${d.nomComplet}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Date <span class="text-danger">*</span></label>
                        <input type="date" name="date" class="form-control" required
                               min="<%= java.time.LocalDate.now() %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Heure <span class="text-danger">*</span></label>
                        <input type="time" name="heure" class="form-control" required
                               min="08:00" max="19:00" step="900">
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
                        <select name="duree" class="form-select">
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
<jsp:include page="/views/common/footer.jsp"/>
