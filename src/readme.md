# README - Spring GraphQL Todo App

## Requisitos previos

- **Java 21**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **MySQL Workbench** (opcional, para ver BD)

## Estructura del proyecto

├── docker-compose.yml              ← MySQL en Docker
├── init-db/                        ← Scripts de inicialización DB
├── pom.xml                         ← Dependencias Maven
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── nayra/
│       │           └── springgraphql/
│       │               ├── SpringGraphQlApplication.java  ← Clase main
│       │               ├── config/
│       │               │   └── WebSocketConfig.java       ← Config WebSocket (Subscriptions)
│       │               ├── controllers/
│       │               │   └── TareaController.java       ← Resolvers GraphQL (Query/Mutation/Subscription)
│       │               ├── entities/
│       │               │   └── Tarea.java                 ← Entidad JPA
│       │               └── repository/
│       │                   └── TareaRepository.java      ← Spring Data JPA
│       └── resources/
│           ├── graphql/
│           │   └── schema.graphqls       ← Esquema GraphQL
│           ├── application.properties   ← Config H2 / local
│           └── application-docker.properties ← Config MySQL (Docker)
├── target/                         ← Build generado (ignorar)
├── .gitignore
├── .gitattributes
├── mvnw
├── mvnw.cmd
└── readme.md

## Lanzamiento en 3 comandos

### 1. Base de Datos (Docker)
cd springGraphQL
docker compose up -d
y SQL en localhost:3307 (puerto cambiado para evitar conflictos)

2. Backend GraphQL
mvn clean spring-boot:run -Dspring.profiles.active=docker
API en http://localhost:8080/graphiql

3. Verificar
http://localhost:8080/graphiql
Probar la API

# Query (leer todas las tareas)
    query {
      allTareas(desdeBBDD: false) {
        id
        titulo
        descripcion
        estado
      }
    }

# Query con filtro (solo tareas con estado PENDIENTE)
    query {
    allTareas(estado: "PENDIENTE", desdeBBDD: false) {
    id
    titulo
    estado
    }
}

# Mutación (crear tarea)
    mutation {
      addTarea(
        titulo: "Mi primera tarea"
        descripcion: "Desde GraphiQL"
        guardarEnBBDD: false
      ) {
        id
        titulo
        estado
      }
    }

# Suscripción (recibir actualización automática cuando se añada una nueva tarea)
    subscription {                                                                                                                                                                                                                                                                                                   
    areaAnadida {                                                                                                                                                                                                                                                                                                 
    id                                                                                                                                                                                                                                                                                                           
    titulo                                                                                                                                                                                                                                                                                                       
    descripcion                                                                                                                                                                                                                                                                                                  
    estado                                                                                                                                                                                                                                                                                                       
  }                                                                                                                                                                                                                                                                                                              
}

# Cambiar estado
    mutation {
      cambiarEstado(id: 1, estado: "COMPLETADO", enBBDD: false) {
        id
        estado
      }
    }

# Eliminar tarea (DELETE)
    mutation {
    eliminarTarea(id: 1, enBBDD: false)
    }

## Configuración MySQL (Opcional)
- application.properties ya configurado:
spring.datasource.url=jdbc:mysql://localhost:3307/todo?...

# MySQL Workbench:

- Host: localhost
    
- Port: 3307
    
- User: root
    
- Password: (vacío)

# Parar todo
- Backend
   Ctrl+C

# Docker
docker compose down
 
## Funcionalidades implementadas

- GraphQL Schema-first

- Queries: allTareas(estado?), tareaById()

- Mutations: addTarea(), cambiarEstado(), eliminarTarea()

- Dual storage: Lista local OR MySQL

- Spring Data JPA + H2 (desarrollo)

- Docker MySQL (producción)

- GraphiQL UI (/graphiql)

## Posibles problemas y sus soluciones
Problema	                Solución
Puerto 3306 ocupado	    Docker ya usa 3307
Puerto 8080 ocupado	    server.port=8081
404 en /graphiql	    Usa /graphql con Postman POST
No conecta MySQL	    docker ps + docker logs todo-mysql
Filtro no funciona      Revisa que TareaRepository tenga findByEstado(String estado).


## Tecnologías
- Spring Boot 4.0.1
- Spring GraphQL
- Spring Data JPA
- Hibernate 7.2
- MySQL 8.0 (Docker)
- Maven
- Java 21
- GraphiQL

Solo hay que hacer docker compose up -d + mvn spring-boot:run

## Equipo de desarrollo
- Nayra
- Ana
- Ainhoa