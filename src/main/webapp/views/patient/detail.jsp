<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="p"         value="${patient}"/>
<c:set var="pageTitle" value="Dossier patient"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-folder2-open me-2"></i>${p.nomComplet}</h4>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/patients?action=edit&id=${p.idPatient}"
               class="btn btn-sm btn-outline-primary">
                <i class="bi bi-pencil me-1"></i>Modifier
            </a>
            <a href="${pageContext.request.contextPath}/rdv?action=add&idPatient=${p.idPatient}"
               class="btn btn-sm btn-mint">
                <i class="bi bi-calendar-plus me-1"></i>Nouveau RDV
            </a>
            <a href="${pageContext.request.contextPath}/patients" class="btn btn-sm btn-outline-secondary">
                <i class="bi bi-arrow-left me-1"></i>Liste
            </a>
        </div>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>

    <div class="row g-3">
        <!-- Fiche identité -->
        <div class="col-md-4">
            <div class="card mb-3">
                <div class="card-header" style="background:var(--teal-dark);color:#fff;">
                    <i class="bi bi-person-vcard me-1"></i>Identité
                </div>
                <div class="card-body">
                    <div class="text-center mb-3">
                        <div class="avatar-circle mx-auto mb-2" style="width:60px;height:60px;font-size:1.4rem;">
                            ${p.prenom.charAt(0)}${p.nom.charAt(0)}
                        </div>
                        <div class="fw-bold fs-5">${p.nomComplet}</div>
                        <div class="text-muted small">${p.sexe.libelle} — ${p.age} ans
                            <c:if test="${p.estMineur()}">
                                <span class="badge bg-warning text-dark ms-1">Mineur</span>
                            </c:if>
                        </div>
                    </div>
                    <dl class="row small mb-0">
                        <dt class="col-5">Naissance</dt><dd class="col-7">${p.dateNaissance}</dd>
                        <dt class="col-5">Téléphone</dt><dd class="col-7">${p.telephone}</dd>
                        <dt class="col-5">CNSS</dt><dd class="col-7">${not empty p.numeroCNSS ? p.numeroCNSS : '—'}</dd>
                        <dt class="col-5">Adresse</dt><dd class="col-7">${p.adresse}</dd>
                        <c:if test="${not empty p.allergie}">
                            <dt class="col-5">Allergie</dt>
                            <dd class="col-7"><span class="badge bg-danger">${p.allergie}</span></dd>
                        </c:if>
                        <c:if test="${not empty p.antecedents}">
                            <dt class="col-5">Antécédents</dt>
                            <dd class="col-7">${p.antecedents}</dd>
                        </c:if>
                    </dl>
                    <c:if test="${p.dossierMedical != null}">
                        <hr class="my-2">
                        <div class="text-muted" style="font-size:.78rem;">
                            <i class="bi bi-folder me-1"></i>Dossier : <strong>${p.dossierMedical.numeroRef}</strong>
                            — Ouvert le ${p.dossierMedical.dateCreation}
                        </div>
                    </c:if>
                </div>
            </div>

            <c:if test="${p.estMineur() and p.responsableLegal != null}">
                <div class="card">
                    <div class="card-header" style="background:#f57c00;color:#fff;">
                        <i class="bi bi-person-heart me-1"></i>Responsable légal
                    </div>
                    <div class="card-body small">
                        <div class="fw-semibold">${p.responsableLegal.nom}</div>
                        <div class="text-muted">${p.responsableLegal.lienParente}</div>
                        <div><i class="bi bi-telephone me-1"></i>${p.responsableLegal.telephone}</div>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Historique médical + RDV -->
        <div class="col-md-8">
            <!-- Consultations -->
            <div class="card mb-3">
                <div class="card-header" style="background:var(--teal);color:#fff;">
                    <i class="bi bi-clipboard2-pulse me-1"></i>
                    Historique des consultations
                    <span class="badge bg-white text-dark ms-2">${consultations.size()}</span>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${empty consultations}">
                            <div class="text-center text-muted py-4">
                                <i class="bi bi-clipboard-x d-block fs-3 mb-1"></i>Aucune consultation
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-hover mb-0" style="font-size:.88rem;">
                                    <thead>
                                        <tr><th>Date</th><th>Dentiste</th><th>Diagnostic</th><th>Actes</th><th></th></tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="c" items="${consultations}">
                                        <tr>
                                            <td class="fw-semibold">${c.date}</td>
                                            <td>Dr. ${c.prenomDentiste} ${c.nomDentiste}</td>
                                            <td>${c.diagnostic}</td>
                                            <td>
                                                <span class="badge bg-secondary">${c.actes.size()} acte(s)</span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/consultation?action=detail&id=${c.idConsultation}"
                                                   class="btn btn-sm btn-outline-secondary">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Rendez-vous -->
            <div class="card">
                <div class="card-header" style="background:var(--teal-dark);color:#fff;">
                    <i class="bi bi-calendar3 me-1"></i>Rendez-vous
                    <span class="badge bg-white text-dark ms-2">${rdvList.size()}</span>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${empty rdvList}">
                            <div class="text-center text-muted py-4">Aucun rendez-vous enregistré</div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table mb-0" style="font-size:.88rem;">
                                    <thead>
                                        <tr><th>Date / Heure</th><th>Motif</th><th>Dentiste</th><th>Statut</th></tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="rv" items="${rdvList}">
                                        <tr>
                                            <td class="fw-semibold">${rv.dateHeure}</td>
                                            <td><span class="badge bg-secondary">${rv.motif.libelle}</span></td>
                                            <td>Dr. ${rv.nomDentiste}</td>
                                            <td>
                                                <span class="badge bg-${rv.statut.badgeColor}">${rv.statut.libelle}</span>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
