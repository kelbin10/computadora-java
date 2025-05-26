import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class Computadora {
    String serviceTag, problema, fecha, cliente, correo, telefono;
    List<String> historial = new ArrayList<>();

    public Computadora(String serviceTag, String problema, String fecha, String cliente, String correo, String telefono) {
        this.serviceTag = serviceTag;
        this.problema = problema;
        this.fecha = fecha;
        this.cliente = cliente;
        this.correo = correo;
        this.telefono = telefono;
        historial.add("Recepción: " + fecha);
    }

    public String resumen() {
        return "Service Tag: " + serviceTag + "\nCliente: " + cliente + "\nEstado actual: " + historial.get(historial.size() - 1);
    }

    public void agregarHistorial(String etapa) {
        historial.add(etapa);
    }

    public String obtenerHistorial() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Historial de ").append(serviceTag).append(" ===\n");
        for (String h : historial) {
            sb.append(h).append("\n");
        }
        return sb.toString();
    }
}

public class SistemaGestionGarantias {
    static Queue<Computadora> colaInspeccion = new LinkedList<>();
    static Queue<Computadora> colaReparacion = new LinkedList<>();
    static Queue<Computadora> colaCalidad = new LinkedList<>();
    static Queue<Computadora> colaEntrega = new LinkedList<>();
    static List<Computadora> historialGeneral = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);
    static final String ARCHIVO_HISTORIAL = "historial_computadoras.txt";
    static final DateTimeFormatter formatoGT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        cargarHistorial(); 
        while (true) {
            limpiarConsola();
            System.out.println("=== Sistema de Gestión de Garantías ===");
            System.out.println("1. Registrar nueva computadora");
            System.out.println("2. Mover entre fases");
            System.out.println("3. Mostrar historial completo");
            System.out.println("4. Ver estado actual");
            System.out.println("5. Salir");
            System.out.print("Opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1": registrarComputadora(); break;
                case "2": moverComputadora(); break;
                case "3": mostrarHistorial(); break;
                case "4": mostrarEstadoActual(); break;
                case "5": guardarHistorial(); System.exit(0); break;
                default: System.out.println("Opción inválida."); esperar(); break;
            }
        }
    }

    static void registrarComputadora() {
        limpiarConsola();
        System.out.print("Service Tag: ");
        String tag = sc.nextLine();
        System.out.print("Descripción del problema: ");
        String problema = sc.nextLine();

        String fecha;
        while (true) {
            try {
                System.out.print("Fecha de recepción (DD/MM/AAAA): ");
                fecha = sc.nextLine();
                LocalDate.parse(fecha, formatoGT); 
                break;
            } catch (DateTimeParseException e) {
                System.out.println(" Formato inválido. Usa DD/MM/AAAA.");
            }
        }

        System.out.print("Nombre del cliente: ");
        String cliente = sc.nextLine();
        System.out.print("Correo electrónico: ");
        String correo = sc.nextLine();
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine();

        Computadora c = new Computadora(tag, problema, fecha, cliente, correo, telefono);
        colaInspeccion.add(c);
        historialGeneral.add(c);
        System.out.println("Computadora registrada en cola de inspección.");
        esperar();
    }

    static void moverComputadora() {
        limpiarConsola();
        System.out.println("Mover computadoras entre fases:");
        System.out.println("1. Inspección → Reparación o Entrega");
        System.out.println("2. Reparación → Control de Calidad");
        System.out.println("3. Control de Calidad → Reparación o Entrega");
        System.out.println("4. Entregar computadora");
        System.out.print("Opción: ");
        String opcion = sc.nextLine();

        switch (opcion) {
            case "1":
                if (!colaInspeccion.isEmpty()) {
                    Computadora c = colaInspeccion.poll();
                    System.out.print("¿Se puede reparar? (s/n): ");
                    String puede = sc.nextLine();
                    if (puede.equalsIgnoreCase("s")) {
                        c.agregarHistorial("Inspección → Reparación");
                        colaReparacion.add(c);
                    } else {
                        c.agregarHistorial("Inspección → Entrega (sin reparación)");
                        colaEntrega.add(c);
                    }
                } else System.out.println(" No hay computadoras en inspección.");
                break;

            case "2":
                if (!colaReparacion.isEmpty()) {
                    Computadora c = colaReparacion.poll();
                    System.out.print("Técnico asignado: ");
                    String tecnico = sc.nextLine();
                    c.agregarHistorial("Reparación por: " + tecnico);
                    colaCalidad.add(c);
                } else System.out.println("No hay computadoras en reparación.");
                break;

            case "3":
                if (!colaCalidad.isEmpty()) {
                    Computadora c = colaCalidad.poll();
                    System.out.print("¿Reparación OK? (s/n): ");
                    String ok = sc.nextLine();
                    if (ok.equalsIgnoreCase("s")) {
                        c.agregarHistorial("Control de calidad OK → Entrega");
                        colaEntrega.add(c);
                    } else {
                        c.agregarHistorial("Control de calidad Falló → Reparación");
                        colaReparacion.add(c);
                    }
                } else System.out.println(" No hay computadoras en control de calidad.");
                break;

            case "4":
                if (!colaEntrega.isEmpty()) {
                    Computadora c = colaEntrega.poll();
                    c.agregarHistorial("Entregada al cliente.");
                    System.out.println(" Computadora entregada.");
                } else System.out.println(" No hay computadoras en entrega.");
                break;

            default: System.out.println("Opción inválida.");
        }
        esperar();
    }

    static void mostrarHistorial() {
        limpiarConsola();
        for (Computadora c : historialGeneral) {
            System.out.println(c.obtenerHistorial());
            System.out.println("----------------------------------");
        }
        esperar();
    }

    static void mostrarEstadoActual() {
        limpiarConsola();
        System.out.println("Estado Actual de las Fases:");
        System.out.println("Inspección: " + colaInspeccion.size());
        System.out.println("Reparación: " + colaReparacion.size());
        System.out.println("Control de Calidad: " + colaCalidad.size());
        System.out.println("Entrega: " + colaEntrega.size());
        esperar();
    }

    static void guardarHistorial() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_HISTORIAL))) {
            for (Computadora c : historialGeneral) {
                pw.println(c.obtenerHistorial());
                pw.println("----------------------------------");
            }
        } catch (IOException e) {
            System.out.println(" Error al guardar historial: " + e.getMessage());
        }
    }

    static void cargarHistorial() {
        
    }

    static void limpiarConsola() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            
        }
    }

    static void esperar() {
        System.out.println("\nPresiona Enter para continuar...");
        sc.nextLine();
    }
}
