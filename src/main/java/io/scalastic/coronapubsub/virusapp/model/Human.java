package io.scalastic.coronapubsub.virusapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Human implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private Boolean isInfected;
    private Boolean isVaccinated;
}