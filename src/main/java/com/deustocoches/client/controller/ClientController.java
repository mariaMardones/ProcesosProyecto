package com.deustocoches.client.controller;

import com.deustocoches.client.service.RestTemplateServiceProxy;
import com.deustocoches.model.Coche;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ClientController {

    @Autowired
    private RestTemplateServiceProxy serviceProxy;

    private String token;
    private String currentUserEmail; // Guarda el email del usuario logueado

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl);
        model.addAttribute("token", token);
    }

    // Página principal: login (index.html)
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Endpoint para mostrar el login (redirige a "/")
    @GetMapping("/login")
    public String showLoginPage() {
        return "index";
    }

    // Login: si es correcto, redirige a usuario.html con el email del usuario
    @PostMapping("/login")
    public String performLogin(@RequestParam String email, @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        try {
            String token = serviceProxy.login(email, password);
            if (token != null) {
                this.token = token;
                this.currentUserEmail = email;
                Usuario u =serviceProxy.getUsuarioByEmail(email);
                if (u.getRol() == TipoRol.ADMIN) {
                    return "redirect:/usuarios";
                } else {
                    return "redirect:/coches/disponibles";
                }
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials");
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/logout")
    public String performLogout(RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.logout(token);
            this.token = null;
            this.currentUserEmail = null;
            redirectAttributes.addFlashAttribute("successMessage", "Logged out successfully");
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Logout failed: " + e.getMessage());
            return "";
        }
    }

    @GetMapping("/usuario/registrar")
    public String showRegisterPage() {
        return "registrar";
    }

    @PostMapping("/usuario/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to register user: " + e.getMessage());
            return "redirect:/usuario/registrar";
        }
    }

    @GetMapping("/coches/disponibles")
    public String listarCochesDisponibles(Model model) {
        try {
            List<Coche> cochesDisponibles = serviceProxy.ListarCochesDisponibles();
            model.addAttribute("coches", cochesDisponibles);
            return "coches";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load available cars: " + e.getMessage());
            return "coches";
        }
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        try {
            List<Usuario> usuarios = serviceProxy.listarUsuariosResgistrados();
            model.addAttribute("usuarios", usuarios);
            return "usuariosADMIN";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load users: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/usuario/eliminar")
    public String eliminarUsuario(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.eliminarUsuario(email);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
            return "redirect:/";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    @GetMapping("/coches")
    public String listarCoches(Model model) {
        try {
            List<Coche> coches = serviceProxy.ListarCoches();
            model.addAttribute("coches", coches);
            return "cochesADMIN";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load cars: " + e.getMessage());
            return "";
        }
    }

    @GetMapping("/coche")
    public String getCocheByMatricula(@RequestParam("matricula") String matricula, Model model) {
        try {
            Coche coche = serviceProxy.getCocheByMatricula(matricula);
            if (coche != null) {
                model.addAttribute("coche", coche);
                return "detalleCocheADMIN"; // Renderiza detalleCocheADMIN.html
            }
            return "redirect:/client/coches?error=car-not-found";
        } catch (RuntimeException e) {
            return "redirect:/client/coches?error=car-error";
        }
    }

    @PostMapping("/coche/crear")
    public String crearCoche(@ModelAttribute Coche coche, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.crearCoche(coche);
            redirectAttributes.addFlashAttribute("successMessage", "Car created successfully");
            return "redirect:/coches";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create car: " + e.getMessage());
            return "redirect:/coches";
        }
    }

    @PostMapping("/coche/actualizar")
    public String actualizarCoche(@RequestParam("matricula") String matricula, @ModelAttribute Coche coche,
            RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.actualizarCoche(matricula, coche);
            redirectAttributes.addFlashAttribute("successMessage", "Car updated successfully");
            return "redirect:/coches";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update car: " + e.getMessage());
            return "redirect:/coche";
        }
    }

    @PostMapping("/coche/eliminar")
    public String eliminarCoche(@RequestParam("matricula") String matricula, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.eliminarCoche(matricula);
            redirectAttributes.addFlashAttribute("successMessage", "Car deleted successfully");
            return "redirect:/coches";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete car: " + e.getMessage());
            return "redirect:/coche";
        }
    }

