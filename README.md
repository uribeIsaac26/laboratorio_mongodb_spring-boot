# MongoDB + Spring Boot Lab

Laboratorio para experimentar con modelado de datos NoSQL usando MongoDB y Spring Boot.

El objetivo del proyecto es entender:

- Modelado de documentos
- Embedding vs References
- Límites de documentos
- Rendimiento con grandes volúmenes de datos

## Objetivo

Este laboratorio busca explorar cómo funcionan las bases de datos NoSQL y cómo modelar correctamente los datos.

Se realizan experimentos progresivos para entender:

- Cómo MongoDB almacena documentos
- Cómo funcionan los arrays embebidos
- Qué sucede cuando los documentos crecen demasiado
- Cuándo usar referencias en lugar de embedding

## Versión 1 - Modelo embebido (comments dentro de post)

En la primera versión se modelaron los comentarios dentro del documento Post.

```
Post
 └ comentarios[]
     └ respuestas[]
```
Ventajas:
- Lectura rápida
- Toda la información en un solo documento

Problemas encontrados:
- El documento crece rápidamente
- Se alcanzó el límite de 16MB de MongoDB

## Experimentos realizados

### Inserción masiva de comentarios

Se insertaron miles de comentarios dentro de un mismo documento Post.

Resultado:

Error encontrado:

Payload document size is larger than maximum of 16793600

Conclusión:

MongoDB tiene un límite máximo de 16MB por documento.

## Versión 2 - Modelo con referencias (posts / comments / replies)

Para evitar el crecimiento ilimitado del documento se refactorizó el modelo.

Colecciones:
```
posts
comments
replies
```

### Objetivo del experimento

Evaluar el rendimiento de diferentes estrategias de inserción masiva en MongoDB
utilizando Spring Boot.

Se compararon tres enfoques:

- Inserción individual usando `save()`
- Inserción por lotes usando `saveAll()`
- Inserción optimizada utilizando `BulkOperations`


### Volumen de datos

Para la prueba se generó la siguiente estructura de datos:

- 100 posts
- 100 comentarios por post
- 100 respuestas por comentario

Total de documentos generados:

Posts: 100  
Comments: 10,000  
Replies: 1,000,000

Total documentos: **1,010,100**

### Modelo de datos

```
Post
 └─ Comment (reference: postId)
     └─ Reply (reference: commentId)
```

### Entorno de prueba

- Java: 21
- Spring Boot: 3.x
- MongoDB: 7.x
- Driver MongoDB: Spring Data MongoDB
- Sistema operativo: Windows
- Hardware: prueba ejecutada en entorno local

### Resultado Pruebas Rendimiento

| Método            | Tiempo |
|-------------------|--------|
| save() individual | 378 s  |
| saveAll()         | 33 s   |
| BulkOperations    | 22.8 s |


| Batch | Tiempo |
|-------|--------|
| 100   | 21 s   |
| 500   | 18 s   |
| 1000  | 15 s   |
| 5000  | 14 s   |


### Conclusiones

1. La inserción individual (`save()`) es extremadamente costosa debido a que cada
   operación implica una llamada independiente a la base de datos.

2. `saveAll()` mejora significativamente el rendimiento al enviar múltiples
   documentos en una sola operación.

3. `BulkOperations` ofrece el mejor rendimiento para inserciones masivas,
   ya que permite enviar grandes lotes de operaciones directamente al driver
   de MongoDB.

4. Aumentar el tamaño del batch mejora el rendimiento hasta cierto punto,
   después del cual la mejora es marginal.

### Throughput

1,000,000 documentos insertados en 14 segundos

≈ 71,000 inserts por segundo


## Versión 3 - Replica Set (alta disponibilidad)



## Versión 4 - Sharding (escalabilidad horizontal)

#  Tecnologías usadas

## Tecnologías

- Java 21
- Spring Boot
- MongoDB
- Spring Data MongoDB
- MongoDB Compass

## Ejecutar el proyecto

1. Iniciar MongoDB

2. Clonar repositorio

git clone https://github.com/uribeIsaac26/laboratorio_mongodb_spring-boot.git

3. Ejecutar proyecto

./gradlew bootRun