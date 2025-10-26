package com.backend.mybungalow.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_item_name", columnNames = {"name"})
})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name; // e.g., Soap, Shampoo, Bedsheet

    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_category"))
    private Category category;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer quantityOnHand = 0;

    private String unit;
}


