1. docker starts
   docker compose up -d db
   docker compose exec db psql -U bank_dev -d bank_db

2. springboot starts
   ./mvnw -DskipTests package
   ./mvnw spring-boot:run

3. set the jwt
4. 

docker compose exec -T db psql -U bank_dev -d bank_db \
-v ON_ERROR_STOP=1 < sql/conversion-type.sql

set -a
source .env
set +a
./mvnw spring-boot:run

