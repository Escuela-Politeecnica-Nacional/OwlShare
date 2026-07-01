<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Moderación de Materiales - OwlShare</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <script>
        tailwind.config = { theme: { extend: { colors: {
            "primary": "#24389c", "surface": "#f7f9fc", "on-surface": "#191c1e"
        }}}}
    </script>
</head>
<body class="bg-surface text-on-surface min-h-screen flex flex-col">

<header class="bg-white shadow-sm h-16 flex justify-between items-center px-8 sticky top-0 z-40">
    <h1 class="text-2xl font-extrabold text-indigo-900">OwlShare · Admin</h1>
    <div class="flex items-center gap-4">
        <a href="${pageContext.request.contextPath}/admin/inicio" class="text-slate-600 hover:text-indigo-600">
            <span class="material-symbols-outlined">home</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="text-slate-600 hover:text-red-500">
            <span class="material-symbols-outlined">logout</span>
        </a>
    </div>
</header>

<main class="flex-1 p-10">
    <div class="max-w-6xl mx-auto space-y-6">
        <div>
            <h2 class="text-4xl font-extrabold mb-2">Moderación de materiales</h2>
            <p class="text-slate-600">Revisa los materiales pendientes y define si quedan disponibles para estudiantes.</p>
        </div>

        <c:if test="${not empty exito}">
            <div class="bg-green-50 border border-green-200 text-green-800 rounded-lg px-4 py-3 text-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-sm">check_circle</span>
                <c:out value="${exito}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-800 rounded-lg px-4 py-3 text-sm flex items-center gap-2">
                <span class="material-symbols-outlined text-sm">error</span>
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty materiales}">
                <div class="bg-white rounded-xl shadow border border-slate-200 p-16 text-center">
                    <span class="material-symbols-outlined text-5xl text-slate-300 block mb-4">task_alt</span>
                    <h3 class="text-xl font-bold text-slate-600">No hay materiales pendientes</h3>
                    <p class="text-slate-500 mt-2">Todos los materiales enviados ya fueron revisados.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="space-y-6">
                    <c:forEach var="material" items="${materiales}">
                        <div class="bg-white rounded-xl shadow border border-slate-200 p-6">
                            <div class="flex flex-wrap justify-between gap-4 mb-4">
                                <div>
                                    <span class="text-xs font-bold uppercase text-slate-400 tracking-widest">
                                        <c:out value="${material.nombreMateria}"/>
                                    </span>
                                    <h3 class="text-xl font-bold text-on-surface mt-1">
                                        <c:out value="${material.titulo}"/>
                                    </h3>
                                    <p class="text-sm text-slate-500 mt-1">
                                        Tutor: <strong><c:out value="${material.nombreTutor}"/></strong>
                                        · Enviado: <c:out value="${material.fechaEnvio}"/>
                                    </p>
                                </div>
                                <div class="text-right">
                                    <span class="text-lg font-bold text-primary">
                                        <c:choose>
                                            <c:when test="${material.gratis}">Gratis</c:when>
                                            <c:otherwise>$<c:out value="${material.costoFormateado}"/></c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>

                            <p class="text-sm text-slate-600 mb-6 leading-relaxed">
                                <c:out value="${material.descripcion}"/>
                            </p>

                            <div class="flex flex-wrap items-end gap-4 border-t border-slate-100 pt-4">
                                <form method="post" action="${pageContext.request.contextPath}/admin/materiales/revisar"
                                      class="inline">
                                    <input type="hidden" name="idMaterial" value="${material.id}"/>
                                    <input type="hidden" name="accion" value="aprobar"/>
                                    <button type="submit"
                                            class="inline-flex items-center gap-2 bg-green-600 text-white font-bold px-5 py-2.5 rounded-lg hover:opacity-90">
                                        <span class="material-symbols-outlined text-sm">check_circle</span>
                                        Aprobar
                                    </button>
                                </form>

                                <form method="post" action="${pageContext.request.contextPath}/admin/materiales/revisar"
                                      class="flex-1 min-w-[280px] flex flex-wrap items-end gap-3">
                                    <input type="hidden" name="idMaterial" value="${material.id}"/>
                                    <input type="hidden" name="accion" value="rechazar"/>
                                    <div class="flex-1 min-w-[200px]">
                                        <label class="block text-xs font-bold text-slate-600 mb-1">Motivo del rechazo</label>
                                        <input type="text" name="comentario" required maxlength="1000"
                                               placeholder="Indica por qué no se aprueba..."
                                               class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-red-200 focus:border-red-400"/>
                                    </div>
                                    <button type="submit"
                                            class="inline-flex items-center gap-2 bg-red-600 text-white font-bold px-5 py-2.5 rounded-lg hover:opacity-90">
                                        <span class="material-symbols-outlined text-sm">cancel</span>
                                        Rechazar
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<footer class="p-4 text-center text-slate-400 text-xs">© 2025 OwlShare</footer>
</body>
</html>
