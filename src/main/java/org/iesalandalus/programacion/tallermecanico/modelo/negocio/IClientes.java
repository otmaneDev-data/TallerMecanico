package org.iesalandalus.programacion.tallermecanico.modelo.negocio;

import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;

import java.util.List;

public interface IClientes {

    void comenzar();

    void terminar();

    List<Cliente> get();

    void insertar(Cliente cliente);

    Cliente modificar(Cliente cliente, String nombre, String telefono);

    Cliente buscar(Cliente cliente);

    void borrar(Cliente cliente);
}
