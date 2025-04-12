package es.deusto.sd.auctions.client.proxies;

import java.util.List;

import com.example.restapi.model.Coche;
import com.example.restapi.model.Usuario;

public interface IServiceProxy {
	// CRUD operations for Usuario
	void eliminarUsuario(String email);
	Usuario actualizarUsuario(String email, Usuario usuario);
	Usuario getUsuarioByEmail(String email);
	List<Usuario> listarUsuariosResgistrados();
	Usuario registrarUsuario(Usuario usuario);
	String login(String email, String password);
	void logout(String token);

	// CRUD operations for Coche
	List<Coche> ListarCoches();
	List<Coche> ListarCochesDisponibles();
	Coche getCocheByMatricula(String matricula);
	Coche actualizarCoche(String matricula, Coche coche);
	void eliminarCoche(String matricula);
	Coche crearCoche(Coche coche);

	// CRUD operations for Reserva
	List<Reserva> obtenReservas();
	Reserva obtenerReservaPorId(Integer id);
	Reserva crearReserva(Reserva reserva);
	Reserva actualizarReserva(Integer id, Reserva detallesReserva);
	void eliminarReserva(Integer id);
}