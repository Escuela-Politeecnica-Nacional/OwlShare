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

<main class="flex-1 p-10">
    <div class="max-w-4xl mx-auto">
        <a href="${pageContext.request.contextPath}/estudiante/tutor/detalle?id=${requestScope.tutorPerfil.id}"
           class="text-primary font-semibold text-sm mb-4 inline-flex items-center gap-1">
            <span class="material-symbols-outlined text-sm">arrow_back</span>
            Volver al perfil del tutor
        </a>

        <h2 class="text-4xl font-extrabold text-on-surface mb-2">Solicitar Mentoría</h2>
        <p class="text-slate-600 mb-8">
            Con <strong><c:out value="${requestScope.tutorPerfil.nombreCompleto}"/></strong>.
            Elige la materia y el horario que prefieres.
        </p>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div class="lg:col-span-2 bg-white rounded-xl shadow p-8 space-y-6">
                <h3 class="text-2xl font-bold text-on-surface">Datos de la solicitud</h3>

                <div id="errorSolicitud" class="hidden flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg border border-red-100">
                    <span class="material-symbols-outlined text-base">error</span>
                    <span id="errorSolicitudMsg"></span>
                </div>

                <form id="formSolicitud" class="space-y-6" novalidate>
                    <input type="hidden" id="estudianteId" value="${requestScope.estudianteId}">
                    <input type="hidden" id="tutorId" value="${requestScope.tutorPerfil.id}">

                    <div>
                        <label for="codigoMateria" class="block text-sm font-bold text-indigo-900 mb-2">Materia *</label>
                        <select id="codigoMateria" name="codigoMateria" required
                                class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                            <option value="">-- Selecciona una materia --</option>
                            <c:forEach var="mat" items="${requestScope.tutorPerfil.materias}">
                                <option value="${mat.codigo}" <c:if test="${mat.codigo == materiaPreseleccionada}">selected</c:if>>
                                    <c:out value="${mat.nombre}"/> (<c:out value="${mat.codigo}"/>)
                                </option>
                            </c:forEach>
                        </select>
                        <p id="errorMateria" class="hidden text-xs text-red-600 mt-1">Selecciona una materia.</p>
                    </div>

                    <div>
                        <label for="fecha" class="block text-sm font-bold text-indigo-900 mb-2">Fecha *</label>
                        <input type="date" id="fecha" name="fecha" required
                               class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                        <p id="errorFecha" class="hidden text-xs text-red-600 mt-1">Indica una fecha válida.</p>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label for="horaInicio" class="block text-sm font-bold text-indigo-900 mb-2">Hora inicio *</label>
                            <input type="time" id="horaInicio" name="horaInicio" required
                                   class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                        </div>
                        <div>
                            <label for="horaFin" class="block text-sm font-bold text-indigo-900 mb-2">Hora fin *</label>
                            <input type="time" id="horaFin" name="horaFin" required
                                   class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary">
                        </div>
                    </div>
                    <p id="errorHorario" class="hidden text-xs text-red-600">La hora de inicio debe ser anterior a la hora de fin.</p>

                    <div>
                        <label for="comentario" class="block text-sm font-bold text-indigo-900 mb-2">Tu duda o motivo *</label>
                        <textarea id="comentario" name="comentario" rows="5" required maxlength="300"
                                  placeholder="Describe brevemente qué necesitas repasar o en qué tema necesitas ayuda..."
                                  class="w-full rounded-xl border border-slate-200 p-3 focus:ring-2 focus:ring-primary text-sm resize-none"></textarea>
                        <div class="flex justify-between items-center mt-1">
                            <p id="errorMotivo" class="hidden text-xs text-red-600">Describe tu motivo o duda.</p>
                            <p id="contadorComentario" class="text-xs text-slate-500 ml-auto">0 / 300</p>
                        </div>
                    </div>

                    <button type="submit" id="btnEnviar"
                            class="w-full bg-primary text-white font-bold py-3 rounded-xl hover:opacity-90 flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined">send</span>
                        Enviar Solicitud
                    </button>
                </form>
            </div>

            <div class="lg:col-span-1">
                <div class="bg-white rounded-xl shadow p-8 sticky top-24 space-y-4">
                    <h3 class="text-xl font-bold text-on-surface">Resumen del tutor</h3>
                    <div class="flex items-center gap-3">
                        <div class="w-12 h-12 rounded-full bg-primary-container text-white flex items-center justify-center font-bold">
                            <c:out value="${requestScope.tutorPerfil.nombreCompleto.substring(0,1).toUpperCase()}"/>
                        </div>
                        <div>
                            <p class="font-bold"><c:out value="${requestScope.tutorPerfil.nombreCompleto}"/></p>
                            <p class="text-xs text-slate-600"><c:out value="${requestScope.tutorPerfil.carrera}"/></p>
                        </div>
                    </div>
                    <p class="text-sm text-slate-600">
                        Semestre: <c:out value="${requestScope.tutorPerfil.semestreActual}"/>
                    </p>
                    <p class="text-xs text-slate-500 border-t pt-4">
                        Tu solicitud quedará en estado <strong>pendiente</strong> hasta que el tutor la acepte o rechace.
                    </p>
                </div>
            </div>
        </div>
    </div>
