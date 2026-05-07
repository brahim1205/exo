FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copie le fichier JAR généré par Maven (dans le dossier target)
# On utilise un wildcard pour s'adapter à la version définie dans le pom.xml
COPY target/*.jar app.jar

# Port exposé par l'application (défini dans application.properties)
EXPOSE 9092

# Commande de lancement
ENTRYPOINT ["java", "-jar", "app.jar"]
