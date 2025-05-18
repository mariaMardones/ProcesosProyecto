package com.deustocoches.client.service;

import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import com.deustocoches.model.Coche;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
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

    @PostConstruct
    public void init() {
        if (apiBaseUrl == null || apiBaseUrl.contains("/api.base.url")) {
            apiBaseUrl = "http://127.0.0.1:8080/api";
        } else if (!apiBaseUrl.endsWith("/api")) {
            apiBaseUrl = apiBaseUrl + "/api";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Usuario> listarUsuariosResgistrados() {
        String url = apiBaseUrl + "/usuario";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to retrieve users: " + e.getStatusText());
        }
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        try {
            String url = apiBaseUrl + "/usuario/buscar?email=" + email;
            
            ResponseEntity<Usuario> response = restTemplate.getForEntity(url, Usuario.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            throw new RuntimeException("Failed to retrieve user by email: " + e.getStatusText());
        }
    }

    @Override
    public void eliminarUsuario(String email) {
        String url = apiBaseUrl + "/usuario/eliminar?email=" + email;
        try {
            restTemplate.delete(url);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to delete user: " + e.getStatusText());
        }
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        String url = apiBaseUrl + "/usuario/registrar";
        try {
            return restTemplate.postForObject(url, usuario, Usuario.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to register user: " + e.getStatusText());
        }
    }

    @Override
    public String login(String email, String password) {
        try {
            String url = apiBaseUrl + "/usuario/login?email=" + email + "&password=" + password;
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                null,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            
            throw new RuntimeException("Login failed: " + response.getStatusCode());
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 400) {
                return null; 
            }
            throw new RuntimeException("Login failed: " + e.getStatusCode());
        }
    }

    @Override
    public void logout(String token) {
        String url = apiBaseUrl + "/usuario/logout?token=" + token;
        try {
            restTemplate.postForObject(url, null, Void.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Logout failed: " + e.getStatusText());
    }
}

@Override
public Reserva crearReserva(Reserva reserva) {
    String url = apiBaseUrl + "/reservas/pedidos";
    try {
        return restTemplate.postForObject(url, reserva, Reserva.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to create reservation: " + e.getStatusText());
    }
}

@Override
public Reserva actualizarReserva(Integer id, Reserva detallesReserva) {
    String url = apiBaseUrl + "/reservas/actualizar/" + id;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(detallesReserva), Reserva.class)
                .getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to update reservation: " + e.getStatusText());
    }
}

@Override
public void eliminarReserva(Integer id) {
    String url = apiBaseUrl + "/reservas/eliminar/" + id;
    try {
        restTemplate.delete(url);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to delete reservation: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
@Override
public List<Coche> ListarCoches() {
    String url = apiBaseUrl + "/coche";
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve cars: " + e.getStatusText());
    }
}

@Override
public Coche getCocheByMatricula(String matricula) {
    String url = apiBaseUrl + "/coche/buscar?matricula=" + matricula;
    try {
        Coche coche = restTemplate.getForObject(url, Coche.class);
        return coche;
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve car by license plate: " + e.getStatusText());
    }
}

@Override
public Coche crearCoche(Coche coche) {
    String url = apiBaseUrl + "/coche/crear";
    try {
        return restTemplate.postForObject(url, coche, Coche.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to create car: " + e.getStatusText());
    }
}

@Override
public Coche actualizarCoche(String matricula, Coche coche) {
    String url = apiBaseUrl + "/coche/actualizar?matricula=" + matricula;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(coche), Coche.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to update car: " + e.getStatusText());
    }
}

@Override
public void eliminarCoche(String matricula) {
    String url = apiBaseUrl + "/coche/eliminar?matricula=" + matricula;
    try {
        restTemplate.delete(url);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to delete car: " + e.getStatusText());
    }
}

public List<Coche> filtrarCoches(String marca, String modelo, Double precioMin, Double precioMax) {
    String url = apiBaseUrl + "/coche/filtrar?";
    if (marca != null) url += "marca=" + marca + "&";
    if (modelo != null) url += "modelo=" + modelo + "&";
    if (precioMin != null) url += "precioMin=" + precioMin + "&";
    if (precioMax != null) url += "precioMax=" + precioMax;
    
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to filter cars: " + e.getStatusText());
    }
}

@Override
public Coche aplicarDescuento(String matricula, Double descuento) {
    String url = apiBaseUrl + "/coche/aplicarDescuento?matricula=" + matricula + "&descuento=" + descuento;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, null, Coche.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to apply discount: " + e.getStatusText());
    }
}

@Override
public Coche eliminarDescuento(String matricula) {
    String url = apiBaseUrl + "/coche/eliminarDescuento?matricula=" + matricula;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, null, Coche.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to remove discount: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
@Override
public List<Coche> ListarCochesDisponibles() {
    String url = apiBaseUrl + "/coche/disponibles";
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve available cars: " + e.getStatusText());
    }
}

@Override
public Reserva hacerPedido(Reserva reserva) {
    String url = apiBaseUrl + "/reservas/pedidos";
    try {
        return restTemplate.postForObject(url, reserva, Reserva.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to create reservation: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
public List<Reserva> obtenerReservasPorFecha(String fecha) {
    String url = apiBaseUrl + "/reservas/filtrar/fecha/" + fecha;
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve reservations by date: " + e.getStatusText());
    }
}

@Override
public Usuario bloquearUsuario(String email) {
    String url = apiBaseUrl + "/usuario/bloquear?email=" + email;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, null, Usuario.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to block user: " + e.getStatusText());
    }
}

@Override
public Usuario desbloquearUsuario(String email) {
    String url = apiBaseUrl + "/usuario/desbloquear?email=" + email;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, null, Usuario.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to unblock user: " + e.getStatusText());
    }
}

@Override
public Usuario crearAdmin(String email) {
    String url = apiBaseUrl + "/usuario/crearadmin?email=" + email;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, null, Usuario.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to change user rol: " + e.getStatusText());
    }
}

