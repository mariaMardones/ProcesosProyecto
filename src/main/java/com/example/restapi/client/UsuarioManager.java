package com.example.restapi.client;

import java.util.List;
import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.restapi.model.TipoRol;
import com.example.restapi.model.Usuario;

public class UsuarioManager {

    private String USUARIO_CONTROLLER_URL_TEMPLATE = "http://%s:%s/api/usuario";
    private final String USUARIO_CONTROLLER_URL;
    private final RestTemplate restTemplate;

    public UsuarioManager(String hostname, String port) {
        USUARIO_CONTROLLER_URL = String.format(USUARIO_CONTROLLER_URL_TEMPLATE, hostname, port);
        this.restTemplate = new RestTemplate();
    }

    public void registrarUsuario(Usuario usuario) {
        ResponseEntity<Usuario> response = restTemplate.postForEntity(
            USUARIO_CONTROLLER_URL + "/registrar", 
            usuario, 
            Usuario.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Usuario registrado exitosamente: " + response.getBody());
        } else {
            System.out.println("Error al registrar usuario. Código de estado: " + response.getStatusCode());
        }
    }

    public List<Usuario> listarUsuarios() {
        ResponseEntity<Usuario[]> response = restTemplate.getForEntity(
            USUARIO_CONTROLLER_URL, 
            Usuario[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return List.of(response.getBody());
        } else {
            System.out.println("Error al listar usuarios. Código de estado: " + response.getStatusCode());
            return List.of();
        }
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        ResponseEntity<Usuario> response = restTemplate.getForEntity(
            USUARIO_CONTROLLER_URL + "/buscar?email=" + email, 
            Usuario.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            System.out.println("Usuario no encontrado. Código de estado: " + response.getStatusCode());
            return null;
        }
    }

    public void actualizarUsuario(String email, Usuario usuario) {
        try {
            ResponseEntity<Usuario> response = restTemplate.postForEntity(
                USUARIO_CONTROLLER_URL + "/actualizar?email=" + email, 
                usuario, 
                Usuario.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Usuario actualizado exitosamente: " + response.getBody());
            } else {
                System.out.println("Error al actualizar usuario. Código de estado: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public void eliminarUsuario(String email) {
        try {
            restTemplate.delete(USUARIO_CONTROLLER_URL + "/eliminar?email=" + email);
            System.out.println("Usuario eliminado exitosamente.");
        } catch (RestClientException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    public String iniciarSesion(String email, String password) {
        ResponseEntity<String> response = restTemplate.postForEntity(
            USUARIO_CONTROLLER_URL + "/login?email=" + email + "&password=" + password,
            null,
            String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Inicio de sesión exitoso.");
            return response.getBody();
        } else {
            System.out.println("Error en inicio de sesión. Código de estado: " + response.getStatusCode());
            return null;
        }
    }

    public void cerrarSesion(String token) {
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                USUARIO_CONTROLLER_URL + "/logout",
                token,
                Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Sesión cerrada exitosamente.");
            } else {
                System.out.println("Error al cerrar sesión. Código de estado: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            System.out.println("Error al cerrar sesión: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: UsuarioManager <hostname> <port>");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];

        UsuarioManager usuarioManager = new UsuarioManager(hostname, port);
        Scanner scanner = new Scanner(System.in);
        String currentToken = null;

        while (true) {
            System.out.println("\n1. Registrar Usuario");
            System.out.println("2. Listar Usuarios");
            System.out.println("3. Buscar Usuario por Email");
            System.out.println("4. Actualizar Usuario");
            System.out.println("5. Eliminar Usuario");
            System.out.println("6. Iniciar Sesión");
            System.out.println("7. Cerrar Sesión");
            System.out.println("8. Salir");
            System.out.print("Ingrese su opción: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Apellido: ");
                    String apellido = scanner.nextLine();
                    System.out.print("Fecha de Nacimiento (YYYY-MM-DD): ");
                    String fechaNacimiento = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Contraseña: ");
                    String password = scanner.nextLine();
                    System.out.print("Teléfono: ");
                    String tlf = scanner.nextLine();
                    System.out.print("Rol (ADMIN/CLIENTE): ");
                    TipoRol rol = TipoRol.valueOf(scanner.nextLine().toUpperCase());
                    
                    Usuario nuevoUsuario = new Usuario(nombre, apellido, fechaNacimiento, 
                                                     email, password, tlf, rol);
                    usuarioManager.registrarUsuario(nuevoUsuario);
                    break;
                    
                case 2:
                    List<Usuario> usuarios = usuarioManager.listarUsuarios();
                    for (Usuario u : usuarios) {
                        System.out.println("ID: " + u.getId());
                        System.out.println("Nombre: " + u.getNombre());
                        System.out.println("Apellido: " + u.getApellido());
                        System.out.println("Email: " + u.getEmail());
                        System.out.println("Teléfono: " + u.getTlf());
                        System.out.println("Rol: " + u.getRol());
                        System.out.println("Fecha Nacimiento: " + u.getFechaNacimiento());
                        System.out.println("---------------------------");
                    }
                    break;
                    
                case 3:
                    System.out.print("Email a buscar: ");
                    String emailBuscar = scanner.nextLine();
                    Usuario usuario = usuarioManager.buscarUsuarioPorEmail(emailBuscar);
                    if (usuario != null) {
                        System.out.println("ID: " + usuario.getId());
                        System.out.println("Nombre: " + usuario.getNombre());
                        System.out.println("Apellido: " + usuario.getApellido());
                        System.out.println("Email: " + usuario.getEmail());
                        System.out.println("Teléfono: " + usuario.getTlf());
                        System.out.println("Rol: " + usuario.getRol());
                        System.out.println("Fecha Nacimiento: " + usuario.getFechaNacimiento());
                    }
                    break;
                    
                case 4:
                    System.out.print("Email del usuario a actualizar: ");
                    String emailActualizar = scanner.nextLine();
                    
                    // Primero buscamos el usuario para mostrar sus datos actuales
                    Usuario usuarioExistente = usuarioManager.buscarUsuarioPorEmail(emailActualizar);
                    if (usuarioExistente == null) {
                        System.out.println("Usuario no encontrado.");
                        break;
                    }
                    
                    System.out.println("Datos actuales:");
                    System.out.println("1. Nombre: " + usuarioExistente.getNombre());
                    System.out.println("2. Apellido: " + usuarioExistente.getApellido());
                    System.out.println("3. Fecha Nacimiento: " + usuarioExistente.getFechaNacimiento());
                    System.out.println("4. Contraseña: " + usuarioExistente.getPassword());
                    System.out.println("5. Teléfono: " + usuarioExistente.getTlf());
                    System.out.println("6. Rol: " + usuarioExistente.getRol());
                    
                    System.out.print("¿Qué campo desea actualizar? (1-6, 0 para todos): ");
                    int campo = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (campo == 0) {
                        System.out.print("Nuevo nombre: ");
                        usuarioExistente.setNombre(scanner.nextLine());
                        System.out.print("Nuevo apellido: ");
                        usuarioExistente.setApellido(scanner.nextLine());
                        System.out.print("Nueva fecha de nacimiento (YYYY-MM-DD): ");
                        usuarioExistente.setFechaNacimiento(scanner.nextLine());
                        System.out.print("Nueva contraseña: ");
                        usuarioExistente.setPassword(scanner.nextLine());
                        System.out.print("Nuevo teléfono: ");
                        usuarioExistente.setTlf(scanner.nextLine());
                        System.out.print("Nuevo rol (ADMIN/CLIENTE): ");
                        usuarioExistente.setRol(TipoRol.valueOf(scanner.nextLine().toUpperCase()));
                    } else {
                        switch (campo) {
                            case 1:
                                System.out.print("Nuevo nombre: ");
                                usuarioExistente.setNombre(scanner.nextLine());
                                break;
                            case 2:
                                System.out.print("Nuevo apellido: ");
                                usuarioExistente.setApellido(scanner.nextLine());
                                break;
                            case 3:
                                System.out.print("Nueva fecha de nacimiento (YYYY-MM-DD): ");
                                usuarioExistente.setFechaNacimiento(scanner.nextLine());
                                break;
                            case 4:
                                System.out.print("Nueva contraseña: ");
                                usuarioExistente.setPassword(scanner.nextLine());
                                break;
                            case 5:
                                System.out.print("Nuevo teléfono: ");
                                usuarioExistente.setTlf(scanner.nextLine());
                                break;
                            case 6:
                                System.out.print("Nuevo rol (ADMIN/CLIENTE): ");
                                usuarioExistente.setRol(TipoRol.valueOf(scanner.nextLine().toUpperCase()));
                                break;
                            default:
                                System.out.println("Opción inválida.");
                                break;
                        }
                    }
                    
                    usuarioManager.actualizarUsuario(emailActualizar, usuarioExistente);
                    break;
                    
                case 5:
                    System.out.print("Email del usuario a eliminar: ");
                    String emailEliminar = scanner.nextLine();
                    usuarioManager.eliminarUsuario(emailEliminar);
                    break;
                    
                case 6:
                    System.out.print("Email: ");
                    String loginEmail = scanner.nextLine();
                    System.out.print("Contraseña: ");
                    String loginPassword = scanner.nextLine();
                    currentToken = usuarioManager.iniciarSesion(loginEmail, loginPassword);
                    if (currentToken != null) {
                        System.out.println("Token: " + currentToken);
                    }
                    break;
                    
                case 7:
                    if (currentToken != null) {
                        usuarioManager.cerrarSesion(currentToken);
                        currentToken = null;
                    } else {
                        System.out.println("No hay sesión activa.");
                    }
                    break;
                    
                case 8:
                    if (currentToken != null) {
                        usuarioManager.cerrarSesion(currentToken);
                    }
                    scanner.close();
                    System.exit(0);
                    
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }
}