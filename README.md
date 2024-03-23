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
```bash
# Alles zusammen
cd blog-backend && ./mvnw verify && cd .. && cd validator-messager && ./mvnw verify && cd .. && docker push ghcr.io/ferberj/kafka-blog-backend && docker push ghcr.io/ferberj/kafka-validator
```

# Docker Compose
Starte alle Container mit Docker Compose.

`docker compose up`

Öffne im Browser die Seite http://localhost:8080

# Minio

Für den Workshop habe ich das Thema Minio ausgewählt und mir folgende Ziele gesetzt:
- CRUD für Blog
- CRUD für Datei
- Datei muss dem Blog zugewiesen werden können
- Dateien sollen immer nur einmal im Minio erstellt werden
- Dateien mit dem gleichen Namen sollen nicht überschrieben werden
- Man muss über einen URL die Datei anzeigen und Downloaden können
- Docker-Images müssen im Github verfügbar sein
- Alles muss mit `Docker compose up` laufen
- Github-Actions müssen die Images erstellen

Die API-Dokumentation ist auf [Postman](https://documenter.getpostman.com/view/24838690/2sA358c5Kg) verfügbar.

## Verwendete Ressourcen:

Ich habe für die Ausführung dieses Projekts vorallem die Ressourcen vom Moodle verwendet. Ich habe die Dokumentation von [Quarkus](https://docs.quarkiverse.io/quarkus-minio/dev/index.html) und das Video von [Quarkusio](https://www.youtube.com/live/ScSdgWx6aAM?si=m86OgB-ZPwiOoK64&t=1129) angesehen und verwendet. Zum Coden habe ich vom Projekt im Verteilte Systeme 1 viel abgesehen zusammen mit der Quarkus-Dokumentation und Copilot.

Für den minio-Container habe ich die Dokumentation auf dem [Docker-hub](https://hub.docker.com/r/minio/minio/#!) verwendet.

## Verwendung

Auf der Seite http://localhost:8080 sind alle nötigen Links verfügbar
| Service   | Beschreibung                    |
| ---       | ---                             |
| Swagger   | Verlinkung zum Swagger          |
| Redpanda  | Verlinkung zur Redpanda Konsole |
| Github    | Link zur Repository             |
| Postman   | Postman Dokumentation           |
| Minio     | Verlinkung zur Minio Konsole    |

Auf Postman können ganz einfach Dateien und Blogs erfasst werden.
Beim erstellen der Datei wird darauf geachtet das der Inhalt sowie auch der Dateienname nicht doppelt vorhanden ist.

## Files 
Für den Inhalt der Dateien wird immer der Hashcode ausgelesen und erfasst in der Datenbank. Jedes mal wenn eine neue Datei erstellt wird, wird zuerst der Hashcode erstellt und mit den vorhandenen Dateien abgeglichen. 
Für Minio wird ein neuer Dateinamen als UUID erstellt. Somit können gleichnamige Dateien sich nicht überschreiben. Zusätzlich wird aber noch der Displayname in der Datenbank abgespeichert für die Darstellung und zum herunterladen.
So werden Dateien weder überschrieben noch kommen sie mehrmals vor.
```json
{
    "bucket": "blogfiles",
    "displayname": "image.jpeg",
    "filename": "3c5699d4-9c49-4aa0-9d0b-5f0d4eadcbaf",
    "hashcode": "rgxf6lq9NJPW0q7evsrLmLbbuOILw71GrF51622Q/Oo=",
    "id": 1
}
```

## Service aufbau

Die Service sind folgendermassen aufgebaut:

![Image](./docs/service.svg)

## Dockercompose

Für das Docker-Compose werden folgende Images erstellt:
- mysql
- minio
- kafka-validator
- kafka-blog-backend
- redpanda
- redpanda-console


