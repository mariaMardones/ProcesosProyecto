SpringBoot + REST API + MySQL
=============================

Este proyecto utiliza Spring Boot para crear una REST API y se conecta a una base de datos MySQL utilizando JPA, con una aplicación de ejemplo que maneja reservas, coches y usuarios.

El proyecto se basa en un servicio backend para gestionar reservas de coches y usuarios mediante una API REST. Usa Spring Boot y Spring Data JPA para interactuar con la base de datos MySQL.

La estructura básica de un proyecto Spring Boot se puede inicializar utilizando Spring Initializr en https://start.spring.io/.

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

API REST
--------

La aplicación expone una API REST que es utilizada por el cliente para interactuar con la base de datos. Algunos ejemplos de métodos son:

Obtener todas las reservas

    GET http://localhost:8080/api/reservas

Crear una nueva reserva

    POST http://localhost:8080/api/reservas
Content-Type: application/json

{
  "fecha": "2025-04-01",
  "precioTotal": 150.0,
  "estado": "confirmada",
  "usuario": {
    "id": 1
  },
  "coche": {
    "id": 2
  }
}

Eliminar una reserva

    DELETE http://localhost:8080/api/reservas/1

Para ver el listado completo de métodos de la API REST, puedes consultar las anotaciones en las clases de los controladores (ReservaController, UsuarioController, etc.) y la configuración de dependencias en el archivo pom.xml y application.properties.

Cliente de Línea de Comandos
----------------------------

El proyecto incluye una implementación de cliente REST que utiliza las bibliotecas de cliente REST de Spring Boot, implementada en la clase MainClient.java. Puedes ejecutar el cliente utilizando el siguiente comando de Maven:

    mvn exec:java

Consulta la sección <build> en el archivo pom.xml para ver cómo se configuró este comando.

Empaquetar la Aplicación
------------------------

Para empaquetar la aplicación, ejecuta el siguiente comando:

    mvn package

Esto incluirá todas las bibliotecas necesarias dentro del archivo target/rest-api-0.0.1-SNAPSHOT.jar, que puede ser distribuido.

Una vez empaquetada, la aplicación puede ser ejecutada con:

    java -jar rest-api-0.0.1-SNAPSHOT.jar

El cliente puede ser lanzado con el siguiente comando:

    java -cp rest-api-0.0.1-SNAPSHOT.jar -Dloader.main=com.example.restapi.client.MainClient org.springframework.boot.loader.launch.PropertiesLauncher localhost 8080

Por lo tanto, en un entorno de desarrollo real, sería recomendable crear proyectos Maven separados para el servidor y el cliente, facilitando la distribución y el mantenimiento de cada aplicación de forma independiente.

References
----------

* Very good explaination of the project: https://medium.com/@pratik.941/building-rest-api-using-spring-boot-a-comprehensive-guide-3e9b6d7a8951 
* Building REST services with Spring: https://spring.io/guides/tutorials/rest
* Good example documenting how to generate Swagger APIs in Spring Boot: https://bell-sw.com/blog/documenting-rest-api-with-swagger-in-spring-boot-3/#mcetoc_1heq9ft3o1v 
* Docker example with Spring: https://medium.com/@yunuseulucay/end-to-end-spring-boot-with-mysql-and-docker-2c42a6e036c0



