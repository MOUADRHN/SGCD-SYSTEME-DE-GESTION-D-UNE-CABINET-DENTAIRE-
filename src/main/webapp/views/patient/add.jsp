<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Ajouter un patient"/>
<jsp:include page="/views/common/header.jsp"/>
<jsp:include page="/views/common/nav.jsp"/>

<div class="main-content">
    <div class="topbar">
        <h4><i class="bi bi-person-plus me-2"></i>Nouveau Patient</h4>
        <a href="${pageContext.request.contextPath}/patients" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i>Retour
        </a>
    </div>
    <div class="card">
        <div class="card-header" style="background:var(--teal-dark);color:#fff;">
            <i class="bi bi-person-vcard me-1"></i>Informations du patient
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/patients">
                <input type="hidden" name="action" value="save">
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Nom <span class="text-danger">*</span></label>
                        <input type="text" name="nom" class="form-control" required placeholder="NOM (majuscules)">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Prénom <span class="text-danger">*</span></label>
                        <input type="text" name="prenom" class="form-control" required placeholder="Prénom">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Date de naissance <span class="text-danger">*</span></label>
                        <input type="date" name="dateNaissance" class="form-control" required id="dn"
                               min="<%= java.time.LocalDate.now().minusYears(120) %>"
                               max="<%= java.time.LocalDate.now() %>">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Sexe <span class="text-danger">*</span></label>
                        <select name="sexe" class="form-select" required>
                            <option value="H">Homme</option>
                            <option value="F">Femme</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Téléphone <span class="text-danger">*</span></label>
                        <input type="tel" name="telephone" class="form-control" required placeholder="06XXXXXXXX"
                               pattern="0[5-7][0-9]{8}">
                    </div>
                    <div class="col-md-5">
                        <label class="form-label fw-semibold">Numéro CNSS</label>
                        <input type="text" name="numeroCNSS" class="form-control" placeholder="Optionnel">
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-semibold">Adresse complète <span class="text-danger">*</span></label>
                        <input type="text" name="adresse" class="form-control" required placeholder="N° Rue, Ville">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Allergies connues</label>
                        <input type="text" name="allergie" class="form-control" placeholder="Ex: Pénicilline, Latex…">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Antécédents médicaux</label>
                        <input type="text" name="antecedents" class="form-control" placeholder="Ex: Diabète, HTA…">
                    </div>
                </div>

                <div id="rl-block" class="mt-4 p-3 border rounded" style="background:#fffde7;display:none;">
                    <h6 class="fw-bold text-warning mb-3">
                        <i class="bi bi-exclamation-triangle-fill me-1"></i>
                        Responsable légal (obligatoire pour les patients mineurs)
                    </h6>
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Nom complet</label>
                            <input type="text" name="rl_nom" class="form-control" placeholder="Nom Prénom">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Téléphone</label>
                            <input type="tel" name="rl_telephone" class="form-control" placeholder="06XXXXXXXX">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Lien de parenté</label>
                            <select name="rl_lienParente" class="form-select">
                                <option value="Père">Père</option>
                                <option value="Mère">Mère</option>
                                <option value="Tuteur légal">Tuteur légal</option>
                                <option value="Autre">Autre</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="mt-4 d-flex gap-2">
                    <button type="submit" class="btn btn-teal px-5 fw-semibold">
                        <i class="bi bi-person-plus me-1"></i>Enregistrer le patient
                    </button>
                    <a href="${pageContext.request.contextPath}/patients" class="btn btn-outline-secondary px-4">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.getElementById('dn').addEventListener('change', function() {
        if (!this.value) return;

        // Calcul précis de l'âge tenant compte des mois/jours
        const birthDate = new Date(this.value);
        const today = new Date();

        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }

        // Validation : Si l'âge est négatif (date dans le futur) ou >= 120
        // Note : On autorise 0 pour les bébés de moins d'un an (si vous voulez > 0 strict, remplacez par "age <= 0")
        if (age < 0 || age >= 120) {
            this.setCustomValidity("L'âge du patient doit être valide et inférieur à 120 ans.");
            this.reportValidity(); // Affiche la bulle d'erreur native
            document.getElementById('rl-block').style.display = 'none';
        } else {
            this.setCustomValidity(""); // Efface l'erreur, le formulaire peut être soumis

            // Affichage du bloc tuteur si mineur
            document.getElementById('rl-block').style.display = age < 18 ? 'block' : 'none';
        }
    });
</script>

<jsp:include page="/views/common/footer.jsp"/>