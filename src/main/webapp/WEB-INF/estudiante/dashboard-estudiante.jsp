<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Dashboard del Estudiante - OwlShare</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: {
                "primary": "#24389c", "primary-container": "#3f51b5", "on-primary": "#ffffff",
                "surface": "#f7f9fc", "on-surface": "#191c1e"
            }}}
        }
    </script>
    <style>
        body { font-family: 'Inter', sans-serif; }
        h1, h2 { font-family: 'Manrope', sans-serif; }
    </style>
</head>
<body class="bg-surface text-on-surface min-h-screen flex flex-col">

<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>

<%-- Header --%>
<header class="bg-white shadow-sm h-16 flex justify-between items-center px-8 sticky top-0 z-40">
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
    <div class="flex items-center gap-4">
        <span class="text-sm text-slate-600">Hola, <strong><c:out value="${requestScope.estudiantePerfil.nombre}"/></strong></span>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<%-- Main Content --%>
<main class="flex-1 p-10">
    <div class="max-w-4xl mx-auto">
        <h2 class="text-4xl font-extrabold text-on-surface mb-2">Hola, <c:out value="${requestScope.estudiantePerfil.nombre}"/></h2>
        <p class="text-slate-600 mb-8">Bienvenido a tu espacio de aprendizaje.</p>

        <div class="bg-white rounded-xl shadow p-8">
            <h3 class="text-2xl font-bold text-primary mb-4 flex items-center gap-2">
                <span class="material-symbols-outlined">person_search</span>
                Buscar Tutor
            </h3>
            <p class="text-slate-600 mb-6">Encuentra al tutor ideal para resolver tus dudas.</p>
            <a href="${pageContext.request.contextPath}/estudiante/buscar-tutor"
               class="inline-block bg-primary text-white font-bold py-3 px-6 rounded-xl hover:opacity-90">
                Explorar Tutores
            </a>
        </div>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>
</body>
</html>
