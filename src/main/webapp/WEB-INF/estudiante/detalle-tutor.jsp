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
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>

<header class="bg-white shadow-sm h-16 flex justify-between items-center px-8 sticky top-0 z-40">
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
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

<main class="flex-1 p-10">
    <div class="max-w-4xl mx-auto">
        <a href="${pageContext.request.contextPath}/estudiante/buscar-tutor"
           class="text-primary font-semibold text-sm mb-6 inline-flex items-center gap-1">
            <span class="material-symbols-outlined text-sm">arrow_back</span>
            Volver a tutores
        </a>

        <div class="flex items-center gap-4 mb-8">
            <div class="w-16 h-16 rounded-full bg-primary-container text-white flex items-center justify-center text-2xl font-bold">
                <c:out value="${requestScope.tutorPerfil.nombreCompleto.substring(0,1).toUpperCase()}"/>
            </div>
            <div>
                <h2 class="text-4xl font-extrabold text-on-surface">
                    <c:out value="${requestScope.tutorPerfil.nombreCompleto}"/>
                </h2>
                <p class="text-slate-600 mt-1">
                    <c:out value="${requestScope.tutorPerfil.carrera}"/>
                    · <c:out value="${requestScope.tutorPerfil.semestreActual}"/>
                </p>
            </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div class="lg:col-span-2 space-y-8">
                <div class="bg-white rounded-xl shadow p-8">
                    <h3 class="text-2xl font-bold text-primary mb-6 flex items-center gap-2">
                        <span class="material-symbols-outlined">school</span>
                        Trayectoria Académica
                    </h3>
                    <div class="space-y-4">
                        <c:forEach var="sem" items="${requestScope.tutorPerfil.trayectoria}">
                            <div class="flex items-center gap-4 p-4 rounded-lg border
                                ${sem.estado == 'cursando' ? 'border-primary bg-indigo-50' : 'border-slate-200'}">
                                <div class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm
                                    ${sem.estado == 'cursando' ? 'bg-primary text-white' : 'bg-slate-200 text-slate-600'}">
                                    <c:out value="${sem.numero}"/>
                                </div>
                                <div class="flex-1">
                                    <p class="font-semibold text-on-surface"><c:out value="${sem.nombre}"/></p>
                                    <p class="text-xs text-slate-500 capitalize"><c:out value="${sem.estado}"/></p>
                                </div>
                                <c:if test="${sem.estado == 'cursando'}">
                                    <span class="text-xs font-bold uppercase text-primary">Actual</span>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="bg-white rounded-xl shadow p-8">
                    <h3 class="text-2xl font-bold text-primary mb-6 flex items-center gap-2">
                        <span class="material-symbols-outlined">menu_book</span>
                        Materias que Ofrece
                    </h3>
                    <c:choose>
                        <c:when test="${not empty requestScope.tutorPerfil.materias}">
                            <div class="overflow-x-auto">
                                <table class="w-full text-sm">
                                    <thead class="bg-slate-50 text-left">
                                        <tr>
                                            <th class="px-4 py-3 font-bold text-slate-600">Código</th>
                                            <th class="px-4 py-3 font-bold text-slate-600">Materia</th>
                                            <th class="px-4 py-3 font-bold text-slate-600">Semestre</th>
                                        </tr>
                                    </thead>
                                    <tbody class="divide-y">
                                        <c:forEach var="mat" items="${requestScope.tutorPerfil.materias}">
                                            <tr>
                                                <td class="px-4 py-3 font-mono text-primary font-semibold">
                                                    <c:out value="${mat.codigo}"/>
                                                </td>
                                                <td class="px-4 py-3"><c:out value="${mat.nombre}"/></td>
                                                <td class="px-4 py-3"><c:out value="${mat.semestre}"/>º</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-slate-600">Este tutor aún no ha registrado materias.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="space-y-6">
                <div class="bg-white rounded-xl shadow p-6">
                    <h3 class="text-lg font-bold text-on-surface mb-4">Información de contacto</h3>
                    <p class="text-sm text-slate-600 mb-1">Correo institucional</p>
                    <p class="font-semibold text-on-surface break-all">
                        <c:out value="${requestScope.tutorPerfil.email}"/>
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/estudiante/solicitar-mentoria?tutorId=${requestScope.tutorPerfil.id}"
                   class="w-full bg-primary text-white font-bold py-3 px-6 rounded-xl hover:opacity-90 flex items-center justify-center gap-2">
                    <span class="material-symbols-outlined">event_available</span>
                    Solicitar Mentoría
                </a>
            </div>
        </div>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>
</body>
</html>
