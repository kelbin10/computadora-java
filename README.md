Sistema de Gestión de Garantías

Este proyecto en Java implementa un sistema para gestionar el proceso de reparación de computadoras bajo garantía. El flujo de trabajo se organiza en fases secuenciales: **Inspección**, **Reparación**, **Control de Calidad** y **Entrega**, con soporte para historial de cada computadora y almacenamiento persistente.

---

 Funcionalidades

- Registro de nuevas computadoras con información del cliente.
- Movimiento de computadoras entre fases del proceso.
- Visualización del estado actual por etapa.
- Historial detallado por equipo.
- Guardado de historial en archivo de texto (`historial_computadoras.txt`).
- Limpieza automática de consola para mejor experiencia visual.

---

 Cómo ejecutar el programa

1. Asegúrate de tener **Java 8 o superior** instalado.
2. Compila el programa:

```bash
javac SistemaGestionGarantias.java
java SistemaGestionGarantias
