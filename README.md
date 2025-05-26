# 📚 Book Service - Iron Library

> Microservicio de gestión de catálogo de libros e inventario para el sistema Iron Library

## 🎯 Descripción

Este microservicio forma parte de la arquitectura distribuida de Iron Library y se encarga de **gestionar el catálogo completo de libros, control de inventario y disponibilidad**. Permite crear, consultar, actualizar y eliminar libros, así como controlar las copias disponibles para préstamos.

## 🚀 Características

- ✅ **CRUD completo** de libros con validaciones
- ✅ **Gestión de inventario** con control de copias totales y disponibles
- ✅ **Búsquedas avanzadas** por título, autor, categoría e ISBN
- ✅ **Control de disponibilidad** para sistema de préstamos
- ✅ **Categorización** de libros por géneros
- ✅ **Validaciones de negocio** (ISBN único, copias válidas)
- ✅ **API REST** documentada con endpoints específicos
- ✅ **Integración con Loan Service** para actualización de disponibilidad

## 🛠️ Stack Tecnológico

- **Spring Boot** 3.4.6
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - API REST
- **Spring Cloud Netflix Eureka Client** - Service Discovery
- **MySQL** - Base de datos relacional
- **Bean Validation** - Validaciones de entrada
- **Lombok** - Reducción de código boilerplate
- **JUnit 5** - Testing unitario
- **Mockito** - Mocking para tests

## 📡 Endpoints Principales

### Base URL: `http://localhost:8081/api/books`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/books` | Obtener todos los libros |
| GET | `/api/books/{id}` | Obtener libro por ID |
| GET | `/api/books/available` | Obtener solo libros disponibles |
| GET | `/api/books/category?category=FICTION` | Filtrar por categoría |
| GET | `/api/books/search/author?author=García` | Buscar por autor |
| GET | `/api/books/search/title?title=Quijote` | Buscar por título |
| GET | `/api/books/{id}/available` | Verificar disponibilidad específica |
| POST | `/api/books` | Crear nuevo libro |
| PUT | `/api/books/{id}` | Actualizar libro completo |
| PATCH | `/api/books/{id}/availability?copies=-1` | Actualizar solo disponibilidad |
| DELETE | `/api/books/{id}` | Eliminar libro |
| GET | `/api/books/health` | Health check del servicio |

## 📊 Modelo de Datos

### Entidad Principal: Book
```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 255)
    private String title;
    
    @NotBlank(message = "El autor es obligatorio")
    @Column(nullable = false, length = 255)
    private String author;
    
    @NotBlank(message = "El ISBN es obligatorio")
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "La categoría es obligatoria")
    private Category category;
    
    @Min(value = 1, message = "Debe haber al menos 1 copia")
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;
    
    @Min(value = 0, message = "Las copias disponibles no pueden ser negativas")
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;
    
    // Método de negocio
    public boolean isAvailable() {
        return availableCopies != null && availableCopies > 0;
    }
}
```

### Enum Category
```java
public enum Category {
    FICTION("Ficción"),
    NON_FICTION("No Ficción"),
    SCIENCE("Ciencia"),
    HISTORY("Historia");
    
    private final String displayName;
    
    public String getDisplayName() {
        return displayName;
    }
}
```

## 🔧 Configuración

### Variables de Entorno
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/book_service
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update

# Puerto del servicio
server.port=8081

# Eureka
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
spring.application.name=book-service
```

### Configuración de Base de Datos
```sql
CREATE DATABASE book_service;
USE book_service;

-- La tabla se crea automáticamente por JPA
-- Estructura resultante:
-- books (id, title, author, isbn, category, total_copies, available_copies)
```

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Java 21
- Maven 3.6+
- MySQL 8.0+
- Discovery Server ejecutándose en puerto 8761

### Pasos de Instalación
```bash
# Clonar el repositorio
git clone https://github.com/IronLibray/book-service.git
cd book-service

# Configurar base de datos
mysql -u root -p -e "CREATE DATABASE book_service;"

