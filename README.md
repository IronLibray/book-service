# 📚 Book Service - Iron Library

> Microservicio de gestión de catálogo de libros e inventario para el sistema Iron Library

## 🎯 Descripción

Este microservicio forma parte de la arquitectura distribuida de Iron Library y se encarga de **gestionar el catálogo completo de libros, control de inventario y disponibilidad**. Permite crear, consultar, actualizar y eliminar libros, así como controlar las copias disponibles para préstamos.

## 🚀 Características

- ✅ **CRUD completo** de libros con validaciones robustas
- ✅ **Gestión de inventario** con control de copias totales y disponibles
- ✅ **Búsquedas avanzadas** por título, autor, categoría e ISBN
- ✅ **Control de disponibilidad** para sistema de préstamos
- ✅ **Categorización** de libros por géneros literarios
- ✅ **Validaciones de negocio** (ISBN único, copias válidas)
- ✅ **API REST** documentada con endpoints específicos
- ✅ **Integración con Loan Service** para actualización de disponibilidad
- ✅ **Manejo de excepciones** centralizado y detallado
- ✅ **Testing comprehensivo** con cobertura completa

## 🛠️ Stack Tecnológico

- **Spring Boot** 3.4.6
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - API REST
- **Spring Cloud Netflix Eureka Client** - Service Discovery
- **Spring Cloud OpenFeign** - Comunicación entre servicios
- **MySQL** - Base de datos relacional
- **Bean Validation** - Validaciones de entrada
- **Lombok** - Reducción de código boilerplate
- **H2** - Base de datos en memoria para testing
- **JUnit 5** - Testing unitario
- **Mockito** - Mocking para tests
- **TestContainers** - Tests de integración con contenedores

## 📡 Endpoints Principales

### Base URL: `http://localhost:8081/api/books`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **GET** | `/api/books` | Obtener todos los libros |
| **GET** | `/api/books/{id}` | Obtener libro por ID |
| **GET** | `/api/books/available` | Obtener solo libros disponibles |
| **GET** | `/api/books/category?category=FICTION` | Filtrar por categoría |
| **GET** | `/api/books/search/author?author=García` | Buscar por autor |
| **GET** | `/api/books/search/title?title=Quijote` | Buscar por título |
| **GET** | `/api/books/{id}/available` | Verificar disponibilidad específica |
| **POST** | `/api/books` | Crear nuevo libro |
| **PUT** | `/api/books/{id}` | Actualizar libro completo |
| **PATCH** | `/api/books/{id}/availability?copies=-1` | Actualizar solo disponibilidad |
| **DELETE** | `/api/books/{id}` | Eliminar libro |
| **GET** | `/api/books/health` | Health check del servicio |

## 📊 Modelo de Datos

### Entidad Principal: Book
```java
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(nullable = false, length = 50)
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
# Aplicación
spring.application.name=book-service
server.port=8081

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/book_service
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update

# Eureka Service Discovery
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
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
git clone https://github.com/IronLibrary/book-service.git
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

# Probar endpoint básico
curl http://localhost:8081/api/books
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

# Tests con perfiles específicos
./mvnw test -Dspring.profiles.active=test
```

### Cobertura de Tests
- ✅ **BookController** - Tests con MockMvc (@WebMvcTest)
- ✅ **BookService** - Tests unitarios con @Mock
- ✅ **BookRepository** - Tests de integración con @DataJpaTest  
- ✅ **Exception Handling** - Tests de manejo de errores
- ✅ **Bean Validation** - Tests de validaciones de entrada
- ✅ **Business Logic** - Tests de lógica de negocio

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

// Obtener información completa del libro
GET /api/books/{id} → Book
```

## 📈 Lógica de Negocio

### Reglas de Disponibilidad
- Un libro está **disponible** si `availableCopies > 0`
- Las **copias disponibles** nunca pueden ser negativas
- Las **copias disponibles** nunca pueden exceder las **copias totales**
- El **ISBN** debe ser único en todo el sistema
- Se requiere al menos **1 copia total** para crear un libro

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
    "title": "Cien años de soledad",
    "author": "Gabriel García Márquez",
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
  "title": "Cien años de soledad",
  "author": "Gabriel García Márquez", 
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

### Búsquedas Avanzadas
```bash
# Por autor
curl "http://localhost:8081/api/books/search/author?author=García"

# Por título
curl "http://localhost:8081/api/books/search/title?title=Cien"

