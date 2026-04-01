<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Rendez-vous" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
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
            <form method="get" action="${pageContext.request.contextPath}/rdv" class="d-flex gap-2 align-items-center">
                <label class="fw-semibold mb-0">Date :</label>
                <input type="date" name="date" class="form-control" style="max-width:180px;" value="${dateSelected}">
                <button class="btn btn-teal"><i class="bi bi-search"></i> Afficher</button>
                <a href="${pageContext.request.contextPath}/rdv" class="btn btn-outline-secondary">Aujourd'hui</a>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty rdvList}">
                    <div class="text-center text-muted py-5">
                        <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>
                        Aucun rendez-vous pour cette date
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr><th>Heure</th><th>Patient</th><th>Dentiste</th>
                                    <th>Motif</th><th>Durée</th><th>Statut</th><th>Actions</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="rv" items="${rdvList}">
                                <tr>
                                    <td class="fw-bold">${rv.dateHeure.toLocalTime()}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/patients?action=detail&id=${rv.idPatient}">
                                            ${rv.nomCompletPatient}
                                        </a>
                                    </td>
                                    <td>${rv.nomCompletDentiste}</td>
                                    <td><span class="badge bg-secondary">${rv.motif.libelle}</span></td>
                                    <td>${rv.duree} min</td>
                                    <td><span class="badge bg-${rv.statut.badgeColor}">${rv.statut.libelle}</span></td>
                                    <td>
                                        <div class="d-flex gap-1">
                                        <%-- Arrivée --%>
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
                                        <%-- En cours --%>
                                        <c:if test="${rv.statut.name() eq 'EN_SALLE_ATTENTE'}">
                                            <form method="post" action="${pageContext.request.contextPath}/rdv">
                                                <input type="hidden" name="action" value="statut">
                                                <input type="hidden" name="id"     value="${rv.idRDV}">
                                                <input type="hidden" name="type"   value="encours">
                                                <button class="btn btn-sm btn-primary" title="En cours">
                                                    <i class="bi bi-play-circle"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                        <%-- Ouvrir consultation --%>
                                        <c:if test="${rv.statut.name() eq 'EN_COURS'}">
                                            <a href="${pageContext.request.contextPath}/consultation?action=ouvrir&idRdv=${rv.idRDV}"
                                               class="btn btn-sm btn-success" title="Consultation">
                                                <i class="bi bi-clipboard2-pulse"></i>
                                            </a>
                                        </c:if>
                                        <%-- Annuler --%>
                                        <c:if test="${rv.statut.name() eq 'PLANIFIE' or rv.statut.name() eq 'EN_SALLE_ATTENTE'}">
                                            <form method="post" action="${pageContext.request.contextPath}/rdv">
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
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
