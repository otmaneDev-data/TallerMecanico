package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clientes persistidos en el fichero clientes.json (singleton).
 * Los datos se leen y se escriben en el fichero en cada operación.
 */
public class Clientes implements IClientes {

    private static final String FICHERO_CLIENTES = "datos/ficheros/json/clientes.json";
    private static Clientes instancia;
    // El mapper convierte objetos Java en JSON y viceversa.
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private Clientes() {
    }

    static Clientes getInstancia() {
        if (instancia == null) {
            instancia = new Clientes();
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

    List<Cliente> leer() {
        File fichero = new File(FICHERO_CLIENTES);
        List<Cliente> clientes = new ArrayList<>();
        if (fichero.exists() && fichero.length() > 0) {
            try {
                clientes = mapper.readValue(fichero, new TypeReference<List<Cliente>>() {
                });
            } catch (IOException e) {
                throw new TallerMecanicoExcepcion("No se ha podido leer el fichero de clientes: " + e.getMessage());
            }
        }
        return clientes;
    }

    void escribir(List<Cliente> clientes) {
        File fichero = new File(FICHERO_CLIENTES);
        File directorio = fichero.getParentFile();
        if (directorio != null) {
            directorio.mkdirs();
        }
        try {
            mapper.writeValue(fichero, clientes);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("No se ha podido escribir en el fichero de clientes: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> get() {
        return leer();
    }

    @Override
    public void insertar(Cliente cliente) {
        Objects.requireNonNull(cliente, "No se puede insertar un cliente nulo.");
        List<Cliente> clientes = leer();
        if (clientes.contains(cliente)) {
            throw new TallerMecanicoExcepcion("Ya existe un cliente con ese DNI.");
        }
        clientes.add(new Cliente(cliente));
        escribir(clientes);
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) {
        Objects.requireNonNull(cliente, "No se puede modificar un cliente nulo.");
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);
        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        Cliente clienteEncontrado = clientes.get(indice);
        if (nombre != null && !nombre.isBlank()) {
            clienteEncontrado.setNombre(nombre);
        }
        if (telefono != null && !telefono.isBlank()) {
            clienteEncontrado.setTelefono(telefono);
        }
        escribir(clientes);
        return new Cliente(clienteEncontrado);
    }

    @Override
    public Cliente buscar(Cliente cliente) {
        Objects.requireNonNull(cliente, "No se puede buscar un cliente nulo.");
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);
        return (indice == -1) ? null : new Cliente(clientes.get(indice));
    }

    @Override
    public void borrar(Cliente cliente) {
        Objects.requireNonNull(cliente, "No se puede borrar un cliente nulo.");
        List<Cliente> clientes = leer();
        if (!clientes.remove(cliente)) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        escribir(clientes);
    }
}
