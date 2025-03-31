// Función para manejar el registro
async function handleRegister(event) {
    event.preventDefault();
    
    const usuario = {
        nombre: document.getElementById('regNombre').value,
        apellido: document.getElementById('regApellido').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value,
        tlf: document.getElementById('regTlf').value,
        fechaNacimiento: document.getElementById('regFechaNac').value,
        rol: document.getElementById('regRol').value
    };
    
    try {
        const response = await fetch('/api/usuario/registrar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuario)
        });
        
        if (response.ok) {
            alert('Registro exitoso! Por favor inicia sesión');
            window.location.href = 'login.html';
        } else {
            const error = await response.json();
            document.getElementById('registerError').textContent = error.message || 'Error en el registro';
        }
    } catch (error) {
        document.getElementById('registerError').textContent = 'Error de conexión';
    }
}

// Event listeners para registro
document.addEventListener('DOMContentLoaded', function() {
    // Si estamos en la página de login
    if (document.getElementById('loginForm')) {
        document.getElementById('loginForm').addEventListener('submit', handleLogin);
    }
    
    // Si estamos en la página de registro
    if (document.getElementById('registerForm')) {
        document.getElementById('registerForm').addEventListener('submit', handleRegister);
    }
    
    // Resto de tu código existente...
});

// Función para manejar el login
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('userRole', data.rol);
            window.location.href = 'index.html'; // Redirige al sistema
        } else {
            const error = await response.json();
            document.getElementById('loginError').textContent = error.message || 'Error al iniciar sesión';
        }
    } catch (error) {
        document.getElementById('loginError').textContent = 'Error de conexión';
    }
}

// Verificar autenticación al cargar
function checkAuth() {
    const token = localStorage.getItem('token');
    const currentPage = window.location.pathname.split('/').pop();
    
    if (!token && currentPage !== 'login.html') {
        window.location.href = 'login.html';
    } else if (token && currentPage === 'login.html') {
        window.location.href = 'index.html';
    }
}

// Función para cerrar sesión
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    window.location.href = 'login.html';
}

// Modificar fetch para incluir token
async function authFetch(url, options = {}) {
    const token = localStorage.getItem('token');
    
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    
    const response = await fetch(url, options);
    
    if (response.status === 401) {
        logout();
        return;
    }
    
    return response;
}

// Actualizar todas las funciones fetch para usar authFetch
async function fetchUsuarios() {
    const response = await authFetch('/api/usuario');
    if (!response) return;
    
    const usuarios = await response.json();
    // ... resto del código igual ...
}

// ... (todas las demás funciones fetch deben usar authFetch en lugar de fetch)

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
    
    // Si estamos en la página de login
    if (document.getElementById('loginForm')) {
        document.getElementById('loginForm').addEventListener('submit', handleLogin);
    }
    
    // Si estamos en el sistema principal
    if (document.getElementById('usuarioForm')) {
        // Agregar botón de logout
        const header = document.querySelector('h1');
        const logoutBtn = document.createElement('button');
        logoutBtn.textContent = 'Cerrar Sesión';
        logoutBtn.onclick = logout;
        logoutBtn.style.float = 'right';
        header.appendChild(logoutBtn);
        
        // ... otros event listeners existentes ...
    }
});

