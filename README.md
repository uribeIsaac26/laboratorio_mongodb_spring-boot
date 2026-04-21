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

En esta versión se implementa un cluster de MongoDB con 3 nodos utilizando un Replica Set.

Objetivos del experimento:

- entender cómo se crea un cluster
- observar la replicación entre nodos
- probar el failover automático

---

## Experimentos realizados

| Experimento | Descripción |
|-------------|-------------|
| Replica Set | Cluster MongoDB con 3 nodos |
| Replicación | Verificación de sincronización de datos |
| Failover | Elección automática de nuevo PRIMARY |

# 1. Creación del cluster

## Arquitectura del cluster

Se levantó un Replica Set con tres nodos.

```
        PRIMARY
        mongo1
          │
   ┌──────┴──────┐
 mongo2       mongo3
SECONDARY     SECONDARY
```

### Levantar los nodos

Se utilizó Docker Compose para crear tres instancias de MongoDB.

``` 
docker compose up -d
```

Inicialización del Replica Set:

``` 
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo1:27017" },
    { _id: 1, host: "mongo2:27017" },
    { _id: 2, host: "mongo3:27017" }
  ]
})
```

Resultado
```
mongo1 → PRIMARY
mongo2 → SECONDARY
mongo3 → SECONDARY
```

# 2. Replicación de datos

Para comprobar la replicación se insertó un documento en el nodo PRIMARY.

```
use lab
db.posts.insertOne({
  title: "Replica test",
  author: "Isaac"
})
```

Luego se consultó desde un nodo secundario:

```declarative
docker exec -it mongo2 mongosh
```

Consulta:

```declarative
rs.secondaryOk()
use lab
db.posts.find()
```

Resultado:

El documento insertado en el PRIMARY aparece también en el nodo SECONDARY.

Esto confirma que la replicación está funcionando correctamente.


# 3. Failover automático

Se simuló la caída del nodo principal.

```declarative
docker stop mongo1
```

MongoDB realizó automáticamente una nueva elección de líder.

Verificación:

```declarative
rs.status()
```

Resultado:

```declarative
mongo1 → OFFLINE
mongo2 → PRIMARY
mongo3 → SECONDARY
```

## Experimento de Tolerancia a Fallos Críticos

Se realizó una prueba de estrés tumbando 2 de los 3 nodos simultáneamente:

1. Al caer mongo1, el cluster mantuvo la mayoría (2/3) y eligió a mongo2 como PRIMARY.
2. Al caer mongo2, solo quedó 1 nodo vivo (1/3).Resultado: El cluster perdió el Quorum y el nodo restante (mongo3) pasó automáticamente a modo SECONDARY (solo lectura).Conclusión: Un Replica Set de 3 nodos solo puede garantizar escrituras si al menos 2 nodos están en línea ($n/2 + 1$).


## Configuración de Resiliencia en Spring Boot

Para que la aplicación soporte el failover sin intervención manual, se utilizó la siguiente URI de conexión:

``` 
mongodb://mongo1:27017,mongo2:27017,mongo3:27017/lab_messaging_app?replicaSet=rs0&retryWrites=true
```

1. replicaSet=rs0: Permite al driver descubrir todos los nodos del cluster automáticamente.

2. retryWrites=true: Instruye al driver para reintentar una operación de escritura si el nodo PRIMARY cae durante el proceso, garantizando la continuidad del servicio.


## Evidencia de Failover en Logs

Durante la caída de un nodo, el driver de MongoDB en Java registró la transición:

``` 
INFO: Discovered replica set primary mongo2:27017
ERROR: Exception in monitor thread while connecting to server mongo1:27017 (ShutdownInProgress)
INFO: Documento 272 guardado # <-- La app siguió funcionando tras el cambio de líder
```

Conclusión:

El cluster continuó funcionando sin interrupción gracias al mecanismo de elección automática de MongoDB.

## Conclusiones

- MongoDB Replica Set permite alta disponibilidad.
- Los nodos secundarios mantienen una copia sincronizada de los datos.
- Si el nodo principal falla, otro nodo es elegido automáticamente como PRIMARY.
- Las aplicaciones pueden continuar operando sin intervención manual.



## Versión 4 - Sharding (escalabilidad horizontal)

#  Tecnologías usadas

## Tecnologías

- Java 21
- Spring Boot
- MongoDB
- Spring Data MongoDB
- MongoDB Compass
- WSL2

## Comandos mongodb consola

- Entrar al contenedor 

    ```docker exec -it mongo1 mongosh```
- Listar las bases de datos

    ```show dbs```

- Entrar a la base de datos 
    
    ```use mi_base_datos```

- Mirar las colecciones 

    ```show collections```

- Ver datos 

    ```db.mi_coleccion.find()```
## Ejecutar el proyecto

1. Iniciar MongoDB

2. Clonar repositorio

git clone https://github.com/uribeIsaac26/laboratorio_mongodb_spring-boot.git

3. Ejecutar proyecto

./gradlew bootRun

4. Para ejecutar el proyecto desde docker en la raiz del proyecto:

``` 
docker compose -f docker/replica-set/docker-compose.yml up -d --build
```

