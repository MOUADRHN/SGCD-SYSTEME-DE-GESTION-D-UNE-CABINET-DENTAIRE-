<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"  value="${pageContext.request.contextPath}"/>
<c:set var="u"    value="${sessionScope.utilisateur}"/>
<c:set var="path" value="${pageContext.request.servletPath}"/>

<div class="sidebar d-flex flex-column">
    <div class="sidebar-brand">
        <h5><i class="bi bi-tooth me-2"></i>SGCD</h5>
        <small>Cabinet Dentaire</small>
    </div>
    <nav class="nav flex-column mt-3 flex-grow-1">
        <a class="nav-link ${path eq '/dashboard' ? 'active':''}" href="${ctx}/dashboard">
            <i class="bi bi-speedometer2"></i> Tableau de bord
        </a>
        <a class="nav-link ${path eq '/patients' ? 'active':''}" href="${ctx}/patients">
            <i class="bi bi-people-fill"></i> Patients
        </a>
        <c:if test="${u.role.name() ne 'DENTISTE' and u.role.name() ne 'ADMINISTRATEUR'}">
            <a class="nav-link ${path eq '/rdv' ? 'active':''}" href="${ctx}/rdv">
                <i class="bi bi-calendar3"></i> Rendez-vous
            </a>
        </c:if>
        <c:if test="${u.role.name() eq 'DENTISTE'}">
            <a class="nav-link" href="${ctx}/rdv">
                <i class="bi bi-clipboard2-pulse"></i> Planing
            </a>
        </c:if>
        <c:if test="${u.role.name() eq 'ASSISTANTE'}">
            <a class="nav-link ${path eq '/facture' ? 'active':''}" href="${ctx}/facture">
                <i class="bi bi-receipt-cutoff"></i> Facturation
            </a>
        </c:if>
        <c:if test="${u.role.name() eq 'ADMINISTRATEUR'}">
            <div class="nav-link text-white-50 mt-2" style="font-size:.72rem;padding:.3rem 1.2rem;">ADMINISTRATION</div>
            <a class="nav-link ${path eq '/admin/utilisateurs' ? 'active':''}" href="${ctx}/admin/utilisateurs">
                <i class="bi bi-person-gear"></i> Utilisateurs
            </a>
            <a class="nav-link ${path eq '/statistiques' ? 'active':''}" href="${ctx}/statistiques">
                <i class="bi bi-bar-chart-line"></i> Statistiques
            </a>
        </c:if>
    </nav>
    <div class="p-3 border-top border-secondary mt-auto">
        <div class="d-flex align-items-center gap-2 mb-2">
            <div class="avatar-circle" style="font-size:.7rem;">
                ${u.prenom.charAt(0)}${u.nom.charAt(0)}
            </div>
            <div>
                <div class="text-white" style="font-size:.82rem;font-weight:600;">${u.nomComplet}</div>
                <div style="font-size:.68rem;color:var(--mint);">${u.role.libelle}</div>
            </div>
        </div>
        <a href="${ctx}/logout" class="btn btn-sm btn-outline-light w-100">
            <i class="bi bi-box-arrow-right me-1"></i>Déconnexion
        </a>
    </div>
</div>
