package org.iesalandalus.programacion.tallermecanico;

import org.iesalandalus.programacion.tallermecanico.controlador.Controlador;
import org.iesalandalus.programacion.tallermecanico.modelo.FabricaModelo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.FabricaFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.vista.FabricaVista;

public class Main {

    public static void main(String[] args) {
        Controlador controlador = new Controlador(FabricaModelo.CASCADA, procesarArgumentosFuenteDatos(args),
                FabricaVista.TEXTO);
        controlador.comenzar();
    }

    /**
     * Elige la fuente de datos según los argumentos: -fdficherosjson usa ficheros JSON.
     * Si no se indica ninguna (o no se reconoce), se usan ficheros JSON por defecto.
     */
    private static FabricaFuenteDatos procesarArgumentosFuenteDatos(String[] args) {
        FabricaFuenteDatos fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_JSON;
        for (String argumento : args) {
            if (argumento.equalsIgnoreCase("-fdficherosjson")) {
                fabricaFuenteDatos = FabricaFuenteDatos.FICHEROS_JSON;
            }
        }
        return fabricaFuenteDatos;
    }
}
