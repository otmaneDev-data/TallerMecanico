package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Mecanico;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Revision;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Comprueba la serialización de los trabajos sin tocar los ficheros reales. */
class TrabajosJsonTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final TypeReference<List<Trabajo>> tipoLista = new TypeReference<>() {
    };

    @Test
    void escribeYLeeTrabajosConTipoYFechas() throws Exception {
        Cliente cliente = new Cliente("Bob Esponja", "11223344B", "950112233");
        Vehiculo vehiculo = new Vehiculo("Renault", "Megane", "2222CCC");
        Mecanico mecanico = new Mecanico(cliente, vehiculo, LocalDate.of(2025, 10, 30));
        mecanico.anadirHoras(11);
        mecanico.anadirPrecioMaterial(12);
        mecanico.cerrar(LocalDate.of(2025, 11, 2));
        Revision revision = new Revision(cliente, new Vehiculo("Seat", "León", "1111BBB"), LocalDate.of(2025, 11, 1));

        String json = mapper.writerFor(tipoLista).writeValueAsString(List.of(mecanico, revision));

        assertTrue(json.contains("\"tipo\":\"Mecanico\""));
        assertTrue(json.contains("\"tipo\":\"Revision\""));
        assertTrue(json.contains("\"fechaInicio\":\"2025-10-30\""));
        assertFalse(json.contains("precio\":"), "Los precios calculados no deben guardarse");
        assertFalse(json.contains("dias"));

        List<Trabajo> leidos = mapper.readValue(json, tipoLista);
        assertEquals(2, leidos.size());
        assertInstanceOf(Mecanico.class, leidos.get(0));
        assertInstanceOf(Revision.class, leidos.get(1));
        assertEquals(mecanico.getPrecio(), leidos.get(0).getPrecio());
        assertTrue(leidos.get(0).estaCerrado());
        assertFalse(leidos.get(1).estaCerrado());
    }
}
