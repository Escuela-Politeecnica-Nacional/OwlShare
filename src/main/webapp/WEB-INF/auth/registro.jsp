<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Registro - OwlShare</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = {
            theme: { extend: { colors: {
                "primary": "#24389c", "primary-container": "#3f51b5", "on-primary": "#ffffff",
                "surface": "#f7f9fc", "on-surface": "#191c1e", "error": "#ba1a1a"
            }}}
        }
    </script>
</head>
<body class="bg-surface text-on-surface min-h-screen flex items-center justify-center p-4">

<div class="max-w-2xl w-full bg-white rounded-2xl shadow-xl p-8 md:p-12">
    <div class="text-center mb-10">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-indigo-50 text-primary mb-4">
            <span class="material-symbols-outlined text-4xl">person_add</span>
        </div>
        <h1 class="text-3xl font-extrabold text-indigo-900 tracking-tight">Crea tu cuenta</h1>
        <p class="text-slate-500 mt-2">Únete a la comunidad de OwlShare</p>
    </div>

    <c:if test="${not empty error}">
        <div class="mb-6 flex items-center gap-3 bg-red-50 text-red-700 text-sm font-medium px-4 py-3 rounded-lg border border-red-100">
            <span class="material-symbols-outlined text-base">error</span>
            <c:out value="${error}"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/registro" method="POST" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <%-- Email --%>
            <div class="md:col-span-2">
                <label class="block text-sm font-bold text-indigo-900 mb-2">Correo Electrónico *</label>
                <input type="email" id="emailRegistro" name="email" required
                       class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border"
                       placeholder="ejemplo@correo.com">
                <p id="emailErrorReg" class="hidden mt-1 text-xs text-red-600 font-medium flex items-center gap-1">
                    <span class="material-symbols-outlined text-sm">error</span>
                    <span id="emailErrorMsgReg">Ingresa un correo válido</span>
                </p>
            </div>

            <%-- Password --%>
            <div class="md:col-span-2">
                <label class="block text-sm font-bold text-indigo-900 mb-2">Contraseña *</label>
                <div class="relative">
                    <input type="password" id="passwordRegistro" name="password" required
                           class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border pr-10"
                           placeholder="••••••••">
                    <button type="button" id="togglePasswordReg" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500 hover:text-indigo-600 transition-colors">
                        <span class="material-symbols-outlined text-sm">visibility</span>
                    </button>
                </div>
            </div>

            <%-- Confirmar Contraseña --%>
            <div class="md:col-span-2">
                <label class="block text-sm font-bold text-indigo-900 mb-2">Confirmar Contraseña *</label>
                <div class="relative">
                    <input type="password" id="confirmPasswordRegistro" name="confirmPassword" required
                           class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border pr-10"
                           placeholder="••••••••">
                    <button type="button" id="toggleConfirmReg" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-500 hover:text-indigo-600 transition-colors">
                        <span class="material-symbols-outlined text-sm">visibility</span>
                    </button>
                </div>
                <p id="confirmErrorReg" class="hidden mt-1 text-xs text-red-600 font-medium flex items-center gap-1">
                    <span class="material-symbols-outlined text-sm">error</span>
                    <span id="confirmErrorMsgReg">Las contraseñas no coinciden</span>
                </p>
            </div>

            <%-- Nombre --%>
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">Primer Nombre *</label>
                <input type="text" name="nombre" required
                       class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border"
                       placeholder="Nombre">
            </div>

            <%-- Segundo Nombre --%>
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">Segundo Nombre</label>
                <input type="text" name="segundoNombre"
                       class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border"
                       placeholder="Segundo Nombre">
            </div>

            <%-- Apellido --%>
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">Primer Apellido *</label>
                <input type="text" name="apellido" required
                       class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border"
                       placeholder="Apellido">
            </div>

            <%-- Segundo Apellido --%>
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">Segundo Apellido</label>
                <input type="text" name="segundoApellido"
                       class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border"
                       placeholder="Segundo Apellido">
            </div>

            <%-- Semestre --%>
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">
                    Semestre <span id="semestreLabelExtra" class="text-slate-400 font-normal text-xs"></span>
                </label>
                <select id="selectSemestreRegistro" name="semestre"
                        class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border">
                    <option value="">-- Selecciona tu semestre --</option>
                    <c:forEach var="sem" items="${semestres}">
                        <option value="${sem.name()}" data-num="${sem.numero}"><c:out value="${sem.nombre}"/></option>
                    </c:forEach>
                </select>
                <p id="hintSemestreTutor" class="hidden text-xs text-slate-500 mt-1">
                    Solo estudiantes del <strong>2.º al 9.º semestre</strong> pueden ser tutores. Solo podrás elegir materias de semestres <strong>anteriores</strong> al que cursas.
                </p>
            </div>

            <%-- Carrera --%>
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">
                    Carrera <span id="carreraLabelExtra" class="text-slate-400 font-normal text-xs"></span>
                </label>
                <select id="selectCarreraRegistro" name="carrera"
                        class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border">
                    <option value="">-- Selecciona tu carrera --</option>
                    <c:forEach var="car" items="${carreras}">
                        <option value="${car.name()}"><c:out value="${car.nombre}"/></option>
                    </c:forEach>
                </select>
            </div>

            <%-- Rol --%>
            <div class="md:col-span-2">
                <label class="block text-sm font-bold text-indigo-900 mb-2">Rol deseado *</label>
                <select id="rolSelect" name="rol" required
                        class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border">
                    <option value="" disabled selected>Selecciona un rol</option>
                    <option value="ESTUDIANTE">Estudiante</option>
                    <option value="TUTOR">Tutor</option>
                </select>
            </div>
        </div>

        <%-- Sección de materias (solo TUTOR): lista depende de la carrera elegida arriba --%>
        <div id="seccionMaterias" class="hidden space-y-4">
            <div>
                <label class="block text-sm font-bold text-indigo-900 mb-2">Materias relacionadas *</label>
                <p class="text-xs text-slate-500 mb-2">Misma <strong>carrera</strong> y <strong>semestre</strong> que arriba: solo asignaturas de semestres ya cursados (inferiores al tuyo).</p>
                <div id="chipsRegistro" class="flex flex-wrap gap-2 mb-3 min-h-[2rem]"></div>
                <select id="selectMateriaRegistro" disabled
                        class="w-full rounded-xl border-slate-200 focus:border-indigo-500 focus:ring focus:ring-indigo-200 transition-all text-sm outline-none p-3 border">
                    <option value="">— Primero confirma tu carrera arriba —</option>
                </select>
                <p id="errorMaterias" class="hidden mt-2 text-sm text-red-600 font-medium flex items-center gap-1">
                    <span class="material-symbols-outlined text-base">error</span>
                    <span id="errorMateriasMsg">Debes seleccionar al menos una materia.</span>
                </p>
            </div>
        </div>

        <button type="submit" id="btnRegistro"
                class="w-full bg-primary text-white font-bold py-3.5 rounded-xl hover:bg-primary-container transition-all shadow-lg shadow-indigo-200 mt-4">
            Registrarse
        </button>

        <p class="text-center text-sm text-slate-500">
            ¿Ya tienes una cuenta?
            <a href="${pageContext.request.contextPath}/login" class="text-primary font-bold hover:underline">Inicia sesión</a>
        </p>
    </form>
