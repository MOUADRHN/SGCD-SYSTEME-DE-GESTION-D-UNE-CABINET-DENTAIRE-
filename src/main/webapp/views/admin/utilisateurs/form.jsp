<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- On utilise maintenant userToEdit pour ne pas confondre avec la session --%>
<c:set var="isEdit"    value="${userToEdit != null}" />
<c:set var="pageTitle" value="${isEdit ? 'Modifier utilisateur' : 'Nouvel utilisateur'}" />
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"  />
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-person-gear me-2"></i>${isEdit ? 'Modifier' : 'Créer'} un utilisateur</h4>
        <a href="${pageContext.request.contextPath}/admin/utilisateurs" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour
        </a>
    </div>
    <div class="card" style="max-width:600px;">
        <div class="card-header" style="background:var(--teal-dark);color:#fff;">
            <i class="bi bi-person-badge me-1"></i>${isEdit ? 'Modifier le compte' : 'Nouveau compte utilisateur'}
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/admin/utilisateurs">
                <input type="hidden" name="action" value="${isEdit ? 'update' : 'save'}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="idUtilisateur" value="${userToEdit.idUtilisateur}">
                </c:if>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Nom <span class="text-danger">*</span></label>
                        <input type="text" name="nom" class="form-control" required
                               value="${isEdit ? userToEdit.nom : ''}">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Prénom <span class="text-danger">*</span></label>
                        <input type="text" name="prenom" class="form-control" required
                               value="${isEdit ? userToEdit.prenom : ''}">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Email <span class="text-danger">*</span></label>
                        <input type="email" name="email" class="form-control" required
                               value="${isEdit ? userToEdit.email : ''}">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Login <span class="text-danger">*</span></label>
                        <input type="text" name="login" class="form-control" required
                               value="${isEdit ? userToEdit.login : ''}"
                        ${isEdit ? 'readonly' : ''}>
                        <c:if test="${isEdit}">
                            <div class="form-text">Le login ne peut pas être modifié.</div>
                        </c:if>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">
                            Mot de passe ${isEdit ? '(laisser vide = inchangé)' : ''} <span class="text-danger">${isEdit ? '' : '*'}</span>
                        </label>
                        <input type="password" name="password" class="form-control"
                        ${isEdit ? '' : 'required'} placeholder="••••••••"
                               autocomplete="new-password">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Rôle <span class="text-danger">*</span></label>
                        <select name="role" class="form-select" required>
                            <c:forEach var="r" items="${roles}">
                                <option value="${r.name()}"
                                    ${isEdit and userToEdit.role.name() eq r.name() ? 'selected' : ''}>
                                        ${r.libelle}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-teal px-4">
                        <i class="bi bi-save me-1"></i>${isEdit ? 'Enregistrer les modifications' : 'Créer le compte'}
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/utilisateurs"
                       class="btn btn-outline-secondary px-4">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>