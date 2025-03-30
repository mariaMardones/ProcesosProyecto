package com.example.restapi.client;

import java.util.List;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.restapi.model.Coche;
import com.example.restapi.model.EstadoReserva;
import com.example.restapi.model.Reserva;
import com.example.restapi.model.Usuario;

public class ReservaManager {

    private String RESERVA_CONTROLLER_URL_TEMPLATE = "http://%s:%s/api/reservas";
    private final String RESERVA_CONTROLLER_URL;
    private final RestTemplate restTemplate;

    public ReservaManager(String hostname, String port) {
        RESERVA_CONTROLLER_URL = String.format(RESERVA_CONTROLLER_URL_TEMPLATE, hostname, port);
        this.restTemplate = new RestTemplate();
    }

    public List<Reserva> obtenerReservas() {
        ResponseEntity<Reserva[]> response = restTemplate.getForEntity(
            RESERVA_CONTROLLER_URL, 
            Reserva[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return List.of(response.getBody());
        } else {
            System.out.println("Error al obtener reservas. Código de estado: " + response.getStatusCode());
            return List.of();
        }
    }

    public Reserva obtenerReservaPorId(int id) {
        ResponseEntity<Reserva> response = restTemplate.getForEntity(
            RESERVA_CONTROLLER_URL + "/" + id, 
            Reserva.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            System.out.println("Reserva no encontrada. Código de estado: " + response.getStatusCode());
            return null;
        }
    }

    public void crearReserva(Reserva reserva) {
        ResponseEntity<Reserva> response = restTemplate.postForEntity(
            RESERVA_CONTROLLER_URL, 
            reserva, 
            Reserva.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Reserva creada exitosamente: " + response.getBody());
        } else {
            System.out.println("Error al crear reserva. Código de estado: " + response.getStatusCode());
        }
    }

    public void actualizarReserva(int id, Reserva reserva) {
        try {
            restTemplate.put(
                RESERVA_CONTROLLER_URL + "/" + id, 
                reserva
            );
            System.out.println("Reserva actualizada exitosamente.");
        } catch (RestClientException e) {
            System.out.println("Error al actualizar reserva: " + e.getMessage());
        }
    }

    public void eliminarReserva(int id) {
        try {
            restTemplate.delete(RESERVA_CONTROLLER_URL + "/" + id);
            System.out.println("Reserva eliminada exitosamente.");
        } catch (RestClientException e) {
            System.out.println("Error al eliminar reserva: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: ReservaManager <hostname> <port>");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];

        ReservaManager reservaManager = new ReservaManager(hostname, port);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Listar Todas las Reservas");
            System.out.println("2. Buscar Reserva por ID");
            System.out.println("3. Crear Reserva");
            System.out.println("4. Actualizar Reserva");
            System.out.println("5. Eliminar Reserva");
            System.out.println("6. Salir");
            System.out.print("Ingrese su opción: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    List<Reserva> reservas = reservaManager.obtenerReservas();
                    for (Reserva r : reservas) {
                        System.out.println("\nID: " + r.getId());
                        System.out.println("Usuario ID: " + r.getUsuario().getId());
                        System.out.println("Coche Matrícula: " + r.getCoche().getMatricula());
                        System.out.println("Fecha: " + r.getFecha());
                        System.out.println("Precio Total: " + r.getPrecioTotal());
                        System.out.println("Estado: " + r.getEstado());
                        System.out.println("---------------------------");
                    }
                    break;
                    
                case 2:
                    System.out.print("ID de la reserva a buscar: ");
                    int idBuscar = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    Reserva reserva = reservaManager.obtenerReservaPorId(idBuscar);
                    if (reserva != null) {
                        System.out.println("\nID: " + reserva.getId());
                        System.out.println("Usuario ID: " + reserva.getUsuario().getId());
                        System.out.println("Coche Matrícula: " + reserva.getCoche().getMatricula());
                        System.out.println("Fecha: " + reserva.getFecha());
                        System.out.println("Precio Total: " + reserva.getPrecioTotal());
                        System.out.println("Estado: " + reserva.getEstado());
                    }
                    break;
                    
                case 3:
                    System.out.print("ID del usuario: ");
                    int usuarioId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.print("Matrícula del coche: ");
                    String matricula = scanner.nextLine();
                    System.out.print("Fecha (YYYY-MM-DD): ");
                    String fecha = scanner.nextLine();
                    System.out.print("Precio total: ");
                    double precioTotal = scanner.nextDouble();
                    scanner.nextLine(); // consume newline
                    System.out.print("Estado (PENDIENTE/CONFIRMADA/CANCELADA/FINALIZADA): ");
                    EstadoReserva estado = EstadoReserva.valueOf(scanner.nextLine().toUpperCase());
                    
                    // Crear objetos Usuario y Coche mínimos con solo el ID/matrícula
                    Usuario usuario = new Usuario();
                    usuario.setId((long) usuarioId);
                    
                    Coche coche = new Coche();
                    coche.setMatricula(matricula);
                    
                    Reserva nuevaReserva = new Reserva(usuario, coche, fecha, precioTotal, estado);
                    reservaManager.crearReserva(nuevaReserva);
                    break;
                    
                case 4:
                    System.out.print("ID de la reserva a actualizar: ");
                    int idActualizar = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    
                    // Obtener reserva existente
                    Reserva reservaExistente = reservaManager.obtenerReservaPorId(idActualizar);
                    if (reservaExistente == null) {
                        System.out.println("Reserva no encontrada.");
                        break;
                    }
                    
                    System.out.println("Datos actuales:");
                    System.out.println("1. Fecha: " + reservaExistente.getFecha());
                    System.out.println("2. Precio Total: " + reservaExistente.getPrecioTotal());
                    System.out.println("3. Estado: " + reservaExistente.getEstado());
                    
                    System.out.print("¿Qué campo desea actualizar? (1-3, 0 para todos): ");
                    int campo = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    
                    if (campo == 0) {
                        System.out.print("Nueva fecha (YYYY-MM-DD): ");
                        reservaExistente.setFecha(scanner.nextLine());
                        System.out.print("Nuevo precio total: ");
                        reservaExistente.setPrecioTotal(scanner.nextDouble());
                        scanner.nextLine(); // consume newline
                        System.out.print("Nuevo estado (PENDIENTE/CONFIRMADA/CANCELADA/FINALIZADA): ");
                        reservaExistente.setEstado(EstadoReserva.valueOf(scanner.nextLine().toUpperCase()));
                    } else {
                        switch (campo) {
                            case 1:
                                System.out.print("Nueva fecha (YYYY-MM-DD): ");
                                reservaExistente.setFecha(scanner.nextLine());
                                break;
                            case 2:
                                System.out.print("Nuevo precio total: ");
                                reservaExistente.setPrecioTotal(scanner.nextDouble());
                                scanner.nextLine(); // consume newline
                                break;
                            case 3:
                                System.out.print("Nuevo estado (PENDIENTE/CONFIRMADA/CANCELADA/FINALIZADA): ");
                                reservaExistente.setEstado(EstadoReserva.valueOf(scanner.nextLine().toUpperCase()));
                                break;
                            default:
                                System.out.println("Opción inválida.");
                                break;
                        }
                    }
                    
                    reservaManager.actualizarReserva(idActualizar, reservaExistente);
                    break;
                    
                case 5:
                    System.out.print("ID de la reserva a eliminar: ");
                    int idEliminar = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    reservaManager.eliminarReserva(idEliminar);
                    break;
                    
                case 6:
                    scanner.close();
                    System.exit(0);
                    
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }
}