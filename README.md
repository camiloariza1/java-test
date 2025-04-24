# Java User Registration & Login API

This project is a Spring Boot application providing RESTful endpoints for user registration and login, including JWT authentication and phone number management.

## Features
- User registration with email, password, and optional phone numbers
- Secure password handling and JWT token generation
- User login with JWT authentication
- Data validation and error handling

## Requirements
- Java 17+
- Gradle
- (Optional) Docker/PostgreSQL if you want to use a real DB

## Running the Application

```
./gradlew bootRun
```

The app will start on `http://localhost:8080`.

## API Endpoints

### 1. Register User

**Request:**
```
curl -X POST http://localhost:8080/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Andres Example",
    "email": "andres@example.com",
    "password": "Password12",
    "phones": [
      {
        "number": 123456789,
        "citycode": 1,
        "countrycode": "57"
      }
    ]
  }'
```

**Response:**
```
{
  "id": "<uuid>",
  "created": "Apr 24, 2025 03:35:58 PM",
  "lastLogin": "Apr 24, 2025 03:35:58 PM",
  "token": "<jwt-token>",
  "phones": [
    {
      "number": 123456789,
      "cityCode": 1,
      "countryCode": "57"
    }
  ],
  "active": true
}
```

### 2. Login User

**Request:**
```
curl -X POST http://localhost:8080/login \
  -H "Authorization: Bearer <jwt-token>"
```

**Response:**
```
{
  "id": "<uuid>",
  "created": "Apr 24, 2025 03:35:58 PM",
  "lastLogin": "Apr 24, 2025 03:36:22 PM",
  "token": "<new-jwt-token>",
  "name": "Andres Example",
  "email": "andres@example.com",
  "phones": [
    {
      "number": 123456789,
      "cityCode": 1,
      "countryCode": "57"
    }
  ],
  "active": true
}
```

## Notes
- Passwords must be 8-12 characters, contain exactly one uppercase letter, and exactly two numbers.
- JWT tokens are required for login and are returned upon registration.
- All dates are formatted as `MMM dd, yyyy hh:mm:ss a`.

## Testing
Run all tests with:
```
./gradlew clean test
```

## License
MIT
