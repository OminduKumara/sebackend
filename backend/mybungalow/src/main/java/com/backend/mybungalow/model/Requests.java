package com.backend.mybungalow.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;

    @Column(nullable = false, length = 255)
    private String subject; // <-- new field

    @Column(nullable = false, length = 2000)
    private String content; // message body

    private boolean done = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
