package com.supera.enem.domain;
import com.supera.enem.domain.enums.TestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType type;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;
}
