<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Gestión de Horarios - OwlShare</title>
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
            <h2 class="text-4xl font-extrabold text-on-surface">Mis Horarios</h2>
            <p class="text-slate-600 mt-2">Configura tu horario de trabajo semanal y revisa las tutorías que tienes agendadas.</p>
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

        <div class="flex gap-3 mb-8 border-b border-slate-200">
            <button type="button" id="tabTrabajo" onclick="mostrarTab('trabajo')"
                    class="tab-btn px-4 py-3 font-bold text-sm border-b-2 border-primary text-primary">
                Horario de trabajo
            </button>
            <button type="button" id="tabAgenda" onclick="mostrarTab('agenda')"
                    class="tab-btn px-4 py-3 font-bold text-sm border-b-2 border-transparent text-slate-500 hover:text-primary">
                Mi agenda personal
            </button>
        </div>

        <%-- Horario de trabajo (disponibilidad semanal pública) --%>
        <section id="seccionTrabajo">
            <div class="flex justify-between items-center mb-6">
                <div>
                    <h3 class="text-2xl font-bold text-on-surface">Disponibilidad semanal</h3>
                    <p class="text-sm text-slate-600 mt-1">Indica en qué días y horas puedes recibir solicitudes de mentoría.</p>
                </div>
                <button type="button" onclick="abrirModalFranja()"
                        class="flex items-center gap-2 bg-primary text-white font-bold py-3 px-6 rounded-xl hover:opacity-90">
                    <span class="material-symbols-outlined">add_circle</span>
                    Agregar franja
                </button>
            </div>

            <div class="bg-white rounded-xl shadow overflow-hidden">
                <c:choose>
                    <c:when test="${not empty disponibilidades}">
                        <div class="overflow-x-auto">
                            <table class="w-full">
                                <thead class="bg-slate-50 border-b">
                                    <tr>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Día</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Horario</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Estado</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody class="divide-y">
                                    <c:forEach var="franja" items="${disponibilidades}">
                                        <tr class="hover:bg-slate-50">
                                            <td class="px-6 py-4 text-sm font-semibold"><c:out value="${franja.diaEtiqueta}"/></td>
                                            <td class="px-6 py-4 text-sm">
                                                <c:out value="${franja.horaInicio}"/> - <c:out value="${franja.horaFin}"/>
                                            </td>
                                            <td class="px-6 py-4 text-sm">
                                                <c:choose>
                                                    <c:when test="${franja.activo}">
                                                        <span class="px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">Activo</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="px-3 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-600">Inactivo</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="px-6 py-4 text-sm">
                                                <form method="post" action="${pageContext.request.contextPath}/tutor/horarios/disponibilidad"
                                                      class="inline"
                                                      onsubmit="return confirm('¿Eliminar esta franja de disponibilidad?');">
                                                    <input type="hidden" name="accion" value="eliminar"/>
                                                    <input type="hidden" name="idFranja" value="${franja.id}"/>
                                                    <button type="submit" class="text-red-600 hover:text-red-700 font-semibold text-xs">
                                                        Eliminar
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-12 text-slate-600">
                            <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">schedule</span>
                            <p>Aún no has definido tu horario de trabajo.</p>
                            <p class="text-sm text-slate-500 mt-2">Agrega franjas como "Lunes 09:00–11:00" o "Martes 14:00–18:00".</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <%-- Agenda personal (tutorías agendadas) --%>
        <section id="seccionAgenda" class="hidden">
            <div class="mb-6">
                <h3 class="text-2xl font-bold text-on-surface">Tutorías agendadas</h3>
                <p class="text-sm text-slate-600 mt-1">Sesiones pendientes o confirmadas con estudiantes.</p>
            </div>

            <div class="bg-white rounded-xl shadow overflow-hidden">
                <c:choose>
                    <c:when test="${not empty sesionesAgendadas}">
                        <div class="overflow-x-auto">
                            <table class="w-full">
                                <thead class="bg-slate-50 border-b">
                                    <tr>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Estudiante</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Materia</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Fecha</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Hora</th>
                                        <th class="px-6 py-4 text-left text-sm font-bold">Estado</th>
                                    </tr>
                                </thead>
                                <tbody class="divide-y">
                                    <c:forEach var="sesion" items="${sesionesAgendadas}">
                                        <tr class="hover:bg-slate-50">
                                            <td class="px-6 py-4 text-sm font-semibold"><c:out value="${sesion.estudiante}"/></td>
                                            <td class="px-6 py-4 text-sm">
                                                <c:out value="${sesion.materiaNombre}"/>
                                                <span class="text-xs text-slate-500">(<c:out value="${sesion.materiaCodigo}"/>)</span>
                                            </td>
                                            <td class="px-6 py-4 text-sm"><c:out value="${sesion.fecha}"/></td>
                                            <td class="px-6 py-4 text-sm">
                                                <c:out value="${sesion.horaInicio}"/> - <c:out value="${sesion.horaFin}"/>
                                            </td>
                                            <td class="px-6 py-4 text-sm">
                                                <c:choose>
                                                    <c:when test="${sesion.estado == 'aceptada'}">
                                                        <span class="px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                                                            <c:out value="${sesion.estadoEtiqueta}"/>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="px-3 py-1 rounded-full text-xs font-semibold bg-yellow-100 text-yellow-800">
                                                            <c:out value="${sesion.estadoEtiqueta}"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-12 text-slate-600">
                            <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">event_busy</span>
                            <p>No tienes tutorías agendadas por ahora.</p>
                            <a href="${pageContext.request.contextPath}/tutor/solicitudes"
                               class="inline-block mt-4 text-primary font-semibold text-sm hover:underline">
                                Revisar solicitudes pendientes
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </div>
</main>

