package org.iesalandalus.programacion.tallermecanico.vista.eventos;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Patrón observador: los receptores se suscriben a eventos y se les avisa cuando ocurren.
 */
public class GestorEventos {

    private final Map<Evento, List<ReceptorEventos>> receptores = new EnumMap<>(Evento.class);

    public GestorEventos(Evento... eventos) {
        for (Evento evento : eventos) {
            receptores.put(evento, new ArrayList<>());
        }
    }

    public void suscribir(ReceptorEventos receptor, Evento... eventos) {
        for (Evento evento : eventos) {
            receptores.get(evento).add(receptor);
        }
    }

    public void desuscribir(ReceptorEventos receptor, Evento... eventos) {
        for (Evento evento : eventos) {
            receptores.get(evento).remove(receptor);
        }
    }

    public void notificar(Evento evento) {
        for (ReceptorEventos receptor : receptores.get(evento)) {
            receptor.actualizar(evento);
        }
    }
}
