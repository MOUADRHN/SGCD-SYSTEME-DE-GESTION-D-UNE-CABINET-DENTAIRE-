<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Rendez-vous"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>

<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-calendar3 me-2"></i>Rendez-vous — ${dateSelected}</h4>
        <a href="${pageContext.request.contextPath}/rdv?action=add" class="btn btn-mint">
            <i class="bi bi-calendar-plus me-1"></i>Nouveau RDV
        </a>
    </div>

    <jsp:include page="/views/common/flash.jsp"/>

    <div class="card mb-3">
        <div class="card-body py-2">
            <form method="get" action="${pageContext.request.contextPath}/rdv" class="row g-2 align-items-center">
                <div class="col-auto">
                    <label class="form-label fw-semibold mb-0 small">Date :</label>
                </div>
                <div class="col-auto">
                    <input type="date" name="date" class="form-control form-control-sm" value="${dateSelected}">
                </div>
                <div class="col">
                    <input type="text" name="q" class="form-control form-control-sm"
                           placeholder="Rechercher patient, dentiste, motif, statut…"
                           value="<c:out value='${searchQuery}'/>">
                </div>
                <div class="col-auto">
                    <button class="btn btn-sm btn-teal px-3">
                        <i class="bi bi-search me-1"></i>Rechercher
                    </button>
                </div>
                <div class="col-auto">
                    <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">
                        Aujourd'hui
                    </a>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty rdvList}">
                    <div class="text-center text-muted py-5">
                        <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>
                        Aucun rendez-vous trouvé pour cette sélection.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                            <tr>
                                <th>Heure</th>
                                <th>Patient</th>
                                <th>Dentiste</th>
                                <th>Motif</th>
                                <th>Durée</th>
                                <th>Statut</th>
                                <th class="text-center">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="rv" items="${rdvList}">
                                <tr>
                                    <td class="fw-bold align-middle text-primary">${rv.dateHeure.toLocalTime()}</td>
                                    <td class="align-middle">
                                        <a href="${pageContext.request.contextPath}/patients?action=detail&id=${rv.idPatient}"
                                           class="text-decoration-none fw-semibold">
                                                ${rv.nomCompletPatient}
                                        </a>
                                    </td>
                                    <td class="text-muted align-middle">${rv.nomCompletDentiste}</td>
                                    <td class="align-middle">
                                        <span class="badge bg-secondary">${rv.motif.libelle}</span>
                                    </td>
                                    <td class="align-middle">${rv.duree} min</td>
                                    <td class="align-middle">
                                        <span class="badge bg-${rv.statut.badgeColor}">${rv.statut.libelle}</span>
                                    </td>
                                    <td class="align-middle">
                                        <div class="d-flex gap-1 justify-content-center">

                                                <%-- Action : Arrivée --%>
                                            <c:if test="${rv.statut.name() eq 'PLANIFIE'}">
                                                <form method="post" action="${pageContext.request.contextPath}/rdv">
                                                    <input type="hidden" name="action" value="statut">
                                                    <input type="hidden" name="id"     value="${rv.idRDV}">
                                                    <input type="hidden" name="type"   value="arrivee">
                                                    <button class="btn btn-sm btn-warning" title="Marquer arrivée">
                                                        <i class="bi bi-person-check"></i>
                                                    </button>
                                                </form>
                                            </c:if>

                                                <%-- Action : Passer en consultation --%>
                                            <c:if test="${rv.statut.name() eq 'EN_SALLE_ATTENTE'}">
                                                <form method="post" action="${pageContext.request.contextPath}/rdv">
                                                    <input type="hidden" name="action" value="statut">
                                                    <input type="hidden" name="id"     value="${rv.idRDV}">
                                                    <input type="hidden" name="type"   value="encours">
                                                    <button class="btn btn-sm btn-primary" title="En cours">
                                                        <i class="bi bi-play-circle"></i>
                                                    </button>                                            </form>
                                            </c:if>

                                                <%-- Action : Ouvrir la consultation --%>
                                            <c:if test="${rv.statut.name() eq 'EN_COURS'}">
                                                <a href="${pageContext.request.contextPath}/consultation?action=ouvrir&idRdv=${rv.idRDV}"
                                                   class="btn btn-sm btn-success" title="Ouvrir consultation">
                                                    <i class="bi bi-clipboard2-pulse"></i>
                                                </a>
                                            </c:if>

                                                <%-- Action : Annuler --%>
                                            <c:if test="${rv.statut.name() eq 'PLANIFIE' or rv.statut.name() eq 'EN_SALLE_ATTENTE'}">
                                                <form method="post" action="${pageContext.request.contextPath}/rdv"
                                                      onsubmit="return confirm('Annuler ce rendez-vous ?')">
                                                    <input type="hidden" name="action" value="statut">
                                                    <input type="hidden" name="id"     value="${rv.idRDV}">
                                                    <input type="hidden" name="type"   value="annuler">
                                                    <button class="btn btn-sm btn-outline-danger" title="Annuler">
                                                        <i class="bi bi-x-circle"></i>
                                                    </button>
                                                </form>
                                            </c:if>

                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card-footer text-muted small d-flex justify-content-between">
            <span>Affichage de <strong>${rdvList.size()}</strong> rendez-vous</span>
            <span>Cabinet Dentaire SGCD</span>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp"/>