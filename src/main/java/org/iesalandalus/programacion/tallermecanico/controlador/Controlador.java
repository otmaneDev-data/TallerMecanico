package org.iesalandalus.programacion.tallermecanico.controlador;

import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.FabricaModelo;
import org.iesalandalus.programacion.tallermecanico.modelo.Modelo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.FabricaFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.vista.FabricaVista;
import org.iesalandalus.programacion.tallermecanico.vista.Vista;
import org.iesalandalus.programacion.tallermecanico.vista.eventos.Evento;

import java.util.Objects;

public class Controlador implements IControlador {

    private final Modelo modelo;
    private final Vista vista;

    public Controlador(FabricaModelo fabricaModelo, FabricaFuenteDatos fabricaFuenteDatos, FabricaVista fabricaVista) {
        Objects.requireNonNull(fabricaModelo, "La fábrica del modelo no puede ser nula.");
        Objects.requireNonNull(fabricaFuenteDatos, "La fábrica de la fuente de datos no puede ser nula.");
        Objects.requireNonNull(fabricaVista, "La fábrica de la vista no puede ser nula.");
        modelo = fabricaModelo.crear(fabricaFuenteDatos);
        vista = fabricaVista.crear();
        // El controlador escucha todos los eventos que lance la vista.
        vista.getGestorEventos().suscribir(this, Evento.values());
    }

    @Override
    public void comenzar() {
        modelo.comenzar();
        vista.comenzar();
    }

    @Override
    public void terminar() {
        modelo.terminar();
        vista.terminar();
    }

    @Override
    public void actualizar(Evento evento) {
        try {
            ejecutar(evento);
        } catch (TallerMecanicoExcepcion | IllegalArgumentException | NullPointerException e) {
            // Cualquier error se muestra al usuario y el programa continúa.
            vista.notificarResultado(evento, e.getMessage(), false);
        }
    }

    private void ejecutar(Evento evento) {
        switch (evento) {
            case INSERTAR_CLIENTE -> {
                modelo.insertar(vista.leerCliente());
                vista.notificarResultado(evento, "Cliente insertado correctamente.", true);
            }
            case BUSCAR_CLIENTE -> vista.mostrarCliente(modelo.buscar(vista.leerClienteDni()));
            case BORRAR_CLIENTE -> {
                modelo.borrar(vista.leerClienteDni());
                vista.notificarResultado(evento, "Cliente borrado correctamente.", true);
            }
            case LISTAR_CLIENTES -> vista.mostrarClientes(modelo.getClientes());
            case MODIFICAR_CLIENTE -> {
                modelo.modificar(vista.leerClienteDni(), vista.leerNuevoNombre(), vista.leerNuevoTelefono());
                vista.notificarResultado(evento, "Cliente modificado correctamente.", true);
            }
            case INSERTAR_VEHICULO -> {
                modelo.insertar(vista.leerVehiculo());
                vista.notificarResultado(evento, "Vehículo insertado correctamente.", true);
            }
            case BUSCAR_VEHICULO -> vista.mostrarVehiculo(modelo.buscar(vista.leerVehiculoMatricula()));
            case BORRAR_VEHICULO -> {
                modelo.borrar(vista.leerVehiculoMatricula());
                vista.notificarResultado(evento, "Vehículo borrado correctamente.", true);
            }
            case LISTAR_VEHICULOS -> vista.mostrarVehiculos(modelo.getVehiculos());
            case INSERTAR_REVISION -> {
                modelo.insertar(vista.leerRevision());
                vista.notificarResultado(evento, "Revisión insertada correctamente.", true);
            }
            case INSERTAR_MECANICO -> {
                modelo.insertar(vista.leerMecanico());
                vista.notificarResultado(evento, "Trabajo mecánico insertado correctamente.", true);
            }
            case BUSCAR_TRABAJO -> vista.mostrarTrabajo(modelo.buscar(vista.leerTrabajoBuscar()));
            case BORRAR_TRABAJO -> {
                modelo.borrar(vista.leerTrabajoBuscar());
                vista.notificarResultado(evento, "Trabajo borrado correctamente.", true);
            }
            case LISTAR_TRABAJOS -> vista.mostrarTrabajos(modelo.getTrabajos());
            case LISTAR_TRABAJOS_CLIENTE -> {
                Cliente cliente = vista.leerClienteDni();
                vista.mostrarTrabajos(modelo.getTrabajos(cliente));
            }
            case LISTAR_TRABAJOS_VEHICULO -> {
                Vehiculo vehiculo = vista.leerVehiculoMatricula();
                vista.mostrarTrabajos(modelo.getTrabajos(vehiculo));
            }
            case ANADIR_HORAS_TRABAJO -> {
                Trabajo trabajo = vista.leerTrabajoVehiculo();
                modelo.anadirHoras(trabajo, vista.leerHoras());
                vista.notificarResultado(evento, "Horas añadidas correctamente.", true);
            }
            case ANADIR_PRECIO_MATERIAL_TRABAJO -> {
                Trabajo trabajo = vista.leerTrabajoVehiculo();
                modelo.anadirPrecioMaterial(trabajo, vista.leerPrecioMaterial());
                vista.notificarResultado(evento, "Precio del material añadido correctamente.", true);
            }
            case CERRAR_TRABAJO -> {
                Trabajo trabajo = vista.leerTrabajoVehiculo();
                modelo.cerrar(trabajo, vista.leerFechaCierre());
                vista.notificarResultado(evento, "Trabajo cerrado correctamente.", true);
            }
            case MOSTRAR_ESTADISTICAS_MENSUALES -> vista.mostrarEstadisticasMensuales(
                    modelo.getEstadisticasMensuales(vista.leerMes()));
            case SALIR -> terminar();
        }
    }
}
