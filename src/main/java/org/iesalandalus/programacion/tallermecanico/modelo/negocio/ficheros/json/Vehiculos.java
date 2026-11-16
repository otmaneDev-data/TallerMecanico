package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Vehículos persistidos en el fichero vehiculos.json (singleton).
 */
public class Vehiculos implements IVehiculos {

    private static final String FICHERO_VEHICULOS = "datos/ficheros/json/vehiculos.json";
    private static Vehiculos instancia;
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private Vehiculos() {
    }

    static Vehiculos getInstancia() {
        if (instancia == null) {
            instancia = new Vehiculos();
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

    List<Vehiculo> leer() {
        File fichero = new File(FICHERO_VEHICULOS);
        List<Vehiculo> vehiculos = new ArrayList<>();
        if (fichero.exists() && fichero.length() > 0) {
            try {
                vehiculos = mapper.readValue(fichero, new TypeReference<List<Vehiculo>>() {
                });
            } catch (IOException e) {
                throw new TallerMecanicoExcepcion("No se ha podido leer el fichero de vehículos: " + e.getMessage());
            }
        }
        return vehiculos;
    }

    void escribir(List<Vehiculo> vehiculos) {
        File fichero = new File(FICHERO_VEHICULOS);
        File directorio = fichero.getParentFile();
        if (directorio != null) {
            directorio.mkdirs();
        }
        try {
            mapper.writeValue(fichero, vehiculos);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion("No se ha podido escribir en el fichero de vehículos: " + e.getMessage());
        }
    }

    @Override
    public List<Vehiculo> get() {
        return leer();
    }

    @Override
    public void insertar(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "No se puede insertar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        if (vehiculos.contains(vehiculo)) {
            throw new TallerMecanicoExcepcion("Ya existe un vehículo con esa matrícula.");
        }
        vehiculos.add(vehiculo);
        escribir(vehiculos);
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "No se puede buscar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        int indice = vehiculos.indexOf(vehiculo);
        return (indice == -1) ? null : vehiculos.get(indice);
    }

    @Override
    public void borrar(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "No se puede borrar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        if (!vehiculos.remove(vehiculo)) {
            throw new TallerMecanicoExcepcion("No existe ningún vehículo con esa matrícula.");
        }
        escribir(vehiculos);
    }
}
