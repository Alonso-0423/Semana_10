import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Actividad_10 {

    // Expresión regular para validar la contraseña
    private static final String PATRON_CONTRASENA =
            "^(?=(?:.*[A-Z]){2})(?=(?:.*[a-z]){3})(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

    private static final Pattern patron = Pattern.compile(PATRON_CONTRASENA);

    // Método para validar una contraseña
    public static boolean validarContrasena(String contrasena) {
        return patron.matcher(contrasena).matches();
    }

    public static void main(String[] args) {
        // Lista de contraseñas a validar
        List<String> contrasenas = List.of(
                "ClaveSegura123!",  // Válida
                "clave1@",          // Inválida (muy corta)
                "ClaveSegura",      // Inválida (falta carácter especial y número)
                "12345!!",          // Inválida (falta letras mayúsculas y minúsculas)
                "Clave1234!",       // Válida
                "ABabc12!!"         // Válida
        );

        // Archivo donde se almacenarán los resultados de validación
        String archivoLog = "registro_validacion_contrasenas.txt";

        // Servicio ejecutor para manejar los hilos
        ExecutorService servicioEjecutor = Executors.newFixedThreadPool(4);

        // Validar cada contraseña en un hilo separado y escribir resultados en el archivo
        for (String contrasena : contrasenas) {
            servicioEjecutor.submit(() -> {
                boolean esValida = validarContrasena(contrasena);
                String resultado = String.format("Contraseña: %s | Válida: %s\n", contrasena, esValida);

                // Escribir el resultado en el archivo de registro
                synchronized (Actividad_10.class) {
                    try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoLog, true))) {
                        escritor.write(resultado);
                    } catch (IOException e) {
                        System.err.println("Error al escribir en el archivo de registro: " + e.getMessage());
                    }
                }

                // Imprimir el resultado en la consola
                synchronized (System.out) {
                    System.out.print(resultado);
                }
            });
        }

        // Apagar el servicio ejecutor
        servicioEjecutor.shutdown();
        while (!servicioEjecutor.isTerminated()) {
            // Esperar a que se completen todas las tareas
        }

        System.out.println("Validación completa. Resultados almacenados en: " + archivoLog);
    }
}