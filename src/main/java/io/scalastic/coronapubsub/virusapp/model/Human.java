package io.scalastic.coronapubsub.virusapp.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Human implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String name;
    private Boolean isInfected;
    private Boolean isVaccinated;
}