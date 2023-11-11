package ru.averkiev.bankservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * Базовый класс сущностей.
 * @author mrGreenNV
 */
@MappedSuperclass
@Data
public abstract class BaseEntity {

    /** Идентификатор сущности. */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Идентификатор сущности", example = "321654")
    private Long id;

    /** Время создания сущности. */
    @Column(name = "created_at")
    @CreationTimestamp
    @Schema(description = "Дата и время создания")
    @JsonIgnore
    private Date createdAt;

    /** Время обновления сущности. */
    @Column(name = "updated_at")
    @UpdateTimestamp
    @Schema(description = "Дата и время обновления")
    @JsonIgnore
    private Date updatedAt;

    /** Статус сущности в системе. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Schema(description = "Статус в системе", exampleClasses = EntityStatus.class)
    @JsonIgnore
    private EntityStatus entityStatus = EntityStatus.ACTIVE;
}
