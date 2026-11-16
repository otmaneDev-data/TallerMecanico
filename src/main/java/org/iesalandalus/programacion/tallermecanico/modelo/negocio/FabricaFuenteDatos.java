package org.iesalandalus.programacion.tallermecanico.modelo.negocio;

import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json.FuenteDatosFicherosJSON;

/**
 * Patrón método de fabricación: cada valor del enumerado sabe crear su fuente de datos.
 */
public enum FabricaFuenteDatos {

    FICHEROS_JSON {
        @Override
        public IFuenteDatos crear() {
            return new FuenteDatosFicherosJSON();
        }
    };

    public abstract IFuenteDatos crear();
}
