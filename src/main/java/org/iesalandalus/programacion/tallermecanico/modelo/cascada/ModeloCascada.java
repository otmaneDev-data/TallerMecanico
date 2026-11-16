package org.iesalandalus.programacion.tallermecanico.modelo.cascada;

import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Mecanico;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Revision;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.TipoTrabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.Modelo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.FabricaFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Modelo "en cascada": las operaciones comprueban que existan los datos relacionados
 * (cliente y vehículo de un trabajo) antes de delegar en las colecciones.
 */
public class ModeloCascada implements Modelo {

    private IClientes clientes;
    private IVehiculos vehiculos;
    private ITrabajos trabajos;

    public ModeloCascada(FabricaFuenteDatos fabricaFuenteDatos) {
        Objects.requireNonNull(fabricaFuenteDatos, "La fábrica de la fuente de datos no puede ser nula.");
        IFuenteDatos fuenteDatos = fabricaFuenteDatos.crear();
        clientes = fuenteDatos.crearClientes();
        vehiculos = fuenteDatos.crearVehiculos();
        trabajos = fuenteDatos.crearTrabajos();
    }

    @Override
    public void comenzar() {
        clientes.comenzar();
        vehiculos.comenzar();
        trabajos.comenzar();
    }

    @Override
    public void terminar() {
        clientes.terminar();
        vehiculos.terminar();
        trabajos.terminar();
    }

    @Override
    public void insertar(Cliente cliente) {
        clientes.insertar(cliente);
    }

    @Override
    public void insertar(Vehiculo vehiculo) {
        vehiculos.insertar(vehiculo);
    }

    @Override
    public void insertar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "No se puede insertar un trabajo nulo.");
        Cliente cliente = clientes.buscar(trabajo.getCliente());
        if (cliente == null) {
            throw new TallerMecanicoExcepcion("No existe el cliente del trabajo.");
        }
        Vehiculo vehiculo = vehiculos.buscar(trabajo.getVehiculo());
        if (vehiculo == null) {
            throw new TallerMecanicoExcepcion("No existe el vehículo del trabajo.");
        }
        // Se crea el trabajo con el cliente y el vehículo reales (los que están guardados).
        Trabajo nuevoTrabajo = null;
        if (trabajo instanceof Revision) {
            nuevoTrabajo = new Revision(cliente, vehiculo, trabajo.getFechaInicio());
        } else if (trabajo instanceof Mecanico) {
            nuevoTrabajo = new Mecanico(cliente, vehiculo, trabajo.getFechaInicio());
        }
        trabajos.insertar(nuevoTrabajo);
    }

    @Override
    public Cliente buscar(Cliente cliente) {
        return clientes.buscar(cliente);
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        return vehiculos.buscar(vehiculo);
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        return trabajos.buscar(trabajo);
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) {
        return clientes.modificar(cliente, nombre, telefono);
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) {
        return trabajos.anadirHoras(trabajo, horas);
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) {
        return trabajos.anadirPrecioMaterial(trabajo, precioMaterial);
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) {
        return trabajos.cerrar(trabajo, fechaFin);
    }

    @Override
    public void borrar(Cliente cliente) {
        // Al borrar un cliente se borran también sus trabajos.
        List<Trabajo> trabajosCliente = trabajos.get(cliente);
        if (clientes.buscar(cliente) == null) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        for (Trabajo trabajo : trabajosCliente) {
            trabajos.borrar(trabajo);
        }
        clientes.borrar(cliente);
    }

    @Override
    public void borrar(Vehiculo vehiculo) {
        // Al borrar un vehículo se borran también sus trabajos.
        List<Trabajo> trabajosVehiculo = trabajos.get(vehiculo);
        if (vehiculos.buscar(vehiculo) == null) {
            throw new TallerMecanicoExcepcion("No existe ningún vehículo con esa matrícula.");
        }
        for (Trabajo trabajo : trabajosVehiculo) {
            trabajos.borrar(trabajo);
        }
        vehiculos.borrar(vehiculo);
    }

    @Override
    public void borrar(Trabajo trabajo) {
        trabajos.borrar(trabajo);
    }

    @Override
    public List<Cliente> getClientes() {
        List<Cliente> lista = clientes.get();
        lista.sort(Comparator.comparing(Cliente::getNombre).thenComparing(Cliente::getDni));
        return lista;
    }

    @Override
    public List<Vehiculo> getVehiculos() {
        List<Vehiculo> lista = vehiculos.get();
        lista.sort(Comparator.comparing(Vehiculo::marca).thenComparing(Vehiculo::modelo)
                .thenComparing(Vehiculo::matricula));
        return lista;
    }

    @Override
    public List<Trabajo> getTrabajos() {
        return ordenar(trabajos.get());
    }

    @Override
    public List<Trabajo> getTrabajos(Cliente cliente) {
        return ordenar(trabajos.get(cliente));
    }

    @Override
    public List<Trabajo> getTrabajos(Vehiculo vehiculo) {
        return ordenar(trabajos.get(vehiculo));
    }

    private List<Trabajo> ordenar(List<Trabajo> lista) {
        lista.sort(Comparator.comparing(Trabajo::getFechaInicio)
                .thenComparing(trabajo -> trabajo.getCliente().getNombre()));
        return lista;
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        return trabajos.getEstadisticasMensuales(mes);
    }
}
