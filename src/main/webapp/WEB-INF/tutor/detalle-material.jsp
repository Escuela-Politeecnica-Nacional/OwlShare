<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Detalle del Material - OwlShare</title>
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
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
    <div class="flex items-center gap-4">
        <a href="${pageContext.request.contextPath}/tutor/materiales" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">arrow_back</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<main class="flex-1 p-10">
    <div class="max-w-3xl mx-auto">
        <h2 class="text-3xl font-extrabold mb-6">Detalle del material</h2>

        <div class="bg-white rounded-xl shadow border border-slate-200 p-8 space-y-6">
            <div class="flex items-start justify-between gap-4">
                <div>
                    <span class="text-xs font-bold uppercase text-slate-400 tracking-widest">
                        <c:out value="${material.nombreMateria}"/>
                    </span>
                    <h3 class="text-2xl font-bold mt-1"><c:out value="${material.titulo}"/></h3>
                </div>
                <c:choose>
                    <c:when test="${material.estado == 'aprobado'}">
                        <span class="badge-aceptada text-xs font-bold px-3 py-1 rounded-full"><c:out value="${material.estadoEtiqueta}"/></span>
                    </c:when>
                    <c:when test="${material.estado == 'pendiente'}">
                        <span class="badge-pendiente text-xs font-bold px-3 py-1 rounded-full"><c:out value="${material.estadoEtiqueta}"/></span>
                    </c:when>
                    <c:when test="${material.estado == 'rechazado'}">
                        <span class="badge-rechazada text-xs font-bold px-3 py-1 rounded-full"><c:out value="${material.estadoEtiqueta}"/></span>
                    </c:when>
                </c:choose>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Publicación</span>
                    <p class="font-semibold mt-1"><c:out value="${material.fechaEnvio}"/></p>
                </div>
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Precio</span>
                    <p class="font-semibold mt-1">
                        <c:choose>
                            <c:when test="${material.gratis}">Gratis</c:when>
                            <c:otherwise>$<c:out value="${material.costoFormateado}"/></c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Archivo</span>
                    <p class="font-semibold mt-1"><c:out value="${material.nombreArchivo}"/></p>
                </div>
                <c:if test="${not empty material.fechaRevision}">
                    <div>
                        <span class="text-xs font-bold uppercase text-slate-500">Última revisión</span>
                        <p class="font-semibold mt-1"><c:out value="${material.fechaRevision}"/></p>
                    </div>
                </c:if>
            </div>

            <div>
                <span class="text-xs font-bold uppercase text-slate-500">Descripción</span>
                <p class="text-slate-700 mt-2 leading-relaxed whitespace-pre-line"><c:out value="${material.descripcion}"/></p>
            </div>

            <c:if test="${not empty material.comentarioRevision}">
                <div class="bg-red-50 border border-red-200 rounded-lg p-4">
                    <span class="text-xs font-bold uppercase text-red-700">Motivo del rechazo</span>
                    <p class="text-sm text-red-800 mt-2"><c:out value="${material.comentarioRevision}"/></p>
                </div>
            </c:if>

            <div class="flex flex-wrap gap-4 pt-4 border-t border-slate-100">
                <a href="${pageContext.request.contextPath}/tutor/materiales"
                   class="text-primary font-bold hover:underline">Volver al listado</a>
                <c:if test="${material.eliminable}">
                    <form method="post" action="${pageContext.request.contextPath}/tutor/materiales/eliminar"
                          onsubmit="return confirm('¿Eliminar este material?');" class="inline">
                        <input type="hidden" name="idMaterial" value="${material.id}"/>
                        <button type="submit" class="text-red-600 font-bold hover:underline">Eliminar material</button>
                    </form>
                </c:if>
            </div>
        </div>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">© 2025 OwlShare</footer>
</body>
</html>
