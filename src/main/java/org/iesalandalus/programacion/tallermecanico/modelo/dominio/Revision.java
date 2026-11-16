package org.iesalandalus.programacion.tallermecanico.modelo.dominio;

import java.time.LocalDate;
import java.util.Objects;

public class Revision extends Trabajo {

    private static final float FACTOR_HORA = 35;

    // Constructor para Jackson.
    private Revision() {
        super();
    }

    public Revision(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio) {
        super(cliente, vehiculo, fechaInicio);
    }

    public Revision(Revision revision) {
        super(Objects.requireNonNull(revision, "El trabajo no puede ser nulo."));
    }

    @Override
    public float getPrecioEspecifico() {
        return FACTOR_HORA * getHoras();
    }

    @Override
    public String toString() {
        String fechaFin = (getFechaFin() == null) ? "" : getFechaFin().format(FORMATO_FECHA);
        String cadena = String.format("Revisión -> %s - %s (%s - %s): %d horas", getCliente(), getVehiculo(),
                getFechaInicio().format(FORMATO_FECHA), fechaFin, getHoras());
        if (estaCerrado()) {
            cadena += String.format(", %.2f € total", getPrecio());
        }
        return cadena;
    }
}
