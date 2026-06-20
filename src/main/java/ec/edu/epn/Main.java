package ec.edu.epn;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        // Azure Web Apps o Docker usarán el puerto 8080 configurado
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        tomcat.setPort(Integer.parseInt(webPort));

        // Apuntar al directorio de recursos webapp
        String webappDirLocation = "src/main/webapp/";
        File webappDir = new File(webappDirLocation);

        // En producción (contenedor), si la ruta cambia, se adapta de forma relativa
        if (!webappDir.exists()) {
            webappDirLocation = "webapp/";
            webappDir = new File(webappDirLocation);
        }

        Context context = tomcat.addWebapp("/", webappDir.getAbsolutePath());
        System.out.println("Configurando aplicación web en el directorio: " + webappDir.getAbsolutePath());

        tomcat.getConnector(); // Inicializa el conector HTTP
        tomcat.start();
        System.out.println("Servidor Tomcat iniciado en el puerto " + webPort + "...");
        tomcat.getServer().await(); // Mantiene el contenedor vivo escuchando tráfico
    }
}