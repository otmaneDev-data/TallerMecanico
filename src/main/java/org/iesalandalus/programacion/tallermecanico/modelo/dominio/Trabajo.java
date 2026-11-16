package org.iesalandalus.programacion.tallermecanico.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Trabajo realizado en el taller (revisión o mecánico).
 */
// En el JSON aparecerá la pareja "tipo": "Revision" o "tipo": "Mecanico".
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Revision.class, name = "Revision"),
        @JsonSubTypes.Type(value = Mecanico.class, name = "Mecanico")
})
// Si fechaFin es null (trabajo abierto) no se escribe en el JSON.
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Trabajo {

    static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float FACTOR_DIA = 10;

    private Cliente cliente;
    private Vehiculo vehiculo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    private int horas;

    // Constructor para que Jackson pueda crear el objeto y rellenarlo después.
    protected Trabajo() {
    }

    protected Trabajo(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio) {
        setCliente(cliente);
        setVehiculo(vehiculo);
        setFechaInicio(fechaInicio);
        horas = 0;
    }

    protected Trabajo(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "El trabajo no puede ser nulo.");
        cliente = new Cliente(trabajo.cliente);
        vehiculo = trabajo.vehiculo;
        fechaInicio = trabajo.fechaInicio;
        fechaFin = trabajo.fechaFin;
        horas = trabajo.horas;
    }

    public static Trabajo copiar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "El trabajo no puede ser nulo.");
        Trabajo copia = null;
        if (trabajo instanceof Revision revision) {
            copia = new Revision(revision);
        } else if (trabajo instanceof Mecanico mecanico) {
            copia = new Mecanico(mecanico);
        }
        return copia;
    }

    /** Trabajo "de búsqueda": solo importa el vehículo. */
    public static Trabajo get(Vehiculo vehiculo) {
        return new Revision(Cliente.get("11111111H"), vehiculo, LocalDate.now());
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo.");
        this.cliente = cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");
        this.vehiculo = vehiculo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    private void setFechaInicio(LocalDate fechaInicio) {
        Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula.");
        if (fechaInicio.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser futura.");
        }
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    private void setFechaFin(LocalDate fechaFin) {
        Objects.requireNonNull(fechaFin, "La fecha de fin no puede ser nula.");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (fechaFin.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser futura.");
        }
        this.fechaFin = fechaFin;
    }

    public int getHoras() {
        return horas;
    }

    public void anadirHoras(int horas) {
        if (estaCerrado()) {
            throw new TallerMecanicoExcepcion("No se puede añadir horas, ya que el trabajo está cerrado.");
        }
        if (horas <= 0) {
            throw new IllegalArgumentException("Las horas a añadir deben ser mayores que cero.");
        }
        this.horas += horas;
    }

    // @JsonIgnore: estos métodos se calculan, no son datos que haya que guardar en el fichero.
    @JsonIgnore
    public boolean estaCerrado() {
        return fechaFin != null;
    }

    public void cerrar(LocalDate fechaFin) {
        if (estaCerrado()) {
            throw new TallerMecanicoExcepcion("El trabajo ya está cerrado.");
        }
        setFechaFin(fechaFin);
    }

    @JsonIgnore
    public float getPrecio() {
        return getPrecioFijo() + getPrecioEspecifico();
    }

    @JsonIgnore
    public float getPrecioFijo() {
        return FACTOR_DIA * getDias();
    }

    @JsonIgnore
    private int getDias() {
        int dias = 0;
        if (fechaFin != null) {
            dias = (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        }
        return dias;
    }

    @JsonIgnore
    public abstract float getPrecioEspecifico();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trabajo trabajo)) {
            return false;
        }
        return cliente.equals(trabajo.cliente) && vehiculo.equals(trabajo.vehiculo)
                && fechaInicio.equals(trabajo.fechaInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cliente, vehiculo, fechaInicio);
    }
}