# Instalar dependencias
./mvnw clean install

# Ejecutar el servicio
./mvnw spring-boot:run
```

### Verificar Instalación
```bash
# Health check
curl http://localhost:8081/api/books/health

# Verificar registro en Eureka
# Ir a http://localhost:8761 y verificar que aparece BOOK-SERVICE
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Solo tests unitarios
./mvnw test -Dtest="*Test"

# Solo tests de integración
./mvnw test -Dtest="*IntegrationTest"
```

### Cobertura de Tests
- ✅ **BookController** - Tests con MockMvc (@WebMvcTest)
- ✅ **BookService** - Tests unitarios con @Mock
- ✅ **BookRepository** - Tests de integración con @DataJpaTest
- ✅ **Validaciones** - Tests de Bean Validation
- ✅ **Exception Handling** - Tests de manejo de errores

## 🔗 Comunicación con Otros Servicios

### Servicios que consumen Book Service
- **Loan Service** - Para verificar disponibilidad y actualizar inventario
- **Gateway Service** - Para enrutamiento de peticiones

### APIs expuestas para otros servicios
```java
// Verificar disponibilidad (usado por Loan Service)
GET /api/books/{id}/available → Boolean

// Actualizar inventario (usado por Loan Service)
PUT /api/books/{id}/availability?copies=-1 → void
```

## 📈 Lógica de Negocio

### Reglas de Disponibilidad
- Un libro está **disponible** si `availableCopies > 0`
- Las **copias disponibles** nunca pueden ser negativas
- Las **copias disponibles** nunca pueden exceder las **copias totales**
- El **ISBN** debe ser único en todo el sistema

### Flujo de Préstamo (integración)
1. **Loan Service** verifica disponibilidad: `GET /books/{id}/available`
2. Si disponible, **Loan Service** actualiza inventario: `PUT /books/{id}/availability?copies=-1`
3. Cuando se devuelve, **Loan Service** restaura: `PUT /books/{id}/availability?copies=1`

## 📚 Documentación API

### Crear Libro
```bash
curl -X POST http://localhost:8081/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "El Quijote",
    "author": "Miguel de Cervantes",
    "isbn": "978-84-376-0495-7",
    "category": "FICTION",
    "totalCopies": 5,
    "availableCopies": 5
  }'
```

### Respuesta Exitosa
```json
{
  "id": 1,
  "title": "El Quijote",
  "author": "Miguel de Cervantes", 
  "isbn": "978-84-376-0495-7",
  "category": "FICTION",
  "totalCopies": 5,
  "availableCopies": 5
}
```

### Buscar por Categoría
```bash
curl "http://localhost:8081/api/books/category?category=FICTION"
```

### Actualizar Disponibilidad
```bash
# Prestar un libro (reducir copias)
curl -X PATCH "http://localhost:8081/api/books/1/availability?copies=-1"

# Devolver un libro (aumentar copias)
curl -X PATCH "http://localhost:8081/api/books/1/availability?copies=1"
```

## 🔒 Validaciones

### Validaciones de Entrada
- **Title**: No vacío, máximo 255 caracteres
- **Author**: No vacío, máximo 255 caracteres  
- **ISBN**: No vacío, único, máximo 20 caracteres
- **Category**: Debe ser un valor válido del enum
- **TotalCopies**: Mínimo 1
- **AvailableCopies**: Mínimo 0, máximo = totalCopies

### Manejo de Errores
- **400 Bad Request**: Datos de entrada inválidos
- **404 Not Found**: Libro no encontrado
- **409 Conflict**: ISBN duplicado
- **500 Internal Server Error**: Error del servidor

## 🚀 Próximas Mejoras

- [ ] **Imágenes de libros** - Upload y gestión de portadas
- [ ] **Reviews y ratings** - Sistema de calificaciones
- [ ] **Reservas** - Sistema de reservas cuando no hay copias
- [ ] **Auditoría** - Tracking de cambios en inventario
- [ ] **Búsqueda avanzada** - Full-text search con Elasticsearch
