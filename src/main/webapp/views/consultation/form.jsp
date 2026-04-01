<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Nouvelle consultation" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-clipboard2-pulse me-2"></i>Nouvelle Consultation</h4>
        <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour agenda
        </a>
    </div>

    <div class="card">
        <div class="card-header" style="background:var(--teal-dark);color:#fff;">
            <c:if test="${rdv != null}">
                Patient : <strong>${rdv.nomCompletPatient}</strong> —
                RDV du ${rdv.dateHeure.toLocalDate()} à ${rdv.dateHeure.toLocalTime()} —
                Motif : ${rdv.motif.libelle}
            </c:if>
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/consultation">
                <input type="hidden" name="action"    value="save">
                <input type="hidden" name="idDossier" value="${rdv.idPatient}">
                <c:if test="${rdv != null}">
                    <input type="hidden" name="idRdv"     value="${rdv.idRDV}">
                    <input type="hidden" name="idDossier" value="${rdv.idPatient}">
                </c:if>

                <div class="row g-3">
                    <div class="col-12">
                        <label class="form-label fw-semibold">
                            Diagnostic <span class="text-danger">*</span>
                        </label>
                        <textarea name="diagnostic" class="form-control" rows="3" required
                                  placeholder="Diagnostic clinique..."></textarea>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Observations cliniques</label>
                        <textarea name="observations" class="form-control" rows="2"
                                  placeholder="Observations, notes complémentaires..."></textarea>
                    </div>
                </div>

                <!-- Actes -->
                <div class="mt-4">
                    <label class="form-label fw-semibold">
                        Actes réalisés <span class="text-danger">*</span>
                    </label>
                    <div class="row g-2" id="actes-grid">
                        <c:forEach var="a" items="${actes}">
                        <div class="col-md-4">
                            <div class="form-check border rounded p-2 h-100"
                                 style="cursor:pointer;"
                                 onclick="this.querySelector('input').click()">
                                <input class="form-check-input acte-check" type="checkbox"
                                       name="actes" value="${a.code}" id="acte_${a.code}"
                                       data-tarif="${a.tarifBase}">
                                <label class="form-check-label w-100" for="acte_${a.code}">
                                    <div class="fw-semibold small">${a.nom}</div>
                                    <div class="text-muted" style="font-size:.78rem;">
                                        Code : ${a.code} —
                                        <strong class="text-success">
                                            <fmt:formatNumber value="${a.tarifBase}" maxFractionDigits="0"/> MAD
                                        </strong>
                                    </div>
                                </label>
                            </div>
                        </div>
                        </c:forEach>
                    </div>
                    <div class="mt-2 p-2 rounded" style="background:#e8f5f7;font-weight:600;">
                        Total estimé : <span id="total">0</span> MAD
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-teal px-4">
                        <i class="bi bi-save me-1"></i>Enregistrer la consultation
                    </button>
                    <a href="${pageContext.request.contextPath}/rdv" class="btn btn-outline-secondary">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
// Calcul du total en temps réel
document.querySelectorAll('.acte-check').forEach(cb => {
    cb.addEventListener('change', () => {
        let total = 0;
        document.querySelectorAll('.acte-check:checked').forEach(c => {
            total += parseFloat(c.dataset.tarif);
        });
        document.getElementById('total').textContent = total.toFixed(2);
    });
});
</script>
<jsp:include page="/views/common/footer.jsp"/>
