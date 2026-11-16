# Taller Mecánico – Persistencia con ficheros JSON (AD01)

## Cómo abrirlo en IntelliJ
1. Descomprime el ZIP y abre la carpeta `TallerMecanico` (donde está `build.gradle`) con `File > Open`.
2. Espera a que Gradle importe el proyecto (JDK 17 o superior).
3. Ejecuta `Main`. Parámetro opcional: `-fdficherosjson` (es la fuente de datos por defecto).
4. Pruebas: `./gradlew test`.

## Estructura (paquete base `org.iesalandalus.programacion.tallermecanico`)
- `modelo/dominio`: Cliente, Vehiculo (record), Trabajo (abstracta), Revision, Mecanico, TipoTrabajo.
  `Trabajo` lleva `@JsonFormat`, `@JsonTypeInfo`/`@JsonSubTypes` (clave `tipo`) y `@JsonIgnore`.
- `modelo/negocio/ficheros/json`: FuenteDatosFicherosJSON, Clientes, Vehiculos, Trabajos
  (singleton; leen y escriben su fichero en cada operación).
- `modelo/negocio/FabricaFuenteDatos`: enumerado con `FICHEROS_JSON`.
- `modelo/cascada`, `vista`, `controlador`: MVC con eventos.
- `datos/ficheros/json/`: clientes.json, vehiculos.json y trabajos.json.
- `libs/entrada-1.0.3.jar`: clase `Entrada` del profesor.
