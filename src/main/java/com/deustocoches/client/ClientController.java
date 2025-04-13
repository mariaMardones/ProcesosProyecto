package com.deustocoches.client;

import com.deustocoches.model.Coche;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private IServiceProxy serviceProxy;

    private String token;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("token", token);
    }

    // Home endpoint
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Usuario> usuarios = serviceProxy.listarUsuariosResgistrados();
            model.addAttribute("usuarios", usuarios);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load users: " + e.getMessage());
        }
        return "home";
    }

    // Authentication endpoints
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email, @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        try {
            String token = serviceProxy.login(email, password);
            if (token != null) {
                this.token = token;
                return "redirect:/client/";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid credentials");
            return "redirect:/client/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "redirect:/client/login";
        }
    }

    @PostMapping("/logout")
    public String performLogout(@RequestBody String token, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.logout(token);
            this.token = null;
            redirectAttributes.addFlashAttribute("successMessage", "Logged out successfully");
            return "redirect:/client/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Logout failed: " + e.getMessage());
            return "redirect:/client/login";
        }
    }

    // Usuario endpoints
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        try {
            List<Usuario> usuarios = serviceProxy.listarUsuariosResgistrados();
            model.addAttribute("usuarios", usuarios);
            return "usuarios/lista";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load users: " + e.getMessage());
            return "usuarios/lista";
        }
    }

    @GetMapping("/usuario")
    public String getUsuarioByEmail(@RequestParam("email") String email, Model model) {
        try {
            Usuario usuario = serviceProxy.getUsuarioByEmail(email);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                return "usuario/detalle";
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

    @PostMapping("/usuario/eliminar")
    public String eliminarUsuario(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.eliminarUsuario(email);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
            return "redirect:/client/usuarios";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
            return "redirect:/client/usuarios";
        }
    }

    @PostMapping("/usuario/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            Usuario nuevoUsuario = serviceProxy.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully");
            return "redirect:/client/usuario?email=" + nuevoUsuario.getEmail();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to register user: " + e.getMessage());
            return "redirect:/client/usuarios";
        }
    }

    // Coche endpoints
    @GetMapping("/coches")
    public String listarCoches(Model model) {
        try {
            List<Coche> coches = serviceProxy.ListarCoches();
            model.addAttribute("coches", coches);
            return "coches/lista";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load cars: " + e.getMessage());
            return "coches/lista";
        }
    }

    @GetMapping("/coches/disponibles")
    public String listarCochesDisponibles(Model model) {
        try {
            List<Coche> cochesDisponibles = serviceProxy.ListarCochesDisponibles();
            model.addAttribute("coches", cochesDisponibles);
            return "coches/disponibles";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load available cars: " + e.getMessage());
            return "coches/disponibles";
        }
    }

    @GetMapping("/coche")
    public String getCocheByMatricula(@RequestParam("matricula") String matricula, Model model) {
        try {
            Coche coche = serviceProxy.getCocheByMatricula(matricula);
            if (coche != null) {
                model.addAttribute("coche", coche);
                return "coches/detalle";
            }
            return "redirect:/client/coches?error=car-not-found";
        } catch (RuntimeException e) {
            return "redirect:/client/coches?error=car-error";
        }
    }

    @PostMapping("/coche/crear")
    public String crearCoche(@ModelAttribute Coche coche, RedirectAttributes redirectAttributes) {
        try {
            Coche nuevoCoche = serviceProxy.crearCoche(coche);
            redirectAttributes.addFlashAttribute("successMessage", "Car created successfully");
            return "redirect:/client/coche?matricula=" + nuevoCoche.getMatricula();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create car: " + e.getMessage());
            return "redirect:/client/coches";
        }
    }

    @PutMapping("/coche/actualizar")
    public String actualizarCoche(@RequestParam("matricula") String matricula, @ModelAttribute Coche coche,
            RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.actualizarCoche(matricula, coche);
            redirectAttributes.addFlashAttribute("successMessage", "Car updated successfully");
            return "redirect:/client/coche?matricula=" + matricula;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update car: " + e.getMessage());
            return "redirect:/client/coche?matricula=" + matricula;
        }
    }

    @PostMapping("/coche/eliminar")
    public String eliminarCoche(@RequestParam("matricula") String matricula, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.eliminarCoche(matricula);
            redirectAttributes.addFlashAttribute("successMessage", "Car deleted successfully");
            return "redirect:/client/coches";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete car: " + e.getMessage());
            return "redirect:/client/coches";
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