// Funciones generales
function openTab(evt, tabName) {
    const tabcontent = document.getElementsByClassName("tabcontent");
    for (let i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    
    const tablinks = document.getElementsByClassName("tablinks");
    for (let i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
}

// Funciones para Usuarios
async function fetchUsuarios() {
    const response = await fetch('/api/usuario');
    const usuarios = await response.json();
    
    let tableContent = usuarios.map(usuario => `
        <tr id="usuario-row-${usuario.id}">
            <td>${usuario.id}</td>
            <td>${usuario.nombre} ${usuario.apellido}</td>
            <td>${usuario.email}</td>
            <td>${usuario.tlf}</td>
            <td>${usuario.rol}</td>
            <td>
                <button onclick="editUsuario(${usuario.id})">Editar</button>
                <button onclick="deleteUsuario('${usuario.email}')">Eliminar</button>
            </td>
        </tr>
    `).join('');
    
    document.getElementById('usuarioTable').innerHTML = tableContent;
    
    // Llenar select de usuarios para reservas
    const usuarioSelect = document.getElementById('usuarioId');
    usuarioSelect.innerHTML = '<option value="">Seleccione usuario</option>' + 
        usuarios.map(u => `<option value="${u.id}">${u.nombre} ${u.apellido}</option>`).join('');
}

async function addUsuario() {
    const usuario = {
        nombre: document.getElementById('nombre').value,
        apellido: document.getElementById('apellido').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        tlf: document.getElementById('tlf').value,
        fechaNacimiento: document.getElementById('fechaNacimiento').value,
        rol: document.getElementById('rol').value
    };
    
    await fetch('/api/usuario/registrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuario)
    });
    
    document.getElementById('usuarioForm').reset();
    fetchUsuarios();
}

async function deleteUsuario(email) {
    await fetch(`/api/usuario/eliminar?email=${email}`, {
        method: 'DELETE'
    });
    fetchUsuarios();
}

// Funciones para Coches
async function fetchCoches() {
    const response = await fetch('/api/coche');
    const coches = await response.json();
    
    let tableContent = coches.map(coche => `
        <tr id="coche-row-${coche.matricula}">
            <td>${coche.matricula}</td>
            <td>${coche.marca}</td>
            <td>${coche.modelo}</td>
            <td>${coche.anio}</td>
            <td>${coche.precio}€</td>
            <td>${coche.disponible ? 'Sí' : 'No'}</td>
            <td>
                <button onclick="editCoche('${coche.matricula}')">Editar</button>
                <button onclick="deleteCoche('${coche.matricula}')">Eliminar</button>
            </td>
        </tr>
    `).join('');
    
    document.getElementById('cocheTable').innerHTML = tableContent;
    
    // Llenar select de coches para reservas
    const cocheSelect = document.getElementById('cocheMatricula');
    cocheSelect.innerHTML = '<option value="">Seleccione coche</option>' + 
        coches.filter(c => c.disponible)
             .map(c => `<option value="${c.matricula}">${c.marca} ${c.modelo} (${c.matricula})</option>`)
             .join('');
}

async function addCoche() {
    const coche = {
        matricula: document.getElementById('matricula').value,
        marca: document.getElementById('marca').value,
        modelo: document.getElementById('modelo').value,
        anio: parseInt(document.getElementById('anio').value),
        color: document.getElementById('color').value,
        precio: parseFloat(document.getElementById('precio').value),
        disponible: document.getElementById('disponible').checked
    };
    
    await fetch('/api/coche/crear', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(coche)
    });
    
    document.getElementById('cocheForm').reset();
    fetchCoches();
}

async function deleteCoche(matricula) {
    await fetch(`/api/coche/eliminar?matricula=${matricula}`, {
        method: 'DELETE'
    });
    fetchCoches();
}

// Funciones para Reservas
async function fetchReservas() {
    const response = await fetch('/api/reservas');
    const reservas = await response.json();
    
    let tableContent = reservas.map(reserva => `
        <tr id="reserva-row-${reserva.id}">
            <td>${reserva.id}</td>
            <td>${reserva.usuario.nombre} (ID: ${reserva.usuario.id})</td>
            <td>${reserva.coche.marca} ${reserva.coche.modelo} (${reserva.coche.matricula})</td>
            <td>${reserva.fecha}</td>
            <td>${reserva.precioTotal}€</td>
            <td>${reserva.estado}</td>
            <td>
                <button onclick="editReserva(${reserva.id})">Editar</button>
                <button onclick="deleteReserva(${reserva.id})">Eliminar</button>
            </td>
        </tr>
    `).join('');
    
    document.getElementById('reservaTable').innerHTML = tableContent;
}

async function addReserva() {
    const reserva = {
        usuario: { id: parseInt(document.getElementById('usuarioId').value) },
        coche: { matricula: document.getElementById('cocheMatricula').value },
        fecha: document.getElementById('fechaReserva').value,
        precioTotal: parseFloat(document.getElementById('precioTotal').value),
        estado: document.getElementById('estadoReserva').value
    };
    
    await fetch('/api/reservas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reserva)
    });
    
    document.getElementById('reservaForm').reset();
    fetchReservas();
    fetchCoches(); // Actualizar disponibilidad
}

async function deleteReserva(id) {
    await fetch(`/api/reservas/${id}`, {
        method: 'DELETE'
    });
    fetchReservas();
    fetchCoches(); // Actualizar disponibilidad
}

// Event listeners para formularios
document.getElementById('usuarioForm').addEventListener('submit', function(e) {
    e.preventDefault();
    addUsuario();
});

document.getElementById('cocheForm').addEventListener('submit', function(e) {
    e.preventDefault();
    addCoche();
});

document.getElementById('reservaForm').addEventListener('submit', function(e) {
    e.preventDefault();
    addReserva();
});

// Cargar datos iniciales
window.onload = function() {
    fetchUsuarios();
    fetchCoches();
    fetchReservas();
};