<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="p"         value="${patient}"/>
<c:set var="pageTitle" value="Modifier patient"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>
<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-pencil-square me-2"></i>Modifier : ${p.nomComplet}</h4>
        <a href="${pageContext.request.contextPath}/patients?action=detail&id=${p.idPatient}"
           class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour au dossier
        </a>
    </div>
    <div class="card">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/patients">
                <input type="hidden" name="action"    value="update">
                <input type="hidden" name="idPatient" value="${p.idPatient}">
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Nom</label>
                        <input type="text" name="nom" class="form-control" value="${p.nom}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Prénom</label>
                        <input type="text" name="prenom" class="form-control" value="${p.prenom}" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Date de naissance</label>
                        <input type="date" name="dateNaissance" class="form-control" value="${p.dateNaissance}" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Sexe</label>
                        <select name="sexe" class="form-select">
                            <option value="H" ${p.sexe.name() eq 'H' ? 'selected':''}>Homme</option>
                            <option value="F" ${p.sexe.name() eq 'F' ? 'selected':''}>Femme</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Téléphone</label>
                        <input type="tel" name="telephone" class="form-control" value="${p.telephone}" required>
                    </div>
                    <div class="col-md-5">
                        <label class="form-label fw-semibold">CNSS</label>
                        <input type="text" name="numeroCNSS" class="form-control" value="${p.numeroCNSS}">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Adresse</label>
                        <input type="text" name="adresse" class="form-control" value="${p.adresse}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Allergies</label>
                        <input type="text" name="allergie" class="form-control" value="${p.allergie}">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Antécédents</label>
                        <input type="text" name="antecedents" class="form-control" value="${p.antecedents}">
                    </div>
                </div>
                <c:if test="${p.estMineur()}">
                    <div class="mt-4 p-3 border rounded" style="background:#fffde7;">
                        <h6 class="text-warning fw-bold"><i class="bi bi-person-heart me-1"></i>Responsable légal</h6>
                        <div class="row g-2">
                            <div class="col-md-4">
                                <input type="text" name="rl_nom" class="form-control"
                                       value="${p.responsableLegal.nom}" placeholder="Nom complet">
                            </div>
                            <div class="col-md-4">
                                <input type="tel" name="rl_telephone" class="form-control"
                                       value="${p.responsableLegal.telephone}" placeholder="Téléphone">
                            </div>
                            <div class="col-md-4">
                                <input type="text" name="rl_lienParente" class="form-control"
                                       value="${p.responsableLegal.lienParente}" placeholder="Lien parenté">
                            </div>
                        </div>
                    </div>
                </c:if>
                <div class="mt-4 d-flex gap-2">
                    <button type="submit" class="btn btn-teal px-4 fw-semibold">
                        <i class="bi bi-save me-1"></i>Enregistrer les modifications
                    </button>
                    <a href="${pageContext.request.contextPath}/patients?action=detail&id=${p.idPatient}"
                       class="btn btn-outline-secondary px-4">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
