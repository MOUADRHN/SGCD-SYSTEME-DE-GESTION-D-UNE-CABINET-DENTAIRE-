<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SGCD – <c:out value="${pageTitle != null ? pageTitle : 'Cabinet Dentaire'}"/></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        :root { --teal:#028090; --teal-dark:#0D3B4E; --mint:#02C39A; }
        body { background:#f4f7f9; font-family:'Segoe UI',sans-serif; }
        .sidebar {
            width:240px; min-height:100vh; background:var(--teal-dark);
            position:fixed; top:0; left:0; z-index:100;
        }
        .sidebar-brand { padding:1.2rem 1rem; border-bottom:1px solid rgba(255,255,255,.1); }
        .sidebar-brand h5 { color:#fff; margin:0; font-weight:700; }
        .sidebar-brand small { color:var(--mint); font-size:.72rem; }
        .sidebar .nav-link {
            color:rgba(255,255,255,.75); padding:.55rem 1.2rem; border-radius:6px;
            margin:2px 8px; display:flex; align-items:center; gap:10px; transition:all .15s;
        }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background:var(--teal); color:#fff; }
        .main-content { margin-left:240px; padding:1.5rem; }
        .topbar {
            background:#fff; border-bottom:3px solid var(--teal);
            padding:.6rem 1.5rem; margin:-1.5rem -1.5rem 1.5rem;
            display:flex; justify-content:space-between; align-items:center;
        }
        .topbar h4 { margin:0; color:var(--teal-dark); font-weight:700; }
        .card { border:none; border-radius:12px; box-shadow:0 2px 12px rgba(0,0,0,.07); }
        .btn-teal { background:var(--teal); color:#fff; border:none; }
        .btn-teal:hover { background:#026d7a; color:#fff; }
        .btn-mint  { background:var(--mint); color:#fff; border:none; }
        .btn-mint:hover { background:#01a882; color:#fff; }
        .table th { background:var(--teal-dark); color:#fff; font-weight:500; }
        .table-hover tbody tr:hover { background:#e8f5f7; }
        .stat-card .icon-box {
            width:52px; height:52px; border-radius:12px;
            display:flex; align-items:center; justify-content:center; font-size:1.5rem;
        }
        .avatar-circle {
            width:36px; height:36px; border-radius:50%; background:var(--teal); color:#fff;
            display:flex; align-items:center; justify-content:center; font-weight:700;
        }
        .flash-banner { border-radius:8px; padding:.75rem 1rem; margin-bottom:1rem; }
        @media print {
            .sidebar, .topbar, .btn, form { display:none !important; }
            .main-content { margin:0 !important; padding:0 !important; }
        }
    </style>
</head>
<body>
