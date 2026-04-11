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

    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="card text-center p-3 h-100 shadow-sm border-0">
                <div style="font-size:2.5rem;font-weight:700;color:#028090;">${totalPatients}</div>
                <div class="text-muted fw-semibold">Total Patients</div>
                <div style="height:4px;background:var(--teal);border-radius:4px;margin-top:auto;margin-bottom:0.5rem;"></div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3 h-100 shadow-sm border-0">
                <div style="font-size:2.5rem;font-weight:700;color:#ef6c00;">${rdvToday}</div>
                <div class="text-muted fw-semibold">RDV aujourd'hui</div>
                <div style="height:4px;background:#ef6c00;border-radius:4px;margin-top:auto;margin-bottom:0.5rem;"></div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3 h-100 shadow-sm border-0">
                <div style="font-size:2.5rem;font-weight:700;color:#2e7d32;">${consultsMois}</div>
                <div class="text-muted fw-semibold">Consultations (mois)</div>
                <div style="height:4px;background:#2e7d32;border-radius:4px;margin-top:auto;margin-bottom:0.5rem;"></div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3 h-100 shadow-sm border-0">
                <div style="font-size:2rem;font-weight:700;color:#c2185b; margin-top:0.5rem;">
                    <fmt:formatNumber value="${caMois}" maxFractionDigits="0"/> MAD
                </div>
                <div class="text-muted fw-semibold">CA ce mois (payé)</div>
                <div style="height:4px;background:#c2185b;border-radius:4px;margin-top:auto;margin-bottom:0.5rem;"></div>
            </div>
        </div>
    </div>

    <div class="row g-3 mb-4">
        <div class="col-md-6">
            <div class="card p-3 shadow-sm border-0">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted fw-semibold mb-1">Taux d'Absences (Non honorés)</div>
                        <div style="font-size:2rem;font-weight:700;color:#d32f2f;">${tauxNonHonore}%</div>
                        <div class="small text-muted">${nbNonHonore} RDV non honorés sur ${totalRdv} au total</div>
                    </div>
                    <div class="rounded-circle d-flex align-items-center justify-content-center" style="width: 60px; height: 60px; background-color: #ffebee; color: #d32f2f;">
                        <i class="bi bi-person-x fs-2"></i>
                    </div>
                </div>
                <div class="progress mt-3" style="height: 6px;">
                    <div class="progress-bar bg-danger" role="progressbar" style="width: ${tauxNonHonore}%;" aria-valuenow="${tauxNonHonore}" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card p-3 shadow-sm border-0">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="text-muted fw-semibold mb-1">Taux d'Annulations (Prévenus)</div>
                        <div style="font-size:2rem;font-weight:700;color:#f57c00;">${tauxAnnules}%</div>
                        <div class="small text-muted">${nbAnnules} RDV annulés sur ${totalRdv} au total</div>
                    </div>
                    <div class="rounded-circle d-flex align-items-center justify-content-center" style="width: 60px; height: 60px; background-color: #fff3e0; color: #f57c00;">
                        <i class="bi bi-x-circle fs-2"></i>
                    </div>
                </div>
                <div class="progress mt-3" style="height: 6px;">
                    <div class="progress-bar bg-warning" role="progressbar" style="width: ${tauxAnnules}%;" aria-valuenow="${tauxAnnules}" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
            </div>
        </div>
    </div>

    <div class