//NO UTILIZADOS, DE MOMENTO
    @GetMapping("/usuario")
    public String getUsuarioByEmail(@RequestParam("email") String email, Model model) {
        try {
            Usuario usuario = serviceProxy.getUsuarioByEmail(email);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                return "usuario"; // Renderiza usuario.html
            }
            return "redirect:/client/usuarios?error=user-not-found";
        } catch (RuntimeException e) {
            return "redirect:/client/usuarios?error=user-error";
        }
    }

    @PutMapping("/usuario/actualizar")
    public String actualizarUsuario(@RequestParam("email") String email, @ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.actualizarUsuario(email, usuario);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
            return "redirect:/client/usuario?email=" + email;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user: " + e.getMessage());
            return "redirect:/client/usuario?email=" + email;
        }
    }

    // Reserva endpoints
    @GetMapping("/reservas")
    public String listarReservas(Model model) {
        try {
            List<Reserva> reservas = serviceProxy.obtenerReservas();
            model.addAttribute("reservas", reservas);
            return "reservas/lista";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load reservations: " + e.getMessage());
            return "reservas/lista";
        }
    }

    @GetMapping("/reservas/confirmadas")
    public String listarReservasConfirmadas(Model model) {
        try {
            List<Reserva> reservasConfirmadas = serviceProxy.obtenerReservasConfirmadas();
            model.addAttribute("reservas", reservasConfirmadas);
            model.addAttribute("tipoLista", "confirmadas");
            return "reservas/lista";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load confirmed reservations: " + e.getMessage());
            return "reservas/lista";
        }
    }

    @GetMapping("/reserva/{id}")
    public String obtenerReserva(@PathVariable Integer id, Model model) {
        try {
            Reserva reserva = serviceProxy.obtenerReservaPorId(id);
            if (reserva != null) {
                model.addAttribute("reserva", reserva);
                return "reservas/detalle";
            }
            return "redirect:/client/reservas?error=reservation-not-found";
        } catch (RuntimeException e) {
            return "redirect:/client/reservas?error=reservation-error";
        }
    }

    @PostMapping("/reserva/crear")
    public String crearReserva(@ModelAttribute Reserva reserva, RedirectAttributes redirectAttributes) {
        try {
            Reserva nuevaReserva = serviceProxy.crearReserva(reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation created successfully");
            return "redirect:/client/reserva/" + nuevaReserva.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create reservation: " + e.getMessage());
            return "redirect:/client/reservas";
        }
    }

    @PutMapping("/reserva/{id}/actualizar")
    public String actualizarReserva(@PathVariable Integer id, @ModelAttribute Reserva reserva,
            RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.actualizarReserva(id, reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation updated successfully");
            return "redirect:/client/reserva/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update reservation: " + e.getMessage());
            return "redirect:/client/reserva/" + id;
        }
    }

    @PostMapping("/reserva/{id}/eliminar")
    public String eliminarReserva(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.eliminarReserva(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation deleted successfully");
            return "redirect:/client/reservas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete reservation: " + e.getMessage());
            return "redirect:/client/reservas";
        }
    }

    @PostMapping("/reserva/pedido")
    public String hacerPedido(@ModelAttribute Reserva reserva, RedirectAttributes redirectAttributes) {
        if (reserva.getUsuario() == null || reserva.getCoche() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario y coche son obligatorios para hacer el pedido.");
            return "redirect:/client/reservas";
        }

        try {
            Reserva nuevaReserva = serviceProxy.crearReserva(reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido realizado correctamente.");
            return "redirect:/client/reserva/" + nuevaReserva.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al hacer el pedido: " + e.getMessage());
            return "redirect:/client/reservas";
        }
    }

}