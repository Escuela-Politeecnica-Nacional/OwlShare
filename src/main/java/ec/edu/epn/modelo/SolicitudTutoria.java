package ec.edu.epn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitudes_tutoria")
public class SolicitudTutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_materia", nullable = false)
    private MateriaCatalogo materia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(length = 500)
    private String comentario;

    public SolicitudTutoria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Usuario estudiante) {
        this.estudiante = estudiante;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public MateriaCatalogo getMateria() {
        return materia;
    }

    public void setMateria(MateriaCatalogo materia) {
        this.materia = materia;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
