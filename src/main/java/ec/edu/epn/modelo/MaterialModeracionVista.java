package ec.edu.epn.modelo;

import ec.edu.epn.util.CatalogoRegistro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Material pendiente de revisión, expuesto al panel de administración.
 */
public class MaterialModeracionVista {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.forLanguageTag("es"));

    private final Long id;
    private final String titulo;
    private final String descripcion;
    private final String nombreMateria;
    private final String nombreTutor;
    private final String fechaEnvio;
    private final String costoFormateado;
    private final boolean gratis;

    public MaterialModeracionVista(Long id, String titulo, String descripcion, String nombreMateria,
                                   String nombreTutor, String fechaEnvio, BigDecimal costo) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion != null ? descripcion : "";
        this.nombreMateria = nombreMateria;
        this.nombreTutor = nombreTutor;
        this.fechaEnvio = fechaEnvio;
        this.costoFormateado = costo.setScale(2, RoundingMode.HALF_UP).toPlainString();
        this.gratis = costo.compareTo(BigDecimal.ZERO) == 0;
    }

    public static MaterialModeracionVista desde(Material material, String nombreTutor) {
        Materia materia = CatalogoRegistro.buscarMateriaPorCodigo(material.getCodigoMateria());
        String nombreMateria = materia != null ? materia.getNombre() : material.getCodigoMateria();
        String fecha = material.getFechaRegistro() != null
                ? material.getFechaRegistro().format(FECHA)
                : "—";

        return new MaterialModeracionVista(
                material.getId(),
                material.getTitulo(),
                material.getDescripcion(),
                nombreMateria,
                nombreTutor,
                fecha,
                material.getCosto()
        );
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

    public String getCostoFormateado() {
        return costoFormateado;
    }

    public boolean isGratis() {
        return gratis;
    }
}
