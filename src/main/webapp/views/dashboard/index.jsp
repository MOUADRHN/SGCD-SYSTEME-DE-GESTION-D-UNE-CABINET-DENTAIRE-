<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Tableau de bord"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-speedometer2 me-2"></i>Tableau de bord</h4>
        <small class="text-muted">
            <jsp:useBean id="now" class="java.util.Date"/>
            <fmt:formatDate value="${now}" pattern="EEEE dd MMMM yyyy" />
        </small>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>

    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="card stat-card h-100">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="icon-box" style="background:#e0f7fa;color:#028090;"><i class="bi bi-people-fill"></i></div>
                    <div>
                        <div class="fw-bold fs-3">${countPatients}</div>
                        <div class="text-muted small">Patients enregistrés</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card stat-card h-100">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="icon-box" style="background:#fff3e0;color:#ef6c00;"><i class="bi bi-calendar-check"></i></div>
                    <div>
                        <div class="fw-bold fs-3">${countRdvToday}</div>
                        <div class="text-muted small">RDV aujourd'hui</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card stat-card h-100">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="icon-box" style="background:#e8f5e9;color:#2e7d32;"><i class="bi bi-clipboard2-pulse"></i></div>
                    <div>
                        <div class="fw-bold fs-3">${countConsultMois}</div>
                        <div class="text-muted small">Consultations ce mois</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card stat-card h-100">
                <div class="card-body d-flex align-items-center gap-3">
                    <div class="icon-box" style="background:#fce4ec;color:#c2185b;"><i class="bi bi-cash-coin"></i></div>
                    <div>
                        <div class="fw-bold fs-4">
                            <fmt:formatNumber value="${caMonth}" maxFractionDigits="0"/> MAD
                        </div>
                        <div class="text-muted small">CA ce mois</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center"
             style="background:var(--teal-dark);color:#fff;">
            <span><i class="bi bi-calendar3 me-2"></i>Agenda du jour</span>
         <c:if test="${sessionScope.utilisateur.role != 'DENTISTE'}">
             <a href="${pageContext.request.contextPath}/rdv?action=add" class="btn btn-mint">
                 <i class="bi bi-calendar-plus me-1"></i>Nouveau RDV
           </a>
         </c:if>
             </div>
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty rdvToday}">
                    <div class="text-center text-muted py-5">
                        <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>Aucun rendez-vous aujourd'hui
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                            <tr>
                                <th>Heure</th>
                                <th>Patient</th>
                                    <%-- Masquer la colonne Dentiste si c'est un Dentiste connecté --%>
                                <c:if test="${sessionScope.utilisateur.role != 'DENTISTE'}">
                                    <th>Dentiste</th>
                                </c:if>
                                <th>Motif</th>
                                <th>Statut</th>
                                <th>Action rapide</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="rv" items="${rdvToday}">
                                <tr>
                                    <td class="fw-bold">${rv.dateHeure.toLocalTime()}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/patients?action=detail&id=${rv.idPatient}"
                                           class="text-decoration-none fw-semibold">
                                                ${rv.nomCompletPatient}
                                        </a>
                                    </td>
                                        <%-- Masquer le nom du Dentiste si c'est un Dentiste connecté --%>
                                    <c:if test="${sessionScope.utilisateur.role != 'DENTISTE'}">
                                        <td class="text-muted">${rv.nomCompletDentiste}</td>
                                    </c:if>
                                    <td><span class="badge bg-secondary">${rv.motif.libelle}</span></td>
                                    <td>
                                        <span class="badge bg-${rv.statut.badgeColor}">
                                                ${rv.statut.libelle}
                                        </span>
                                    </td>
                                    <td>
                                            <%-- 1. Boutons de gestion de file d'attente (Dispo pour tout le monde) --%>
                                        <c:if test="${rv.statut.name() eq 'PLANIFIE'}">
                                            <form method="post" action="${pageContext.request.contextPath}/rdv" class="d-inline">
                                                <input type="hidden" name="action" value="statut">
                                                <input type="hidden" name="id"     value="${rv.idRDV}">
                                                <input type="hidden" name="type"   value="arrivee">
                                                <button class="btn btn-sm btn-warning">
                                                    <i class="bi bi-person-check me-1"></i>Arrivée
                                                </button>
                                            </form>
                                        </c:if>

                                        <c:if test="${rv.statut.name() eq 'EN_SALLE_ATTENTE'}">
                                            <form method="post" action="${pageContext.request.contextPath}/rdv" class="d-inline">
                                                <input type="hidden" name="action" value="statut">
                                                <input type="hidden" name="id"     value="${rv.idRDV}">
                                                <input type="hidden" name="type"   value="encours">
                                                <button class="btn btn-sm btn-primary">
                                                    <i class="bi bi-play-circle me-1"></i>En cours
                                                </button>
                                            </form>
                                        </c:if>

                                            <%-- 2. Logique d'accès à la Consultation (Sécurisée selon le rôle) --%>
                                        <c:if test="${rv.statut.name() eq 'EN_COURS' or rv.statut.name() eq 'TERMINE'}">
                                            <c:choose>
                                                <%-- Si l'utilisateur est le DENTISTE --%>
                                                <c:when test="${sessionScope.utilisateur.role == 'DENTISTE'}">
                                                    <c:choose>
                                                        <c:when test="${rv.statut.name() eq 'TERMINE'}">
                                                            <a href="${pageContext.request.contextPath}/consultation?action=detail&idRdv=${rv.idRDV}"
                                                               class="btn btn-sm btn-info text-white">
                                                                <i class="bi bi-eye"></i> Détails
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${pageContext.request.contextPath}/consultation?action=ouvrir&idRdv=${rv.idRDV}"
                                                               class="btn btn-sm btn-success">
                                                                <i class="bi bi-clipboard2-pulse me-1"></i>Consultation
                                                            </a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>

                                                <%-- Si l'utilisateur est une ASSISTANTE --%>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${rv.statut.name() eq 'TERMINE'}">
                                                            <a href="${pageContext.request.contextPath}/consultation?action=detail&idRdv=${rv.idRDV}"
                                                               class="btn btn-sm btn-info text-white">
                                                                <i class="bi bi-receipt"></i> Détails
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn btn-sm btn-outline-secondary" disabled title="Réservé au dentiste">
                                                                <i class="bi bi-lock-fill"></i> Réservé
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card-footer text-end">
            <a href="${pageContext.request.contextPath}/rdv" class="btn btn-sm btn-outline-secondary">
                Voir tout l'agenda <i class="bi bi-arrow-right"></i>
            </a>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>