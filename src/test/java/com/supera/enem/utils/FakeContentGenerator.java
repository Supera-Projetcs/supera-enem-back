package com.supera.enem.utils;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Subject;

import java.util.LinkedHashSet;
import java.util.Set;

public class FakeContentGenerator {
    public static Set<Content> generateFakeContents() {
        Set<Content> contents = new LinkedHashSet<>();

        Subject fakeSubject = new Subject();
        fakeSubject.setId(1L);
        fakeSubject.setName("Matemática");

        Content content1 = new Content(1L, "Funções", 0.8, 0.7, fakeSubject, null, null);
        Content content2 = new Content(2L, "Trigonometria", 0.6, 0.9, fakeSubject, null, null);

        contents.add(content1);
        contents.add(content2);

        return contents;
    }
}
