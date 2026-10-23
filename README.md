# Módulo Citas Médicas — Evaluación N°01

Sistema de gestión de citas médicas, correspondiente al Módulo 3 del caso
"Sistema de Gestión Hospitalaria". Administra el ciclo de vida completo de
una cita médica: desde su registro, pasando por modificaciones y cambios
de estado, hasta su cancelación o finalización.

## Stack

- Java 21 + Spring Boot 4.1.1
- Spring Data JPA + Hibernate 7.4.5
- Thymeleaf
- SQLite (`sqlite-jdbc` + `hibernate-community-dialects`)
- IntelliJ IDEA Community Edition

## Integrantes y autoría

| Integrante | Requerimientos implementados |
|---|---|
| **Guevara** | RF-CIT-01, RF-CIT-02, RF-CIT-04, RF-CIT-05 |
| **Flores Valencia** | RF-CIT-09, RF-CIT-11, RF-CIT-13, RF-CIT-17, además de correcciones (fixes) detectadas sobre los requerimientos anteriores durante el desarrollo de su parte |

## Cómo ejecutar

```bash
git clone https://github.com/pieroguevaragarrido/ev01-citasmedicas-dae.git
cd ev01-citasmedicas-dae
./mvnw spring-boot:run
```

La app queda disponible en `http://localhost:8080/citas` (ajustar el
puerto según tu configuración). La base de datos SQLite
(`citas_medicas.db`) se crea sola en la raíz del proyecto la primera vez
que se ejecuta, junto con los datos de prueba de `data.sql`.

## Requerimientos funcionales

| Código | Descripción |
|---|---|
| RF-CIT-01 | Registrar cita |
| RF-CIT-02 | Generar código único de cita (`CIT-000001`, ...) |
| RF-CIT-04 | Validar disponibilidad del médico |
| RF-CIT-05 | Impedir doble reserva en el mismo horario |
| RF-CIT-09 | Modificar cita |
| RF-CIT-11 | Cancelar cita (con motivo obligatorio) |
| RF-CIT-13 | Cambiar estado de la cita (máquina de estados) |
| RF-CIT-17 | Buscar/filtrar citas |

## Reglas de negocio

- **Disponibilidad del médico**: un médico no puede tener 2 citas que se
  crucen en horario, sin importar la especialidad escrita en cada cita.
- **Disponibilidad del paciente**: un mismo paciente tampoco puede tener 2
  citas cruzadas en horario, sin importar el médico. Esta regla no estaba
  contemplada explícitamente en los requerimientos originales (que solo
  hablan del médico) y se agregó por consistencia de negocio.
- **Cancelación controlada**: cancelar una cita siempre exige un motivo.
  No es posible saltarse esta regla usando el cambio de estado directo —
  ese endpoint rechaza explícitamente `CANCELADA` como destino y obliga a
  usar el endpoint dedicado de cancelación.
- **Máquina de estados** (para el cambio de estado directo, sin incluir
  cancelación):

```
PROGRAMADA   -> CONFIRMADA, NO_ASISTIO   (CANCELADA solo por cancelación)
CONFIRMADA   -> EN_ESPERA, NO_ASISTIO    (CANCELADA solo por cancelación)
EN_ESPERA    -> EN_ATENCION
EN_ATENCION  -> ATENDIDA
```

- **Cancelar** solo está permitido desde `PROGRAMADA` o `CONFIRMADA`.
- **Modificar** revalida disponibilidad de médico y paciente, excluyendo
  la propia cita de la comparación para no "chocar consigo misma".

## Estructura del proyecto

```
com.lab04.citas/
├── CitasApplication.java
├── entity/        (Paciente, Medico, Cita, CitaEstado)
├── repository/    (PacienteRepository, MedicoRepository, CitaRepository)
├── dto/           (CitaFormDTO)
├── exception/     (DisponibilidadException, TransicionEstadoInvalidaException)
├── service/       (CitaService + impl/CitaServiceImpl)
└── controller/    (CitaController)
```

## Descubrimientos durante el desarrollo

Durante la implementación y pruebas de la segunda parte del módulo
surgieron 4 hallazgos que requirieron ajustes también sobre partes ya
existentes del proyecto:

1. El formulario de edición no precargaba fecha ni hora, y tanto registrar
   como editar fallaban sin mostrar ningún mensaje — causado por un
   formato de fecha/hora que no coincidía entre el backend y los campos
   HTML5.
2. Los errores de validación no se mostraban en ninguna parte de la
   interfaz, lo que ocultaba por completo el motivo real de una falla.
3. Era posible cancelar una cita sin motivo si se usaba el cambio de
   estado directo en lugar del botón de cancelar.
4. El filtro de búsqueda solo funcionaba correctamente al combinar la
   especialidad; los demás filtros (paciente, médico, fecha, estado)
   devolvían resultados vacíos por una diferencia en cómo se interpretan
   los valores vacíos de un formulario según el tipo de dato.

