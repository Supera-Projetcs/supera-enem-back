package com.supera.enem.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private char answer;

//    @ElementCollection
//    @CollectionTable(name = "question_images", joinColumns = @JoinColumn(name = "question_id"))
//    @Column(name = "image")
//    private List<String> images;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String answersJson;

    @Transient
    private Map<String, String> answers;

    @PrePersist
    @PreUpdate
    private void serializeAnswers() throws IOException {
        if (this.answers != null) {
            ObjectMapper mapper = new ObjectMapper();
            this.answersJson = mapper.writeValueAsString(this.answers);
        }
    }

    @PostLoad
    private void deserializeAnswers() throws IOException {
        if (this.answersJson != null) {
            ObjectMapper mapper = new ObjectMapper();
            this.answers = mapper.readValue(this.answersJson, new TypeReference<Map<String, String>>() {});
        }
    }

}