<div id="modalFranja" class="hidden fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-xl shadow-lg max-w-md w-full">
        <form method="post" action="${pageContext.request.contextPath}/tutor/horarios/disponibilidad" class="p-8 space-y-6">
            <input type="hidden" name="accion" value="crear"/>
            <div class="flex justify-between items-center">
                <h3 class="text-2xl font-bold text-on-surface">Nueva franja</h3>
                <button type="button" onclick="cerrarModalFranja()" class="text-slate-500 hover:text-slate-700">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>

            <div>
                <label for="diaSemana" class="block text-sm font-bold text-on-surface mb-2">Día de la semana *</label>
                <select id="diaSemana" name="diaSemana" required
                        class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                    <option value="">Selecciona un día</option>
                    <c:forEach var="dia" items="${diasSemana}">
                        <option value="${dia.name()}"><c:out value="${dia.etiqueta}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label for="horaInicio" class="block text-sm font-bold text-on-surface mb-2">Hora inicio *</label>
                    <input type="time" id="horaInicio" name="horaInicio" required
                           class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                </div>
                <div>
                    <label for="horaFin" class="block text-sm font-bold text-on-surface mb-2">Hora fin *</label>
                    <input type="time" id="horaFin" name="horaFin" required
                           class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                </div>
            </div>

            <p class="text-xs text-slate-500">Ejemplo: Lunes de 09:00 a 11:00, o Martes de 14:00 a 18:00.</p>

            <div class="flex gap-3">
                <button type="button" onclick="cerrarModalFranja()"
                        class="flex-1 py-3 border border-slate-200 rounded-lg font-bold hover:bg-slate-50">
                    Cancelar
                </button>
                <button type="submit" class="flex-1 py-3 bg-primary text-white rounded-lg font-bold hover:opacity-90">
                    Guardar
                </button>
            </div>
        </form>
    </div>
</div>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script>
    function mostrarTab(tab) {
        const esTrabajo = tab === 'trabajo';
        document.getElementById('seccionTrabajo').classList.toggle('hidden', !esTrabajo);
        document.getElementById('seccionAgenda').classList.toggle('hidden', esTrabajo);

        document.getElementById('tabTrabajo').classList.toggle('border-primary', esTrabajo);
        document.getElementById('tabTrabajo').classList.toggle('text-primary', esTrabajo);
        document.getElementById('tabTrabajo').classList.toggle('border-transparent', !esTrabajo);
        document.getElementById('tabTrabajo').classList.toggle('text-slate-500', !esTrabajo);

        document.getElementById('tabAgenda').classList.toggle('border-primary', !esTrabajo);
        document.getElementById('tabAgenda').classList.toggle('text-primary', !esTrabajo);
        document.getElementById('tabAgenda').classList.toggle('border-transparent', esTrabajo);
        document.getElementById('tabAgenda').classList.toggle('text-slate-500', esTrabajo);
    }

    function abrirModalFranja() {
        document.getElementById('modalFranja').classList.remove('hidden');
    }

    function cerrarModalFranja() {
        document.getElementById('modalFranja').classList.add('hidden');
    }

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            cerrarModalFranja();
        }
    });
</script>

</body>
</html>
