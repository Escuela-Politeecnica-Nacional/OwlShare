package ec.edu.epn.modelo;

import ec.edu.epn.util.CatalogoRegistro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Datos de un material expuestos en vistas de tutor y estudiante.
 */
public class MaterialVista {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.forLanguageTag("es"));

    private final Long id;
    private final String titulo;
    private final String descripcion;
    private final String nombreMateria;
    private final String nombreTutor;
    private final String fechaEnvio;
    private final String estado;
    private final String estadoEtiqueta;
    private final String comentarioRevision;
    private final String categoriaAcademica;
    private final String nombreArchivo;
    private final String fechaRevision;
    private final boolean eliminable;
    private final BigDecimal costo;
    private final String costoFormateado;
    private final boolean adquirido;

    private MaterialVista(Long id, String titulo, String descripcion, String nombreMateria,
                          String nombreTutor, String fechaEnvio, String estado, String estadoEtiqueta,
                          String comentarioRevision, String categoriaAcademica, String nombreArchivo,
                          String fechaRevision, boolean eliminable, BigDecimal costo, boolean adquirido) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreMateria = nombreMateria;
        this.nombreTutor = nombreTutor;
        this.fechaEnvio = fechaEnvio;
        this.estado = estado;
        this.estadoEtiqueta = estadoEtiqueta;
        this.comentarioRevision = comentarioRevision;
        this.categoriaAcademica = categoriaAcademica;
        this.nombreArchivo = nombreArchivo;
        this.fechaRevision = fechaRevision;
        this.eliminable = eliminable;
        this.costo = costo;
        this.costoFormateado = costo.setScale(2, RoundingMode.HALF_UP).toPlainString();
        this.adquirido = adquirido;
    }

    public static MaterialVista paraTutor(Material material) {
        return desde(material, null, false, true);
    }

    public static MaterialVista paraBiblioteca(Material material, String nombreTutor, boolean adquirido) {
        return desde(material, nombreTutor, adquirido, false);
    }

    private static MaterialVista desde(Material material, String nombreTutor, boolean adquirido,
                                       boolean incluirDetalleTutor) {
        Materia materia = CatalogoRegistro.buscarMateriaPorCodigo(material.getCodigoMateria());
        String nombreMateria = materia != null ? materia.getNombre() : material.getCodigoMateria();
        String fecha = material.getFechaRegistro() != null
                ? material.getFechaRegistro().format(FECHA)
                : "—";

        String fechaRevision = material.getFechaRevision() != null
                ? material.getFechaRevision().format(FECHA)
                : null;
        boolean eliminable = incluirDetalleTutor
                && (material.getEstado() == EstadoMaterial.PENDIENTE
                || material.getEstado() == EstadoMaterial.RECHAZADO);

        return new MaterialVista(
                material.getId(),
                material.getTitulo(),
                material.getDescripcion() != null ? material.getDescripcion() : "",
                nombreMateria,
                nombreTutor,
                fecha,
                material.getEstado().name().toLowerCase(),
                etiquetaEstado(material.getEstado()),
                comentarioRevision(material),
                material.getCategoriaAcademica(),
                material.getNombreArchivo(),
                fechaRevision,
                eliminable,
                material.getCosto(),
                adquirido
        );
    }

    private static String etiquetaEstado(EstadoMaterial estado) {
        return switch (estado) {
            case PENDIENTE -> "En revisión";
            case APROBADO -> "Aprobado";
            case RECHAZADO -> "Rechazado";
        };
    }

    private static String comentarioRevision(Material material) {
        if (material.getEstado() != EstadoMaterial.RECHAZADO) {
            return null;
        }
        String comentario = material.getComentarioAdmin();
        return comentario != null && !comentario.isBlank() ? comentario.trim() : null;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public String getNombreTutor() {
        return nombreTutor;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public String getEstadoEtiqueta() {
        return estadoEtiqueta;
    }

    public String getComentarioRevision() {
        return comentarioRevision;
    }

    public boolean isRechazado() {
        return "rechazado".equals(estado);
    }

    public String getCategoriaAcademica() {
        return categoriaAcademica;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getFechaRevision() {
        return fechaRevision;
    }

    public boolean isEliminable() {
        return eliminable;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public String getCostoFormateado() {
        return costoFormateado;
    }

    public boolean isAdquirido() {
        return adquirido;
    }

    public boolean isGratis() {
        return costo.compareTo(BigDecimal.ZERO) == 0;
    }
}
