package ec.edu.epn.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa un bloque de tiempo que un tutor declara disponible.
 * Relación: Tutor 1 — N HorarioDisponible (idTutor como FK lógica).
 *
 * Estados:
 *   DISPONIBLE  – el bloque está libre para ser reservado
 *   OCUPADO     – ya tiene una sesión asignada
 *   CANCELADO   – el tutor lo anuló
 */
@Entity
@Table(
    name = "horario_disponible",
    indexes = {
        @Index(name = "idx_horario_tutor", columnList = "id_tutor"),
        @Index(name = "idx_horario_estado", columnList = "estado")
    }
)
public class HorarioDisponible {

    // ── Identidad ─────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ── Relación con el tutor (FK lógica) ────────────────────────────────────

    @Column(name = "id_tutor", nullable = false)
    private Long idTutor;

    // ── Bloque de tiempo ──────────────────────────────────────────────────────

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fin", nullable = false)
    private LocalDateTime fin;

    // ── Estado ────────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoHorario estado = EstadoHorario.DISPONIBLE;

    // ── Auditoría ─────────────────────────────────────────────────────────────

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ── Ciclo de vida JPA ─────────────────────────────────────────────────────

    @PrePersist
    private void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaRegistro = ahora;
        this.fechaActualizacion = ahora;
        if (this.estado == null) {
            this.estado = EstadoHorario.DISPONIBLE;
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ── Constructores ─────────────────────────────────────────────────────────

    protected HorarioDisponible() {
    }

    public HorarioDisponible(Long idTutor, LocalDateTime inicio, LocalDateTime fin) {
        if (idTutor == null) {
            throw new IllegalArgumentException("El id del tutor es obligatorio.");
        }
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("El inicio y fin del horario son obligatorios.");
        }
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("El fin debe ser posterior al inicio.");
        }
        this.idTutor = idTutor;
        this.inicio  = inicio;
        this.fin     = fin;
        this.estado  = EstadoHorario.DISPONIBLE;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId()                      { return id; }
    public Long getIdTutor()                 { return idTutor; }
    public LocalDateTime getInicio()         { return inicio; }
    public LocalDateTime getFin()            { return fin; }
    public EstadoHorario getEstado()         { return estado; }
    public LocalDateTime getFechaRegistro()  { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    // ── Setters (solo campos mutables tras la creación) ───────────────────────

    public void setEstado(EstadoHorario estado) { this.estado = estado; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public void setFin(LocalDateTime fin)       { this.fin = fin; }

    @Override
    public String toString() {
        return "HorarioDisponible{" +
                "id=" + id +
                ", idTutor=" + idTutor +
                ", inicio=" + inicio +
                ", fin=" + fin +
                ", estado=" + estado +
                '}';
    }
}