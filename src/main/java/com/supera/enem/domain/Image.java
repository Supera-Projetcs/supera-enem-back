package com.supera.enem.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String description;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public String getPath() {
        return this.url;
    }
}