</main>

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
        <button type="button" onclick="cerrarConfirmacion()"
                class="w-full bg-primary text-white font-bold py-3 rounded-lg hover:opacity-90">
            Volver a Buscar Tutores
        </button>
    </div>
</div>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script src="${pageContext.request.contextPath}/js/validacion-campos.js"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';

    document.getElementById('fecha').min = new Date().toISOString().split('T')[0];

    OwlValidacion.inicializarContadorTextarea(
        document.getElementById('comentario'),
        document.getElementById('contadorComentario'),
        OwlValidacion.LIMITES.comentarioMentoriaMax
    );

    document.getElementById('formSolicitud').addEventListener('submit', async function (e) {
        e.preventDefault();
        if (!validarFormulario()) {
            return;
        }

        const btn = document.getElementById('btnEnviar');
        btn.disabled = true;
        ocultarErrorGlobal();

        const params = new URLSearchParams();
        params.append('estudianteId', document.getElementById('estudianteId').value);
        params.append('tutorId', document.getElementById('tutorId').value);
        params.append('codigoMateria', document.getElementById('codigoMateria').value);
        params.append('fecha', document.getElementById('fecha').value);
        params.append('horaInicio', document.getElementById('horaInicio').value);
        params.append('horaFin', document.getElementById('horaFin').value);
        params.append('comentario', document.getElementById('comentario').value.trim());

        try {
            const response = await fetch(contextPath + '/api/solicitudes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: params.toString()
            });

            const raw = await response.text();
            let data = {};
            try {
                data = raw ? JSON.parse(raw) : {};
            } catch (parseError) {
                data = {};
            }

            if (response.ok) {
                document.getElementById('modalConfirmacion').classList.remove('hidden');
                return;
            }

            mostrarErrorGlobal(data.error || raw || 'No se pudo enviar la solicitud. Intenta de nuevo.');
        } catch (err) {
            mostrarErrorGlobal('Error de conexión. Verifica tu red e intenta de nuevo.');
        } finally {
            btn.disabled = false;
        }
    });

    function validarFormulario() {
        let valido = true;
        ocultarErrores();

        const materia = document.getElementById('codigoMateria').value;
        const fecha = document.getElementById('fecha').value;
        const horaInicio = document.getElementById('horaInicio').value;
        const horaFin = document.getElementById('horaFin').value;
        const comentario = document.getElementById('comentario').value.trim();

        if (!materia) {
            document.getElementById('errorMateria').classList.remove('hidden');
            valido = false;
        }
        if (!fecha) {
            document.getElementById('errorFecha').classList.remove('hidden');
            valido = false;
        }
        if (horaInicio && horaFin && horaInicio >= horaFin) {
            document.getElementById('errorHorario').classList.remove('hidden');
            valido = false;
        }
        const errorComentario = OwlValidacion.validarComentarioMentoria(comentario);
        if (errorComentario) {
            const errorMotivo = document.getElementById('errorMotivo');
            errorMotivo.textContent = errorComentario;
            errorMotivo.classList.remove('hidden');
            valido = false;
        }

        return valido;
    }

    function ocultarErrores() {
        ['errorMateria', 'errorFecha', 'errorHorario', 'errorMotivo'].forEach(id => {
            const el = document.getElementById(id);
            el.classList.add('hidden');
            if (id === 'errorMotivo') {
                el.textContent = 'Describe tu motivo o duda.';
            }
        });
    }

    function mostrarErrorGlobal(mensaje) {
        document.getElementById('errorSolicitudMsg').textContent = mensaje;
        document.getElementById('errorSolicitud').classList.remove('hidden');
    }

    function ocultarErrorGlobal() {
        document.getElementById('errorSolicitud').classList.add('hidden');
    }

    function cerrarConfirmacion() {
        window.location.href = contextPath + '/estudiante/buscar-tutor';
    }
</script>

</body>
</html>