</div>

<script type="application/json" id="materiasPorCarreraJsonReg"><c:out value="${materiasPorCarreraJson}" escapeXml="false"/></script>

<script>
    // ── Validaciones de Email y Contraseña ──
    (function () {
        var emailInput = document.getElementById('emailRegistro');
        var passwordInput = document.getElementById('passwordRegistro');
        var confirmInput = document.getElementById('confirmPasswordRegistro');
        var togglePasswordBtn = document.getElementById('togglePasswordReg');
        var toggleConfirmBtn = document.getElementById('toggleConfirmReg');
        var emailError = document.getElementById('emailErrorReg');
        var confirmError = document.getElementById('confirmErrorReg');
        var form = document.querySelector('form');

        // Validar email en tiempo real
        function validateEmail(email) {
            var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return re.test(email);
        }

        emailInput.addEventListener('blur', function () {
            if (this.value && !validateEmail(this.value)) {
                emailError.classList.remove('hidden');
            } else {
                emailError.classList.add('hidden');
            }
        });

        // Validar coincidencia de contraseñas en tiempo real
        function validatePasswords() {
            if (confirmInput.value && passwordInput.value !== confirmInput.value) {
                confirmError.classList.remove('hidden');
                return false;
            } else {
                confirmError.classList.add('hidden');
                return true;
            }
        }

        passwordInput.addEventListener('input', validatePasswords);
        confirmInput.addEventListener('input', validatePasswords);

        // Toggle ver/ocultar contraseña
        togglePasswordBtn.addEventListener('click', function (e) {
            e.preventDefault();
            var icon = this.querySelector('.material-symbols-outlined');
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.textContent = 'visibility_off';
            } else {
                passwordInput.type = 'password';
                icon.textContent = 'visibility';
            }
        });

        toggleConfirmBtn.addEventListener('click', function (e) {
            e.preventDefault();
            var icon = this.querySelector('.material-symbols-outlined');
            if (confirmInput.type === 'password') {
                confirmInput.type = 'text';
                icon.textContent = 'visibility_off';
            } else {
                confirmInput.type = 'password';
                icon.textContent = 'visibility';
            }
        });

        // Validar al enviar
        form.addEventListener('submit', function (e) {
            if (emailInput.value && !validateEmail(emailInput.value)) {
                e.preventDefault();
                emailError.classList.remove('hidden');
                emailInput.focus();
                return;
            }
            if (passwordInput.value !== confirmInput.value) {
                e.preventDefault();
                confirmError.classList.remove('hidden');
                confirmInput.focus();
                return;
            }
        });
    })();
