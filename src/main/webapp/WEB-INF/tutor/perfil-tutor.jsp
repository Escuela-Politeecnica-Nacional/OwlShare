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
    <c:redirect url="/login"/>
</c:if>

<%-- Header --%>
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

<%-- Main Content --%>
<main class="flex-1 p-10">
    <div class="max-w-3xl mx-auto">
        <h2 class="text-4xl font-extrabold text-on-surface mb-8">Mi Perfil</h2>

        <c:if test="${not empty exito}">
            <div class="mb-6 bg-green-50 border border-green-200 text-green-800 rounded-lg px-4 py-3 text-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-sm">check_circle</span>
                <c:out value="${exito}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="mb-6 bg-red-50 border border-red-200 text-red-800 rounded-lg px-4 py-3 text-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-sm">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

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
            <h3 class="text-2xl font-bold text-primary mb-2">Materias que Ofreces</h3>
            <p class="text-sm text-slate-600 mb-6">
                Indica las asignaturas de <strong><c:out value="${requestScope.tutorPerfil.carrera.nombre}"/></strong>
                que estás capacitado para enseñar. Solo puedes elegir materias de semestres anteriores al
                <strong><c:out value="${requestScope.tutorPerfil.semestre.nombre}"/></strong>.
            </p>

            <form id="formMateriasPerfil" method="post" action="${pageContext.request.contextPath}/tutor/perfil" class="space-y-4">
                <div id="chipsPerfil" class="flex flex-wrap gap-2 min-h-[2rem] p-3 bg-slate-50 rounded-lg border border-slate-200"></div>

                <select id="selectMateriaPerfil"
                        class="w-full rounded-lg border border-slate-200 focus:border-primary focus:ring focus:ring-primary/20 text-sm outline-none p-3 bg-white">
                    <option value="">— Selecciona una materia para agregar —</option>
                </select>

                <p id="materiasVaciasPerfil" class="hidden text-center py-6 text-slate-500 bg-slate-50 rounded-lg text-sm">
                    No hay materias disponibles para tu carrera y semestre actuales.
                </p>

                <p id="contadorMateriasPerfil" class="text-xs text-slate-600 flex items-center gap-1">
                    <span class="material-symbols-outlined text-sm">check_circle</span>
                    <span id="contadorMateriasPerfilMsg">0 materias seleccionadas</span>
                </p>

                <p id="errorMateriasPerfil" class="hidden text-sm text-red-600 font-medium flex items-center gap-1">
                    <span class="material-symbols-outlined text-base">error</span>
                    <span id="errorMateriasPerfilMsg">Debes seleccionar al menos una materia.</span>
                </p>

                <button type="submit"
                        class="inline-flex items-center gap-2 bg-primary text-white font-bold py-3 px-6 rounded-lg hover:bg-primary-container transition-all">
                    <span class="material-symbols-outlined text-sm">save</span>
                    Guardar materias
                </button>
            </form>
        </div>
    </div>
</main>

<script type="application/json" id="materiasPermitidasJsonPerfil"><c:out value="${materiasPermitidasJson}" escapeXml="false"/></script>
<script type="application/json" id="materiasSeleccionadasJsonPerfil"><c:out value="${materiasSeleccionadasJson}" escapeXml="false"/></script>

<script>
    (function () {
        var materiasPermitidas = JSON.parse(document.getElementById('materiasPermitidasJsonPerfil').textContent || '[]');
        var seleccionInicial = JSON.parse(document.getElementById('materiasSeleccionadasJsonPerfil').textContent || '[]');
        var selectMateria = document.getElementById('selectMateriaPerfil');
        var chipsContainer = document.getElementById('chipsPerfil');
        var materiasVacias = document.getElementById('materiasVaciasPerfil');
        var contadorMsg = document.getElementById('contadorMateriasPerfilMsg');
        var errorMaterias = document.getElementById('errorMateriasPerfil');
        var form = document.getElementById('formMateriasPerfil');

        var materiasPorCodigo = {};
        materiasPermitidas.forEach(function (m) {
            materiasPorCodigo[m.codigo] = m;
        });

        var selectedCodigos = new Set(seleccionInicial);

        function actualizarContador() {
            var n = selectedCodigos.size;
            contadorMsg.textContent = n + (n === 1 ? ' materia seleccionada' : ' materias seleccionadas');
        }

        function sincronizarHiddenInputs() {
            Array.from(form.querySelectorAll('input[name="materias"]')).forEach(function (el) {
                el.remove();
            });
            selectedCodigos.forEach(function (codigo) {
                var hidden = document.createElement('input');
                hidden.type = 'hidden';
                hidden.name = 'materias';
                hidden.value = codigo;
                form.appendChild(hidden);
            });
        }

        function renderChips() {
            chipsContainer.innerHTML = '';
            if (selectedCodigos.size === 0) {
                chipsContainer.innerHTML = '<span class="text-sm text-slate-400 italic">Aún no has seleccionado materias.</span>';
            } else {
                selectedCodigos.forEach(function (codigo) {
                    var materia = materiasPorCodigo[codigo];
                    var chip = document.createElement('span');
                    chip.className = 'inline-flex items-center gap-2 bg-secondary/10 text-secondary px-3 py-1.5 rounded-lg font-semibold text-sm';
                    chip.innerHTML = '<span>' + (materia ? materia.codigo + ' — ' + materia.nombre : codigo) + '</span>'
                        + '<button type="button" class="hover:text-red-600" data-codigo="' + codigo + '" title="Quitar">'
                        + '<span class="material-symbols-outlined text-sm">close</span></button>';
                    chipsContainer.appendChild(chip);
                });
            }
            sincronizarHiddenInputs();
            actualizarContador();
            actualizarSelect();
        }

        function actualizarSelect() {
            selectMateria.innerHTML = '';
            var placeholder = document.createElement('option');
            placeholder.value = '';
            placeholder.textContent = materiasPermitidas.length === 0
                ? '— Sin materias disponibles —'
                : '— Selecciona una materia para agregar —';
            selectMateria.appendChild(placeholder);

            materiasPermitidas.forEach(function (m) {
                if (selectedCodigos.has(m.codigo)) {
                    return;
                }
                var opt = document.createElement('option');
                opt.value = m.codigo;
                opt.textContent = m.codigo + ' — ' + m.nombre + ' (' + m.semestre + '.º sem.)';
                selectMateria.appendChild(opt);
            });

            selectMateria.disabled = materiasPermitidas.length === 0;
            materiasVacias.classList.toggle('hidden', materiasPermitidas.length > 0);
        }

        chipsContainer.addEventListener('click', function (e) {
            var btn = e.target.closest('button[data-codigo]');
            if (!btn) {
                return;
            }
            selectedCodigos.delete(btn.getAttribute('data-codigo'));
            renderChips();
        });

        selectMateria.addEventListener('change', function () {
            if (!this.value) {
                return;
            }
            selectedCodigos.add(this.value);
            this.value = '';
            renderChips();
            errorMaterias.classList.add('hidden');
        });

        form.addEventListener('submit', function (e) {
            if (selectedCodigos.size === 0) {
                e.preventDefault();
                errorMaterias.classList.remove('hidden');
            }
        });

        renderChips();
    })();
</script>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>
</body>
</html>
