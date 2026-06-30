<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Subir Material - OwlShare</title>
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

<%-- Protección de ruta --%>
<c:if test="${empty sessionScope.usuarioLogueado}">
    <c:redirect url="/login"/>
</c:if>

<%-- Header --%>
<header class="bg-white shadow-sm h-16 flex justify-between items-center px-8 sticky top-0 z-40">
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare</h1>
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
    <div class="max-w-4xl mx-auto space-y-6">

        <%-- Encabezado de sección --%>
        <div class="mb-8">
            <h2 class="text-4xl font-extrabold text-on-surface tracking-tight">Publicar nuevo material</h2>
            <p class="text-slate-600 mt-2">Comparte tus conocimientos con la comunidad académica.</p>
        </div>

        <%-- Mensaje de error del servidor --%>
        <c:if test="${not empty error}">
            <div class="flex items-center gap-3 bg-red-50 border border-red-200 text-red-700 px-5 py-4 rounded-xl">
                <span class="material-symbols-outlined">error</span>
                <span class="text-sm font-semibold"><c:out value="${error}"/></span>
            </div>
        </c:if>

        <%-- Mensajes flash --%>
        <c:if test="${not empty flashMensaje}">
            <div class="flex items-center gap-3 bg-green-50 border border-green-200 text-green-700 px-5 py-4 rounded-xl">
                <span class="material-symbols-outlined">check_circle</span>
                <span class="text-sm font-semibold"><c:out value="${flashMensaje}"/></span>
            </div>
        </c:if>
        <c:if test="${not empty flashError}">
            <div class="flex items-center gap-3 bg-red-50 border border-red-200 text-red-700 px-5 py-4 rounded-xl">
                <span class="material-symbols-outlined">error</span>
                <span class="text-sm font-semibold"><c:out value="${flashError}"/></span>
            </div>
        </c:if>

        <%-- Formulario --%>
        <div class="bg-white rounded-xl p-8 md:p-10 shadow-sm border border-slate-200">

            <%-- Identidad del creador --%>
            <div class="flex items-center gap-3 p-4 bg-slate-50 rounded-lg mb-8">
                <span class="material-symbols-outlined text-primary">account_circle</span>
                <div>
                    <span class="text-[10px] font-bold uppercase tracking-widest text-slate-500 block">Identidad del Creador</span>
                    <span class="text-on-surface font-semibold">
                        Autor: <c:out value="${requestScope.tutorPerfil.nombre}"/>
                        <c:if test="${not empty requestScope.tutorPerfil.apellido}">
                            <c:out value=" ${requestScope.tutorPerfil.apellido}"/>
                        </c:if>
                    </span>
                </div>
            </div>

            <form id="formSubir"
                  action="${pageContext.request.contextPath}/tutor/subir"
                  method="post"
                  enctype="multipart/form-data"
                  novalidate
                  class="space-y-8">

                <div class="grid grid-cols-1 md:grid-cols-2 gap-8">

                    <%-- Columna izquierda --%>
                    <div class="space-y-6">

                        <%-- Título --%>
                        <div class="flex flex-col gap-1">
                            <label for="titulo" class="text-sm font-bold text-on-surface px-1">
                                Título del material <span class="text-red-600">*</span>
                            </label>
                            <input id="titulo" name="titulo" type="text"
                                   placeholder="Ej. Guía Completa: Derivadas e Integrales"
                                   class="w-full bg-slate-50 border border-slate-200 rounded-lg py-3 px-4 focus:ring-2 focus:ring-primary focus:border-transparent text-on-surface placeholder:text-slate-400"/>
                            <div id="err-titulo" class="field-error items-center gap-1 text-red-600 text-xs px-1 mt-0.5">
                                <span class="material-symbols-outlined text-sm">error</span>
                                El título es obligatorio.
                            </div>
                        </div>

                        <%-- Categoría --%>
                        <div class="flex flex-col gap-1">
                            <label for="nombreMateria" class="text-sm font-bold text-on-surface px-1">Categoría académica</label>
                            <select id="nombreMateria" name="nombreMateria"
                                    class="w-full bg-slate-50 border border-slate-200 rounded-lg py-3 px-4 focus:ring-2 focus:ring-primary focus:border-transparent text-on-surface">
                                <option value="">-- Seleccionar categoría --</option>
                                <c:forEach var="cat" items="${categorias}">
                                    <option value="${cat.nombre}"><c:out value="${cat.nombre}"/></option>
                                </c:forEach>
                            </select>
                        </div>

                        <%-- Materia --%>
                        <div class="flex flex-col gap-1">
                            <label for="materia" class="text-sm font-bold text-on-surface px-1">
                                Materia <span class="text-red-600">*</span>
                            </label>
                            <select id="materia" name="materia" required
                                    class="w-full bg-slate-50 border border-slate-200 rounded-lg py-3 px-4 focus:ring-2 focus:ring-primary focus:border-transparent text-on-surface">
                                <option value="">-- Seleccionar materia --</option>
                                <c:forEach var="m" items="${materiasOpciones}">
                                    <option value="${m.codigo}"><c:out value="${m.nombre}"/> (<c:out value="${m.codigo}"/>)</option>
                                </c:forEach>
                            </select>
                            <div id="err-materia" class="field-error items-center gap-1 text-red-600 text-xs px-1 mt-0.5">
                                <span class="material-symbols-outlined text-sm">error</span>
                                Debes seleccionar la materia del material.
                            </div>
                            <c:if test="${empty materiasOpciones}">
                                <p class="text-xs text-amber-700 mt-1">
                                    No tienes materias registradas en tu perfil. Actualiza tu registro como tutor para poder publicar material.
                                </p>
                            </c:if>
                        </div>

                        <%-- Precio --%>
                        <div class="flex flex-col gap-1">
                            <label for="costo" class="text-sm font-bold text-on-surface px-1">
                                Precio sugerido (USD) <span class="text-red-600">*</span>
                            </label>
                            <div class="relative">
                                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-bold">$</span>
                                <input id="costo" name="costo" type="number" step="0.01" min="0"
                                       placeholder="0.00"
                                       class="w-full bg-slate-50 border border-slate-200 rounded-lg py-3 pl-8 pr-4 focus:ring-2 focus:ring-primary focus:border-transparent text-on-surface placeholder:text-slate-400"/>
                            </div>
                            <div id="err-costo" class="field-error items-center gap-1 text-red-600 text-xs px-1 mt-0.5">
                                <span class="material-symbols-outlined text-sm">error</span>
                                El precio es obligatorio y debe ser mayor o igual a 0.
                            </div>
                        </div>

                    </div>

                    <%-- Columna derecha --%>
                    <div class="space-y-6">

                        <%-- Descripción --%>
                        <div class="flex flex-col gap-1">
                            <label for="descripcion" class="text-sm font-bold text-on-surface px-1">
                                Descripción del contenido <span class="text-red-600">*</span>
                            </label>
                            <textarea id="descripcion" name="descripcion" rows="8"
                                      placeholder="Explica brevemente de qué trata este material, a quién va dirigido y qué temas cubre..."
                                      class="w-full bg-slate-50 border border-slate-200 rounded-lg py-3 px-4 focus:ring-2 focus:ring-primary focus:border-transparent text-on-surface placeholder:text-slate-400 resize-none"></textarea>
                            <div id="err-descripcion" class="field-error items-center gap-1 text-red-600 text-xs px-1 mt-0.5">
                                <span class="material-symbols-outlined text-sm">error</span>
                                La descripción es obligatoria.
                            </div>
                        </div>

                    </div>
                </div>

                <%-- Dropzone PDF --%>
                <div class="flex flex-col gap-1">
                    <label class="text-sm font-bold text-on-surface px-1">
                        Archivo del material (.pdf) <span class="text-red-600">*</span>
                    </label>
                    <label for="archivo"
                           id="dropzone"
                           class="dropzone-container border-2 border-dashed border-slate-300 rounded-xl p-10 flex flex-col items-center justify-center bg-slate-50 hover:bg-slate-100 transition-colors cursor-pointer group">
                        <div class="w-16 h-16 bg-indigo-50 rounded-full flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                            <span class="material-symbols-outlined text-primary text-3xl">picture_as_pdf</span>
                        </div>
                        <p id="dropzone-label" class="text-on-surface font-semibold">Arrastra y suelta tu archivo PDF aquí</p>
                        <p class="text-slate-600 text-sm mt-1">O haz clic para seleccionar desde tu ordenador</p>
                        <p class="text-[10px] text-slate-500 uppercase tracking-widest mt-4">Tamaño máximo: 25 MB</p>
                        <input id="archivo" name="archivo" type="file" accept=".pdf,application/pdf" class="hidden"/>
                    </label>
                    <div id="err-archivo" class="field-error items-center gap-1 text-red-600 text-xs px-1 mt-0.5">
                        <span class="material-symbols-outlined text-sm">error</span>
                        Debes adjuntar un archivo PDF.
                    </div>
                </div>

                <%-- Acciones --%>
                <div class="pt-8 flex items-center justify-end gap-6 border-t border-slate-200">
                    <a href="${pageContext.request.contextPath}/tutor/inicio"
                       class="text-slate-600 font-bold hover:text-primary transition-colors">
                        Cancelar
                    </a>
                    <button type="submit"
                            class="flex items-center gap-2 bg-primary text-white px-10 py-3 rounded-lg font-bold shadow-lg hover:bg-primary-container transition-all">
                        <span class="material-symbols-outlined">publish</span>
                        Subir material
                    </button>
                </div>

            </form>
        </div>

    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">
    © 2025 OwlShare · Plataforma Educativa Colaborativa
