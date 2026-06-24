<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Solicitar Mentoría - OwlShare</title>
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
    <div class="max-w-4xl mx-auto">
        <a href="${pageContext.request.contextPath}/estudiante/buscar-tutor" class="text-primary font-semibold text-sm mb-4 inline-flex items-center gap-1">
            <span class="material-symbols-outlined text-sm">arrow_back</span>
            Volver a tutores
        </a>

        <h2 class="text-4xl font-extrabold text-on-surface mb-2">Solicitar Mentoría</h2>
        <p class="text-slate-600 mb-8">Selecciona un horario disponible y envía tu solicitud.</p>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <%-- Columna izquierda: Horarios disponibles --%>
            <div class="lg:col-span-2">
                <div class="bg-white rounded-xl shadow p-8">
                    <h3 class="text-2xl font-bold text-on-surface mb-6">Horarios Disponibles</h3>

                    <div id="horariosContainer" class="space-y-4">
                        <div class="horario-card" onclick="seleccionarHorario(this, 'calculo-1', '24 Jun 2026', '10:00 - 11:30', 'Cálculo Diferencial')">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="font-bold text-on-surface">Cálculo Diferencial</p>
                                    <p class="text-sm text-slate-600 mt-1">24 Jun 2026</p>
                                    <p class="text-sm text-slate-600">10:00 - 11:30</p>
                                </div>
                                <span class="material-symbols-outlined text-slate-400">chevron_right</span>
                            </div>
                        </div>

                        <div class="horario-card" onclick="seleccionarHorario(this, 'algebra-1', '25 Jun 2026', '09:00 - 10:00', 'Álgebra Lineal')">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="font-bold text-on-surface">Álgebra Lineal</p>
                                    <p class="text-sm text-slate-600 mt-1">25 Jun 2026</p>
                                    <p class="text-sm text-slate-600">09:00 - 10:00</p>
                                </div>
                                <span class="material-symbols-outlined text-slate-400">chevron_right</span>
                            </div>
                        </div>

                        <div class="horario-card" onclick="seleccionarHorario(this, 'calculo-2', '25 Jun 2026', '14:00 - 15:30', 'Cálculo Diferencial')">
                            <div class="flex justify-between items-start">
                                <div>
                                    <p class="font-bold text-on-surface">Cálculo Diferencial</p>
                                    <p class="text-sm text-slate-600 mt-1">25 Jun 2026</p>
                                    <p class="text-sm text-slate-600">14:00 - 15:30</p>
                                </div>
                                <span class="material-symbols-outlined text-slate-400">chevron_right</span>
                            </div>
                        </div>
                    </div>

                    <div id="sinHorarios" class="hidden text-center py-8 text-slate-600">
                        <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">schedule</span>
                        <p>Este tutor no tiene horarios disponibles en este momento.</p>
                    </div>
                </div>
            </div>

            <%-- Columna derecha: Resumen y formulario --%>
            <div class="lg:col-span-1">
                <div class="bg-white rounded-xl shadow p-8 sticky top-24">
                    <h3 class="text-2xl font-bold text-on-surface mb-6">Resumen</h3>

                    <div id="resumenVacio" class="text-center py-12 text-slate-600">
                        <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">check_circle</span>
                        <p>Selecciona un horario para continuar.</p>
                    </div>

                    <div id="resumenLlenado" class="hidden space-y-6">
                        <div class="pb-6 border-b">
                            <p class="text-xs font-bold uppercase text-slate-500 mb-2">Horario Seleccionado</p>
                            <p class="font-bold text-on-surface"><span id="resumenMateria">Cálculo Diferencial</span></p>
                            <p class="text-sm text-slate-600 mt-2">
                                <span id="resumenFecha">24 Jun 2026</span><br>
                                <span id="resumenHora">10:00 - 11:30</span>
                            </p>
                        </div>

                        <div>
                            <label class="block text-sm font-bold text-on-surface mb-2">Tu Duda o Motivo *</label>
                            <textarea id="motivoInput" placeholder="Describe brevemente qué necesitas ayuda..." rows="5"
                                      class="w-full rounded-lg border border-slate-200 p-3 focus:ring-2 focus:ring-primary text-sm resize-none"></textarea>
                            <p id="errorMotivo" class="hidden text-xs text-red-600 mt-1">Describe tu motivo o duda</p>
                        </div>

                        <button onclick="enviarSolicitud()" class="w-full bg-primary text-white font-bold py-3 rounded-lg hover:opacity-90">
                            Enviar Solicitud
                        </button>

                        <button onclick="limpiarSeleccion()" class="w-full border border-slate-200 text-on-surface font-bold py-3 rounded-lg hover:bg-slate-50">
                            Seleccionar Otro
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%-- Modal: Confirmación --%>
<div id="modalConfirmacion" class="hidden fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-xl shadow-lg max-w-sm w-full text-center p-8 space-y-6">
        <div class="flex justify-center">
            <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
                <span class="material-symbols-outlined text-4xl text-green-600">check_circle</span>
            </div>
        </div>
        <div>
            <h3 class="text-2xl font-bold text-on-surface mb-2">¡Solicitud Enviada!</h3>
            <p class="text-slate-600">El tutor recibirá tu solicitud y te contactará si la acepta.</p>
        </div>
        <button onclick="cerrarConfirmacion()" class="w-full bg-primary text-white font-bold py-3 rounded-lg hover:opacity-90">
            Volver a Buscar Tutores
        </button>
    </div>
</div>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script>
    let horarioSeleccionado = null;

    function seleccionarHorario(element, id, fecha, hora, materia) {
        document.querySelectorAll('.horario-card').forEach(el => el.classList.remove('seleccionado'));
        element.classList.add('seleccionado');

        horarioSeleccionado = { id, fecha, hora, materia };

        document.getElementById('resumenVacio').classList.add('hidden');
        document.getElementById('resumenLlenado').classList.remove('hidden');
        document.getElementById('resumenMateria').textContent = materia;
        document.getElementById('resumenFecha').textContent = fecha;
        document.getElementById('resumenHora').textContent = hora;
        document.getElementById('motivoInput').value = '';
        document.getElementById('errorMotivo').classList.add('hidden');
    }

    function limpiarSeleccion() {
        document.querySelectorAll('.horario-card').forEach(el => el.classList.remove('seleccionado'));
        document.getElementById('resumenVacio').classList.remove('hidden');
        document.getElementById('resumenLlenado').classList.add('hidden');
        horarioSeleccionado = null;
    }

    function enviarSolicitud() {
        const motivo = document.getElementById('motivoInput').value.trim();
        const errorMotivo = document.getElementById('errorMotivo');

        if (!motivo) {
            errorMotivo.classList.remove('hidden');
            return;
        }

        errorMotivo.classList.add('hidden');
        document.getElementById('modalConfirmacion').classList.remove('hidden');
    }

    function cerrarConfirmacion() {
        document.getElementById('modalConfirmacion').classList.add('hidden');
        window.location.href = '${pageContext.request.contextPath}/estudiante/buscar-tutor';
    }
</script>

</body>
</html>
