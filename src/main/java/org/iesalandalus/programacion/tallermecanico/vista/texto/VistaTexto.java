package org.iesalandalus.programacion.tallermecanico.vista.texto;

import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Mecanico;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Revision;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.TipoTrabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.vista.Vista;
import org.iesalandalus.programacion.tallermecanico.vista.eventos.Evento;
import org.iesalandalus.programacion.tallermecanico.vista.eventos.GestorEventos;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VistaTexto implements Vista {

    private final GestorEventos gestorEventos = new GestorEventos(Evento.values());

    @Override
    public GestorEventos getGestorEventos() {
        return gestorEventos;
    }

    @Override
    public void comenzar() {
        Evento evento;
        do {
            Consola.mostrarMenu();
            evento = Consola.elegirOpcion();
            gestorEventos.notificar(evento);
        } while (evento != Evento.SALIR);
    }

    @Override
    public void terminar() {
        System.out.println("\nEl programa ha terminado. ¡Hasta pronto!");
    }

    @Override
    public Cliente leerCliente() {
        Cliente cliente = null;
        do {
            try {
                String nombre = Consola.leerCadena("Introduce el nombre del cliente: ");
                String dni = Consola.leerCadena("Introduce el DNI del cliente: ");
                String telefono = Consola.leerCadena("Introduce el teléfono del cliente: ");
                cliente = new Cliente(nombre, dni, telefono);
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (cliente == null);
        return cliente;
    }

    @Override
    public Cliente leerClienteDni() {
        Cliente cliente = null;
        do {
            try {
                cliente = Cliente.get(Consola.leerCadena("Introduce el DNI del cliente: "));
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (cliente == null);
        return cliente;
    }

    @Override
    public String leerNuevoNombre() {
        return Consola.leerCadena("Introduce el nuevo nombre (vacío para no cambiarlo): ");
    }

    @Override
    public String leerNuevoTelefono() {
        return Consola.leerCadena("Introduce el nuevo teléfono (vacío para no cambiarlo): ");
    }

    @Override
    public Vehiculo leerVehiculo() {
        Vehiculo vehiculo = null;
        do {
            try {
                String marca = Consola.leerCadena("Introduce la marca del vehículo: ");
                String modelo = Consola.leerCadena("Introduce el modelo del vehículo: ");
                String matricula = Consola.leerCadena("Introduce la matrícula del vehículo: ");
                vehiculo = new Vehiculo(marca, modelo, matricula);
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (vehiculo == null);
        return vehiculo;
    }

    @Override
    public Vehiculo leerVehiculoMatricula() {
        Vehiculo vehiculo = null;
        do {
            try {
                vehiculo = Vehiculo.get(Consola.leerCadena("Introduce la matrícula del vehículo: "));
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (vehiculo == null);
        return vehiculo;
    }

    @Override
    public Trabajo leerRevision() {
        Trabajo revision = null;
        do {
            try {
                Cliente cliente = leerClienteDni();
                Vehiculo vehiculo = leerVehiculoMatricula();
                LocalDate fechaInicio = Consola.leerFecha("Introduce la fecha de inicio");
                revision = new Revision(cliente, vehiculo, fechaInicio);
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (revision == null);
        return revision;
    }

    @Override
    public Trabajo leerMecanico() {
        Trabajo mecanico = null;
        do {
            try {
                Cliente cliente = leerClienteDni();
                Vehiculo vehiculo = leerVehiculoMatricula();
                LocalDate fechaInicio = Consola.leerFecha("Introduce la fecha de inicio");
                mecanico = new Mecanico(cliente, vehiculo, fechaInicio);
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (mecanico == null);
        return mecanico;
    }

    @Override
    public Trabajo leerTrabajoVehiculo() {
        return Trabajo.get(leerVehiculoMatricula());
    }

    @Override
    public Trabajo leerTrabajoBuscar() {
        Trabajo trabajo = null;
        do {
            try {
                Cliente cliente = leerClienteDni();
                Vehiculo vehiculo = leerVehiculoMatricula();
                LocalDate fechaInicio = Consola.leerFecha("Introduce la fecha de inicio del trabajo");
                trabajo = new Revision(cliente, vehiculo, fechaInicio);
            } catch (IllegalArgumentException | NullPointerException e) {
                System.out.println(e.getMessage());
            }
        } while (trabajo == null);
        return trabajo;
    }

    @Override
    public int leerHoras() {
        return Consola.leerEntero("Introduce las horas a añadir: ");
    }

    @Override
    public float leerPrecioMaterial() {
        return Consola.leerReal("Introduce el precio del material a añadir: ");
    }

    @Override
    public LocalDate leerFechaCierre() {
        return Consola.leerFecha("Introduce la fecha de cierre");
    }

    @Override
    public LocalDate leerMes() {
        LocalDate mes = null;
        do {
            try {
                int numeroMes = Consola.leerEntero("Introduce el mes (1-12): ");
                int anio = Consola.leerEntero("Introduce el año: ");
                mes = LocalDate.of(anio, numeroMes, 1);
            } catch (java.time.DateTimeException e) {
                System.out.println("El mes o el año no son válidos.");
            }
        } while (mes == null);
        return mes;
    }

    @Override
    public void notificarResultado(Evento evento, String texto, boolean exito) {
        if (exito) {
            System.out.println(texto);
        } else {
            System.out.println("ERROR: " + texto);
        }
    }

    @Override
    public void mostrarCliente(Cliente cliente) {
        System.out.println((cliente != null) ? cliente : "No existe ningún cliente con ese DNI.");
    }

    @Override
    public void mostrarVehiculo(Vehiculo vehiculo) {
        System.out.println((vehiculo != null) ? vehiculo : "No existe ningún vehículo con esa matrícula.");
    }

    @Override
    public void mostrarTrabajo(Trabajo trabajo) {
        System.out.println((trabajo != null) ? trabajo : "No existe ningún trabajo igual.");
    }

    @Override
    public void mostrarClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes que mostrar.");
        } else {
            clientes.forEach(System.out::println);
        }
    }

    @Override
    public void mostrarVehiculos(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehículos que mostrar.");
        } else {
            vehiculos.forEach(System.out::println);
        }
    }

    @Override
    public void mostrarTrabajos(List<Trabajo> trabajos) {
        if (trabajos.isEmpty()) {
            System.out.println("No hay trabajos que mostrar.");
        } else {
            trabajos.forEach(System.out::println);
        }
    }

    @Override
    public void mostrarEstadisticasMensuales(Map<TipoTrabajo, Integer> estadisticas) {
        for (Map.Entry<TipoTrabajo, Integer> estadistica : estadisticas.entrySet()) {
            System.out.printf("%s: %d%n", estadistica.getKey(), estadistica.getValue());
        }
    }
}
