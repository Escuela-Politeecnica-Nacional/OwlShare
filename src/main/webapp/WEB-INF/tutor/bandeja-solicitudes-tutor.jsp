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
        <div class="mb-8">
            <h2 class="text-4xl font-extrabold text-on-surface">Bandeja de Solicitudes</h2>
            <p class="text-slate-600 mt-2">Revisa y responde las solicitudes de mentoría de los estudiantes.</p>
        </div>

        <%-- Filtros --%>
        <div class="flex gap-4 mb-8 flex-wrap">
            <button onclick="filtrarSolicitudes(event, 'todas')" class="filtro-btn bg-primary text-white px-4 py-2 rounded-lg font-semibold text-sm hover:opacity-90" data-filtro="todas">
                Todas
            </button>
            <button onclick="filtrarSolicitudes(event, 'pendiente')" class="filtro-btn border border-slate-200 px-4 py-2 rounded-lg font-semibold text-sm hover:bg-slate-50" data-filtro="pendiente">
                Pendientes
            </button>
            <button onclick="filtrarSolicitudes(event, 'aceptada')" class="filtro-btn border border-slate-200 px-4 py-2 rounded-lg font-semibold text-sm hover:bg-slate-50" data-filtro="aceptada">
                Aceptadas
            </button>
            <button onclick="filtrarSolicitudes(event, 'rechazada')" class="filtro-btn border border-slate-200 px-4 py-2 rounded-lg font-semibold text-sm hover:bg-slate-50" data-filtro="rechazada">
                Rechazadas
            </button>
        </div>

        <%-- Lista de solicitudes --%>
        <div id="listaSolicitudes" class="space-y-4">
            <%-- Solicitud pendiente --%>
            <div class="solicitud-card bg-white rounded-xl shadow p-6 border-l-4 border-yellow-500" data-estado="pendiente">
                <div class="flex justify-between items-start mb-4">
                    <div>
                        <h3 class="text-xl font-bold text-on-surface">Juan García López</h3>
                        <p class="text-sm text-slate-600">Cálculo Diferencial - 24 Jun 2026, 10:00 - 11:30</p>
                    </div>
                    <span class="badge-pendiente px-3 py-1 rounded-full text-xs font-semibold">Pendiente</span>
                </div>

                <div class="bg-slate-50 rounded-lg p-4 mb-6">
                    <p class="text-sm text-slate-700">
                        <strong>Motivo:</strong> No entiendo bien cómo resolver problemas de límites infinitos. Necesito una explicación clara con ejemplos prácticos.
                    </p>
                </div>

                <div class="flex gap-3">
                    <button onclick="aceptarSolicitud(this)" class="flex-1 bg-green-500 text-white font-bold py-2 rounded-lg hover:bg-green-600 flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined text-sm">check_circle</span>
                        Aceptar
                    </button>
                    <button onclick="rechazarSolicitud(this)" class="flex-1 bg-red-500 text-white font-bold py-2 rounded-lg hover:bg-red-600 flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined text-sm">cancel</span>
                        Rechazar
                    </button>
                </div>
            </div>

            <%-- Solicitud aceptada --%>
            <div class="solicitud-card bg-white rounded-xl shadow p-6 border-l-4 border-green-500" data-estado="aceptada">
                <div class="flex justify-between items-start mb-4">
                    <div>
                        <h3 class="text-xl font-bold text-on-surface">María Rodríguez</h3>
                        <p class="text-sm text-slate-600">Álgebra Lineal - 25 Jun 2026, 09:00 - 10:00</p>
                    </div>
                    <span class="badge-aceptada px-3 py-1 rounded-full text-xs font-semibold">Aceptada</span>
                </div>

                <div class="bg-slate-50 rounded-lg p-4 mb-6">
                    <p class="text-sm text-slate-700">
                        <strong>Motivo:</strong> Necesito ayuda con determinantes y matrices inversas para mi examen parcial.
                    </p>
                </div>

                <div class="bg-green-50 p-4 rounded-lg border border-green-200">
                    <p class="text-sm text-green-700 font-semibold">✓ Solicitud aceptada. El horario está reservado.</p>
                </div>
            </div>

            <%-- Solicitud rechazada --%>
            <div class="solicitud-card bg-white rounded-xl shadow p-6 border-l-4 border-red-500" data-estado="rechazada">
                <div class="flex justify-between items-start mb-4">
                    <div>
                        <h3 class="text-xl font-bold text-on-surface">Carlos Mendez</h3>
                        <p class="text-sm text-slate-600">Cálculo Diferencial - 23 Jun 2026, 15:00 - 16:00</p>
                    </div>
                    <span class="badge-rechazada px-3 py-1 rounded-full text-xs font-semibold">Rechazada</span>
                </div>

                <div class="bg-slate-50 rounded-lg p-4 mb-6">
                    <p class="text-sm text-slate-700">
                        <strong>Motivo:</strong> Tengo dudas sobre derivadas parciales y regla de la cadena.
                    </p>
                </div>

                <div class="bg-red-50 p-4 rounded-lg border border-red-200">
                    <p class="text-sm text-red-700 font-semibold">✗ Solicitud rechazada. El horario fue liberado.</p>
                </div>
            </div>

            <%-- Solicitud pendiente --%>
            <div class="solicitud-card bg-white rounded-xl shadow p-6 border-l-4 border-yellow-500" data-estado="pendiente">
                <div class="flex justify-between items-start mb-4">
                    <div>
                        <h3 class="text-xl font-bold text-on-surface">Ana Pérez</h3>
                        <p class="text-sm text-slate-600">Álgebra Lineal - 26 Jun 2026, 14:00 - 15:30</p>
                    </div>
                    <span class="badge-pendiente px-3 py-1 rounded-full text-xs font-semibold">Pendiente</span>
                </div>

                <div class="bg-slate-50 rounded-lg p-4 mb-6">
                    <p class="text-sm text-slate-700">
                        <strong>Motivo:</strong> Necesito ayuda con espacios vectoriales. No me queda claro el concepto de dimensión.
                    </p>
                </div>

                <div class="flex gap-3">
                    <button onclick="aceptarSolicitud(this)" class="flex-1 bg-green-500 text-white font-bold py-2 rounded-lg hover:bg-green-600 flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined text-sm">check_circle</span>
                        Aceptar
                    </button>
                    <button onclick="rechazarSolicitud(this)" class="flex-1 bg-red-500 text-white font-bold py-2 rounded-lg hover:bg-red-600 flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined text-sm">cancel</span>
                        Rechazar
                    </button>
                </div>
            </div>
        </div>

        <div id="sinSolicitudes" class="hidden text-center py-12 bg-white rounded-xl shadow">
            <span class="material-symbols-outlined text-4xl text-slate-300 block mb-4">mail_outline</span>
            <p class="text-slate-600">No tienes solicitudes para este filtro.</p>
        </div>
    </div>
