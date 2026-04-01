<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Statistiques" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-bar-chart-line me-2"></i>Tableau de bord — Statistiques</h4>
    </div>

    <!-- KPI Cards -->
    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div style="font-size:2.5rem;font-weight:700;color:#028090;">${totalPatients}</div>
                <div class="text-muted">Total Patients</div>
                <div style="height:4px;background:var(--teal);border-radius:4px;margin-top:.5rem;"></div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div style="font-size:2.5rem;font-weight:700;color:#ef6c00;">${rdvToday}</div>
                <div class="text-muted">RDV aujourd'hui</div>
                <div style="height:4px;background:#ef6c00;border-radius:4px;margin-top:.5rem;"></div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div style="font-size:2.5rem;font-weight:700;color:#2e7d32;">${consultsMois}</div>
                <div class="text-muted">Consultations (mois)</div>
                <div style="height:4px;background:#2e7d32;border-radius:4px;margin-top:.5rem;"></div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div style="font-size:2rem;font-weight:700;color:#c2185b;">
                    <fmt:formatNumber value="${caMois}" maxFractionDigits="0"/> MAD
                </div>
                <div class="text-muted">CA ce mois (payé)</div>
                <div style="height:4px;background:#c2185b;border-radius:4px;margin-top:.5rem;"></div>
            </div>
        </div>
    </div>

    <!-- Agenda du jour -->
    <div class="card">
        <div class="card-header" style="background:var(--teal-dark);color:#fff;">
            <i class="bi bi-calendar3 me-1"></i>Rendez-vous du jour
        </div>
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty rdvList}">
                    <div class="text-center text-muted py-4">Aucun rendez-vous aujourd'hui</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead><tr><th>Heure</th><th>Patient</th><th>Dentiste</th><th>Motif</th><th>Statut</th></tr></thead>
                            <tbody>
                                <c:forEach var="rv" items="${rdvList}">
                                <tr>
                                    <td class="fw-bold">${rv.dateHeure.toLocalTime()}</td>
                                    <td>${rv.nomCompletPatient}</td>
                                    <td>${rv.nomCompletDentiste}</td>
                                    <td><span class="badge bg-secondary">${rv.motif.libelle}</span></td>
                                    <td><span class="badge bg-${rv.statut.badgeColor}">${rv.statut.libelle}</span></td>
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
