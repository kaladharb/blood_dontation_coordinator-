# Blood Bank & Emergency Medical Supply Coordinator

Initial Java 17 college lab project foundation using Java Servlets, JSP, JavaBeans, Hibernate ORM, and MySQL. Later modules will add blood inventory, donors, requests, and emergency supply coordination.

## Technology

- Java 17
- Maven WAR
- Apache Tomcat 10.1.61
- `jakarta.servlet-api` 6.0.0
- Hibernate ORM 6.4.10.Final
- `jakarta.persistence-api` 3.1.0
- MySQL 8 with Connector/J 8.0.33
- JSP, HTML5, CSS3, and vanilla JavaScript

## Local database setup

Create the development database in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS bloodbank_db;
```

The application reads database settings from `DB_URL`, `DB_USER`, and `DB_PASSWORD`. The local development fallbacks are:

- `DB_URL`: `jdbc:mysql://localhost:3306/bloodbank_db?useSSL=false&serverTimezone=UTC`
- `DB_USER`: `root`
- `DB_PASSWORD`: empty

Set these environment variables to non-default values on your machine. Do not commit credentials.

## Build

From the project directory, run the exact Maven build:

```bash
mvn clean package
```

The deployable file is created at `target/bloodbank-coordinator.war`.

## Deploy to Tomcat 10.1

1. Install and start Apache Tomcat 10.1.
2. Ensure MySQL 8 is running and the `bloodbank_db` database exists.
3. Set `DB_URL`, `DB_USER`, and `DB_PASSWORD` in the Tomcat service/environment before startup.
4. Copy `target/bloodbank-coordinator.war` into Tomcat's `webapps` directory.
5. Start or restart Tomcat and wait for the WAR to expand.
6. Open `http://localhost:8080/bloodbank-coordinator/`.

The root page redirects to `/login`, which is handled by `AuthServlet` and forwarded to the protected JSP view under `WEB-INF/views`.

Hibernate is initialized only when its utility is first used and is closed by the servlet context listener during application shutdown.

## Creating an administrator for the lab demo

With Tomcat running locally, open `http://localhost:8080/bloodbank-coordinator/admin-setup` and choose the admin username and password in the form. This endpoint:

- only accepts requests from localhost;
- is available only while no `ADMIN` account exists;
- uses the same PBKDF2 password hashing as normal registration;
- never prints or logs the password.

After creation, the endpoint returns 404 permanently unless the database has no administrator again. Do not use the existing `kaladhar` USER account as the admin.

As an alternative, an existing normal user can be promoted by running this SQL as the database administrator:

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'your_registered_username';
```

Log out and log in again to open the admin dashboard. Public registration always creates a `USER`; it never accepts a role from the browser.

## Implemented MVP workflow

- User registration, PBKDF2 password hashing, login, logout, and role-based routing.
- User dashboard with blood availability and emergency request submission.
- Admin dashboard with non-negative inventory updates.
- Admin request approval/rejection. Approval and inventory deduction occur in the same Hibernate transaction and approval is rejected when inventory is insufficient.
