<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="f"         value="${facture}" />
<c:set var="pageTitle" value="Facture FAC-${f.idFacture}" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-receipt me-2"></i>Facture N° FAC-${f.idFacture}</h4>
        <a href="${pageContext.request.contextPath}/facture" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour
        </a>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>

    <div class="row g-3">
        <div class="col-md-7">
            <div class="card" id="invoice-print">
                <div class="card-header d-flex justify-content-between"
                     style="background:var(--teal-dark);color:#fff;">
                    <span><i class="bi bi-tooth me-2"></i>SGCD — Cabinet Dentaire</span>
                    <span>FAC-${f.idFacture}</span>
                </div>
                <div class="card-body">
                    <div class="row mb-4">
                        <div class="col-6">
                            <div class="text-muted small">Patient</div>
                            <div class="fw-bold fs-5">${f.prenomPatient} ${f.nomPatient}</div>
                        </div>
                        <div class="col-6 text-end">
                            <div class="text-muted small">Date</div>
                            <div class="fw-semibold">${f.date}</div>
                            <div class="mt-1">
                                <span class="badge bg-${f.statut.badgeColor} fs-6">${f.statut.libelle}</span>
                            </div>
                        </div>
                    </div>

                    <table class="table table-bordered mb-0">
                        <thead style="background:var(--teal-dark);color:#fff;">
                        <tr><th>Description</th><th class="text-end">Montant</th></tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>Consultation du ${f.date}</td>
                            <td class="text-end fw-bold">
                                <fmt:formatNumber value="${f.montantTotal}" maxFractionDigits="2"/> MAD
                            </td>
                        </tr>
                        </tbody>
                        <tfoot>
                        <tr class="table-active">
                            <td class="fw-bold text-end fs-5">TOTAL À PAYER</td>
                            <td class="fw-bold text-end fs-5 text-success">
                                <fmt:formatNumber value="${f.montantTotal}" maxFractionDigits="2"/> MAD
                            </td>
                        </tr>
                        </tfoot>
                    </table>

                    <c:if test="${f.paiement != null}">
                        <div class="mt-3 p-3 rounded" style="background:#e8f5e9;">
                            <div class="fw-semibold text-success mb-1">
                                <i class="bi bi-check-circle-fill me-1"></i>Paiement reçu
                            </div>
                            <div class="small">
                                Mode : <strong>${f.paiement.modePaiement.libelle}</strong> —
                                Date : ${f.paiement.datePaiement} —
                                Montant : <fmt:formatNumber value="${f.paiement.montant}" maxFractionDigits="2"/> MAD
                            </div>
                        </div>
                    </c:if>
                </div>
                <div class="card-footer text-center text-muted small">
                    Merci de votre confiance — SGCD Cabinet Dentaire
                </div>
            </div>

            <div class="d-flex gap-2 mt-3 no-print">
                <button class="btn btn-outline-secondary" onclick="window.print()">
                    <i class="bi bi-printer me-1"></i>Imprimer
                </button>

                <%-- --- BLOC EMAIL AJOUTÉ ICI --- --%>
                <c:choose>
                    <c:when test="${f.emailEnvoye}">
                        <button class="btn btn-outline-success" disabled>
                            <i class="bi bi-envelope-check me-1"></i>Email envoyé
                        </button>
                    </c:when>
                    <c:when test="${f.statut.name() eq 'PAYEE'}">
                        <form method="post" action="${pageContext.request.contextPath}/facture"
                              onsubmit="return confirm('Envoyer le reçu par email au patient ?')">
                            <input type="hidden" name="action" value="email">
                            <input type="hidden" name="id"     value="${f.idFacture}">
                            <button type="submit" class="btn btn-teal">
                                <i class="bi bi-envelope me-1"></i>Envoyer le reçu par email
                            </button>
                        </form>
                    </c:when>
                </c:choose>
                <%-- ---------------------------- --%>
            </div>
        </div>

        <c:if test="${f.statut.name() eq 'EN_ATTENTE'}">
            <div class="col-md-5">
                <div class="card">
                    <div class="card-header" style="background:var(--teal);color:#fff;">
                        <i class="bi bi-cash-coin me-1"></i>Enregistrer le paiement
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/facture">
                            <input type="hidden" name="action"    value="payer">
                            <input type="hidden" name="idFacture" value="${f.idFacture}">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Montant reçu (MAD)</label>
                                <input type="number" name="montant" class="form-control"
                                       value="${f.montantTotal}" step="0.01" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Mode de paiement</label>
                                <c:forEach var="mp" items="${modesPaiement}">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="modePaiement"
                                               value="${mp.name()}" id="mp_${mp.name()}"
                                            ${mp.name() eq 'ESPECES' ? 'checked' : ''} required>
                                        <label class="form-check-label" for="mp_${mp.name()}">${mp.libelle}</label>
                                    </div>
                                </c:forEach>
                            </div>
                            <button type="submit" class="btn btn-mint w-100 fw-semibold">
                                <i class="bi bi-check-circle me-1"></i>Valider le paiement
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>
<style>
    @media print {
        .sidebar, .topbar, .btn, .no-print, form { display:none !important; }
        .main-content { margin:0 !important; padding:0 !important; }
        #invoice-print { box-shadow:none !important; border:1px solid #ccc !important; }
    }
</style>
<jsp:include page="/views/common/footer.jsp"/>