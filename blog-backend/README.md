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