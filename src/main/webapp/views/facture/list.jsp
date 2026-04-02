<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Facturation"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-receipt-cutoff me-2"></i>Facturation</h4>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>

    <!-- Barre de recherche -->
    <div class="card mb-3">
        <div class="card-body py-2">
            <form method="get" action="${pageContext.request.contextPath}/facture" class="d-flex gap-2">
                <input type="text" name="q" class="form-control"
                       placeholder="Rechercher par patient, N° facture, statut…"
                       value="<c:out value='${searchQuery}'/>">
                <button class="btn btn-teal px-4"><i class="bi bi-search"></i></button>
                <a href="${pageContext.request.contextPath}/facture" class="btn btn-outline-secondary">Réinitialiser</a>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                    <tr><th>#</th><th>Patient</th><th>Date</th>
                        <th class="text-end">Montant</th><th>Statut</th>
                        <th>Paiement</th><th>Email</th><th>Actions</th></tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty factures}">
                            <tr><td colspan="8" class="text-center text-muted py-5">
                                <i class="bi bi-search d-block fs-1 mb-2"></i>
                                Aucune facture trouvée
                            </td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="f" items="${factures}">
                                <tr>
                                    <td class="text-muted small align-middle">FAC-${f.idFacture}</td>
                                    <td class="fw-semibold align-middle">${f.prenomPatient} ${f.nomPatient}</td>
                                    <td class="align-middle">${f.date}</td>
                                    <td class="text-end fw-bold align-middle">
                                        <fmt:formatNumber value="${f.montantTotal}" maxFractionDigits="0"/> MAD
                                    </td>
                                    <td class="align-middle">
                                        <span class="badge bg-${f.statut.badgeColor}">${f.statut.libelle}</span>
                                    </td>
                                    <td class="align-middle">
                                        <c:choose>
                                            <c:when test="${f.paiement != null}">
                                                <span class="badge bg-light text-dark border">
                                                        ${f.paiement.modePaiement.libelle}
                                                </span>
                                            </c:when>
                                            <c:otherwise><span class="text-muted small">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="align-middle">
                                        <c:choose>
                                            <c:when test="${f.emailEnvoye}">
                                                <i class="bi bi-envelope-check text-success" title="Email envoyé"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-envelope text-muted" title="Email non envoyé"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="align-middle">
                                        <a href="${pageContext.request.contextPath}/facture?action=detail&id=${f.idFacture}"
                                           class="btn btn-sm btn-outline-secondary">
                                            <i class="bi bi-eye"></i> Détail
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="card-footer text-muted small">
            ${factures.size()} facture(s) trouvée(s)
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
