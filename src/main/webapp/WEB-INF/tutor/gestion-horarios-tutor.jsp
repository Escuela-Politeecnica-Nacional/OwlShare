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
    <div class="max-w-5xl mx-auto">
        <div class="flex justify-between items-start mb-8">
            <div>
                <h2 class="text-4xl font-extrabold text-on-surface">Mis Horarios Disponibles</h2>
                <p class="text-slate-600 mt-2">Define cuándo estás disponible para recibir solicitudes de mentoría.</p>
            </div>
            <button onclick="abrirModalCrearHorario()" class="flex items-center gap-2 bg-primary text-white font-bold py-3 px-6 rounded-xl hover:opacity-90">
                <span class="material-symbols-outlined">add_circle</span>
                Nuevo Horario
            </button>
        </div>

        <%-- Lista de horarios --%>
        <div class="bg-white rounded-xl shadow overflow-hidden">
            <div class="overflow-x-auto">
                <table class="w-full">
                    <thead class="bg-slate-50 border-b">
                        <tr>
                            <th class="px-6 py-4 text-left text-sm font-bold text-on-surface">Materia</th>
                            <th class="px-6 py-4 text-left text-sm font-bold text-on-surface">Fecha</th>
                            <th class="px-6 py-4 text-left text-sm font-bold text-on-surface">Hora</th>
                            <th class="px-6 py-4 text-left text-sm font-bold text-on-surface">Estado</th>
                            <th class="px-6 py-4 text-left text-sm font-bold text-on-surface">Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="listaHorarios" class="divide-y">
                        <tr class="hover:bg-slate-50">
                            <td class="px-6 py-4 text-sm">Cálculo Diferencial</td>
                            <td class="px-6 py-4 text-sm">24 Jun 2026</td>
                            <td class="px-6 py-4 text-sm">10:00 - 11:30</td>
                            <td class="px-6 py-4 text-sm"><span class="badge-disponible px-3 py-1 rounded-full text-xs font-semibold">Disponible</span></td>
                            <td class="px-6 py-4 text-sm space-x-2">
                                <button class="text-primary hover:text-indigo-700 font-semibold text-xs">Editar</button>
                                <button class="text-red-600 hover:text-red-700 font-semibold text-xs">Desactivar</button>
                            </td>
                        </tr>
                        <tr class="hover:bg-slate-50">
                            <td class="px-6 py-4 text-sm">Cálculo Diferencial</td>
                            <td class="px-6 py-4 text-sm">24 Jun 2026</td>
                            <td class="px-6 py-4 text-sm">14:00 - 15:30</td>
                            <td class="px-6 py-4 text-sm"><span class="badge-reservado px-3 py-1 rounded-full text-xs font-semibold">Reservado</span></td>
                            <td class="px-6 py-4 text-sm space-x-2">
                                <button class="text-primary hover:text-indigo-700 font-semibold text-xs">Ver Solicitud</button>
                            </td>
                        </tr>
                        <tr class="hover:bg-slate-50">
                            <td class="px-6 py-4 text-sm">Álgebra Lineal</td>
                            <td class="px-6 py-4 text-sm">23 Jun 2026</td>
                            <td class="px-6 py-4 text-sm">09:00 - 10:00</td>
                            <td class="px-6 py-4 text-sm"><span class="badge-cancelado px-3 py-1 rounded-full text-xs font-semibold">Cancelado</span></td>
                            <td class="px-6 py-4 text-sm space-x-2">
                                <button class="text-primary hover:text-indigo-700 font-semibold text-xs">Reactivar</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div id="sinHorarios" class="hidden text-center py-12 text-slate-600">
                <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">schedule</span>
                <p>No has creado horarios aún. ¡Crea uno para empezar!</p>
            </div>
        </div>
    </div>
</main>

