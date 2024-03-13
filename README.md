# Message Verifyer
Erstellt man einen neuen Blog, muss dieser verifiziert werden. 
Wenn in einem Blog im Content "yolo" vorkommt wird er nicht verifiziert. 
```json
[
  {
    "content": "string",
    "id": 1,
    "title": "string",
    "valid": true
  },
  {
    "content": "yolo",
    "id": 2,
    "title": "string",
    "valid": false
  }
]
```

# Dev
Um das Projekt im Dev-Modus zu starten, müssen in zwei Terminals folgende Befehle ausgeführt werden:
```bash
# Starten vom Blog Backend
cd blog-backend && ./mvnw quarkus:dev && cd ..
```
```bash
# Starten vom Content Validator
cd validator-messager && ./mvnw quarkus:dev && cd ..
```
# Docker-Images
Images werden erstellt mit `verify`
```bash
# Image vom Blog Backend
cd blog-backend && ./mvnw verify && cd ..
```
```bash
# Image vom Content Validator
cd validator-messager && ./mvnw verify && cd ..
```

Damit die Images im Github veröffentlicht werden, müssen diese noch gepusht werden.
```bash
# Blog Backend
docker push ghcr.io/ferberj/kafka-blog-backend
```
```bash
# Content Validator
docker push ghcr.io/ferberj/kafka-validator
```

# Docker Compose
Starte alle Container mit Docker Compose.

`docker compose up`

Öffne im Browser die Seite http://localhost:8080






Ziel der Vertiefungsarbeit
- Der Benutzer kann weiterhin normal einen Blog erstellen
  - Ergänze DTO
- Der Benutzer kann eine Datei für den Blog erfassen und zuweisen
- Der Benutzer kann eine Datei erfassen
- Der Benutzer kann Alle Dateien abrufen 


Start minio docker:
docker run -p 9000:9000 -p 9001:9001 \
  quay.io/minio/minio server /data --console-address ":9001"