<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Patients"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-people-fill me-2"></i>Gestion des Patients</h4>
        <a href="${pageContext.request.contextPath}/patients?action=add" class="btn btn-mint">
            <i class="bi bi-person-plus me-1"></i>Nouveau patient
        </a>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>
    <div class="card mb-3">
        <div class="card-body py-2">
            <form method="get" action="${pageContext.request.contextPath}/patients" class="d-flex gap-2">
                <input type="text" name="q" class="form-control"
                       placeholder="Rechercher par nom, prénom, téléphone, CNSS…"
                       value="<c:out value='${searchQuery}'/>">
                <button class="btn btn-teal px-4"><i class="bi bi-search"></i></button>
                <a href="${pageContext.request.contextPath}/patients" class="btn btn-outline-secondary">Réinitialiser</a>
            </form>
        </div>
    </div>
    <div class="card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                        <tr><th>#</th><th>Patient</th><th>Âge / Sexe</th><th>Téléphone</th><th>Allergie</th><th>Actions</th></tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty patients}">
                                <tr><td colspan="6" class="text-center text-muted py-5">
                                    <i class="bi bi-search d-block fs-1 mb-2"></i>Aucun patient trouvé
                                </td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="p" items="${patients}">
                                <tr>
                                    <td class="text-muted small align-middle">${p.idPatient}</td>
                                    <td class="align-middle">
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="avatar-circle" style="font-size:.7rem;min-width:36px;">
                                                ${p.prenom.charAt(0)}${p.nom.charAt(0)}
                                            </div>
                                            <div>
                                                <div class="fw-semibold">${p.nomComplet}</div>
                                                <div class="text-muted" style="font-size:.78rem;">${p.dateNaissance}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="align-middle">
                                        ${p.age} ans / ${p.sexe.libelle}
                                        <c:if test="${p.estMineur()}">
                                            <span class="badge bg-warning text-dark ms-1" style="font-size:.62rem;">Mineur</span>
                                        </c:if>
                                    </td>
                                    <td class="align-middle">${p.telephone}</td>
                                    <td class="align-middle">
                                        <c:choose>
                                            <c:when test="${not empty p.allergie}">
                                                <span class="badge bg-danger"><i class="bi bi-exclamation-triangle me-1"></i>${p.allergie}</span>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="align-middle">
                                        <div class="d-flex gap-1">
                                            <a href="${pageContext.request.contextPath}/patients?action=detail&id=${p.idPatient}"
                                               class="btn btn-sm btn-outline-secondary" title="Dossier">
                                                <i class="bi bi-folder2-open"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/patients?action=edit&id=${p.idPatient}"
                                               class="btn btn-sm btn-outline-primary" title="Modifier">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/rdv?action=add&idPatient=${p.idPatient}"
                                               class="btn btn-sm btn-teal" title="Nouveau RDV">
                                                <i class="bi bi-calendar-plus"></i>
                                            </a>
                                        </div>
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
            ${patients.size()} patient(s) trouvé(s)
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
