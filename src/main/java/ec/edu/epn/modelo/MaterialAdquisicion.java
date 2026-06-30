package ec.edu.epn.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "material_adquisicion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_material", "id_estudiante"})
)
public class MaterialAdquisicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_material", nullable = false)
    private Long idMaterial;

    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "fecha_adquisicion", nullable = false)
    private LocalDateTime fechaAdquisicion;

    protected MaterialAdquisicion() {
    }

    public MaterialAdquisicion(Long idMaterial, Long idEstudiante) {
        this.idMaterial = idMaterial;
        this.idEstudiante = idEstudiante;
        this.fechaAdquisicion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public Long getIdEstudiante() {
        return idEstudiante;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }
}
