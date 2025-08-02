
# ✈️ Moveo Flight API

Moveo Flight API is a **Spring Boot** backend for managing private flight reservations, online payments, and notifications (Email/SMS).

It supports:

- **User registration with OTP (Email/SMS) verification**
- **Flight search & reservation system**
- **Payment processing via Stripe**
- **Notification system (scheduled Email & SMS)**
- **Webhook handling for payment confirmation**

---

## 📌 Tech Stack

- **Backend:** Spring Boot 3.x
- **Database:** MySQL (JPA/Hibernate)
- **Security:** Spring Security + JWT
- **Payment Gateway:** Stripe
- **Notifications:**
  - Email → Mailtrap for test
  - SMS → Twilio
- **Scheduling:** Spring Scheduler (cron job for pending notifications)
- **Frontend:** React + Stripe Elements

---

## 📂 Project Structure

    moveo-flight-api/
    ├── controller/        # REST API Controllers
    ├── model/             # Entities (User, Flight, Reservation, Payment, Notification)
    ├── repository/        # JPA Repositories
    ├── service/           # Business logic (PaymentService, NotificationService, OtpService)
    ├── scheduler/         # Scheduled jobs (send pending notifications)
    ├── config/            # Security, Stripe, Twilio config
    └── resources/
      └── application.yml
---

## 🚀 Features

- **Users & Authentication**
  - Register users with OTP verification (Email or SMS)
  - JWT-based authentication for API access

- **Flights**
  - Create, update, list available flights
  - Flight statuses → `AVAILABLE`, `MAINTENANCE`, `RESERVED`

- **Reservations**
  - Reserve a flight → status initially `PENDING`
  - After payment success → status becomes `CONFIRMED`

- **Payments**
  - Stripe checkout with `payment_intent`
  - Webhook to confirm `payment_intent.succeeded`
  - Payment status → `PENDING`, `PAID`, `FAILED`

- **Notifications**
  - Send booking confirmation (EMAIL / SMS)
  - Scheduler retries pending notifications

---

## 🔗 API Endpoints

### 👤 **Users**

| Method | Endpoint                  | Description |
|--------|--------------------------|-------------|
| POST   | `/api/users`     | Register user & send OTP |
| POST   | `/api/users/verify-otp`   | Verify OTP |
| GET   | `/api/users/resend-otp`   | Resend OTP |
| POST   | `/api/auth/login`        | Authenticate & get JWT |

User information (for test) :

User with user role

```json
{
  "email": "john.doe@moveo.com",
  "password": "secret",
  "roles": "USER",
  "fullName": "John Doe",
  "phoneNumber": "+123456789"
}
```

User with admin role

```json
{
  "email": "admin@moveo.com",
  "password": "123456",
  "roles": "ADMIN",
  "fullName": "Admin",
  "phoneNumber": "+123456789"
}
```

---

### ✈️ **Flights**

| Method | Endpoint              | Description |
|--------|----------------------|-------------|
| GET    | `/api/flights`       | List flights |
| POST   | `/api/flights`       | Create new flight |
| PUT    | `/api/flights/{id}`  | Update flight |
| DELETE | `/api/flights/{id}`  | Delete flight |

---

### 📝 **Reservations**

| Method | Endpoint                       | Description |
|--------|-------------------------------|-------------|
| POST   | `/api/reservations?flightId={id}&userId={id}`           | Create a reservation |
| GET    | `/api/reservations/{id}`      | Get reservation details |
| GET    | `/api/reservations/user/{userId}`      | Get a user reservation details |
| PUT    | `/api/reservations/{id}/cancel`      | Cancel a reservation |

---

### 💳 **Payments**

| Method | Endpoint                                      | Description |
|--------|-----------------------------------------------|-------------|
| POST   | `/api/payments/{reservationId}?notificationType=SMS` | Create Stripe PaymentIntent |
| POST   | `/api/payments/stripe/webhook`                | Stripe webhook listener |
| GET   | `/api/payments/reservation/{reservationId}`                | Get payment details for a reservation |

---

### 🔔 **Notifications**

| Method | Endpoint                       | Description |
|--------|-------------------------------|-------------|
| POST   | `/api/notifications/{userId}` | Create notification (EMAIL/SMS) |

---

## 🛠️ Setup & Installation

1️⃣ **Clone repo**

```bash
git clone https://github.com/org/moveo-flight-api.git
cd moveo-flight-api
```

2️⃣ **Configure application properties**

```bash
sprint.application.name=moveo-flight-api
spring.datasource.url = jdbc:postgresql://localhost:5432/flightbd
spring.datasource.username =  root
spring.datasource.password =  root
spring.datasource.jpa.hibernate.ddl-auto = update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
server.port=8000

stripe.secret.key=sk_test_xxx
stripe.currency=eur
stripe.webhook-secret=whsec_xxx

spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=587
spring.mail.username=xxx
spring.mail.password=xxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=no-reply@moveo.com

twilio.account-sid=Acxxxxx
twilio.auth-token=xxx
twilio.phone-number=+123456789
```

3️⃣ Run backend

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🏦 Stripe Webhook Setup

For local dev, we need to have the webhook signing secret so need to install the Stripe CLI. See the following steps :

- [Download Stripe CLI](https://stripe.com/docs/stripe-cli#install) and place the stripe file in the path environement variable.
- Login in the stripe cli using the command `stripe login`

```bash
stripe listen --forward-to localhost:8000/api/payments/stripe/webhook
```

- Note that this method is applied because we are using localhost we not have the domain.
- If we had a domain we were supposed to configure the webhook on the stripe dashboard.

When a payment succeeds:

- Stripe sends payment_intent.succeeded
- Backend updates DB → Payment status → PAID
- Related Reservation status → CONFIRMED
- Notification is created & sent
