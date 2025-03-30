package com.example.restapi.client;

import java.util.Scanner;

public class MainClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: MainClient <hostname> <port>");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSeleccione el cliente a ejecutar:");
            System.out.println("1. Book Manager");
            System.out.println("2. Usuario Manager");
            System.out.println("3. Coche Manager");
            System.out.println("4. Reserva Manager");
            System.out.println("5. Salir");
            System.out.print("Ingrese su opción: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    BookManager bookManager = new BookManager(hostname, port);
                    bookManager.main(new String[]{hostname, port});
                    break;
                case 2:
                    UsuarioManager usuarioManager = new UsuarioManager(hostname, port);
                    usuarioManager.main(new String[]{hostname, port});
                    break;
                case 3:
                    CocheManager cocheManager = new CocheManager(hostname, port);
                    cocheManager.main(new String[]{hostname, port});
                    break;
                case 4:
                    ReservaManager reservaManager = new ReservaManager(hostname, port);
                    reservaManager.main(new String[]{hostname, port});
                    break;
                case 5:
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }
}