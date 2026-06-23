<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Perfil del Tutor - OwlShare</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: {
                "primary": "#24389c", "primary-container": "#3f51b5", "on-primary": "#ffffff",
                "surface": "#f7f9fc", "on-surface": "#191c1e", "secondary": "#006a60"
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
    <div class="flex items-center gap-3">
        <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
    </div>
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
    <div class="max-w-3xl mx-auto">
        <h2 class="text-4xl font-extrabold text-on-surface mb-8">Mi Perfil</h2>

        <%-- Información actual --%>
        <div class="bg-white rounded-xl shadow p-8 mb-8">
            <h3 class="text-2xl font-bold text-primary mb-6">Información Actual</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Nombre</span>
                    <p class="text-lg font-semibold text-on-surface mt-2">
                        <c:out value="${requestScope.tutorPerfil.nombre}"/>
                        <c:if test="${not empty requestScope.tutorPerfil.apellido}">
                            <c:out value=" ${requestScope.tutorPerfil.apellido}"/>
                        </c:if>
                    </p>
                </div>
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Correo</span>
                    <p class="text-lg font-semibold text-on-surface mt-2">
                        <c:out value="${sessionScope.usuarioLogueado.email}"/>
                    </p>
                </div>
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Semestre que Cursas</span>
                    <p class="text-lg font-semibold text-on-surface mt-2">
                        <c:out value="${requestScope.tutorPerfil.semestre.nombre}"/>
                    </p>
                </div>
                <div>
                    <span class="text-xs font-bold uppercase text-slate-500">Carrera</span>
                    <p class="text-lg font-semibold text-on-surface mt-2">
                        <c:out value="${requestScope.tutorPerfil.carrera.nombre}"/>
                    </p>
                </div>
            </div>

            <%-- Descripción --%>
            <div>
                <span class="text-xs font-bold uppercase text-slate-500">Descripción Profesional</span>
                <p class="text-slate-700 mt-2 leading-relaxed">
                    <c:out value="${requestScope.tutorPerfil.descripcionProfesional}"/>
                </p>
            </div>
        </div>

        <%-- Materias --%>
        <div class="bg-white rounded-xl shadow p-8">
            <h3 class="text-2xl font-bold text-primary mb-6">Materias que Ofreces</h3>
            <c:choose>
                <c:when test="${not empty requestScope.tutorPerfil.codigosMateriaRelacionadas}">
                    <div class="flex flex-wrap gap-3">
                        <c:forEach var="codigo" items="${requestScope.tutorPerfil.codigosMateriaRelacionadas}">
                            <span class="bg-secondary-container text-secondary px-4 py-2 rounded-lg font-semibold text-sm">
                                <c:out value="${codigo}"/>
                            </span>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-slate-600">Aún no has registrado materias.</p>
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