# Solo disponibles
curl "http://localhost:8081/api/books/available"
```

## 🔒 Validaciones y Manejo de Errores

### Validaciones de Entrada
- **Title**: No vacío, máximo 255 caracteres
- **Author**: No vacío, máximo 255 caracteres  
- **ISBN**: No vacío, único, máximo 20 caracteres
- **Category**: Debe ser un valor válido del enum
- **TotalCopies**: Mínimo 1
- **AvailableCopies**: Mínimo 0, máximo = totalCopies

### Excepciones Personalizadas
```java
// Libro no encontrado
public class BookNotFoundException extends RuntimeException

// Copias insuficientes para operación
public class InsufficientCopiesException extends RuntimeException
```

### Manejo de Errores HTTP
- **400 Bad Request**: Datos de entrada inválidos
- **404 Not Found**: Libro no encontrado
- **409 Conflict**: ISBN duplicado o conflicto de inventario
- **500 Internal Server Error**: Error del servidor

### Ejemplo de Respuesta de Error
```json
{
  "status": 404,
  "message": "Libro no encontrado con ID: 999",
  "timestamp": "2025-01-29T10:30:00",
  "path": "/api/books/999"
}
```

## 🛠️ Arquitectura y Patrones

### Capas de la Aplicación
```
BookController -> BookService -> BookRepository -> Database
                     ↓
              GlobalExceptionHandler
```

### Patrones Implementados
- **Repository Pattern** - Abstracción de acceso a datos
- **Service Layer** - Lógica de negocio centralizada
- **DTO Pattern** - Transferencia de datos entre capas
- **Exception Handler** - Manejo centralizado de errores
- **Validation** - Validaciones declarativas con Bean Validation

## 🚀 Características Avanzadas

### Búsquedas Especializadas
```java
// Repositorio con queries personalizadas
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByCategory(Category category);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category = :category")
    Long countByCategory(@Param("category") Category category);
}
```

### Servicios de Negocio
```java
@Service
@Transactional
public class BookService {
    
    // Crear libro con validaciones de negocio
    public Book saveBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Ya existe un libro con el ISBN: " + book.getIsbn());
        }
        
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        
        return bookRepository.save(book);
    }
    
    // Actualizar disponibilidad con control de stock
    public void updateAvailability(Long id, int copies) {
        Book book = findBookById(id);
        int newAvailable = book.getAvailableCopies() + copies;
        
        if (newAvailable < 0) {
            throw new InsufficientCopiesException("No hay suficientes copias disponibles");
        }
        
        if (newAvailable > book.getTotalCopies()) {
            throw new IllegalArgumentException("Las copias disponibles no pueden exceder el total");
        }
        
        book.setAvailableCopies(newAvailable);
        bookRepository.save(book);
    }
}
```

## 🔍 Monitoreo y Logging

### Health Check
```bash
curl http://localhost:8081/api/books/health
# Respuesta: "Book Service is running on port 8081"
```

### Logging Estructurado
```java
@Slf4j
public class BookService {
    
    public Book saveBook(Book book) {
        log.info("Guardando nuevo libro: {}", book.getTitle());
        
        try {
            Book savedBook = bookRepository.save(book);
            log.info("Libro guardado exitosamente con ID: {}", savedBook.getId());
            return savedBook;
        } catch (Exception e) {
            log.error("Error guardando libro: {}", e.getMessage());
            throw e;
        }
    }
}
```

## 🚀 Próximas Mejoras

- [ ] **Imágenes de libros** - Upload y gestión de portadas
- [ ] **Reviews y ratings** - Sistema de calificaciones y comentarios
- [ ] **Reservas** - Sistema de reservas cuando no hay copias disponibles
- [ ] **Auditoría** - Tracking completo de cambios en inventario
- [ ] **Búsqueda avanzada** - Full-text search con Elasticsearch
- [ ] **Cache** - Redis para mejorar performance de consultas
- [ ] **Métricas** - Integración con Micrometer/Prometheus
- [ ] **Versionado de API** - Soporte para múltiples versiones
- [ ] **Importación masiva** - Carga de libros desde archivos CSV/Excel
- [ ] **Notificaciones** - Alertas cuando stock es bajo

## 📝 Notas de Desarrollo

### Configuraciones de Testing
```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.cloud.openfeign.client.config.default.connect-timeout=1000
eureka.client.enabled=false
```

### Profiles Disponibles
- **default** - Configuración para desarrollo local
- **test** - Configuración para ejecución de tests
- **prod** - Configuración para producción (configuración externa)

---

## 📞 Soporte

Para reportar bugs o solicitar nuevas características, crear un issue en el repositorio del proyecto.

**Puerto del servicio**: 8081  
**Base de datos**: book_service  
**Nombre en Eureka**: BOOK-SERVICE
