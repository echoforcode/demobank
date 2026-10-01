1. docker starts
   docker compose up -d db
   docker compose exec db psql -U bank_dev -d bank_db

2. springboot starts
   ./mvnw -DskipTests package
   ./mvnw spring-boot:run

3. set the jwt
4. export JWT_SECRET="$(openssl rand -base64 32)"