<%-- Modal: Crear/Editar Horario --%>
<div id="modalHorario" class="hidden fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-xl shadow-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div class="p-8 space-y-6">
            <div class="flex justify-between items-center">
                <h3 class="text-2xl font-bold text-on-surface">Nuevo Horario</h3>
                <button onclick="cerrarModalHorario()" class="text-slate-500 hover:text-slate-700">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>

            <div class="space-y-4">
                <%-- Materia --%>
                <div>
                    <label class="block text-sm font-bold text-on-surface mb-2">Materia *</label>
                    <select id="materiasSelect" class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                        <option value="">Selecciona una materia</option>
                        <option value="calculo">Cálculo Diferencial</option>
                        <option value="algebra">Álgebra Lineal</option>
                        <option value="fisica">Física I</option>
                    </select>
                    <p id="errorMateria" class="hidden text-xs text-red-600 mt-1">Selecciona una materia</p>
                </div>

                <%-- Fecha --%>
                <div>
                    <label class="block text-sm font-bold text-on-surface mb-2">Fecha *</label>
                    <input type="date" id="fechaInput" class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                    <p id="errorFecha" class="hidden text-xs text-red-600 mt-1">Ingresa una fecha válida</p>
                </div>

                <%-- Hora Inicio --%>
                <div>
                    <label class="block text-sm font-bold text-on-surface mb-2">Hora de Inicio *</label>
                    <input type="time" id="horaInicioInput" class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                    <p id="errorHoraInicio" class="hidden text-xs text-red-600 mt-1">Ingresa la hora de inicio</p>
                </div>

                <%-- Hora Fin --%>
                <div>
                    <label class="block text-sm font-bold text-on-surface mb-2">Hora de Fin *</label>
                    <input type="time" id="horaFinInput" class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                    <p id="errorHoraFin" class="hidden text-xs text-red-600 mt-1">La hora de fin debe ser mayor que la de inicio</p>
                </div>

                <p class="text-xs text-slate-500">* Campos obligatorios</p>
            </div>

            <div class="flex gap-3">
                <button onclick="cerrarModalHorario()" class="flex-1 py-3 border border-slate-200 rounded-lg font-bold text-on-surface hover:bg-slate-50">
                    Cancelar
                </button>
                <button onclick="crearHorario()" class="flex-1 py-3 bg-primary text-white rounded-lg font-bold hover:opacity-90">
                    Crear Horario
                </button>
            </div>
        </div>
    </div>
</div>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script>
    function abrirModalCrearHorario() {
        document.getElementById('modalHorario').classList.remove('hidden');
        limpiarFormulario();
    }

    function cerrarModalHorario() {
        document.getElementById('modalHorario').classList.add('hidden');
    }

    function limpiarFormulario() {
        document.getElementById('materiasSelect').value = '';
        document.getElementById('fechaInput').value = '';
        document.getElementById('horaInicioInput').value = '';
        document.getElementById('horaFinInput').value = '';
        document.querySelectorAll('[id^="error"]').forEach(el => el.classList.add('hidden'));
    }

    function validarFormulario() {
        let valido = true;
        document.querySelectorAll('[id^="error"]').forEach(el => el.classList.add('hidden'));

        if (!document.getElementById('materiasSelect').value) {
            document.getElementById('errorMateria').classList.remove('hidden');
            valido = false;
        }
        if (!document.getElementById('fechaInput').value) {
            document.getElementById('errorFecha').classList.remove('hidden');
            valido = false;
        }
        if (!document.getElementById('horaInicioInput').value) {
            document.getElementById('errorHoraInicio').classList.remove('hidden');
            valido = false;
        }
        if (!document.getElementById('horaFinInput').value) {
            document.getElementById('errorHoraFin').classList.remove('hidden');
            valido = false;
        }

        if (document.getElementById('horaInicioInput').value && document.getElementById('horaFinInput').value) {
            if (document.getElementById('horaInicioInput').value >= document.getElementById('horaFinInput').value) {
                document.getElementById('errorHoraFin').classList.remove('hidden');
                valido = false;
            }
        }

        return valido;
    }

    function crearHorario() {
        if (!validarFormulario()) return;

        const materia = document.getElementById('materiasSelect').options[document.getElementById('materiasSelect').selectedIndex].text;
        const fecha = new Date(document.getElementById('fechaInput').value).toLocaleDateString('es-ES', {day: '2-digit', month: 'short', year: 'numeric'});
        const horaInicio = document.getElementById('horaInicioInput').value;
        const horaFin = document.getElementById('horaFinInput').value;

        const nuevaFila = `
            <tr class="hover:bg-slate-50">
                <td class="px-6 py-4 text-sm">${materia}</td>
                <td class="px-6 py-4 text-sm">${fecha}</td>
                <td class="px-6 py-4 text-sm">${horaInicio} - ${horaFin}</td>
                <td class="px-6 py-4 text-sm"><span class="badge-disponible px-3 py-1 rounded-full text-xs font-semibold">Disponible</span></td>
                <td class="px-6 py-4 text-sm space-x-2">
                    <button class="text-primary hover:text-indigo-700 font-semibold text-xs">Editar</button>
                    <button class="text-red-600 hover:text-red-700 font-semibold text-xs">Desactivar</button>
                </td>
            </tr>
        `;

        document.getElementById('listaHorarios').insertAdjacentHTML('afterbegin', nuevaFila);
        cerrarModalHorario();
    }

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') cerrarModalHorario();
    });
</script>

</body>
</html>