</script>

<script>
    (function () {
        var rolSelect        = document.getElementById('rolSelect');
        var seccionMaterias  = document.getElementById('seccionMaterias');
        var selectCarrera    = document.getElementById('selectCarreraRegistro');
        var selectSemestre   = document.getElementById('selectSemestreRegistro');
        var selectMateria    = document.getElementById('selectMateriaRegistro');
        var chipsContainer   = document.getElementById('chipsRegistro');
        var errorMaterias    = document.getElementById('errorMaterias');
        var form             = document.querySelector('form');
        var materiasPorCarrera = JSON.parse(document.getElementById('materiasPorCarreraJsonReg').textContent);

        var SEMESTRE_MIN_TUTOR = 2;
        var SEMESTRE_MAX_TUTOR = 9;

        var selectedCodigos = new Set();

        function semestreValidoParaTutor(numero) {
            return numero >= SEMESTRE_MIN_TUTOR && numero <= SEMESTRE_MAX_TUTOR;
        }

        function actualizarOpcionesSemestre() {
            var esTutor = rolSelect.value === 'TUTOR';
            Array.from(selectSemestre.options).forEach(function (opt, index) {
                if (index === 0) {
                    return;
                }
                var numero = parseInt(opt.getAttribute('data-num'), 10);
                var permitido = !esTutor || semestreValidoParaTutor(numero);
                opt.hidden = !permitido;
                opt.disabled = !permitido;
            });

            if (esTutor && selectSemestre.value) {
                var seleccionado = selectSemestre.options[selectSemestre.selectedIndex];
                var numeroSeleccionado = parseInt(seleccionado.getAttribute('data-num'), 10);
                if (!semestreValidoParaTutor(numeroSeleccionado)) {
                    selectSemestre.value = '';
                }
            }
        }

        function topeSemestreTutor() {
            if (rolSelect.value !== 'TUTOR') return null;
            var opt = selectSemestre.options[selectSemestre.selectedIndex];
            if (!opt || !opt.value) return null;
            var n = parseInt(opt.getAttribute('data-num'), 10);
            return isNaN(n) ? null : n;
        }

        function appendHintOption(text) {
            var h = document.createElement('option');
            h.value = '';
            h.disabled = true;
            h.textContent = text;
            selectMateria.appendChild(h);
        }

        function refillMaterias() {
            selectMateria.innerHTML = '';
            var def = document.createElement('option');
            def.value = '';
            def.textContent = '— Seleccionar materia —';
            selectMateria.appendChild(def);

            if (rolSelect.value !== 'TUTOR') {
                return;
            }

            var car = selectCarrera.value;
            if (!car || !materiasPorCarrera[car]) {
                appendHintOption('— Primero elige tu carrera arriba —');
                return;
            }

            var tope = topeSemestreTutor();
            if (tope == null) {
                appendHintOption('— Primero elige tu semestre arriba —');
                return;
            }
            if (!semestreValidoParaTutor(tope)) {
                if (tope <= 1) {
                    appendHintOption('— En 1.er semestre no puedes ofrecer materias —');
                } else {
                    appendHintOption('— Solo tutores del 2.º al 9.º semestre —');
                }
                return;
            }

            var list = materiasPorCarrera[car].slice().filter(function (m) {
                return m.semestre < tope;
            }).sort(function (a, b) {
                if (a.semestre !== b.semestre) return a.semestre - b.semestre;
                return a.nombre.localeCompare(b.nombre);
            });

            if (list.length === 0) {
                appendHintOption('— No hay materias de semestres anteriores para tu caso —');
                return;
            }

            list.forEach(function (m) {
                var opt = document.createElement('option');
                opt.value = m.codigo;
                opt.setAttribute('data-nombre', m.nombre);
                opt.textContent = m.nombre + ' — ' + m.codigo;
                selectMateria.appendChild(opt);
            });
        }

        function clearMaterias() {
            selectedCodigos.forEach(function (cod) {
                var h = document.getElementById('hidden_mat_' + cod.replace(/[^a-zA-Z0-9]/g, '_'));
                if (h) h.remove();
            });
            selectedCodigos.clear();
            chipsContainer.innerHTML = '';
            errorMaterias.classList.add('hidden');
        }

        function syncRolUi() {
            var hint = document.getElementById('hintSemestreTutor');
            var extraSemestre = document.getElementById('semestreLabelExtra');
            var extraCarrera = document.getElementById('carreraLabelExtra');
            var esTutor = rolSelect.value === 'TUTOR';

            extraSemestre.textContent = esTutor ? '(obligatorio)' : '';
            extraCarrera.textContent = esTutor ? '(obligatorio)' : '';
            hint.classList.toggle('hidden', !esTutor);

            // Campos exclusivos de tutor: requeridos y habilitados solo si el rol es TUTOR;
            // deshabilitados cuando el rol no es TUTOR para que no se envíen con el formulario.
            selectSemestre.required = esTutor;
            selectCarrera.required = esTutor;
            selectMateria.disabled = !esTutor;
            actualizarOpcionesSemestre();
        }

        rolSelect.addEventListener('change', function () {
            if (this.value === 'TUTOR') {
                seccionMaterias.classList.remove('hidden');
                syncRolUi();
                refillMaterias();
            } else {
                seccionMaterias.classList.add('hidden');
                clearMaterias();
                syncRolUi();
            }
        });

        selectSemestre.addEventListener('change', function () {
            if (rolSelect.value === 'TUTOR') {
                clearMaterias();
                refillMaterias();
            }
        });

        selectCarrera.addEventListener('change', function () {
            if (rolSelect.value === 'TUTOR') {
                clearMaterias();
                refillMaterias();
            }
        });

        selectMateria.addEventListener('change', function () {
            var opt = this.options[this.selectedIndex];
            var cod = opt.value;
            var nombre = opt.getAttribute('data-nombre');
            this.value = '';
            if (!cod || selectedCodigos.has(cod)) return;
            selectedCodigos.add(cod);
            errorMaterias.classList.add('hidden');

            var chip = document.createElement('span');
            chip.className = 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-semibold text-white';
            chip.style.backgroundColor = '#56C7E6';

            var hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = 'materias';
            hidden.value = cod;
            hidden.id = 'hidden_mat_' + cod.replace(/[^a-zA-Z0-9]/g, '_');

            var label = document.createElement('span');
            label.textContent = nombre || cod;

            var btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'ml-1 text-white/80 hover:text-white font-bold leading-none';
            btn.textContent = '×';
            btn.addEventListener('click', function () {
                selectedCodigos.delete(cod);
                chip.remove();
                var h = document.getElementById('hidden_mat_' + cod.replace(/[^a-zA-Z0-9]/g, '_'));
                if (h) h.remove();
            });

            chip.appendChild(label);
            chip.appendChild(btn);
            chipsContainer.appendChild(chip);
            form.appendChild(hidden);
        });

        form.addEventListener('submit', function (e) {
            if (rolSelect.value === 'TUTOR') {
                if (!selectSemestre.value) {
                    e.preventDefault();
                    document.getElementById('errorMateriasMsg').textContent = 'Selecciona tu semestre.';
                    errorMaterias.classList.remove('hidden');
                    selectSemestre.focus();
                    return;
                }
                var tn = topeSemestreTutor();
                if (tn !== null && !semestreValidoParaTutor(tn)) {
                    e.preventDefault();
                    document.getElementById('errorMateriasMsg').textContent =
                        tn <= 1
                            ? 'En primer semestre no puedes ofrecer materias como tutor; elige otro semestre o regístrate como estudiante.'
                            : 'Solo estudiantes del 2.º al 9.º semestre pueden registrarse como tutores.';
                    errorMaterias.classList.remove('hidden');
                    selectSemestre.focus();
                    return;
                }
                if (!selectCarrera.value) {
                    e.preventDefault();
                    document.getElementById('errorMateriasMsg').textContent = 'Selecciona tu carrera.';
                    errorMaterias.classList.remove('hidden');
                    selectCarrera.focus();
                    return;
                }
                if (selectedCodigos.size === 0) {
                    e.preventDefault();
                    document.getElementById('errorMateriasMsg').textContent = 'Debes seleccionar al menos una materia.';
                    errorMaterias.classList.remove('hidden');
                    seccionMaterias.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        });
    })();
</script>
</body>
</html>
