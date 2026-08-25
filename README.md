# Specialty Coffee Roasters — Clean Architecture

Sistema backend para una tostaduría de café de especialidad, construido con **Arquitectura Hexagonal (Ports & Adapters)** usando el scaffold de Clean Architecture de Bancolombia.

## Integrantes

- Alejandro Aguilar García

## Stack Tecnológico

- Java 21
- Spring Boot
- Gradle (scaffold Bancolombia Clean Architecture v3.22.1)
- Lombok
- JUnit 5 + Mockito + AssertJ

---

## Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

### Domain — Model

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

En este proyecto contiene:

- **Entidades ricas:** `CoffeeBean` (protege su propio inventario con `deductStock()`) y `Order` (constructor privado + factory method `Order.confirmed()`).
- **Puertos de salida (gateway):** `InventoryPort` y `OrderRepositoryPort` — las interfaces que el dominio exige sin saber quién las implementa.
- **Excepciones de negocio:** jerarquía con `BusinessException` como padre abstracto, `InsufficientInventoryException` y `BeanNotFoundException` como hijas.

### Domain — Usecases

Este módulo perteneciente a la capa del dominio implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

En este proyecto contiene:

- **`ProcessCoffeeOrderUseCase`** — orquesta el flujo de crear un pedido: busca el grano, le pide que descuente stock (la validación vive en la entidad, no acá), crea la orden confirmada y la persiste. Recibe primitivos (`String beanName, int quantityGrams, String brewMethod`), no objetos de dominio — el mundo exterior no decide cómo se construye una `Order`.

### Infrastructure — Driven Adapters

Los driven adapters representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios REST, SOAP, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos interactuar.

En este proyecto contiene:

- **`InMemoryInventoryAdapter`** — implementa `InventoryPort` usando un `HashMap` con granos precargados (Geisha 500g, Bourbon Rosado 200g, etc.).
- **`InMemoryOrderAdapter`** — implementa `OrderRepositoryPort` usando una `ArrayList` en memoria.

Ambos anotados con `@Repository` para que Spring los detecte e inyecte automáticamente.

### Infrastructure — Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

En este proyecto contiene:

- **`OrderController`** — controlador REST con endpoint `POST /api/orders`.
- **`OrderRequest`** — record de Java que modela el body de entrada.
- **`GlobalExceptionHandler`** — `@RestControllerAdvice` que traduce excepciones de dominio a códigos HTTP (404 / 409).

### Infrastructure — Helpers

Utilidades generales para los Driven Adapters y Entry Points. Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos genéricos de los diferentes objetos de persistencia que puedan existir, basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006).

### Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de uso (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función `public static void main(String[] args)`).

Los beans de los casos de uso se disponibilizan automáticamente gracias a un `@ComponentScan` ubicado en esta capa.

---

## Estructura del Proyecto

```
specialty-coffee/
├── domain/
│   ├── model/                          ← Entidades ricas, puertos (gateway), excepciones de negocio
│   └── usecase/                        ← Caso de uso: ProcessCoffeeOrderUseCase
├── infrastructure/
│   ├── driven-adapters/
│   │   ├── in-memory-inventory/        ← Adaptador secundario: inventario en memoria
│   │   └── in-memory-order/            ← Adaptador secundario: pedidos en memoria
│   ├── entry-points/
│   │   └── api-rest/                   ← Adaptador primario: controlador REST
│   └── helpers/                        ← Utilidades compartidas
└── applications/
    └── app-service/                    ← Ensamblaje Spring Boot + tests de integración
```

---

## Reglas de Negocio

1. Un pedido (`Order`) requiere tipo de grano, cantidad en gramos y método de preparación.
2. El sistema verifica inventario suficiente antes de confirmar el pedido.
3. Si no hay inventario suficiente, el pedido se rechaza con una excepción de dominio.

## Decisiones de Diseño

- **Entidades ricas (no anémicas):** `CoffeeBean` protege su propio estado — `deductStock()` valida y lanza `InsufficientInventoryException` internamente. El caso de uso solo orquesta, no valida.
- **Factory method en `Order`:** constructor privado + `Order.confirmed()` — es imposible crear un pedido confirmado sin pasar por el flujo correcto.
- **Jerarquía de excepciones:** `BusinessException` como clase padre abstracta, con `InsufficientInventoryException` y `BeanNotFoundException` como hijas. Permite manejo genérico o específico en el controller (409 Conflict vs 404 Not Found).
- **Primitivos en el caso de uso:** `execute(String beanName, int quantityGrams, String brewMethod)` recibe datos crudos, no un objeto `Order` — el dominio es quien decide cómo construir la entidad, no el mundo exterior.

