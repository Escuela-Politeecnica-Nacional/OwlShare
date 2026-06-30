package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.CatalogoRegistro;
import ec.edu.epn.util.MaterialAlmacenamiento;
import ec.edu.epn.util.MaterialValidacion;
import ec.edu.epn.util.MateriaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

public class SubirMaterialServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        cargarOpcionesFormulario(request, tutor);
        transferirMensaje(request.getSession(false), request, "flashMensaje");
        transferirMensaje(request.getSession(false), request, "flashError");

        request.getRequestDispatcher("/WEB-INF/tutor/subir-material.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        String titulo = trim(request.getParameter("titulo"));
        String descripcion = trim(request.getParameter("descripcion"));
        String codigoMateria = trim(request.getParameter("materia"));
        String categoria = trim(request.getParameter("nombreMateria"));
        String costoParam = trim(request.getParameter("costo"));

        Part archivo = request.getPart("archivo");

        String error = validarCampos(tutor, titulo, descripcion, codigoMateria, categoria, costoParam, archivo);
        if (error != null) {
            mostrarError(request, response, tutor, error);
            return;
        }

        BigDecimal costo = MaterialValidacion.parseCosto(costoParam);
        String rutaRelativa = null;
        String nombreArchivo = Path.of(archivo.getSubmittedFileName()).getFileName().toString();

        try {
            rutaRelativa = MaterialAlmacenamiento.guardarPdf(tutor.getId(), archivo);
            Material material = new Material(
                    titulo.trim(),
                    codigoMateria,
                    tutor.getId(),
                    nombreArchivo,
                    rutaRelativa,
                    descripcion.trim(),
                    costo,
                    categoria.isEmpty() ? null : categoria
            );
            materialDAO.guardar(material);
        } catch (IOException e) {
            MaterialAlmacenamiento.eliminarSiExiste(rutaRelativa);
            mostrarError(request, response, tutor,
                    e.getMessage() != null ? e.getMessage() : "No se pudo guardar el archivo PDF.");
            return;
        } catch (RuntimeException e) {
            MaterialAlmacenamiento.eliminarSiExiste(rutaRelativa);
            mostrarError(request, response, tutor, "No se pudo registrar el material. Intenta de nuevo.");
            return;
        }

        guardarMensajeFlash(request, "flashMensaje",
                "Material subido correctamente. Quedará disponible cuando sea aprobado.");
        response.sendRedirect(request.getContextPath() + "/tutor/subir");
    }

    private String validarCampos(Usuario tutor, String titulo, String descripcion, String codigoMateria,
                                 String categoria, String costoParam, Part archivo) {
        String error = MaterialValidacion.validarTitulo(titulo).orElse(null);
        if (error != null) {
            return error;
        }
        error = MaterialValidacion.validarDescripcion(descripcion).orElse(null);
        if (error != null) {
            return error;
        }
        error = MaterialValidacion.validarCodigoMateriaTutor(tutor, codigoMateria).orElse(null);
        if (error != null) {
            return error;
        }
        error = MaterialValidacion.validarCosto(costoParam).orElse(null);
        if (error != null) {
            return error;
        }
        error = MaterialValidacion.validarCategoria(categoria).orElse(null);
        if (error != null) {
            return error;
        }
        return MaterialValidacion.validarArchivoPdf(archivo).orElse(null);
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response,
                              Usuario tutor, String mensaje) throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        cargarOpcionesFormulario(request, tutor);
        request.getRequestDispatcher("/WEB-INF/tutor/subir-material.jsp").forward(request, response);
    }

    private void cargarOpcionesFormulario(HttpServletRequest request, Usuario tutor) {
        List<Materia> materiasOpciones = CatalogoRegistro.materiasPorCodigos(
                MateriaUtil.toList(tutor.getMaterias()));
        request.setAttribute("materiasOpciones", materiasOpciones);
        request.setAttribute("categorias", List.of());
    }

    private void guardarMensajeFlash(HttpServletRequest request, String clave, String mensaje) {
        HttpSession session = request.getSession(true);
        session.setAttribute(clave, mensaje);
    }

    private void transferirMensaje(HttpSession session, HttpServletRequest request, String clave) {
        if (session == null) {
            return;
        }
        Object valor = session.getAttribute(clave);
        if (valor != null) {
            request.setAttribute(clave, valor);
            session.removeAttribute(clave);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
