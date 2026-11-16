package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Mecanico;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.TipoTrabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Trabajos persistidos en el fichero trabajos.json (singleton).
 */
public class Trabajos implements ITrabajos {

    private static final String FICHERO_TRABAJOS = "datos/ficheros/json/trabajos.json";
    private static final TypeReference<List<Trabajo>> TYPE_LIST_TRABAJO = new TypeReference<>() {
    };
    private static Trabajos instancia;
    // JavaTimeModule permite a Jackson trabajar con LocalDate.
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    private Trabajos() {
    }

    static Trabajos getInstancia() {
        if (instancia == null) {
            instancia = new Trabajos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        // Vacío: los datos se leen cuando se solicitan.
    }

    @Override
    public void terminar() {
        // Vacío: los datos se escriben cada vez que hay una modificación.
    }

    List<Trabajo> leer() {
        File fichero = new File(FICHERO_TRABAJOS);
        List<Trabajo> trabajos = new ArrayList<>();
        if (fichero.exists() && fichero.length() > 0) {
            try {
                trabajos = mapper.readValue(fichero, TYPE_LIST_TRABAJO);
            } catch (IOException e) {
                throw new TallerMecanicoExcepcion("No se ha podido leer el fichero de trabajos: " + e.getMessage());
            }
            actualizarClientes(trabajos);
        }
        return trabajos;
    }

    // Cada trabajo guarda una copia del cliente. Si el cliente se ha modificado, se usan sus datos actuales.
    private void actualizarClientes(List<Trabajo> trabajos) {
        List<Cliente> clientes = Clientes.getInstancia().leer();
        for (Trabajo trabajo : trabajos) {
            int indice = clientes.indexOf(trabajo.getCliente());
            if (indice != -1) {
                trabajo.setCliente(clientes.get(indice));
            }
        }
    }

    void escribir(List<Trabajo> trabajos) {
        File fichero = new File(FICHERO_TRABAJOS);
        File directorio = fichero.getParentFile();
        if (directorio != null) {
            directorio.mkdirs();
        }
        try {
            mapper.writerFor(TYPE_LIST_TRABAJO).writeValue(fichero, trabajos);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("No se ha podido escribir en el fichero de trabajos: " + e.getMessage());
        }
    }

    @Override
    public List<Trabajo> get() {
        return leer();
    }

    @Override
    public List<Trabajo> get(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo.");
        List<Trabajo> trabajosCliente = new ArrayList<>();
        for (Trabajo trabajo : leer()) {
            if (trabajo.getCliente().equals(cliente)) {
                trabajosCliente.add(trabajo);
            }
        }
        return trabajosCliente;
    }

    @Override
    public List<Trabajo> get(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");
        List<Trabajo> trabajosVehiculo = new ArrayList<>();
        for (Trabajo trabajo : leer()) {
            if (trabajo.getVehiculo().equals(vehiculo)) {
                trabajosVehiculo.add(trabajo);
            }
        }
        return trabajosVehiculo;
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        Objects.requireNonNull(mes, "El mes no puede ser nulo.");
        Map<TipoTrabajo, Integer> estadisticas = inicializarEstadisticas();
        for (Trabajo trabajo : leer()) {
            LocalDate fecha = trabajo.getFechaInicio();
            if (fecha.getMonth() == mes.getMonth() && fecha.getYear() == mes.getYear()) {
                TipoTrabajo tipo = TipoTrabajo.get(trabajo);
                estadisticas.put(tipo, estadisticas.get(tipo) + 1);
            }
        }
        return estadisticas;
    }

    private Map<TipoTrabajo, Integer> inicializarEstadisticas() {
        Map<TipoTrabajo, Integer> estadisticas = new EnumMap<>(TipoTrabajo.class);
        for (TipoTrabajo tipo : TipoTrabajo.values()) {
            estadisticas.put(tipo, 0);
        }
        return estadisticas;
    }

    @Override
    public void insertar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "No se puede insertar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        comprobarTrabajoNuevo(trabajos, trabajo);
        trabajos.add(Trabajo.copiar(trabajo));
        escribir(trabajos);
    }

    private void comprobarTrabajoNuevo(List<Trabajo> trabajos, Trabajo nuevo) {
        for (Trabajo trabajo : trabajos) {
            if (!trabajo.estaCerrado()) {
                if (trabajo.getCliente().equals(nuevo.getCliente())) {
                    throw new TallerMecanicoExcepcion("El cliente tiene otro trabajo en curso.");
                }
                if (trabajo.getVehiculo().equals(nuevo.getVehiculo())) {
                    throw new TallerMecanicoExcepcion("El vehículo está actualmente en el taller.");
                }
            } else {
                if (trabajo.getCliente().equals(nuevo.getCliente())
                        && nuevo.getFechaInicio().isBefore(trabajo.getFechaFin())) {
                    throw new TallerMecanicoExcepcion("El cliente tiene otro trabajo posterior.");
                }
                if (trabajo.getVehiculo().equals(nuevo.getVehiculo())
                        && nuevo.getFechaInicio().isBefore(trabajo.getFechaFin())) {
                    throw new TallerMecanicoExcepcion("El vehículo tiene otro trabajo posterior.");
                }
            }
        }
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) {
        Objects.requireNonNull(trabajo, "No se puede añadir horas a un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        Trabajo trabajoAbierto = getTrabajoAbierto(trabajos, trabajo.getVehiculo());
        trabajoAbierto.anadirHoras(horas);
        escribir(trabajos);
        return Trabajo.copiar(trabajoAbierto);
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) {
        Objects.requireNonNull(trabajo, "No se puede añadir precio al material de un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        Trabajo trabajoAbierto = getTrabajoAbierto(trabajos, trabajo.getVehiculo());
        if (!(trabajoAbierto instanceof Mecanico mecanico)) {
            throw new TallerMecanicoExcepcion("No se puede añadir precio al material para este tipo de trabajos.");
        }
        mecanico.anadirPrecioMaterial(precioMaterial);
        escribir(trabajos);
        return Trabajo.copiar(mecanico);
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) {
        Objects.requireNonNull(trabajo, "No se puede cerrar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        Trabajo trabajoAbierto = getTrabajoAbierto(trabajos, trabajo.getVehiculo());
        trabajoAbierto.cerrar(fechaFin);
        escribir(trabajos);
        return Trabajo.copiar(trabajoAbierto);
    }

    // Devuelve el trabajo abierto del vehículo (el que está en el taller) o lanza una excepción si no hay.
    private Trabajo getTrabajoAbierto(List<Trabajo> trabajos, Vehiculo vehiculo) {
        for (Trabajo trabajo : trabajos) {
            if (!trabajo.estaCerrado() && trabajo.getVehiculo().equals(vehiculo)) {
                return trabajo;
            }
        }
        throw new TallerMecanicoExcepcion("No existe ningún trabajo abierto para dicho vehículo.");
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "No se puede buscar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        int indice = trabajos.indexOf(trabajo);
        return (indice == -1) ? null : Trabajo.copiar(trabajos.get(indice));
    }

    @Override
    public void borrar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "No se puede borrar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        if (!trabajos.remove(trabajo)) {
            throw new TallerMecanicoExcepcion("No existe ningún trabajo igual.");
        }
        escribir(trabajos);
    }
}
