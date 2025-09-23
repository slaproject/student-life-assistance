# Production Deployment Guide

## Backend Deployment (Koyeb)

### Environment Variables to Set in Koyeb:
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=ishan_and_akole1419
DB_PASSWORD=TVM#knu%RT8%Tn!
ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app
```

### Build Command:
```
mvn clean package -DskipTests
```

### Run Command:
```
java -jar target/student-app-backend-1.0.0-SNAPSHOT.jar
```

### Health Check Endpoint:
```
/actuator/health
```

---

## Frontend Deployment (Vercel)

### Environment Variables to Set in Vercel Dashboard:
```
NEXT_PUBLIC_API_BASE_URL=https://your-backend-domain.koyeb.app
```

### Build Command (automatic):
```
npm run build
```

### Install Command (automatic):
```
npm install
```

---

## Database Setup (Supabase)

1. Create tables using the SQL scripts from your `create_tables.sql`
2. Run any initial data scripts if needed
3. Ensure your database connection string is correctly configured in production properties

---

## Post-Deployment Checklist

- [ ] Backend health check responds at `/actuator/health`
- [ ] CORS allows requests from your frontend domain
- [ ] Database connection is successful
- [ ] JWT authentication works properly
- [ ] Frontend can successfully call backend APIs
- [ ] All environment variables are properly set

---

## Local Development

To run locally with development profile:
```bash
cd student-app-backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

To run frontend locally:
```bash
cd student-app-web
npm run dev
```

Make sure your local PostgreSQL is running and accessible with the credentials in `application-dev.properties`.