<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Mis Materiales - OwlShare</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: {
                "primary": "#24389c", "primary-container": "#3f51b5", "on-primary": "#ffffff",
                "surface": "#f7f9fc", "on-surface": "#191c1e", "secondary": "#006a60"
            }}}
        }
    </script>
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
        <a href="${pageContext.request.contextPath}/tutor/dashboard" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">home</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<%-- Main Content --%>
<main class="flex-1 p-10">
    <div class="max-w-6xl mx-auto space-y-8">

        <%-- Encabezado --%>
        <div class="flex justify-between items-center mb-8">
            <div>
                <h2 class="text-4xl font-extrabold text-on-surface mb-2">Mis Materiales</h2>
                <p class="text-slate-600">Visualiza y gestiona tus materiales académicos publicados.</p>
            </div>
            <a href="${pageContext.request.contextPath}/tutor/subir"
               class="flex items-center gap-2 bg-primary text-white px-6 py-3 rounded-lg font-bold shadow-lg hover:bg-primary-container transition-all">
                <span class="material-symbols-outlined">add</span>
                Nuevo Material
            </a>
        </div>

        <%-- Estadísticas (Bento Style) --%>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="bg-white rounded-xl shadow p-6 border border-slate-200">
                <div class="flex justify-between items-start mb-4">
                    <span class="material-symbols-outlined text-primary">description</span>
                    <span class="text-xs bg-secondary/20 text-secondary font-bold px-3 py-1 rounded-full">+4 este mes</span>
                </div>
                <p class="text-4xl font-bold text-on-surface mb-1">
                    <c:out value="${totalMateriales != null ? totalMateriales : 0}"/>
                </p>
                <p class="text-sm text-slate-500 font-medium">Total de Materiales</p>
            </div>

            <div class="bg-white rounded-xl shadow p-6 border border-slate-200">
                <div class="flex justify-between items-start mb-4">
                    <span class="material-symbols-outlined text-green-600">verified_user</span>
                </div>
                <p class="text-4xl font-bold text-on-surface mb-1">
                    <c:out value="${materialesAprobados != null ? materialesAprobados : 0}"/>
                </p>
                <p class="text-sm text-slate-500 font-medium">Aprobados</p>
            </div>

            <div class="bg-white rounded-xl shadow p-6 border border-slate-200">
                <div class="flex justify-between items-start mb-4">
                    <span class="material-symbols-outlined text-amber-600">schedule</span>
                </div>
                <p class="text-4xl font-bold text-on-surface mb-1">
                    <c:out value="${materialesEnRevision != null ? materialesEnRevision : 0}"/>
                </p>
                <p class="text-sm text-slate-500 font-medium">En Revisión</p>
            </div>
        </div>

        <%-- Tabla de Materiales --%>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div class="px-8 py-6 border-b border-slate-200">
                <h3 class="text-xl font-bold text-on-surface mb-1">Catálogo de Archivos</h3>
                <p class="text-sm text-slate-600">Visualiza el estado y detalles de tus materiales publicados.</p>
            </div>

            <div class="overflow-x-auto">
                <c:choose>
                    <c:when test="${not empty materiales}">
                        <table class="w-full text-left">
                            <thead class="bg-slate-50 border-b border-slate-200">
                                <tr>
                                    <th class="px-8 py-4 text-xs font-bold text-slate-600 uppercase tracking-widest">Material</th>
                                    <th class="px-8 py-4 text-xs font-bold text-slate-600 uppercase tracking-widest">Publicación</th>
                                    <th class="px-8 py-4 text-xs font-bold text-slate-600 uppercase tracking-widest">Estado</th>
                                    <th class="px-8 py-4 text-xs font-bold text-slate-600 uppercase tracking-widest">Costo</th>
                                    <th class="px-8 py-4 text-xs font-bold text-slate-600 uppercase tracking-widest">Acciones</th>
                                </tr>
                            </thead>
                            <tbody class="divide-y divide-slate-200">
                                <c:forEach var="material" items="${materiales}">
                                    <tr class="hover:bg-slate-50 transition-colors">
                                        <td class="px-8 py-5">
                                            <div class="flex items-center gap-4">
                                                <div class="w-10 h-10 bg-red-100 text-red-600 flex items-center justify-center rounded-lg flex-shrink-0">
                                                    <span class="material-symbols-outlined">picture_as_pdf</span>
                                                </div>
                                                <div>
                                                    <p class="font-semibold text-on-surface"><c:out value="${material.titulo}"/></p>
                                                    <p class="text-xs text-slate-500"><c:out value="${material.nombreMateria}"/></p>
                                                </div>
                                            </div>
                                        </td>
                                        <td class="px-8 py-5">
                                            <span class="text-sm text-slate-600"><c:out value="${material.fechaEnvio}"/></span>
                                        </td>
                                        <td class="px-8 py-5">
                                            <c:choose>
                                                <c:when test="${material.estado == 'aprobado'}">
                                                    <span class="badge-aceptada text-xs font-bold px-3 py-1 rounded-full inline-block">Aprobado</span>
                                                </c:when>
                                                <c:when test="${material.estado == 'pendiente'}">
                                                    <span class="badge-pendiente text-xs font-bold px-3 py-1 rounded-full inline-block">En Revisión</span>
                                                </c:when>
                                                <c:when test="${material.estado == 'rechazado'}">
                                                    <span class="badge-rechazada text-xs font-bold px-3 py-1 rounded-full inline-block">Rechazado</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td class="px-8 py-5">
                                            <span class="text-sm font-semibold text-on-surface">$<c:out value="${material.costo}"/></span>
                                        </td>
                                        <td class="px-8 py-5">
                                            <a href="${pageContext.request.contextPath}/tutor/material/${material.id}"
                                               class="flex items-center gap-1.5 text-sm font-semibold text-primary hover:text-primary-container transition-colors">
                                                <span class="material-symbols-outlined text-sm">visibility</span>
                                                Ver detalles
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <%-- Paginación --%>
                        <div class="px-8 py-4 border-t border-slate-200 flex items-center justify-between bg-slate-50">
                            <p class="text-xs text-slate-600 font-medium">
                                Mostrando materiales de <strong><c:out value="${requestScope.tutorPerfil.nombre}"/></strong>
                            </p>
                            <div class="flex gap-2">
                                <button class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-slate-600 hover:bg-white transition-all">
                                    <span class="material-symbols-outlined text-sm">chevron_left</span>
                                </button>
                                <button class="w-8 h-8 flex items-center justify-center rounded bg-primary text-white font-bold text-xs">1</button>
                                <button class="w-8 h-8 flex items-center justify-center rounded border border-slate-200 text-slate-600 hover:bg-white transition-all">
                                    <span class="material-symbols-outlined text-sm">chevron_right</span>
                                </button>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="px-8 py-16 border-t border-slate-200 text-center">
                            <span class="material-symbols-outlined text-5xl text-slate-300 block mb-4">folder_open</span>
                            <h3 class="text-xl font-bold text-slate-600 mb-2">No hay materiales publicados</h3>
                            <p class="text-slate-500 mb-6">Aún no has publicado ningún material académico. Comienza compartiendo tu conocimiento.</p>
                            <a href="${pageContext.request.contextPath}/tutor/subir"
                               class="inline-flex items-center gap-2 bg-primary text-white px-6 py-3 rounded-lg font-bold hover:bg-primary-container transition-all">
                                <span class="material-symbols-outlined">add</span>
                                Publicar mi primer material
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

</body>
</html>
