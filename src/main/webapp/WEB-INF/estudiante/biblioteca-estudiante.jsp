<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Biblioteca de Materiales - OwlShare</title>
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

<header class="bg-white shadow-sm h-16 flex justify-between items-center px-8 sticky top-0 z-40">
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
    <div class="flex items-center gap-4">
        <span class="text-sm text-slate-600">Hola, <strong><c:out value="${requestScope.estudiantePerfil.nombre}"/></strong></span>
        <a href="${pageContext.request.contextPath}/estudiante/inicio" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">home</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<main class="flex-1 p-10">
    <div class="max-w-6xl mx-auto">

        <div class="mb-8">
            <h2 class="text-4xl font-extrabold text-on-surface">Biblioteca de Materiales</h2>
            <p class="text-slate-600 mt-2">
                Materiales aprobados de tu carrera
                <c:if test="${not empty carreraFiltrada}">
                    — <strong><c:out value="${carreraFiltrada}"/></strong>
                </c:if>
            </p>
        </div>

        <c:if test="${not empty exito}">
            <div class="mb-6 bg-green-50 border border-green-200 text-green-800 rounded-lg px-4 py-3 text-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-sm">check_circle</span>
                <c:out value="${exito}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="mb-6 bg-red-50 border border-red-200 text-red-800 rounded-lg px-4 py-3 text-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-sm">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <%-- Búsqueda por título (dentro de la carrera del estudiante) --%>
        <c:if test="${not sinCarrera}">
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-8">
                <div class="flex items-center gap-2 mb-4 text-sm text-slate-600">
                    <span class="material-symbols-outlined text-primary text-base">school</span>
                    <span>Filtrado automáticamente por tu carrera: <strong><c:out value="${carreraFiltrada}"/></strong></span>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/estudiante/biblioteca"
                      class="flex flex-wrap items-end gap-4">
                    <div class="flex-1 min-w-[220px]">
                        <label for="busqueda" class="block text-sm font-bold text-on-surface mb-2">Buscar por título</label>
                        <input id="busqueda" name="busqueda" type="text"
                               value="<c:out value="${param.busqueda}"/>"
                               placeholder="Ej. Cálculo, Programación..."
                               class="w-full bg-slate-50 border border-slate-200 rounded-lg py-3 px-4 focus:ring-2 focus:ring-primary focus:border-transparent text-on-surface placeholder:text-slate-400"/>
                    </div>
                    <button type="submit"
                            class="flex items-center gap-2 bg-primary text-white font-bold py-3 px-6 rounded-lg hover:opacity-90 transition-opacity">
                        <span class="material-symbols-outlined text-sm">search</span>
                        Buscar
                    </button>
                    <c:if test="${busquedaActiva}">
                        <a href="${pageContext.request.contextPath}/estudiante/biblioteca"
                           class="flex items-center gap-1 text-slate-500 font-semibold text-sm hover:text-primary py-3 px-2">
                            <span class="material-symbols-outlined text-sm">close</span>
                            Limpiar búsqueda
                        </a>
                    </c:if>
                </form>
            </div>
        </c:if>

        <%-- Contador de resultados --%>
        <c:if test="${not empty materiales}">
            <p class="text-sm text-slate-500 mb-6">
                Mostrando <strong><c:out value="${fn:length(materiales)}"/></strong> material(es)
                <c:if test="${not empty carreraFiltrada}">
                    de <strong><c:out value="${carreraFiltrada}"/></strong>
                </c:if>
            </p>
        </c:if>

        <%-- Listado de materiales --%>
        <c:choose>
            <c:when test="${empty materiales}">
                <div class="text-center py-20 bg-white rounded-xl shadow">
                    <span class="material-symbols-outlined text-5xl text-slate-300 block mb-4">menu_book</span>
                    <p class="text-slate-600 font-semibold text-lg">No hay materiales disponibles</p>
                    <p class="text-sm text-slate-400 mt-2">
                        <c:choose>
                            <c:when test="${sinCarrera}">
                                Actualiza tu perfil o contacta al administrador para registrar tu carrera.
                            </c:when>
                            <c:when test="${busquedaActiva}">
                                No hay materiales que coincidan con tu búsqueda en <c:out value="${carreraFiltrada}"/>.
                            </c:when>
                            <c:otherwise>
                                Aún no hay materiales aprobados para tu carrera. ¡Vuelve pronto!
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <c:forEach var="material" items="${materiales}">
                        <div class="bg-white rounded-xl shadow border border-slate-200 flex flex-col overflow-hidden hover:shadow-md transition-shadow">

                            <div class="bg-indigo-50 p-6 flex items-center justify-center">
                                <span class="material-symbols-outlined text-primary text-5xl">picture_as_pdf</span>
                            </div>

                            <div class="p-6 flex flex-col flex-1">
                                <div class="flex-1">
                                    <span class="text-xs font-bold uppercase tracking-widest text-slate-400 block mb-1">
                                        <c:out value="${material.nombreMateria}"/>
                                    </span>
                                    <h3 class="text-lg font-bold text-on-surface mb-2 leading-snug">
                                        <c:out value="${material.titulo}"/>
                                    </h3>
                                    <p class="text-sm text-slate-500 mb-4" style="display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden">
                                        <c:out value="${material.descripcion}"/>
                                    </p>
                                    <div class="flex items-center justify-between text-xs text-slate-400 mb-4">
                                        <span class="flex items-center gap-1">
                                            <span class="material-symbols-outlined text-sm">person</span>
                                            <c:out value="${material.nombreTutor}"/>
                                        </span>
                                        <span class="flex items-center gap-1">
                                            <span class="material-symbols-outlined text-sm">calendar_today</span>
                                            <c:out value="${material.fechaEnvio}"/>
                                        </span>
                                    </div>
                                </div>

                                <div class="border-t border-slate-100 pt-4 flex items-center justify-between">
                                    <span class="text-2xl font-extrabold text-primary">
                                        <c:choose>
                                            <c:when test="${material.gratis}">Gratis</c:when>
                                            <c:otherwise>$<c:out value="${material.costoFormateado}"/></c:otherwise>
                                        </c:choose>
                                    </span>

                                    <c:choose>
                                        <c:when test="${material.adquirido}">
                                            <a href="${pageContext.request.contextPath}/estudiante/biblioteca/descargar?idMaterial=${material.id}"
                                               class="flex items-center gap-1 text-green-700 font-bold text-sm bg-green-50 border border-green-200 px-4 py-2 rounded-lg hover:bg-green-100 transition-colors">
                                                <span class="material-symbols-outlined text-sm">download</span>
                                                Descargar
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="post" action="${pageContext.request.contextPath}/estudiante/biblioteca/adquirir"
                                                  onsubmit="return confirm('¿Confirmar adquisición de este material?');">
                                                <input type="hidden" name="idMaterial" value="${material.id}"/>
                                                <button type="submit"
                                                        class="flex items-center gap-1 bg-primary text-white font-bold text-sm px-4 py-2 rounded-lg hover:opacity-90 transition-opacity">
                                                    <span class="material-symbols-outlined text-sm">shopping_cart</span>
                                                    Adquirir
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

</body>
</html>