---

## Endpoint

```
POST /api/orders
Content-Type: application/json

{
    "beanName": "Geisha",
    "quantityGrams": 100,
    "brewMethod": "V60"
}
```

**Respuestas:**

| Código | Escenario |
|--------|-----------|
| 201 Created | Pedido confirmado exitosamente |
| 404 Not Found | El grano solicitado no existe en el catálogo |
| 409 Conflict | Inventario insuficiente para la cantidad solicitada |

---

## Cómo Ejecutar

```bash
# Compilar
gradle build

# Ejecutar
gradle :app-service:bootRun

# Tests unitarios del dominio
gradle :domain:model:test
gradle :domain:usecase:test

# Tests de integración
gradle :app-service:test
```

---

## Misión 5 — Reflexión Arquitectónica

### 1. Si la tostaduría decide cambiar la base de datos en memoria por PostgreSQL, ¿qué carpetas o clases tendrías que modificar y cuáles se mantendrían intactas?

**Se mantienen intactos (cero líneas cambiadas):**

- `domain/model/` — las entidades (`CoffeeBean`, `Order`), los puertos (`InventoryPort`, `OrderRepositoryPort`), las excepciones y toda la lógica de negocio no se tocan.
- `domain/usecase/` — `ProcessCoffeeOrderUseCase` sigue dependiendo de las interfaces (puertos), no de implementaciones concretas, así que no requiere ningún cambio.
- `infrastructure/entry-points/api-rest/` — el controlador REST llama al caso de uso de la misma manera, independientemente de la tecnología de persistencia.

**Se modifican (solo en infraestructura):**

- Se crea un nuevo módulo `infrastructure/driven-adapters/jpa-repository/` con:
    - Entidades JPA separadas (`CoffeeBeanEntity`, `OrderEntity`) anotadas con `@Entity`, `@Table`, etc. — estas anotaciones **no pueden** ir en las entidades de dominio porque contaminarían la capa de negocio con dependencias de persistencia.
    - Mappers para traducir entre entidades de dominio y entidades JPA (ej. `CoffeeBean` ↔ `CoffeeBeanEntity`).
    - Nuevos adaptadores (`JpaInventoryAdapter`, `JpaOrderAdapter`) que implementan los mismos puertos (`InventoryPort`, `OrderRepositoryPort`).
- En `applications/app-service/build.gradle` se cambia la dependencia: `implementation project(':in-memory-inventory')` se reemplaza por `implementation project(':jpa-repository')`.

Esto demuestra el valor central de la arquitectura hexagonal: el cambio de tecnología de persistencia tiene impacto **cero** en las reglas de negocio.

### 2. ¿Por qué es importante que el ProcessCoffeeOrderUseCase no conozca la existencia del InMemoryInventoryAdapter?

Porque aplica el **Principio de Inversión de Dependencias (DIP)**: el módulo de alto nivel (caso de uso) no depende del módulo de bajo nivel (adaptador in-memory), sino que ambos dependen de una abstracción (`InventoryPort`).

Si el caso de uso hiciera directamente `new InMemoryInventoryAdapter()`:

- Cualquier cambio de tecnología de persistencia (PostgreSQL, MongoDB, una API externa) obligaría a modificar la clase de negocio — exactamente lo contrario de lo que respondimos en la pregunta anterior.
- Sería imposible testear el caso de uso de forma aislada: en vez de mockear una interfaz con Mockito, necesitaríamos la implementación real con sus datos precargados.
- Se pierde la capacidad de extensión sin modificación (Principio Open/Closed): hoy podemos agregar un `JpaInventoryAdapter` que implemente el mismo `InventoryPort` sin tocar una sola línea del caso de uso.

En este proyecto lo experimentamos directamente: los tests unitarios del caso de uso mockean `InventoryPort` y `OrderRepositoryPort` con Mockito, validando la lógica de negocio sin depender de ninguna implementación concreta de persistencia.
