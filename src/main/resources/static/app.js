// Estado global
let currentToken = null;

// ================== AUTENTICACIÓN ================== //
async function login() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await fetch(`http://localhost:8080/api/usuario/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        const responseText = await response.text();
        
        if (response.ok) {
            const token = responseText;
            sessionStorage.setItem('authToken', token);
            currentToken = token;
            window.location.href = 'management.html';
        } else {
            alert(`Error ${response.status}: ${responseText || 'Credenciales incorrectas'}`);
        }
    } catch (error) {
        console.error('Error en login:', error);
        alert('Error al contactar con el servidor: ' + error.message);
    }
}

async function register() {
    const usuario = {
        nombre: document.getElementById('regNombre').value,
        apellido: document.getElementById('regApellido').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value
    };

    try {
        const response = await fetch('/api/usuario/registrar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuario)
        });
        
        if (response.ok) {
            alert('Registro exitoso!');
            switchAuthTab('login');
        } else {
            alert('Error en el registro');
        }
    } catch (error) {
        console.error('Error en registro:', error);
    }
}

function switchAuthTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-content').forEach(c => c.classList.remove('active'));
    
    document.querySelector(`.auth-tab[onclick*="${tab}"]`).classList.add('active');
    document.getElementById(`${tab}Form`).classList.add('active');
}

// ================== GESTIÓN ================== //
async function logout() {
    sessionStorage.removeItem('authToken');
    window.location.href = 'index.html';
}

// Función para cambiar entre pestañas
function openTab(evt, tabName) {
    let tabcontent = document.getElementsByClassName("tabcontent");
    for (let i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    let tablinks = document.getElementsByClassName("tablinks");
    for (let i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
    
    // Carga el contenido específico de la pestaña
    if (tabName === 'reservas') {
        cargarUsuariosYCochesEnSelect();
    }
}

// ================== FETCH CON AUTENTICACIÓN ================== //
async function fetchWithAuth(url, options = {}) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
        logout();
        throw new Error('No autenticado');
    }
    
    const response = await fetch(url, {
        ...options,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            ...options.headers
        }
    });
    
    if (response.status === 401) logout();
    return response;
}

// ================== GESTIÓN DE USUARIOS ================== //
async function cargarUsuarios() {
    try {
        const response = await fetchWithAuth("/api/usuario");
        const usuarios = await response.json();
        const usuarioTable = document.getElementById('usuarioTable');
        usuarioTable.innerHTML = '';

        usuarios.forEach(usuario => {
            usuarioTable.innerHTML += `
                <tr>
                    <td>${usuario.id}</td>
                    <td>${usuario.nombre} ${usuario.apellido || ''}</td>
                    <td>${usuario.email}</td>
                    <td>${usuario.telefono || '-'}</td>
                    <td>${usuario.rol}</td>
                    <td>
                        <button onclick="eliminarUsuario(${usuario.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error cargando usuarios:", error);
    }
}

async function registrarUsuario() {
    const usuario = {
        nombre: document.getElementById('nombre').value,
        apellido: document.getElementById('apellido').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        telefono: document.getElementById('tlf').value,
        fechaNacimiento: document.getElementById('fechaNacimiento').value,
        rol: document.getElementById('rol').value
    };

    try {
        const response = await fetchWithAuth('/api/usuario/registrar', {
            method: 'POST',
            body: JSON.stringify(usuario)
        });
        
        if (response.ok) {
            alert('Usuario registrado con éxito');
            document.getElementById('usuarioForm').reset();
            cargarUsuarios();
        } else {
            const error = await response.text();
            alert('Error al registrar usuario: ' + error);
        }
    } catch (error) {
        console.error('Error registrando usuario:', error);
    }
}

