SpringBoot + REST API + MySQL
=============================

Descripción
--------------------
Este proyecto utiliza Spring Boot para crear una REST API y se conecta a una base de datos MySQL utilizando JPA, con una aplicación de ejemplo que maneja reservas, coches y usuarios.

El proyecto se basa en un servicio backend para gestionar reservas de coches y usuarios mediante una API REST. Usa Spring Boot y Spring Data JPA para interactuar con la base de datos MySQL.

La estructura básica de un proyecto Spring Boot se puede inicializar utilizando Spring Initializr en https://start.spring.io/.

Requisitos previos
--------------------
- Java 17+
- Maven
- MySQL


Lanzar la aplicación
--------------------

Requisitos previos
Asegúrate de que las dependencias necesarias están especificadas en el archivo pom.xml y la configuración de la base de datos en el archivo src/main/resources/application.properties.

Paso 1: Compilar y descargar dependencias
Para verificar y descargar todas las dependencias, ejecuta el siguiente comando:

      mvn compile

Paso 2: Configuración de la base de datos
Antes de conectar la aplicación a MySQL, asegúrate de que la base de datos está configurada correctamente.

Usa el archivo src/main/resources/dbsetup.sql para crear la base de datos y el usuario específico spq (con contraseña spq) para la aplicación. Si usas la línea de comandos de MySQL, ejecuta:

      mysql –uroot -p < src/main/resources/dbsetup.sql

O usa el contenido del archivo en cualquier otro cliente MySQL que estés utilizando.

Paso 3: Ejecutar la aplicación
Una vez configurada la base de datos, lanza el servidor con el siguiente comando:

    mvn spring-boot:run

Si no hay errores, la aplicación estará disponible en http://localhost:8080/. Puedes detener la aplicación presionando Ctrl+C.

Acceso a la aplicación
--------

Una vez que el programa este en funcionamiento, puede acceder a DeustoCoches a través de:

	http://localhost:8080
	
Características
--------------------
- Registro e inicio de sesión de usuarios.
- Ver el listado de coches disponibles.
- Ver el listado de todos los usuarios registrados.
- Como cliente, ver las compras hechas.
- Como admin, bloquear y desbloquear usuarios.
- Hacer compras
- ...

Doxyfile
-------------

doxygen src/main/resources/Doxyfile
start docs/doxygen/html/index.html

Ejecución de tests
------------------------

Puede ejecutar las distintas pruebas del proyecto utilizando los siguientes comandos:

Tests unitarios:
Para compilar y ejecutar las pruebas unitarias:	
	mvn test

Limpiar el directorio, compilar y ejecutar todos los teses unitarios, además de actualizar JaCoCo:
	mvn clean test

Las pruebas de rendimiento:
	mvn verify -Pperformance-tests


Creadores
--------------------

Este proyecto ha sido desarrollado por: 

- **Markel Urquiza** - [MarkelUrquiza](https://github.com/MarkelUrquiza)
- **Génesis Balcazar** - [gnnesis](https://github.com/gnnesis)
- **Ekaitz Hernando** - [hernandoekaitz](https://github.com/hernandoekaitz)
- **Maria Mardones** - [mariaMardones](https://github.com/mariaMardones)
- **Ekhiotz Garay** - [EkHi04](https://github.com/EkHi04)
