package com.deustocoches.client.controller;

import com.deustocoches.client.service.RestTemplateServiceProxy;
import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
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
    private String currentUserEmail;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl);
        model.addAttribute("token", token);
        model.addAttribute("currentUserEmail", currentUserEmail);
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
                currentUserEmail = email;
                Usuario u = serviceProxy.getUsuarioByEmail(email);
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
            return "";
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
            return "";
        } catch (RuntimeException e) {
            return "";
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
            return "redirect:/coches";
        }
    }

    @GetMapping("/coches/filtrar")
    public String filtrarCoches(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            Model model) {
        try {
            List<Coche> coches = serviceProxy.filtrarCoches(marca, modelo, precioMin, precioMax);
            model.addAttribute("coches", coches);
            return "coches";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error al filtrar coches: " + e.getMessage());
            return "coches";
        }
    }

    @PostMapping("/usuario/bloquear")
    public String bloquearUsuario(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.bloquearUsuario(email);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario bloqueado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al bloquear usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/usuario/desbloquear")
    public String desbloquearUsuario(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.desbloquearUsuario(email);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario desbloqueado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al desbloquear usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/reserva/pedido")
    public String hacerPedido(@RequestParam("email") String email, @RequestParam("estado") String estado, @RequestParam("matricula") String matricula , RedirectAttributes redirectAttributes) {
        Usuario usuario = serviceProxy.getUsuarioByEmail(email);
        Coche coche = serviceProxy.getCocheByMatricula(matricula);
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);    
        reserva.setCoche(coche);
        reserva.setPrecioTotal(coche.getPrecio()*1.21); // Precio del coche + IVA del 21%
        reserva.setEstado(EstadoReserva.valueOf(estado));
        String fechaActual = java.time.LocalDate.now().toString(); // yyyy-MM-dd
        reserva.setFecha(fechaActual);
        if (reserva.getUsuario() == null || reserva.getCoche() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario y coche son obligatorios para hacer el pedido.");
            return "redirect:/coches/disponibles";
        }

        try {
            serviceProxy.crearReserva(reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido realizado correctamente.");
            return "redirect:/coches/disponibles";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al hacer el pedido: " + e.getMessage());
            return "redirect:/coches/disponibles";
        }
    }

    // Mostrar reservas confirmadas por usuario
    @GetMapping("/reservas/usuario/confirmadas")
    public String mostrarReservasConfirmadasPorUsuario(
            @RequestParam(value = "email", required = false) String email,
            Model model) {
        String userEmail = (currentUserEmail != null) ? currentUserEmail : email;
        try {
            List<Reserva> reservas = serviceProxy.obtenerReservasConfirmadasPorUsuario(userEmail);
            model.addAttribute("reservas", reservas);
            model.addAttribute("currentUserEmail", userEmail);
            model.addAttribute("selectedEstado", "COMPRADA");
            return "reservas";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load confirmed reservations: " + e.getMessage());
            return "reservas";
        }
    }

    // Mostrar reservas pendientes por usuario
    @GetMapping("/reservas/usuario/pendientes")
    public String mostrarReservasPendientesPorUsuario(
            @RequestParam(value = "email", required = false) String email,
            Model model) {
        String userEmail = (currentUserEmail != null) ? currentUserEmail : email;
        try {
            List<Reserva> reservas = serviceProxy.obtenerReservasPendientesPorUsuario(userEmail);
            model.addAttribute("reservas", reservas);
            model.addAttribute("currentUserEmail", userEmail);
            model.addAttribute("selectedEstado", "PENDIENTE");
            return "reservas";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load pending reservations: " + e.getMessage());
            return "reservas";
        }
    }

    // Mostrar todas las reservas compradas (admin)
    @GetMapping("/reservas/compradas")
    public String mostrarReservasCompradas(Model model) {
        try {
            List<Reserva> reservas = serviceProxy.obtenerReservasCompradas();
            model.addAttribute("reservas", reservas);
            model.addAttribute("selectedEstado", "COMPRADA");
            return "reservasADMIN";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load bought reservations: " + e.getMessage());
            return "reservasADMIN";
        }
    }

    // Mostrar todas las reservas pendientes (admin)
    @GetMapping("/reservas/pendientes")
    public String mostrarReservasPendientes(Model model) {
        try {
            List<Reserva> reservas = serviceProxy.obtenerReservasPendientes();
            model.addAttribute("reservas", reservas);
            model.addAttribute("selectedEstado", "PENDIENTE");
            return "reservasADMIN";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Failed to load pending reservations: " + e.getMessage());
            return "reservasADMIN";
        }
    }
    
    @PostMapping("/reserva/{id}/eliminar")
    public String eliminarReserva(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.eliminarReserva(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation deleted successfully");
            return "redirect:/reservas/usuario/pendientes";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete reservation: " + e.getMessage());
            return "";
        }
    }

    @PostMapping("/reserva/{id}/actualizar")
    public String actualizarReserva(@PathVariable Integer id, @ModelAttribute Reserva reserva,
            RedirectAttributes redirectAttributes) {
        try {
            serviceProxy.actualizarReserva(id, reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation updated successfully");
            return "redirect:/reservas/usuario/confirmadas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update reservation: " + e.getMessage());
            return "redirect:/reservas/usuario/confirmadas";
        }
    }

}