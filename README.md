# Ticket System

Sistema de gestión de tickets desarrollado con:

- Spring Boot
- React + Vite
- PostgreSQL
- Docker Compose
- Nginx

---

# Requisitos

- Docker
- Docker Compose

---

# Configuración

Copiar:

```bash
.env.example
```

como:

```bash
.env
```

y completar las variables de entorno.

---

# Ejecutar con Docker

Desde la raíz del proyecto:

```bash
docker compose up --build
```

La aplicación estará disponible en:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080

---

# Ejecutar sin Docker (desarrollo)

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

---

# Estructura del proyecto

```
ticket-demo/
│
├── backend/
├── frontend/
├── docker-compose.yml
├── .env.example
└── README.md
```