@Override
public Usuario eliminarAdmin(String email) {
    String url = apiBaseUrl + "/usuario/eliminaradmin?email=" + email;
    try {
        return restTemplate.exchange(url, HttpMethod.PUT, null, Usuario.class).getBody();
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to change user rol: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
public List<Reserva> obtenerReservasConfirmadasPorUsuario(String email) {
    String url = apiBaseUrl + "/reservas/usuario/confirmadas?email=" + email;
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve confirmed reservations by user: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
public List<Reserva> obtenerReservasPendientesPorUsuario(String email) {
    String url = apiBaseUrl + "/reservas/usuario/pendientes?email=" + email;
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve pending reservations by user: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
public List<Reserva> obtenerReservasCompradas() {
    String url = apiBaseUrl + "/reservas/compradas";
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve bought reservations: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
public List<Reserva> obtenerReservasPendientes() {
    String url = apiBaseUrl + "/reservas/pendientes";
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve pending reservations: " + e.getStatusText());
    }
}

public void metodoQuePuedeLanzarExcepcion() {
    try {
        restTemplate.getForObject(apiBaseUrl + "/ejemplo", String.class);
    } catch (RestClientException e) {
        throw new RuntimeException("Error durante la llamada a la API: " + e.getMessage(), e);
    }
}

public List<?> metodoQueDevuelveLista() {
    String url = apiBaseUrl + "/coleccion";
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (RestClientException e) {
        throw new RuntimeException("Error al recuperar la lista de elementos: " + e.getMessage(), e);
    }
}

@SuppressWarnings("unchecked")
public List<Reserva> obtenerReservasPorRangoFechas(String desde, String hasta) {
    String url = apiBaseUrl + "/reservas/filtrar/rango?desde=" + desde + "&hasta=" + hasta;
    try {
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        throw new RuntimeException("Failed to retrieve reservations by date range: " + e.getStatusText());
    }
}

@SuppressWarnings("unchecked")
@Override
public List<String> obtenerMarcas() {
    String url = apiBaseUrl + "/coche/marcas";
    try {
        System.out.println("Intentando conectar a: " + url);
        return restTemplate.getForObject(url, List.class);
    } catch (HttpStatusCodeException e) {
        System.err.println("Error HTTP: " + e.getStatusCode() + " - " + e.getStatusText());
        throw new RuntimeException("Failed to retrieve brands: " + e.getStatusText());
    }
}


}