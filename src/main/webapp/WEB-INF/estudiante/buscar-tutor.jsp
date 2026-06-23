<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Buscar Tutor - OwlShare</title>
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
        <span class="text-sm text-slate-600">Hola, <strong><c:out value="${requestScope.estudiantePerfil.nombre}"/></strong></span>
        <a href="${pageContext.request.contextPath}/estudiante/dashboard" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">home</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<%-- Main Content --%>
<main class="flex-1 p-10">
    <div class="max-w-5xl mx-auto">
        <h2 class="text-4xl font-extrabold text-on-surface mb-8">Buscar Tutor</h2>

        <%-- Formulario de búsqueda --%>
        <div class="bg-white rounded-xl shadow p-8 mb-10">
            <form action="${pageContext.request.contextPath}/api/tutores/buscar" method="GET" id="formBusqueda" class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <%-- Carrera --%>
                    <div>
                        <label class="block text-sm font-bold text-indigo-900 mb-2">Carrera</label>
                        <select name="carrera" class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                            <option value="">-- Todas las carreras --</option>
                            <c:forEach var="car" items="${carreras}">
                                <option value="${car.name()}"><c:out value="${car.nombre}"/></option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- Materia --%>
                    <div>
                        <label class="block text-sm font-bold text-indigo-900 mb-2">Materia</label>
                        <select name="materia" class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                            <option value="">-- Todas las materias --</option>
                            <c:forEach var="mat" items="${materias}">
                                <option value="${mat.codigo}"><c:out value="${mat.nombre}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <button type="submit" class="w-full md:w-auto bg-primary text-white font-bold py-3 px-8 rounded-xl hover:opacity-90 flex items-center justify-center gap-2">
                    <span class="material-symbols-outlined">search</span>
                    Buscar
                </button>
            </form>
        </div>

        <%-- Resultados --%>
        <div>
            <h3 class="text-2xl font-bold text-on-surface mb-6">Tutores Disponibles</h3>

            <c:choose>
                <c:when test="${not empty tutores}">
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        <c:forEach var="tutor" items="${tutores}">
                            <div class="bg-white rounded-xl shadow hover:shadow-lg transition-shadow p-6">
                                <div class="flex items-center gap-3 mb-4">
                                    <div class="w-12 h-12 rounded-full bg-primary-container text-white flex items-center justify-center font-bold">
                                        <c:out value="${tutor.nombre.substring(0,1).toUpperCase()}"/>
                                    </div>
                                    <div>
                                        <h4 class="font-bold text-on-surface"><c:out value="${tutor.nombre}"/></h4>
                                        <p class="text-xs text-slate-600"><c:out value="${tutor.carrera.nombre}"/></p>
                                    </div>
                                </div>

                                <p class="text-sm text-slate-600 mb-4">
                                    <c:out value="${tutor.descripcionProfesional}"/>
                                </p>

                                <div class="mb-4">
                                    <span class="text-xs font-bold uppercase text-slate-500">Materias</span>
                                    <div class="flex flex-wrap gap-2 mt-2">
                                        <c:forEach var="mat" items="${tutor.codigosMateriaRelacionadas}" varStatus="st">
                                            <c:if test="${st.count <= 2}">
                                                <span class="text-xs bg-secondary-container text-secondary px-2 py-1 rounded">
                                                    <c:out value="${mat}"/>
                                                </span>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${tutor.codigosMateriaRelacionadas.size() > 2}">
                                            <span class="text-xs text-slate-500">+${tutor.codigosMateriaRelacionadas.size() - 2}</span>
                                        </c:if>
                                    </div>
                                </div>

                                <button class="w-full bg-primary text-white font-bold py-2 rounded-lg hover:opacity-90 text-sm">
                                    Ver Perfil
                                </button>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="bg-white rounded-xl shadow p-12 text-center">
                        <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">person_search</span>
                        <p class="text-slate-600 text-lg">No hay tutores disponibles con esos criterios.</p>
                        <p class="text-slate-500 text-sm mt-2">Intenta con otros filtros.</p>
                    </div>
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
