package ec.edu.epn;

import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        tomcat.setPort(Integer.parseInt(webPort));

        File webapp = new File("target/owlshare-1.0-SNAPSHOT");
        if (!webapp.isDirectory()) {
            throw new IllegalStateException(
                    "No se encontró target/owlshare-1.0-SNAPSHOT. Ejecuta: mvn package");
        }

        verificarServlet(webapp, "ec/edu/epn/controlador/BuscarTutoresServlet.class");
        verificarServlet(webapp, "ec/edu/epn/controlador/RegistroServlet.class");

        tomcat.addWebapp("/", webapp.getAbsolutePath());
        System.out.println("Desplegando: " + webapp.getAbsolutePath());

        tomcat.getConnector();
        tomcat.start();
        System.out.println("Servidor Tomcat iniciado en http://localhost:" + webPort);
        System.out.println("API tutores: http://localhost:" + webPort + "/api/tutores/buscar?materia=ICCD144");
        tomcat.getServer().await();
    }

    private static void verificarServlet(File webapp, String rutaClase) {
        File clase = new File(webapp, "WEB-INF/classes/" + rutaClase);
        if (!clase.isFile()) {
            throw new IllegalStateException(
                    "Falta " + rutaClase + ". Ejecuta 'mvn package' y reinicia el servidor.");
        }
    }
}
