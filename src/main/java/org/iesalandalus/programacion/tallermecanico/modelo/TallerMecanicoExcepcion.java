package org.iesalandalus.programacion.tallermecanico.modelo;

/**
 * Excepción propia de la aplicación (errores de negocio y de acceso a ficheros).
 */
public class TallerMecanicoExcepcion extends RuntimeException {

    public TallerMecanicoExcepcion(String mensaje) {
        super(mensaje);
    }
}
