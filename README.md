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

## Versión 1 - Modelo Embebido

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

## Versión 2 - Modelo con Referencias

Para evitar el crecimiento ilimitado del documento se refactorizó el modelo.

Colecciones:
```
posts
comments
replies
```

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