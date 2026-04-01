<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Gestion des utilisateurs" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-person-gear me-2"></i>Gestion des Utilisateurs</h4>
        <a href="${pageContext.request.contextPath}/admin/utilisateurs?action=add"
           class="btn btn-mint">
            <i class="bi bi-person-plus me-1"></i>Nouvel utilisateur
        </a>
    </div>
    <jsp:include page="/views/common/flash.jsp"/>

    <div class="card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                        <tr><th>Utilisateur</th><th>Email / Login</th><th>Rôle</th><th>Statut</th><th>Actions</th></tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty utilisateurs}">
                                <tr><td colspan="5" class="text-center text-muted py-4">Aucun utilisateur</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="u" items="${utilisateurs}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="avatar-circle" style="font-size:.72rem;">
                                                ${u.prenom.charAt(0)}${u.nom.charAt(0)}
                                            </div>
                                            <div>
                                                <div class="fw-semibold">${u.nomComplet}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div>${u.email}</div>
                                        <div class="text-muted small">${u.login}</div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.role.name() eq 'ADMINISTRATEUR'}">
                                                <span class="badge" style="background:#0D3B4E;">${u.role.libelle}</span>
                                            </c:when>
                                            <c:when test="${u.role.name() eq 'DENTISTE'}">
                                                <span class="badge" style="background:#028090;">${u.role.libelle}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge" style="background:#02C39A;color:#fff;">${u.role.libelle}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge ${u.statut.name() eq 'ACTIF' ? 'bg-success' : 'bg-secondary'}">
                                            ${u.statut.libelle}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="d-flex gap-1">
                                            <a href="${pageContext.request.contextPath}/admin/utilisateurs?action=edit&id=${u.idUtilisateur}"
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <form method="post" action="${pageContext.request.contextPath}/admin/utilisateurs">
                                                <input type="hidden" name="action" value="toggle">
                                                <input type="hidden" name="id" value="${u.idUtilisateur}">
                                                <button class="btn btn-sm ${u.statut.name() eq 'ACTIF' ? 'btn-outline-warning' : 'btn-outline-success'}"
                                                        title="${u.statut.name() eq 'ACTIF' ? 'Désactiver' : 'Activer'}">
                                                    <i class="bi bi-${u.statut.name() eq 'ACTIF' ? 'toggle-on' : 'toggle-off'}"></i>
                                                </button>
                                            </form>
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
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
