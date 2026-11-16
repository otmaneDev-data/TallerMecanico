package org.iesalandalus.programacion.tallermecanico.vista.texto;

import org.iesalandalus.programacion.tallermecanico.vista.eventos.Evento;
import org.iesalandalus.programacion.utilidades.Entrada;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Entrada y salida por consola. Todas las lecturas se repiten hasta que el dato introducido es válido.
 */
public final class Consola {

    private static final String CADENA_FORMATO_FECHA = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(CADENA_FORMATO_FECHA);

    private Consola() {
    }

    public static void mostrarCabecera(String mensaje) {
        System.out.printf("%n%s%n", mensaje);
        System.out.println("-".repeat(mensaje.length()));
    }

    public static void mostrarMenu() {
        mostrarCabecera("Gestión de un taller mecánico");
        for (Evento evento : Evento.values()) {
            System.out.println(evento);
        }
    }

    public static Evento elegirOpcion() {
        Evento evento = null;
        do {
            try {
                evento = Evento.get(leerEntero("\nElige una opción: "));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (evento == null);
        return evento;
    }

    // La clase Entrada ya repite la lectura hasta que el valor es del tipo esperado.
    public static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        return Entrada.entero();
    }

    public static float leerReal(String mensaje) {
        System.out.print(mensaje);
        return Entrada.real();
    }

    public static String leerCadena(String mensaje) {
        System.out.print(mensaje);
        String cadena = Entrada.cadena();
        if (cadena == null) {
            // La entrada se ha cerrado (por ejemplo, con Ctrl+D): no se puede seguir leyendo.
            System.out.println("\nLa entrada de datos se ha cerrado. El programa termina.");
            System.exit(0);
        }
        return cadena;
    }

    public static LocalDate leerFecha(String mensaje) {
        LocalDate fecha = null;
        do {
            try {
                fecha = LocalDate.parse(leerCadena(mensaje + " (" + CADENA_FORMATO_FECHA + "): ").trim(), FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("La fecha debe tener el formato " + CADENA_FORMATO_FECHA + " y ser válida.");
            }
        } while (fecha == null);
        return fecha;
    }
}
