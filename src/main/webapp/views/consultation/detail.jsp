<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="c"         value="${consultation}" />
<c:set var="pageTitle" value="Consultation du ${c.date}" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-clipboard2-pulse me-2"></i>Consultation — ${c.date}</h4>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/facture?action=detail&id=${c.facture.idFacture}"
               class="btn btn-sm btn-mint">
                <i class="bi bi-receipt me-1"></i>Voir facture
            </a>
            <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">Retour</a>
        </div>
    </div>

    <div class="row g-3">
        <!-- Infos consultation -->
        <div class="col-md-7">
            <div class="card h-100">
                <div class="card-header" style="background:var(--teal-dark);color:#fff;">
                    <i class="bi bi-file-medical me-1"></i>Résumé clinique
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <div class="text-muted small fw-semibold text-uppercase mb-1">Dentiste</div>
                        <div>Dr. ${c.prenomDentiste} ${c.nomDentiste}</div>
                    </div>
                    <div class="mb-3">
                        <div class="text-muted small fw-semibold text-uppercase mb-1">Diagnostic</div>
                        <div class="p-2 rounded" style="background:#f8f9fa;">${c.diagnostic}</div>
                    </div>
                    <c:if test="${not empty c.observations}">
                    <div class="mb-3">
                        <div class="text-muted small fw-semibold text-uppercase mb-1">Observations</div>
                        <div class="p-2 rounded" style="background:#f8f9fa;">${c.observations}</div>
                    </div>
                    </c:if>
                    <div>
                        <div class="text-muted small fw-semibold text-uppercase mb-2">Actes réalisés</div>
                        <c:choose>
                            <c:when test="${empty c.actes}">
                                <span class="text-muted">Aucun acte enregistré</span>
                            </c:when>
                            <c:otherwise>
                                <table class="table table-sm mb-0">
                                    <thead><tr><th>Code</th><th>Acte</th><th class="text-end">Tarif</th></tr></thead>
                                    <tbody>
                                        <c:forEach var="a" items="${c.actes}">
                                        <tr>
                                            <td><span class="badge bg-secondary">${a.code}</span></td>
                                            <td>${a.nom}</td>
                                            <td class="text-end fw-semibold">
                                                <fmt:formatNumber value="${a.tarifBase}" maxFractionDigits="0"/> MAD
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </tbody>
                                    <tfoot>
                                        <tr class="table-active">
                                            <td colspan="2" class="fw-bold">Total</td>
                                            <td class="text-end fw-bold text-success">
                                                <fmt:formatNumber value="${c.calculerMontantTotal()}" maxFractionDigits="0"/> MAD
                                            </td>
                                        </tr>
                                    </tfoot>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Prescription -->
        <div class="col-md-5">
            <div class="card mb-3">
                <div class="card-header" style="background:var(--teal);color:#fff;">
                    <i class="bi bi-capsule me-1"></i>Prescription
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${prescription != null}">
                            <div class="text-muted small mb-2">Date : ${prescription.date}</div>
                            <c:if test="${not empty prescription.instructions}">
                                <div class="alert alert-info py-2 small">${prescription.instructions}</div>
                            </c:if>
                            <table class="table table-sm mb-0">
                                <thead><tr><th>Médicament</th><th>Dosage</th><th>Durée</th></tr></thead>
                                <tbody>
                                    <c:forEach var="m" items="${prescription.medicaments}">
                                    <tr>
                                        <td class="fw-semibold">${m.nom}</td>
                                        <td>${m.dosage}</td>
                                        <td>${m.dureeTraitement} j.</td>
                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small mb-2">Aucune prescription pour cette consultation.</p>
                            <!-- Formulaire ajout prescription -->
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="collapse"
                                    data-bs-target="#formPrescription">
                                <i class="bi bi-plus me-1"></i>Ajouter une prescription
                            </button>
                            <div class="collapse mt-3" id="formPrescription">
                                <form method="post" action="${pageContext.request.contextPath}/consultation">
                                    <input type="hidden" name="action"          value="prescrire">
                                    <input type="hidden" name="idConsultation"  value="${c.idConsultation}">
                                    <div class="mb-2">
                                        <label class="form-label small fw-semibold">Instructions générales</label>
                                        <textarea name="instructions" class="form-control form-control-sm" rows="2"></textarea>
                                    </div>
                                    <div id="meds-list">
                                        <div class="med-row border rounded p-2 mb-2">
                                            <div class="row g-1">
                                                <div class="col-5"><input type="text" name="med_nom"
                                                    class="form-control form-control-sm" placeholder="Médicament" required></div>
                                                <div class="col-4"><input type="text" name="med_dosage"
                                                    class="form-control form-control-sm" placeholder="Dosage"></div>
                                                <div class="col-3"><input type="number" name="med_duree"
                                                    class="form-control form-control-sm" placeholder="Jours" min="1" value="7"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <button type="button" class="btn btn-sm btn-outline-secondary mb-2"
                                            onclick="addMed()">+ Médicament</button>
                                    <button type="submit" class="btn btn-sm btn-teal w-100">
                                        <i class="bi bi-save me-1"></i>Enregistrer
                                    </button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
function addMed() {
    const tpl = document.querySelector('.med-row').cloneNode(true);
    tpl.querySelectorAll('input').forEach(i => i.value = '');
    document.getElementById('meds-list').appendChild(tpl);
}
</script>
<jsp:include page="/views/common/footer.jsp"/>
