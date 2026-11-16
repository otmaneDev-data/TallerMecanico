
# Taller Mecánico – Persistencia con ficheros JSON (AD01)

## Estructura (paquete base `org.iesalandalus.programacion.tallermecanico`)
- `modelo/dominio`: Cliente, Vehiculo (record), Trabajo (abstracta), Revision, Mecanico, TipoTrabajo.
  `Trabajo` lleva `@JsonFormat`, `@JsonTypeInfo`/`@JsonSubTypes` (clave `tipo`) y `@JsonIgnore`.
- `modelo/negocio/ficheros/json`: FuenteDatosFicherosJSON, Clientes, Vehiculos, Trabajos
  (singleton; leen y escriben su fichero en cada operación).
- `modelo/negocio/FabricaFuenteDatos`: enumerado con `FICHEROS_JSON`.
- `modelo/cascada`, `vista`, `controlador`: MVC con eventos.
- `datos/ficheros/json/`: clientes.json, vehiculos.json y trabajos.json.
- `libs/entrada-1.0.3.jar`: clase `Entrada` del profesor.