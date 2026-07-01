<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Panel de Administración - OwlShare</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = { theme: { extend: { colors: {
            "primary": "#24389c", "surface": "#f7f9fc", "on-surface": "#191c1e"
        }}}}
    </script>
</head>
<body class="bg-surface text-on-surface min-h-screen flex flex-col">

<header class="bg-white shadow-sm h-16 flex justify-between items-center px-8 sticky top-0 z-40">
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare · Admin</h1>
    <div class="flex items-center gap-4">
        <span class="text-sm text-slate-600">Hola, <strong><c:out value="${adminNombre}"/></strong></span>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<main class="flex-1 p-10">
    <div class="max-w-4xl mx-auto">
        <h2 class="text-4xl font-extrabold mb-2">Panel de administración</h2>
        <p class="text-slate-600 mb-8">Gestiona la revisión de materiales académicos publicados por tutores.</p>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <a href="${pageContext.request.contextPath}/admin/materiales"
               class="bg-white rounded-xl shadow p-8 hover:shadow-lg transition-all group border border-slate-200">
                <div class="flex items-start justify-between mb-4">
                    <span class="material-symbols-outlined text-amber-600 text-4xl">rate_review</span>
                    <span class="text-2xl font-bold text-primary">
                        <c:out value="${materialesPendientes != null ? materialesPendientes : 0}"/>
                    </span>
                </div>
                <h3 class="text-xl font-bold group-hover:text-primary transition-colors">Revisar materiales</h3>
                <p class="text-sm text-slate-600 mt-2">Aprueba o rechaza materiales pendientes de revisión.</p>
            </a>
        </div>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">© 2025 OwlShare</footer>
</body>
</html>
