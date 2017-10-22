package com.db.awmd.challenge.validation;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldErrorTO {
    private final String field;
    private final String message;

    @JsonCreator
    public FieldErrorTO(
            @JsonProperty("field") String field,
            @JsonProperty("message") String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "FieldErrorTO{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
//Getters are omitted.
}
