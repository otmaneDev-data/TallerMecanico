package org.iesalandalus.programacion.tallermecanico.modelo.dominio;

import java.util.Objects;

/**
 * Vehículo del taller. Se identifica por su matrícula.
 */
public record Vehiculo(String marca, String modelo, String matricula) {

    private static final String ER_MARCA = "[A-Z]{2,4}|[A-Z][a-z]+(?:[A-Z][a-z]+)?(?:[- ][A-Z][a-z]+)*";
    private static final String ER_MATRICULA = "\\d{4}[B-DF-HJ-NP-TV-Z]{3}";

    // Constructor compacto: valida los datos antes de crear el objeto.
    public Vehiculo {
        Objects.requireNonNull(marca, "La marca no puede ser nula.");
        Objects.requireNonNull(modelo, "El modelo no puede ser nulo.");
        Objects.requireNonNull(matricula, "La matrícula no puede ser nula.");
        matricula = matricula.trim().toUpperCase();
        if (!marca.matches(ER_MARCA)) {
            throw new IllegalArgumentException("La marca no tiene un formato válido.");
        }
        if (modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo no puede estar en blanco.");
        }
        if (!matricula.matches(ER_MATRICULA)) {
            throw new IllegalArgumentException("La matrícula no tiene un formato válido.");
        }
    }

    /** Devuelve un vehículo "de búsqueda": solo importa la matrícula. */
    public static Vehiculo get(String matricula) {
        return new Vehiculo("Seat", "León", matricula);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehiculo vehiculo)) {
            return false;
        }
        return matricula.equals(vehiculo.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula);
    }

    @Override
    public String toString() {
        return String.format("%s %s - %s", marca, modelo, matricula);
    }
}
