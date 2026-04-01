<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Facturation" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-receipt-cutoff me-2"></i>Facturation</h4>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>
    <div class="card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                        <tr>
                            <th>#</th><th>Patient</th><th>Date</th>
                            <th class="text-end">Montant</th><th>Statut</th><th>Paiement</th><th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty factures}">
                                <tr><td colspan="7" class="text-center text-muted py-4">Aucune facture</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="f" items="${factures}">
                                <tr>
                                    <td class="text-muted small">FAC-${f.idFacture}</td>
                                    <td class="fw-semibold">${f.prenomPatient} ${f.nomPatient}</td>
                                    <td>${f.date}</td>
                                    <td class="text-end fw-bold">
                                        <fmt:formatNumber value="${f.montantTotal}" maxFractionDigits="0"/> MAD
                                    </td>
                                    <td>
                                        <span class="badge bg-${f.statut.badgeColor}">${f.statut.libelle}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${f.paiement != null}">
                                                <span class="badge bg-light text-dark border">
                                                    ${f.paiement.modePaiement.libelle}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted small">—</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
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
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
