<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Panel del Tutor - OwlShare</title>
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
        <span class="text-sm text-slate-600">Hola, <strong><c:out value="${requestScope.tutorPerfil.nombre}"/></strong></span>
        <a href="${pageContext.request.contextPath}/tutor/perfil" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">settings</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<%-- Main Content --%>
<main class="flex-1 p-10">
    <div class="max-w-4xl mx-auto">
        <h2 class="text-4xl font-extrabold text-on-surface mb-2">Panel del Tutor</h2>
        <p class="text-slate-600 mb-8">Bienvenido, <c:out value="${requestScope.tutorPerfil.nombre}"/>. Gestiona tus sesiones y materiales aquí.</p>

        <%-- Estadísticas --%>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-10">
            <div class="bg-white rounded-xl shadow p-6">
                <span class="text-xs font-bold uppercase text-slate-500">Materiales Aprobados</span>
                <p class="text-3xl font-bold text-primary mt-2">
                    <c:out value="${materialesAprobados != null ? materialesAprobados : 0}"/>
                </p>
            </div>
            <div class="bg-white rounded-xl shadow p-6">
                <span class="text-xs font-bold uppercase text-slate-500">Materiales Pendientes</span>
                <p class="text-3xl font-bold text-primary mt-2">
                    <c:out value="${materialesPendientes != null ? materialesPendientes : 0}"/>
                </p>
            </div>
        </div>

        <%-- Próximas Tutorías --%>
        <div class="bg-white rounded-xl shadow p-8">
            <h3 class="text-2xl font-bold text-on-surface mb-6 flex items-center gap-2">
                <span class="material-symbols-outlined">calendar_today</span>
                Próximas Sesiones
            </h3>

            <c:choose>
                <c:when test="${not empty sesiones}">
                    <div class="space-y-4">
                        <c:forEach var="sesion" items="${sesiones}">
                            <div class="flex justify-between items-center p-4 border border-slate-200 rounded-lg hover:bg-slate-50">
                                <div>
                                    <h4 class="font-bold text-on-surface"><c:out value="${sesion.nombreEstudiante}"/></h4>
                                    <p class="text-sm text-slate-600"><c:out value="${sesion.tema}"/></p>
                                </div>
                                <div class="text-right">
                                    <p class="font-bold text-sm"><c:out value="${sesion.fecha}"/></p>
                                    <p class="text-xs text-slate-500"><c:out value="${sesion.duracion}"/></p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-slate-600 text-center py-8">No hay sesiones programadas próximamente.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>
</body>
</html>
