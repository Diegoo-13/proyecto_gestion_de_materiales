# Sistema de Gestión y Control de Materiales

## Descripción

El **Sistema de Gestión y Control de Materiales** es una aplicación de escritorio desarrollada para apoyar la administración de los materiales utilizados en el área de soporte técnico del **Centro de Gestión de Tecnologías de la Información (CGTI)**.

El sistema permite llevar un mejor control de los materiales registrados, préstamos, devoluciones, materiales dañados, usuarios y reportes, evitando depender únicamente de registros manuales y facilitando la consulta de la información.

El proyecto fue desarrollado como parte de la formación académica de la **Universidad Tecnológica del Norte de Guanajuato (UTNG)**.

## Objetivo

Desarrollar una aplicación que permita administrar y consultar de manera organizada la información relacionada con los materiales del área de soporte técnico, facilitando el registro de materiales, el control de préstamos y devoluciones, el seguimiento de materiales dañados y la generación de reportes.

## Funcionalidades principales

* Registro y consulta de materiales.
* Edición de información de materiales.
* Importación masiva de materiales mediante archivos Excel.
* Control de existencias y stock mínimo.
* Registro y seguimiento de préstamos.
* Registro de devoluciones.
* Control de materiales dañados.
* Administración de usuarios.
* Generación y consulta de reportes.
* Inicio de sesión y control de acceso.
* Administración de la cuenta del usuario.

## Tecnologías utilizadas

* **Java 13**
* **JavaFX**
* **Maven**
* **PostgreSQL**
* **JDBC**
* **FXML**
* **CSS**
* **Git y GitHub**
* **Apache POI** para el manejo de archivos Excel

## Estructura del proyecto

```text
proyecto_gestion_de_materiales/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── ...
│
├── instalador/
│
├── docs/
│   └── Manual_Instalacion.pdf
│
├── .gitignore
├── pom.xml
└── README.md
```

## Requisitos

Para ejecutar el proyecto desde el código fuente se requiere:

* Windows 11.
* JDK 13.
* Apache Maven.
* PostgreSQL.
* Una base de datos configurada de acuerdo con la estructura utilizada por el proyecto.

Para conocer el procedimiento completo de instalación y configuración, consulta el manual:

**[📘 Manual de instalación](docs/Manual_Instalacion.pdf)**

## Ejecución del proyecto

Después de configurar Java, Maven y PostgreSQL, el proyecto puede ejecutarse desde la terminal ubicada en la carpeta principal mediante:

```bash
mvn clean javafx:run
```

## Base de datos

El sistema utiliza **PostgreSQL** como sistema gestor de base de datos.

La aplicación se conecta a la base de datos mediante JDBC y utiliza las tablas necesarias para administrar materiales, préstamos, usuarios, ubicaciones, categorías y materiales dañados.

Antes de ejecutar la aplicación es necesario configurar correctamente la conexión a la base de datos.

## Documentación

La documentación de instalación y configuración del sistema se encuentra disponible en:

**[📘 Manual de instalación](docs/Manual_Instalacion.pdf)**

## Proyecto académico

Este proyecto fue desarrollado como parte de las actividades académicas de la carrera de **Ingeniería en Desarrollo y Gestión de Software** de la Universidad Tecnológica del Norte de Guanajuato.

## Autores

Proyecto desarrollado por estudiantes de la Universidad Tecnológica del Norte de Guanajuato (UTNG).

---

**Sistema de Gestión y Control de Materiales — CGTI**
