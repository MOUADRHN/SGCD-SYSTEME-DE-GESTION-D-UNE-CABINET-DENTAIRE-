<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SGCD — Connexion</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        :root{--teal:#028090;--teal-dark:#0D3B4E;--mint:#02C39A;}
        body{background:linear-gradient(135deg,#0D3B4E 0%,#028090 60%,#02C39A 100%);min-height:100vh;display:flex;align-items:center;justify-content:center;}
        .login-card{width:100%;max-width:430px;border:none;border-radius:18px;box-shadow:0 24px 60px rgba(0,0,0,.3);}
        .login-header{background:var(--teal-dark);color:#fff;border-radius:18px 18px 0 0;padding:2rem;text-align:center;}
        .login-header h4{font-weight:800;margin:.5rem 0 .2rem;}
        .login-header small{color:var(--mint);font-size:.8rem;}
        .tooth-icon{font-size:3.5rem;color:var(--mint);}
        .form-control:focus{border-color:var(--teal);box-shadow:0 0 0 .2rem rgba(2,128,144,.2);}
        .btn-login{background:var(--teal);color:#fff;border:none;padding:.75rem;font-weight:700;border-radius:10px;font-size:1rem;}
        .btn-login:hover{background:#026d7a;color:#fff;}
        .badge-role{font-size:.68rem;background:var(--mint);color:#fff;padding:.2em .6em;border-radius:20px;margin-left:.3rem;}
    </style>
</head>
<body>
<div class="login-card card">
    <div class="login-header">
        <div class="tooth-icon"><i class="bi bi-tooth"></i></div>
        <h4>SGCD</h4>
        <small>Système de Gestion d'un Cabinet Dentaire — FST Fès</small>
    </div>
    <div class="card-body p-4">
        <c:if test="${not empty error}">
            <div class="alert alert-danger py-2 mb-3">
                <i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}"/>
            </div>
        </c:if>
        <h6 class="text-center text-muted mb-3">Connectez-vous à votre espace</h6>
        <form action="${pageContext.request.contextPath}/auth" method="post">
            <div class="mb-3">
                <label class="form-label fw-semibold">Identifiant (email)</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                    <input type="text" name="login" class="form-control" placeholder="votre@email.ma"
                           required autofocus autocomplete="username">
                </div>
            </div>
            <div class="mb-4">
                <label class="form-label fw-semibold">Mot de passe</label>
                <div class="input-group">
                    <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
                    <input type="password" name="password" class="form-control" placeholder="••••••••"
                           required autocomplete="current-password">
                </div>
            </div>
            <button type="submit" class="btn btn-login w-100">
                <i class="bi bi-box-arrow-in-right me-2"></i>Se connecter
            </button>
        </form>
        <hr class="my-3">
        <div class="text-muted" style="font-size:.78rem;">
            <strong>Comptes de test :</strong><br>
            h.fassi@sgcd.ma <span class="badge-role">Admin</span><br>
            r.mansouri@sgcd.ma <span class="badge-role">Dentiste</span><br>
            s.alaoui@sgcd.ma <span class="badge-role">Assistante</span><br>
            <span class="mt-1 d-block">Mot de passe : <code>sgcd1234</code></span>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