</main>

<%-- Modal: Confirmación --%>
<div id="modalConfirmacion" class="hidden fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-xl shadow-lg max-w-sm w-full p-8 space-y-6 text-center">
        <div id="confirmIcono" class="flex justify-center">
            <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
                <span class="material-symbols-outlined text-4xl text-green-600">check_circle</span>
            </div>
        </div>
        <div>
            <h3 id="confirmTitulo" class="text-2xl font-bold text-on-surface mb-2">¡Solicitud Aceptada!</h3>
            <p id="confirmMensaje" class="text-slate-600">La mentoría ha sido agendada correctamente.</p>
        </div>
        <button onclick="cerrarConfirmacion()" class="w-full bg-primary text-white font-bold py-3 rounded-lg hover:opacity-90">
            Entendido
        </button>
    </div>
</div>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script>
    let filtroActual = 'todas';

    function filtrarSolicitudes(event, filtro) {
        filtroActual = filtro;

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

        document.getElementById('sinSolicitudes').style.display = visible === 0 ? 'block' : 'none';
    }

    function aceptarSolicitud(button) {
        const card = button.closest('.solicitud-card');
        const estado = card.querySelector('.badge-pendiente');

        if (!estado) return;

        card.classList.remove('border-yellow-500');
        card.classList.add('border-green-500');

        estado.classList.remove('badge-pendiente');
        estado.classList.add('badge-aceptada');
        estado.textContent = 'Aceptada';

        const motivoDiv = card.querySelector('.bg-slate-50');
        motivoDiv.insertAdjacentHTML('afterend', `
            <div class="bg-green-50 p-4 rounded-lg border border-green-200 mt-6">
                <p class="text-sm text-green-700 font-semibold">✓ Solicitud aceptada. El horario está reservado.</p>
            </div>
        `);

        button.parentElement.remove();

        mostrarConfirmacion('¡Solicitud Aceptada!', 'La mentoría ha sido agendada correctamente.', 'green');
    }

    function rechazarSolicitud(button) {
        const card = button.closest('.solicitud-card');
        const estado = card.querySelector('.badge-pendiente');

        if (!estado) return;

        card.classList.remove('border-yellow-500');
        card.classList.add('border-red-500');

        estado.classList.remove('badge-pendiente');
        estado.classList.add('badge-rechazada');
        estado.textContent = 'Rechazada';

        const motivoDiv = card.querySelector('.bg-slate-50');
        motivoDiv.insertAdjacentHTML('afterend', `
            <div class="bg-red-50 p-4 rounded-lg border border-red-200 mt-6">
                <p class="text-sm text-red-700 font-semibold">✗ Solicitud rechazada. El horario fue liberado.</p>
            </div>
        `);

        button.parentElement.remove();

        mostrarConfirmacion('Solicitud Rechazada', 'El horario ha sido liberado para otros estudiantes.', 'red');
    }

    function mostrarConfirmacion(titulo, mensaje, tipo) {
        const icono = document.getElementById('confirmIcono');
        const tituloEl = document.getElementById('confirmTitulo');
        const mensajeEl = document.getElementById('confirmMensaje');

        if (tipo === 'red') {
            icono.innerHTML = '<div class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center"><span class="material-symbols-outlined text-4xl text-red-600">cancel</span></div>';
        } else {
            icono.innerHTML = '<div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center"><span class="material-symbols-outlined text-4xl text-green-600">check_circle</span></div>';
        }

        tituloEl.textContent = titulo;
        mensajeEl.textContent = mensaje;

        document.getElementById('modalConfirmacion').classList.remove('hidden');
    }

    function cerrarConfirmacion() {
        document.getElementById('modalConfirmacion').classList.add('hidden');
    }
</script>

</body>
</html>
