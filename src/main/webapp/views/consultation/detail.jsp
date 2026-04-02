<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="cons"      value="${consultation}"/>
<c:set var="pageTitle" value="Consultation du ${cons.date}"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>

<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-clipboard2-pulse me-2"></i>Consultation — ${cons.date}</h4>
        <div class="d-flex gap-2">
            <%-- Lien Voir Facture — utilise l'attribut "facture" mis par le Servlet --%>
            <c:choose>
                <c:when test="${not empty facture}">
                    <a href="${pageContext.request.contextPath}/facture?action=detail&id=${facture.idFacture}"
                       class="btn btn-sm btn-mint">
                        <i class="bi bi-receipt me-1"></i>Voir facture
                    </a>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-sm btn-outline-secondary" disabled>
                        <i class="bi bi-receipt me-1"></i>Facture non générée
                    </button>
                </c:otherwise>
            </c:choose>
            <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Retour
            </a>
        </div>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>

    <div class="row g-3">

        <%-- ─── Résumé clinique ─────────────────────────────────────── --%>
        <div class="col-md-7">
            <div class="card h-100">
                <div class="card-header" style="background:var(--teal-dark);color:#fff;">
                    <i class="bi bi-file-medical me-1"></i>Résumé clinique
                </div>
                <div class="card-body">

                    <div class="mb-3">
                        <div class="text-muted small fw-semibold text-uppercase mb-1">Dentiste</div>
                        <div class="fw-semibold">
                            Dr. <c:out value="${cons.prenomDentiste}"/> <c:out value="${cons.nomDentiste}"/>
                        </div>
                    </div>

                    <div class="mb-3">
                        <div class="text-muted small fw-semibold text-uppercase mb-1">Diagnostic</div>
                        <div class="p-2 rounded border" style="background:#f8f9fa; white-space:pre-wrap;">
                            <c:out value="${cons.diagnostic}"/>
                        </div>
                    </div>

                    <c:if test="${not empty cons.observations}">
                    <div class="mb-3">
                        <div class="text-muted small fw-semibold text-uppercase mb-1">Observations</div>
                        <div class="p-2 rounded border" style="background:#f8f9fa; white-space:pre-wrap;">
                            <c:out value="${cons.observations}"/>
                        </div>
                    </div>
                    </c:if>

                    <%-- Actes réalisés --%>
                    <div class="mb-2">
                        <div class="text-muted small fw-semibold text-uppercase mb-2">Actes réalisés</div>
                        <c:choose>
                            <c:when test="${empty cons.actes}">
                                <div class="text-muted small fst-italic">Aucun acte enregistré</div>
                            </c:when>
                            <c:otherwise>
                                <table class="table table-sm table-bordered mb-0">
                                    <thead style="background:var(--teal-dark);color:#fff;">
                                        <tr><th>Code</th><th>Acte</th><th class="text-end">Tarif</th></tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="a" items="${cons.actes}">
                                        <tr>
                                            <td><span class="badge bg-secondary"><c:out value="${a.code}"/></span></td>
                                            <td><c:out value="${a.nom}"/></td>
                                            <td class="text-end fw-semibold">
                                                <fmt:formatNumber value="${a.tarifBase}" maxFractionDigits="0"/> MAD
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </tbody>
                                    <tfoot>
                                        <tr class="table-active fw-bold">
                                            <td colspan="2" class="text-end">Total</td>
                                            <td class="text-end text-success">
                                                <fmt:formatNumber value="${cons.calculerMontantTotal()}"
                                                                  maxFractionDigits="0"/> MAD
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

        <%-- ─── Prescription + Facture ─────────────────────────────────── --%>
        <div class="col-md-5 d-flex flex-column gap-3">

            <%-- Prescription --%>
            <div class="card">
                <div class="card-header" style="background:var(--teal);color:#fff;">
                    <i class="bi bi-capsule me-1"></i>Prescription
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty prescription}">
                            <div class="text-muted small mb-2">Date : ${prescription.date}</div>
                            <c:if test="${not empty prescription.instructions}">
                                <div class="alert alert-info py-2 small">
                                    <c:out value="${prescription.instructions}"/>
                                </div>
                            </c:if>
                            <c:if test="${not empty prescription.medicaments}">
                                <table class="table table-sm mb-0">
                                    <thead><tr><th>Médicament</th><th>Dosage</th><th>Durée</th></tr></thead>
                                    <tbody>
                                        <c:forEach var="m" items="${prescription.medicaments}">
                                        <tr>
                                            <td class="fw-semibold"><c:out value="${m.nom}"/></td>
                                            <td><c:out value="${m.dosage}"/></td>
                                            <td>${m.dureeTraitement} j.</td>
                                        </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small mb-3">Aucune prescription pour cette consultation.</p>
                            <%-- Formulaire ajout prescription --%>
                            <button class="btn btn-sm btn-outline-primary"
                                    data-bs-toggle="collapse" data-bs-target="#formPrescription">
                                <i class="bi bi-plus me-1"></i>Ajouter une prescription
                            </button>
                            <div class="collapse mt-3" id="formPrescription">
                                <form method="post" action="${pageContext.request.contextPath}/consultation">
                                    <input type="hidden" name="action"         value="prescrire">
                                    <input type="hidden" name="idConsultation" value="${cons.idConsultation}">
                                    <div class="mb-2">
                                        <label class="form-label small fw-semibold">Instructions générales</label>
                                        <textarea name="instructions" class="form-control form-control-sm" rows="2"
                                                  placeholder="Ex: Prendre après les repas..."></textarea>
                                    </div>
                                    <div class="text-muted small fw-semibold mb-1">Médicaments</div>
                                    <div id="meds-list">
                                        <div class="med-row border rounded p-2 mb-2 bg-light">
                                            <div class="row g-1">
                                                <div class="col-5">
                                                    <input type="text" name="med_nom"
                                                           class="form-control form-control-sm"
                                                           placeholder="Médicament" required>
                                                </div>
                                                <div class="col-4">
                                                    <input type="text" name="med_dosage"
                                                           class="form-control form-control-sm"
                                                           placeholder="Dosage (ex: 500mg 3x/j)">
                                                </div>
                                                <div class="col-3">
                                                    <input type="number" name="med_duree"
                                                           class="form-control form-control-sm"
                                                           placeholder="Jrs" min="1" value="7">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="d-flex gap-2 mb-2">
                                        <button type="button" class="btn btn-sm btn-outline-secondary"
                                                onclick="addMed()">
                                            <i class="bi bi-plus"></i> Médicament
                                        </button>
                                    </div>
                                    <button type="submit" class="btn btn-sm btn-teal w-100 fw-semibold">
                                        <i class="bi bi-save me-1"></i>Enregistrer la prescription
                                    </button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <%-- Résumé facture --%>
            <c:if test="${not empty facture}">
            <div class="card">
                <div class="card-header" style="background:var(--teal-dark);color:#fff;">
                    <i class="bi bi-receipt me-1"></i>Facture associée
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="text-muted small">N° FAC-${facture.idFacture}</span>
                        <span class="badge bg-${facture.statut.badgeColor}">${facture.statut.libelle}</span>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span class="text-muted">Montant total</span>
                        <span class="fw-bold text-success fs-5">
                            <fmt:formatNumber value="${facture.montantTotal}" maxFractionDigits="2"/> MAD
                        </span>
                    </div>
                    <c:if test="${facture.paiement != null}">
                        <div class="text-muted small mt-1">
                            Payé le ${facture.paiement.datePaiement}
                            — ${facture.paiement.modePaiement.libelle}
                        </div>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/facture?action=detail&id=${facture.idFacture}"
                       class="btn btn-sm btn-mint w-100 mt-2">
                        <i class="bi bi-receipt me-1"></i>Accéder à la facture
                    </a>
                </div>
            </div>
            </c:if>

        </div>
    </div>
</div>

<script>
function addMed() {
    const row = document.querySelector('.med-row').cloneNode(true);
    row.querySelectorAll('input').forEach(i => { i.value = ''; });
    document.getElementById('meds-list').appendChild(row);
}
</script>
<jsp:include page="/views/common/footer.jsp"/>
