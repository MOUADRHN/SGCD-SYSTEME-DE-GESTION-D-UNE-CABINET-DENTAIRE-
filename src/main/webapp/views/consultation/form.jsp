<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Nouvelle consultation"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>

<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-clipboard2-pulse me-2"></i>Nouvelle Consultation</h4>
        <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour agenda
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i>${error}</div>
    </c:if>

    <div class="card">
        <div class="card-header" style="background:var(--teal-dark);color:#fff;">
            <c:if test="${not empty rdv}">
                Patient : <strong>${rdv.nomCompletPatient}</strong> &nbsp;—&nbsp;
                <fmt:formatDate value="${rdv.dateHeure}" pattern="dd/MM/yyyy HH:mm"
                    type="both" dateStyle="short" timeStyle="short"/>
                &nbsp;—&nbsp; Motif : ${rdv.motif.libelle}
            </c:if>
            <c:if test="${empty rdv}">Nouvelle consultation</c:if>
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/consultation">
                <input type="hidden" name="action" value="save">
                <%-- Transmission des identifiants depuis le RDV --%>
                <c:if test="${not empty rdv}">
                    <input type="hidden" name="idPatient" value="${rdv.idPatient}">
                    <input type="hidden" name="idRdv"     value="${rdv.idRDV}">
                </c:if>

                <div class="row g-3 mb-4">
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

                <%-- Actes --%>
                <div class="mb-4">
                    <label class="form-label fw-semibold">
                        Actes réalisés <span class="text-danger">*</span>
                    </label>
                    <div class="row g-2">
                        <c:forEach var="a" items="${actes}">
                        <div class="col-md-4">
                            <div class="border rounded p-2 h-100"
                                 style="cursor:pointer; transition:.15s;"
                                 onclick="toggleActe(this)">
                                <div class="form-check mb-0">
                                    <input class="form-check-input" type="checkbox"
                                           name="actes" value="${a.code}" id="acte_${a.code}"
                                           data-tarif="${a.tarifBase}">
                                    <label class="form-check-label w-100" for="acte_${a.code}">
                                        <div class="fw-semibold small">
                                            <span class="badge bg-secondary me-1">${a.code}</span>
                                            ${a.nom}
                                        </div>
                                        <div class="text-muted" style="font-size:.78rem;">
                                            <strong class="text-success">
                                                <fmt:formatNumber value="${a.tarifBase}" maxFractionDigits="0"/> MAD
                                            </strong>
                                        </div>
                                    </label>
                                </div>
                            </div>
                        </div>
                        </c:forEach>
                    </div>

                    <div class="mt-3 p-2 rounded d-flex justify-content-between align-items-center"
                         style="background:#e8f5f7; border: 1px solid #B2D8DF;">
                        <span class="fw-semibold text-muted">Total estimé :</span>
                        <span id="total" class="fw-bold fs-5 text-success">0 MAD</span>
                    </div>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-teal px-5 fw-semibold">
                        <i class="bi bi-save me-1"></i>Enregistrer la consultation
                    </button>
                    <a href="${pageContext.request.contextPath}/rdv" class="btn btn-outline-secondary px-4">
                        Annuler
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function toggleActe(container) {
    const cb = container.querySelector('input[type=checkbox]');
    cb.checked = !cb.checked;
    container.style.background = cb.checked ? '#e8f5f7' : '';
    container.style.borderColor = cb.checked ? '#028090' : '';
    recalcTotal();
}
function recalcTotal() {
    let total = 0;
    document.querySelectorAll('input[name=actes]:checked').forEach(cb => {
        total += parseFloat(cb.dataset.tarif || 0);
    });
    document.getElementById('total').textContent = total.toFixed(0) + ' MAD';
}
document.querySelectorAll('input[name=actes]').forEach(cb => {
    cb.addEventListener('change', recalcTotal);
});
</script>
<jsp:include page="/views/common/footer.jsp"/>
