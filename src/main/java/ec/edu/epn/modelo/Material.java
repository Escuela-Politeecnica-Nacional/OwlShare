package ec.edu.epn.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "material")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo_materia", nullable = false, length = 20)
    private String codigoMateria;

    @Column(name = "id_tutor", nullable = false)
    private Long idTutor;

    @Column(name = "nombre_archivo", nullable = false, length = 260)
    private String nombreArchivo;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 512)
    private String rutaAlmacenamiento;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoMaterial estado = EstadoMaterial.PENDIENTE;

    @Column(name = "comentario_admin", length = 1000)
    private String comentarioAdmin;

    @Column(name = "id_admin_revisor")
    private Long idAdminRevisor;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    private void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaRegistro = ahora;
        this.fechaActualizacion = ahora;
        if (this.estado == null) {
            this.estado = EstadoMaterial.PENDIENTE;
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    protected Material() {
    }

    public Material(String codigoMateria, Long idTutor, String nombreArchivo,
                    String rutaAlmacenamiento, String descripcion) {
        this.codigoMateria = codigoMateria;
        this.idTutor = idTutor;
        this.nombreArchivo = nombreArchivo;
        this.rutaAlmacenamiento = rutaAlmacenamiento;
        this.descripcion = descripcion;
        this.estado = EstadoMaterial.PENDIENTE;
    }

    public Long getId() { return id; }
    public String getCodigoMateria() { return codigoMateria; }
    public Long getIdTutor() { return idTutor; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getRutaAlmacenamiento() { return rutaAlmacenamiento; }
    public String getDescripcion() { return descripcion; }
    public EstadoMaterial getEstado() { return estado; }
    public String getComentarioAdmin() { return comentarioAdmin; }
    public Long getIdAdminRevisor() { return idAdminRevisor; }
    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setEstado(EstadoMaterial estado) { this.estado = estado; }
    public void setComentarioAdmin(String comentarioAdmin) { this.comentarioAdmin = comentarioAdmin; }
    public void setIdAdminRevisor(Long idAdminRevisor) { this.idAdminRevisor = idAdminRevisor; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
}