package com.deustocoches.client;

import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import com.deustocoches.model.Coche;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RestTemplateServiceProxy implements IServiceProxy {

    private final RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public RestTemplateServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Usuario> listarUsuariosResgistrados() {
        String url = apiBaseUrl + "/api/usuario";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve users: " + e.getStatusText());
        }
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        String url = apiBaseUrl + "/api/usuario/buscar?email=" + email;
        try {
            Usuario usuario = restTemplate.getForObject(url, Usuario.class);
            return usuario;
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            throw new RuntimeException("Failed to retrieve user by email: " + e.getStatusText());
        }
    }

    @Override
    public Usuario actualizarUsuario(String email, Usuario usuario) {
        String url = apiBaseUrl + "/api/usuario/actualizar?email=" + email;
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(usuario), Usuario.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to update user: " + e.getStatusText());
        }
    }

    @Override
    public void eliminarUsuario(String email) {
        String url = apiBaseUrl + "/api/usuario/eliminar?email=" + email;
        try {
            restTemplate.delete(url);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to delete user: " + e.getStatusText());
        }
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        String url = apiBaseUrl + "/api/usuario/registrar";
        try {
            return restTemplate.postForObject(url, usuario, Usuario.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to register user: " + e.getStatusText());
        }
    }

    @Override
    public String login(String email, String password) {
        String url = apiBaseUrl + "/api/usuario/login?email=" + email + "&password=" + password;
        try {
            String token = restTemplate.postForObject(url, null, String.class);
            return token;
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Login failed: " + e.getStatusText());
        }
    }

    @Override
    public void logout(String token) {
        String url = apiBaseUrl + "/api/usuario/logout";
        try {
            restTemplate.postForObject(url, token, Void.class);

        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Logout failed: " + e.getStatusText());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Reserva> obtenerReservas() {
        String url = apiBaseUrl + "/api/reservas";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve reservations: " + e.getStatusText());
        }
    }

    @Override
    public Reserva obtenerReservaPorId(Integer id) {
        String url = apiBaseUrl + "/api/reservas/buscar/" + id;
        try {
            return restTemplate.getForObject(url, Reserva.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve reservations: " + e.getStatusText());
        }
    }

    @Override
    public Reserva crearReserva(Reserva reserva) {
        String url = apiBaseUrl + "/api/reservas/crear";
        try {
            return restTemplate.postForObject(url, reserva, Reserva.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to create reservation: " + e.getStatusText());
        }
    }

    @Override
    public Reserva actualizarReserva(Integer id, Reserva detallesReserva) {
        String url = apiBaseUrl + "/api/reservas/actualizar/" + id;
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(detallesReserva), Reserva.class)
                    .getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to update reservation: " + e.getStatusText());
        }
    }

    @Override
    public void eliminarReserva(Integer id) {
        String url = apiBaseUrl + "/api/reservas/eliminar/" + id;
        try {
            restTemplate.delete(url);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to delete reservation: " + e.getStatusText());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Coche> ListarCoches() {
        String url = apiBaseUrl + "/api/coche";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve cars: " + e.getStatusText());
        }
    }

    @Override
    public Coche getCocheByMatricula(String matricula) {
        String url = apiBaseUrl + "/api/coche/buscar?matricula=" + matricula;
        try {
            Coche coche = restTemplate.getForObject(url, Coche.class);
            return coche;
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve car by license plate: " + e.getStatusText());
        }
    }

    @Override
    public Coche crearCoche(Coche coche) {
        String url = apiBaseUrl + "/api/coche/crear";
        try {
            return restTemplate.postForObject(url, coche, Coche.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to create car: " + e.getStatusText());
        }
    }

    @Override
    public Coche actualizarCoche(String matricula, Coche coche) {
        String url = apiBaseUrl + "/api/coche/actualizar?matricula=" + matricula;
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(coche), Coche.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to update car: " + e.getStatusText());
        }
    }

    @Override
    public void eliminarCoche(String matricula) {
        String url = apiBaseUrl + "/api/coche/eliminar?matricula=" + matricula;
        try {
            restTemplate.delete(url);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to delete car: " + e.getStatusText());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Coche> ListarCochesDisponibles() {
        String url = apiBaseUrl + "/api/coche/disponibles";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve available cars: " + e.getStatusText());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Reserva> obtenerReservasConfirmadas() {
        String url = apiBaseUrl + "/api/reservas/confirmadas";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve confirmed reservations: " + e.getStatusText());
        }
    }
}