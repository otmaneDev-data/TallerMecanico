package org.iesalandalus.programacion.tallermecanico.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;

import java.time.LocalDate;
import java.util.Objects;

public class Mecanico extends Trabajo {

    private static final float FACTOR_HORA = 30;
    private static final float FACTOR_PRECIO_MATERIAL = 1.5f;

    private float precioMaterial;

    // Constructor para Jackson.
    private Mecanico() {
        super();
    }

    public Mecanico(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio) {
        super(cliente, vehiculo, fechaInicio);
        precioMaterial = 0;
    }

    public Mecanico(Mecanico mecanico) {
        super(Objects.requireNonNull(mecanico, "El trabajo no puede ser nulo."));
        precioMaterial = mecanico.precioMaterial;
    }

    @JsonProperty("precioMaterial")
    public float getPrecioMaterial() {
        return precioMaterial;
    }

    public void anadirPrecioMaterial(float precioMaterial) {
        if (estaCerrado()) {
            throw new TallerMecanicoExcepcion("No se puede añadir precio del material, ya que el trabajo mecánico está cerrado.");
        }
        if (precioMaterial <= 0) {
            throw new IllegalArgumentException("El precio del material a añadir debe ser mayor que cero.");
        }
        this.precioMaterial += precioMaterial;
    }

    @Override
    public float getPrecioEspecifico() {
        return FACTOR_HORA * getHoras() + FACTOR_PRECIO_MATERIAL * precioMaterial;
    }

    @Override
    public String toString() {
        String fechaFin = (getFechaFin() == null) ? "" : getFechaFin().format(FORMATO_FECHA);
        String cadena = String.format("Mecánico -> %s - %s (%s - %s): %d horas, %.2f € en material", getCliente(),
                getVehiculo(), getFechaInicio().format(FORMATO_FECHA), fechaFin, getHoras(), precioMaterial);
        if (estaCerrado()) {
            cadena += String.format(", %.2f € total", getPrecio());
        }
        return cadena;
    }
}
