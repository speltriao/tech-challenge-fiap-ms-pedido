package com.fiap.techchallenge.domain.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EntityNotFoundException extends Exception {

    private final String entity;
    private final String id;

    public EntityNotFoundException(String entity, UUID id) {
        super("Entity " + entity + " with id " + id.toString() + " not found.");
        this.entity = entity;
        this.id = id.toString();
    }

    public EntityNotFoundException(String entity, String id) {
        super("Entity " + entity + " with id " + id + " not found.");
        this.entity = entity;
        this.id = id;
    }

}
