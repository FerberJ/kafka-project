docker network create blog-nw

docker run -d --name=redpanda-1 -p 9092:9092 --network blog-nw -d docker.redpanda.com/vectorized/redpanda:v23.3.5 redpanda start --advertise-kafka-addr redpanda-1:9092

docker run --name blog-mysql -p 3306:3306 --network blog-nw -e MYSQL_ROOT_PASSWORD=vs4tw -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=dbuser -e MYSQL_DATABASE=blogdb -d mysql:8.0

./mvnw verify 

docker run --network blog-nw -i --rm -p 8080:8080 ghcr.io/ferberj/kafka-blog-backend

docker run --network blog-nw -i --rm -p 8085:8085 ghcr.io/ferberj/kafka-validator

docker push ghcr.io/ferberj/kafka-blog-backend

# Sensibilierung zum Thema Threading anhand einer Race Condition

Simples Projekt als Illustration von Concurency und Race Conditions.

## Beispiel-Zugriffe über httpie
Die Zugriffe sollten die nächste verfübare Versionsnummer zurückgeben. Z.B. um bei einem Caching zur prüfen, ob es eine Änderung gibt. Die Version wird dabei hochgezählt.  
Der entscheidende Source-Code spielt sich dabei in den beiden Service-Klassen ab.  
  
Starte das Quarkus-Projekt mit `mvn quarkus:dev` und führe die folgenden Zugriffe aus.

### Simples Beispiel mit lokaler Variable
  
Normaler Zugriff performt besser, da die Threads nicht blockieren. Bei gleichzeitigem Zugriff haben wir aber das Problem einer Race Condition. Die Version wird nicht sauber hochgezählt.

    http :8080/local/version

Zugriff mit Thread-Synchronisation. Die Threads blockieren sich gegenseitig. Die Version wird sauber hochgezählt.

    http :8080/local/version-sync

Zugriff mit Blocking über das Quarkus-Framework. Die Threads blockieren sich gegenseitig. Die Version wird sauber hochgezählt.

    http :8080/local/version-blocking
  
**Wichtig: Das blockieren passiert auf Container-Ebenen. Funktioniert nicht beim horizontalen Skalieren.**

### Simples Beispiel mit einer DB

Normaler Zugriff performt besser, da die Threads nicht blockieren. Bei gleichzeitigem Zugriff haben wir aber das Problem einer Race Condition. Die Version wird nicht sauber hochgezählt.  

    http :8080/db/version

Zugriff mit Blocking über die Transaktion. Die Threads blockieren sich gegenseitig. Die Version wird sauber hochgezählt.  

    http :8080/local/version-blocking

**Wichtig: Das blockieren passiert auf DB-Ebene. Funktioniert auch beim horizontalen Skalieren.**