async function eliminarUsuario(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este usuario?')) {
        try {
            const response = await fetchWithAuth(`/api/usuario/${id}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                alert('Usuario eliminado con éxito');
                cargarUsuarios();
            } else {
                const error = await response.text();
                alert('Error al eliminar usuario: ' + error);
            }
        } catch (error) {
            console.error('Error eliminando usuario:', error);
        }
    }
}

// ================== GESTIÓN DE COCHES ================== //
async function cargarCoches() {
    try {
        const response = await fetchWithAuth("/api/coche");
        const coches = await response.json();
        const cocheTable = document.getElementById('cocheTable');
        cocheTable.innerHTML = '';

        coches.forEach(coche => {
            cocheTable.innerHTML += `
                <tr>
                    <td>${coche.matricula}</td>
                    <td>${coche.marca}</td>
                    <td>${coche.modelo}</td>
                    <td>${coche.anio}</td>
                    <td>${coche.precio}€</td>
                    <td>${coche.disponible ? 'Sí' : 'No'}</td>
                    <td>
                        <button onclick="eliminarCoche('${coche.matricula}')">Eliminar</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error cargando coches:", error);
    }
}

async function registrarCoche() {
    const coche = {
        matricula: document.getElementById('matricula').value,
        marca: document.getElementById('marca').value,
        modelo: document.getElementById('modelo').value,
        anio: document.getElementById('anio').value,
        precio: document.getElementById('precio').value,
        disponible: document.getElementById('disponible').value === 'true'
    };

    try {
        const response = await fetchWithAuth('/api/coche/crear', {
            method: 'POST',
            body: JSON.stringify(coche)
        });
        
        if (response.ok) {
            alert('Coche registrado con éxito');
            document.getElementById('cocheForm').reset();
            cargarCoches();
        } else {
            const error = await response.text();
            alert('Error al registrar coche: ' + error);
        }
    } catch (error) {
        console.error('Error registrando coche:', error);
    }
}

async function eliminarCoche(matricula) {
    if (confirm('¿Estás seguro de que deseas eliminar este coche?')) {
        try {
            const response = await fetchWithAuth(`/api/coche/${matricula}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                alert('Coche eliminado con éxito');
                cargarCoches();
            } else {
                const error = await response.text();
                alert('Error al eliminar coche: ' + error);
            }
        } catch (error) {
            console.error('Error eliminando coche:', error);
        }
    }
}

// ================== GESTIÓN DE RESERVAS ================== //
async function cargarReservas() {
    try {
        const response = await fetchWithAuth("/api/reservas");
        const reservas = await response.json();
        const reservaTable = document.getElementById('reservaTable');
        reservaTable.innerHTML = '';

        reservas.forEach(reserva => {
            reservaTable.innerHTML += `
                <tr>
                    <td>${reserva.id}</td>
                    <td>${reserva.usuario}</td>
                    <td>${reserva.coche}</td>
                    <td>${reserva.fecha}</td>
                    <td>${reserva.precio}€</td>
                    <td>${reserva.estado}</td>
                    <td>
                        <button onclick="eliminarReserva(${reserva.id})">Cancelar</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error cargando reservas:", error);
    }
}

async function cargarUsuariosYCochesEnSelect() {
    try {
        // Cargar usuarios para el select
        const usuariosResponse = await fetchWithAuth("/api/usuario");
        const usuarios = await usuariosResponse.json();
        const usuarioSelect = document.getElementById('usuarioId');
        
        // Mantener sólo la primera opción por defecto
        usuarioSelect.innerHTML = '<option value="">Seleccionar Usuario</option>';
        
        usuarios.forEach(usuario => {
            usuarioSelect.innerHTML += `
                <option value="${usuario.id}">${usuario.nombre} ${usuario.apellido || ''} (${usuario.email})</option>
            `;
        });
        
        // Cargar coches disponibles para el select
        const cochesResponse = await fetchWithAuth("/api/coche");
        const coches = await cochesResponse.json();
        const cocheSelect = document.getElementById('cocheMatricula');
        
        // Mantener sólo la primera opción por defecto
        cocheSelect.innerHTML = '<option value="">Seleccionar Coche</option>';
        
        coches.filter(coche => coche.disponible).forEach(coche => {
            cocheSelect.innerHTML += `
                <option value="${coche.matricula}">${coche.marca} ${coche.modelo} (${coche.matricula})</option>
            `;
        });
    } catch (error) {
        console.error("Error cargando datos para los selects:", error);
    }
}

async function registrarReserva() {
    const reserva = {
        usuarioId: document.getElementById('usuarioId').value,
        cocheMatricula: document.getElementById('cocheMatricula').value,
        fecha: document.getElementById('fechaReserva').value,
        estado: document.getElementById('estadoReserva').value
    };

    try {
        const response = await fetchWithAuth('/api/reservas/crear', {
            method: 'POST',
            body: JSON.stringify(reserva)
        });
        
        if (response.ok) {
            alert('Reserva creada con éxito');
            document.getElementById('reservaForm').reset();
            cargarReservas();
        } else {
            const error = await response.text();
            alert('Error al crear reserva: ' + error);
        }
    } catch (error) {
        console.error('Error creando reserva:', error);
    }
}

async function eliminarReserva(id) {
    if (confirm('¿Estás seguro de que deseas cancelar esta reserva?')) {
        try {
            const response = await fetchWithAuth(`/api/reserva/${id}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                alert('Reserva cancelada con éxito');
                cargarReservas();
            } else {
                const error = await response.text();
                alert('Error al cancelar reserva: ' + error);
            }
        } catch (error) {
            console.error('Error cancelando reserva:', error);
        }
    }
}

// ================== INICIALIZACIÓN ================== //
document.addEventListener('DOMContentLoaded', () => {
    // Verificar autenticación en management.html
    if (window.location.pathname.includes('management.html')) {
        if (!sessionStorage.getItem('authToken')) {
            window.location.href = 'index.html';
        } else {
            cargarUsuarios();
            cargarCoches();
            cargarReservas();
        }
    }
});