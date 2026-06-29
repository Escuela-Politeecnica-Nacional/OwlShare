<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Bandeja de Solicitudes - OwlShare</title>
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
    <div class="flex items-center gap-3">
        <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
    </div>
    <div class="flex items-center gap-4">
        <span class="text-sm text-slate-600">Hola, <strong><c:out value="${requestScope.tutorPerfil.nombre}"/></strong></span>
        <a href="${pageContext.request.contextPath}/tutor/inicio" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">home</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<main class="flex-1 p-10">
    <div class="max-w-5xl mx-auto">
        <div class="mb-8">
            <h2 class="text-4xl font-extrabold text-on-surface">Bandeja de Solicitudes</h2>
            <p class="text-slate-600 mt-2">Revisa y responde las solicitudes de mentoría de los estudiantes.</p>
        </div>

        <c:if test="${not empty exito}">
            <div class="mb-6 bg-green-50 border border-green-200 text-green-800 rounded-lg px-4 py-3 text-sm">
                <c:out value="${exito}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="mb-6 bg-red-50 border border-red-200 text-red-800 rounded-lg px-4 py-3 text-sm">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <div class="flex gap-4 mb-8 flex-wrap">
            <button type="button" onclick="filtrarSolicitudes(event, 'todas')" class="filtro-btn bg-primary text-white px-4 py-2 rounded-lg font-semibold text-sm hover:opacity-90" data-filtro="todas">
                Todas
            </button>
            <button type="button" onclick="filtrarSolicitudes(event, 'pendiente')" class="filtro-btn border border-slate-200 px-4 py-2 rounded-lg font-semibold text-sm hover:bg-slate-50" data-filtro="pendiente">
                Pendientes
            </button>
            <button type="button" onclick="filtrarSolicitudes(event, 'aceptada')" class="filtro-btn border border-slate-200 px-4 py-2 rounded-lg font-semibold text-sm hover:bg-slate-50" data-filtro="aceptada">
                Aceptadas
            </button>
            <button type="button" onclick="filtrarSolicitudes(event, 'rechazada')" class="filtro-btn border border-slate-200 px-4 py-2 rounded-lg font-semibold text-sm hover:bg-slate-50" data-filtro="rechazada">
                Rechazadas
            </button>
        </div>

        <c:choose>
            <c:when test="${empty solicitudes}">
                <div class="text-center py-12 bg-white rounded-xl shadow">
                    <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">mail_outline</span>
                    <p class="text-slate-600">Aún no tienes solicitudes de mentoría.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div id="listaSolicitudes" class="space-y-4">
                    <c:forEach var="solicitud" items="${solicitudes}">
                        <c:set var="estadoClave" value="${solicitud.estadoClave}"/>
                        <div class="solicitud-card bg-white rounded-xl shadow p-6 border-l-4
                            <c:choose>
                                <c:when test="${estadoClave == 'pendiente'}">border-yellow-500</c:when>
                                <c:when test="${estadoClave == 'aceptada'}">border-green-500</c:when>
                                <c:when test="${estadoClave == 'rechazada'}">border-red-500</c:when>
                                <c:otherwise>border-slate-300</c:otherwise>
                            </c:choose>"
                             data-estado="${estadoClave}">
                            <div class="flex justify-between items-start mb-4">
                                <div>
                                    <h3 class="text-xl font-bold text-on-surface"><c:out value="${solicitud.nombreEstudiante}"/></h3>
                                    <p class="text-sm text-slate-600">
                                        <c:out value="${solicitud.materiaNombre}"/> (<c:out value="${solicitud.materiaCodigo}"/>)
                                        · <c:out value="${solicitud.fecha}"/>, <c:out value="${solicitud.horaInicio}"/> - <c:out value="${solicitud.horaFin}"/>
                                    </p>
                                </div>
                                <span class="px-3 py-1 rounded-full text-xs font-semibold
                                    <c:choose>
                                        <c:when test="${estadoClave == 'pendiente'}">bg-yellow-100 text-yellow-800</c:when>
                                        <c:when test="${estadoClave == 'aceptada'}">bg-green-100 text-green-800</c:when>
                                        <c:when test="${estadoClave == 'rechazada'}">bg-red-100 text-red-800</c:when>
                                        <c:otherwise>bg-slate-100 text-slate-700</c:otherwise>
                                    </c:choose>">
                                    <c:out value="${solicitud.estadoEtiqueta}"/>
                                </span>
                            </div>

                            <c:if test="${not empty solicitud.comentario}">
                                <div class="bg-slate-50 rounded-lg p-4 mb-6">
                                    <p class="text-sm text-slate-700">
                                        <strong>Motivo:</strong> <c:out value="${solicitud.comentario}"/>
                                    </p>
                                </div>
                            </c:if>

                            <c:choose>
                                <c:when test="${estadoClave == 'pendiente'}">
                                    <div class="flex gap-3">
                                        <form method="post" action="${pageContext.request.contextPath}/tutor/solicitudes/responder" class="flex-1"
                                              onsubmit="return confirm('¿Aceptar esta solicitud de mentoría?');">
                                            <input type="hidden" name="idSolicitud" value="${solicitud.id}"/>
                                            <input type="hidden" name="accion" value="aceptar"/>
                                            <button type="submit" class="w-full bg-green-500 text-white font-bold py-2 rounded-lg hover:bg-green-600 flex items-center justify-center gap-2">
                                                <span class="material-symbols-outlined text-sm">check_circle</span>
                                                Aceptar
                                            </button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/tutor/solicitudes/responder" class="flex-1"
                                              onsubmit="return confirm('¿Rechazar esta solicitud? El horario quedará disponible.');">
                                            <input type="hidden" name="idSolicitud" value="${solicitud.id}"/>
                                            <input type="hidden" name="accion" value="rechazar"/>
                                            <button type="submit" class="w-full bg-red-500 text-white font-bold py-2 rounded-lg hover:bg-red-600 flex items-center justify-center gap-2">
                                                <span class="material-symbols-outlined text-sm">cancel</span>
                                                Rechazar
                                            </button>
                                        </form>
                                    </div>
                                </c:when>
                                <c:when test="${estadoClave == 'aceptada'}">
                                    <div class="bg-green-50 p-4 rounded-lg border border-green-200">
                                        <p class="text-sm text-green-700 font-semibold">Solicitud aceptada. El horario está reservado.</p>
                                    </div>
                                </c:when>
                                <c:when test="${estadoClave == 'rechazada'}">
                                    <div class="bg-red-50 p-4 rounded-lg border border-red-200">
                                        <p class="text-sm text-red-700 font-semibold">Solicitud rechazada. El horario fue liberado.</p>
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                    </c:forEach>
                </div>

                <div id="sinSolicitudes" class="hidden text-center py-12 bg-white rounded-xl shadow mt-4">
                    <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">filter_alt_off</span>
                    <p class="text-slate-600">No tienes solicitudes para este filtro.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script>
    function filtrarSolicitudes(event, filtro) {
        document.querySelectorAll('.filtro-btn').forEach(btn => {
            btn.classList.remove('bg-primary', 'text-white', 'hover:opacity-90');
            btn.classList.add('border', 'border-slate-200', 'bg-white', 'hover:bg-slate-50');
        });

        const botonActivo = event.currentTarget;
        botonActivo.classList.remove('border', 'border-slate-200', 'bg-white', 'hover:bg-slate-50');
        botonActivo.classList.add('bg-primary', 'text-white', 'hover:opacity-90');

        const solicitudes = document.querySelectorAll('.solicitud-card');
        let visible = 0;

        solicitudes.forEach(card => {
            const estado = card.getAttribute('data-estado');
            if (filtro === 'todas' || estado === filtro) {
                card.style.display = 'block';
                visible++;
            } else {
                card.style.display = 'none';
            }
        });

        const sinSolicitudes = document.getElementById('sinSolicitudes');
        if (sinSolicitudes) {
            sinSolicitudes.style.display = visible === 0 ? 'block' : 'none';
        }
    }
</script>

</body>
</html>