Los 4 hallazgos fueron corregidos y quedan documentados con más detalle en
el informe técnico del proyecto.

## Lo logrado

- Ciclo completo de una cita médica cubierto de extremo a extremo:
  creación, modificación, cambio de estado, cancelación y búsqueda.
- Validaciones de disponibilidad tanto para el médico como para el
  paciente, evitando cruces de horario en ambos sentidos.
- Reglas de negocio consistentes entre los distintos flujos (por ejemplo,
  que cancelar siempre exija motivo sin importar por qué camino se
  intente).
- Búsqueda combinable por múltiples criterios a la vez.
- Cobertura de pruebas manuales y por Postman de los casos positivos y
  negativos de cada requerimiento.

## Pruebas

- **Manuales desde la interfaz**: navega a `http://localhost:8080/citas`
  y prueba registrar, editar, cambiar estado, cancelar y filtrar citas.
- **Con Postman**: revisar la carpeta de capturas en el repositorio.


---

# Evaluación N°02 — Relaciones JPA, auditoría, usuarios/roles y seguridad

## Credenciales de prueba (se crean solas al iniciar)

| Usuario | Contraseña | Rol | Entra a |
|---|---|---|---|
| `admin` | `admin123` | ADMINISTRADOR | `/usuarios` (todo el sistema) |
| `recepcion` | `recep123` | RECEPCIONISTA | `/citas` |
| `medico` | `medico123` | MEDICO | `/pacientes` |

La app corre en `http://localhost:8081` (puerto definido en `application.properties`).

## Pregunta 1 — Relaciones Hibernate/JPA

| Relación | Anotaciones | Clave foránea |
|---|---|---|
| Cita → Paciente | `@ManyToOne` en `Cita` / `@OneToMany(mappedBy="paciente")` en `Paciente` | `cita.paciente_id` |
| Cita → Médico | `@ManyToOne` en `Cita` / `@OneToMany(mappedBy="medico")` en `Medico` | `cita.medico_id` |
| Usuario → Rol | `@ManyToOne(optional=false)` en `Usuario` / `@OneToMany(mappedBy="rol")` en `Rol` | `usuario.rol_id` |

Integridad: `nullable=false` en las FK, `foreign_keys=on` en la URL de SQLite (por defecto SQLite no valida FK),
y desactivación lógica (cancelar / desactivar) en lugar de borrar registros relacionados.
CRUD con relaciones: citas (paciente + médico), usuarios (rol), detalle de paciente (`/pacientes/{id}` lista sus citas).

## Pregunta 2 — Auditoría

Entidad `Auditoria` (usuario, fecha_hora, operacion, entidad, registro_id, detalle) y `AuditoriaService.registrar(...)`,
llamado desde los servicios dentro de la misma transacción (si la operación falla, el registro también se revierte).
El usuario sale de la sesión autenticada (`SecurityUtils.usuarioActual()`).
Operaciones: REGISTRO, MODIFICACION, CAMBIO_ESTADO, CANCELACION, ACTIVACION, DESACTIVACION sobre Cita, Usuario y Rol.
No hay borrado físico: la "eliminación" es CANCELACION (citas) o DESACTIVACION (usuarios y roles).
Consulta: `/auditoria` (solo ADMINISTRADOR) con filtros por entidad, operación y usuario.

## Preguntas 3 y 4 — Usuarios y roles (backend + frontend)

- `/usuarios`: registrar, listar, editar, activar/desactivar y asignar rol. Contraseñas con BCrypt.
- `/roles`: registrar, listar, editar y activar/desactivar.
- Reglas: el username y el código del rol no se editan; nadie puede desactivarse ni cambiarse el rol a sí mismo;
  el rol ADMINISTRADOR no se puede desactivar; un usuario con rol inactivo no puede iniciar sesión.

## Pregunta 5 — Control de acceso (`SecurityConfig`)

| Ruta | Roles permitidos |
|---|---|
| `/usuarios/**`, `/roles/**`, `/auditoria/**` | ADMINISTRADOR |
| `/citas/**`, `/medicos/**` | ADMINISTRADOR, RECEPCIONISTA |
| `/pacientes/**` | ADMINISTRADOR, MEDICO, RECEPCIONISTA |
| `/login`, `/css/**` | público |

El menú (`fragments/layout.html`) solo muestra lo permitido, el backend rechaza el resto (redirige a `/acceso-denegado`)
y tras el login cada rol va a su pantalla (`LoginSuccessHandler`).

## Guion de pruebas / capturas

1. Login con `admin` → crear rol, crear usuario con ese rol, editar, desactivar y reactivar → capturas de `/usuarios` y `/roles`.
2. Login con `recepcion` → registrar y cancelar una cita. Intentar abrir `/usuarios` a mano → 403.
3. Login con `medico` → ver `/pacientes` y el detalle. Intentar abrir `/citas` → 403.
4. Login con `admin` → `/auditoria`: ver los registros generados por los pasos 1 y 2.
5. Desactivar `recepcion` y verificar que ya no puede iniciar sesión.
