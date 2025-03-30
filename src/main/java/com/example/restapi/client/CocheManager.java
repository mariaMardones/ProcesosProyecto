package com.example.restapi.client;

import java.util.List;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.restapi.model.Coche;

public class CocheManager {

    private String COCHE_CONTROLLER_URL_TEMPLATE = "http://%s:%s/api/coche";
    private final String COCHE_CONTROLLER_URL;
    private final RestTemplate restTemplate;

    public CocheManager(String hostname, String port) {
        COCHE_CONTROLLER_URL = String.format(COCHE_CONTROLLER_URL_TEMPLATE, hostname, port);
        this.restTemplate = new RestTemplate();
    }

    public void crearCoche(Coche coche) {
        ResponseEntity<Coche> response = restTemplate.postForEntity(
            COCHE_CONTROLLER_URL + "/crear", 
            coche, 
            Coche.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Coche creado exitosamente: " + response.getBody());
        } else {
            System.out.println("Error al crear coche. Código de estado: " + response.getStatusCode());
        }
    }

    public List<Coche> listarCoches() {
        ResponseEntity<Coche[]> response = restTemplate.getForEntity(
            COCHE_CONTROLLER_URL, 
            Coche[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return List.of(response.getBody());
        } else {
            System.out.println("Error al listar coches. Código de estado: " + response.getStatusCode());
            return List.of();
        }
    }

    public List<Coche> listarCochesDisponibles() {
        ResponseEntity<Coche[]> response = restTemplate.getForEntity(
            COCHE_CONTROLLER_URL + "/disponibles", 
            Coche[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return List.of(response.getBody());
        } else {
            System.out.println("Error al listar coches disponibles. Código de estado: " + response.getStatusCode());
            return List.of();
        }
    }

    public Coche buscarCochePorMatricula(String matricula) {
        ResponseEntity<Coche> response = restTemplate.getForEntity(
            COCHE_CONTROLLER_URL + "/buscar?matricula=" + matricula, 
            Coche.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            System.out.println("Coche no encontrado. Código de estado: " + response.getStatusCode());
            return null;
        }
    }

    public void actualizarCoche(String matricula, Coche coche) {
        try {
            ResponseEntity<Coche> response = restTemplate.postForEntity(
                COCHE_CONTROLLER_URL + "/actualizar?matricula=" + matricula, 
                coche, 
                Coche.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Coche actualizado exitosamente: " + response.getBody());
            } else {
                System.out.println("Error al actualizar coche. Código de estado: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.out.println("Error al actualizar coche: " + e.getMessage());
        }
    }

    public void eliminarCoche(String matricula) {
        try {
            restTemplate.delete(COCHE_CONTROLLER_URL + "/eliminar?matricula=" + matricula);
            System.out.println("Coche eliminado exitosamente.");
        } catch (RestClientException e) {
            System.out.println("Error al eliminar coche: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: CocheManager <hostname> <port>");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];

        CocheManager cocheManager = new CocheManager(hostname, port);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Crear Coche");
            System.out.println("2. Listar Todos los Coches");
            System.out.println("3. Listar Coches Disponibles");
            System.out.println("4. Buscar Coche por Matrícula");
            System.out.println("5. Actualizar Coche");
            System.out.println("6. Eliminar Coche");
            System.out.println("7. Salir");
            System.out.print("Ingrese su opción: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Matrícula: ");
                    String matricula = scanner.nextLine();
                    System.out.print("Marca: ");
                    String marca = scanner.nextLine();
                    System.out.print("Modelo: ");
                    String modelo = scanner.nextLine();
                    System.out.print("Año: ");
                    int anio = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.print("Color: ");
                    String color = scanner.nextLine();
                    System.out.print("Precio: ");
                    double precio = scanner.nextDouble();
                    scanner.nextLine(); // consume newline
                    System.out.print("Disponible (true/false): ");
                    boolean disponible = scanner.nextBoolean();
                    scanner.nextLine(); // consume newline
                    
                    Coche nuevoCoche = new Coche(matricula, marca, modelo, anio, color, precio, disponible);
                    cocheManager.crearCoche(nuevoCoche);
                    break;
                    
                case 2:
                    List<Coche> coches = cocheManager.listarCoches();
                    for (Coche c : coches) {
                        System.out.println("Matrícula: " + c.getMatricula());
                        System.out.println("Marca: " + c.getMarca());
                        System.out.println("Modelo: " + c.getModelo());
                        System.out.println("Año: " + c.getAnio());
                        System.out.println("Color: " + c.getColor());
                        System.out.println("Precio: " + c.getPrecio());
                        System.out.println("Disponible: " + c.isDisponible());
                        System.out.println("---------------------------");
                    }
                    break;
                    
                case 3:
                    List<Coche> cochesDisponibles = cocheManager.listarCochesDisponibles();
                    for (Coche c : cochesDisponibles) {
                        System.out.println("Matrícula: " + c.getMatricula());
                        System.out.println("Marca: " + c.getMarca());
                        System.out.println("Modelo: " + c.getModelo());
                        System.out.println("Año: " + c.getAnio());
                        System.out.println("Color: " + c.getColor());
                        System.out.println("Precio: " + c.getPrecio());
                        System.out.println("---------------------------");
                    }
                    break;
                    
                case 4:
                    System.out.print("Matrícula a buscar: ");
                    String matriculaBuscar = scanner.nextLine();
                    Coche coche = cocheManager.buscarCochePorMatricula(matriculaBuscar);
                    if (coche != null) {
                        System.out.println("Matrícula: " + coche.getMatricula());
                        System.out.println("Marca: " + coche.getMarca());
                        System.out.println("Modelo: " + coche.getModelo());
                        System.out.println("Año: " + coche.getAnio());
                        System.out.println("Color: " + coche.getColor());
                        System.out.println("Precio: " + coche.getPrecio());
                        System.out.println("Disponible: " + coche.isDisponible());
                    }
                    break;
                    
                case 5:
                    System.out.print("Matrícula del coche a actualizar: ");
                    String matriculaActualizar = scanner.nextLine();
                    
                    // Primero buscamos el coche para mostrar sus datos actuales
                    Coche cocheExistente = cocheManager.buscarCochePorMatricula(matriculaActualizar);
                    if (cocheExistente == null) {
                        System.out.println("Coche no encontrado.");
                        break;
                    }
                    
                    System.out.println("Datos actuales:");
                    System.out.println("1. Marca: " + cocheExistente.getMarca());
                    System.out.println("2. Modelo: " + cocheExistente.getModelo());
                    System.out.println("3. Año: " + cocheExistente.getAnio());
                    System.out.println("4. Color: " + cocheExistente.getColor());
                    System.out.println("5. Precio: " + cocheExistente.getPrecio());
                    System.out.println("6. Disponible: " + cocheExistente.isDisponible());
                    
                    System.out.print("¿Qué campo desea actualizar? (1-6, 0 para todos): ");
                    int campo = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (campo == 0) {
                        System.out.print("Nueva marca: ");
                        cocheExistente.setMarca(scanner.nextLine());
                        System.out.print("Nuevo modelo: ");
                        cocheExistente.setModelo(scanner.nextLine());
                        System.out.print("Nuevo año: ");
                        cocheExistente.setAnio(scanner.nextInt());
                        scanner.nextLine(); // consume newline
                        System.out.print("Nuevo color: ");
                        cocheExistente.setColor(scanner.nextLine());
                        System.out.print("Nuevo precio: ");
                        cocheExistente.setPrecio(scanner.nextDouble());
                        scanner.nextLine(); // consume newline
                        System.out.print("Disponible (true/false): ");
                        cocheExistente.setDisponible(scanner.nextBoolean());
                        scanner.nextLine(); // consume newline
                    } else {
                        switch (campo) {
                            case 1:
                                System.out.print("Nueva marca: ");
                                cocheExistente.setMarca(scanner.nextLine());
                                break;
                            case 2:
                                System.out.print("Nuevo modelo: ");
                                cocheExistente.setModelo(scanner.nextLine());
                                break;
                            case 3:
                                System.out.print("Nuevo año: ");
                                cocheExistente.setAnio(scanner.nextInt());
                                scanner.nextLine(); // consume newline
                                break;
                            case 4:
                                System.out.print("Nuevo color: ");
                                cocheExistente.setColor(scanner.nextLine());
                                break;
                            case 5:
                                System.out.print("Nuevo precio: ");
                                cocheExistente.setPrecio(scanner.nextDouble());
                                scanner.nextLine(); // consume newline
                                break;
                            case 6:
                                System.out.print("Disponible (true/false): ");
                                cocheExistente.setDisponible(scanner.nextBoolean());
                                scanner.nextLine(); // consume newline
                                break;
                            default:
                                System.out.println("Opción inválida.");
                                break;
                        }
                    }
                    
                    cocheManager.actualizarCoche(matriculaActualizar, cocheExistente);
                    break;
                    
                case 6:
                    System.out.print("Matrícula del coche a eliminar: ");
                    String matriculaEliminar = scanner.nextLine();
                    cocheManager.eliminarCoche(matriculaEliminar);
                    break;
                    
                case 7:
                    scanner.close();
                    System.exit(0);
                    
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }
}