</footer>

<script>
    (function () {
        const form = document.getElementById('formSubir');
        const fields = {
            titulo:      { el: document.getElementById('titulo'),      err: document.getElementById('err-titulo') },
            descripcion: { el: document.getElementById('descripcion'), err: document.getElementById('err-descripcion') },
            materia:     { el: document.getElementById('materia'),     err: document.getElementById('err-materia') },
            costo:       { el: document.getElementById('costo'),       err: document.getElementById('err-costo') },
            archivo:     { el: document.getElementById('archivo'),     err: document.getElementById('err-archivo') }
        };

        function showError(key, show) {
            const { el, err } = fields[key];
            err.classList.toggle('visible', show);
            if (show) {
                el.classList.add('invalid-field');
            } else {
                el.classList.remove('invalid-field');
            }
        }

        function validateTitulo()      { return fields.titulo.el.value.trim() !== ''; }
        function validateDescripcion() { return fields.descripcion.el.value.trim() !== ''; }
        function validateMateria()     { return fields.materia.el.value !== ''; }
        function validateCosto()       { const v = fields.costo.el.value; return v !== '' && parseFloat(v) >= 0; }
        function validateArchivo()     { return fields.archivo.el.files.length > 0; }

        Object.keys(fields).forEach(key => {
            const input = fields[key].el;
            const event = (key === 'archivo' || key === 'materia') ? 'change' : 'input';
            input.addEventListener(event, () => {
                const validators = {
                    titulo: validateTitulo,
                    descripcion: validateDescripcion,
                    materia: validateMateria,
                    costo: validateCosto,
                    archivo: validateArchivo
                };
                showError(key, !validators[key]());
            });
        });

        form.addEventListener('submit', function (e) {
            const checks = {
                titulo:      validateTitulo(),
                descripcion: validateDescripcion(),
                materia:     validateMateria(),
                costo:       validateCosto(),
                archivo:     validateArchivo()
            };
            const allValid = Object.entries(checks).every(([key, ok]) => { showError(key, !ok); return ok; });
            if (!allValid) {
                e.preventDefault();
                const firstErr = Object.keys(checks).find(k => !checks[k]);
                if (firstErr) fields[firstErr].el.focus();
            }
        });

        // Actualizar label del dropzone al seleccionar archivo
        fields.archivo.el.addEventListener('change', function () {
            const label = document.getElementById('dropzone-label');
            label.textContent = this.files.length > 0 ? this.files[0].name : 'Arrastra y suelta tu archivo PDF aquí';
        });
    })();
</script>

</body>
</html>
