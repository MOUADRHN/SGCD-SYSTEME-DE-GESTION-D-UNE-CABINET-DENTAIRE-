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

    <div class="card mb-3">
        <div class="card-body py-2">
            <form method="get" action="${pageContext.request.contextPath}/admin/utilisateurs" class="d-flex gap-2">
                <input type="text" name="q" class="form-control"
                       placeholder="Rechercher par nom, prénom, login, rôle…"
                       value="<c:out value='${searchQuery}'/>">
                <button class="btn btn-teal px-4">
                    <i class="bi bi-search"></i>
                </button>
                <a href="${pageContext.request.contextPath}/admin/utilisateurs" class="btn btn-outline-secondary">
                    Réinitialiser
                </a>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                    <tr>
                        <th>Utilisateur</th>
                        <th>Email / Login</th>
                        <th>Rôle</th>
                        <th>Statut</th>
                        <th class="text-center">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty utilisateurs}">
                            <tr>
                                <td colspan="5" class="text-center text-muted py-5">
                                    <i class="bi bi-person-x d-block fs-1 mb-2"></i>
                                    Aucun utilisateur trouvé <c:if test="${not empty searchQuery}">pour "${searchQuery}"</c:if>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="u" items="${utilisateurs}">
                                <tr>
                                    <td class="align-middle">
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="avatar-circle" style="font-size:.72rem; width:32px; height:32px; display:flex; align-items:center; justify-content:center; background:var(--teal-light); color:var(--teal-dark); border-radius:50%; font-weight:bold;">
                                                    ${u.prenom.charAt(0)}${u.nom.charAt(0)}
                                            </div>
                                            <div>
                                                <div class="fw-semibold text-dark">${u.nomComplet}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="align-middle">
                                        <div class="fw-normal">${u.email}</div>
                                        <div class="text-muted small">${u.login}</div>
                                    </td>
                                    <td class="align-middle">
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
                                    <td class="align-middle">
                                        <span class="badge ${u.statut.name() eq 'ACTIF' ? 'bg-success' : 'bg-secondary'}">
                                                ${u.statut.libelle}
                                        </span>
                                    </td>
                                    <td class="align-middle">
                                        <div class="d-flex gap-1 justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/utilisateurs?action=edit&id=${u.idUtilisateur}"
                                               class="btn btn-sm btn-outline-primary" title="Modifier">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <form method="post" action="${pageContext.request.contextPath}/admin/utilisateurs"
                                                  onsubmit="return confirm('Voulez-vous vraiment changer le statut de cet utilisateur ?')">
                                                <input type="hidden" name="action" value="toggle">
                                                <input type="hidden" name="id" value="${u.idUtilisateur}">
                                                <button type="submit" class="btn btn-sm ${u.statut.name() eq 'ACTIF' ? 'btn-outline-warning' : 'btn-outline-success'}"
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
        <div class="card-footer text-muted small">
            ${utilisateurs.size()} utilisateur(s) au total
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp"/>