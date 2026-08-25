## DoseGuard - Safe and smart medication management!
Developed for the Software Engineering and Web Design course, University of Rome Tor Vergata.

<p align="center">
  <img src="src/main/resources/icons/Brand_logo.png" width="300" style="background-color: white; padding: 10px; border-radius: 10px;"/>
</p>

## Description

DoseGuard is a Java-based platform where users can book medical appointments by selecting the desired medical specialty, viewing available doctors in that field, and choosing from available time slots. Patients can also manage their medication intake, dosages, and therapeutic schedules. Medical supervisors or doctors can oversee plans, check adherence, and track patient progress. A pharmacist role provides access to reviewing and managing patient prescriptions. The application supports both a graphical interface (JavaFX) and a command-line interface.

- **Patients** can select a medical specialty, choose a doctor, pick available time slots, and track medication schedules
- **Doctors** can manage their availability, monitor patient adherence, and therapeutic progress
- **Pharmacists** can review and verify patient prescriptions

## Tecnologies

- Java 17
- Maven
- MySQL
- JavaFX

## Architecture
**BCE** (Boundary-Control-Entity)- **MVC** (Model-View-Controller) pattern with clear separation between:
- `controller/applicativo` — business logic
- `controller/cli` — CLI user interface
- `controller/gui` — GUI user interface
- `view/cli` — CLI boundary view
- `view/gui` — GUI boundary view
- `dao` — data access layer (DB, File, Memory)
- `model` — domain entities
- `bean` — data transfer objects
- `pattern` — GoF patterns (Singleton, Observer, State)

The system supports three peristence mode:
- **DATABASE** — MySQL (full-version)
- **FILE** — JSON (full-version)
- **MEMORY** — in-memory (demo-version)

## Getting started

At startup, the application asks to select the persistence mode:

- `Demo` → simulated in-memory data
- `Database` → MySQL persistence
- `File` → JSON file persistence

Then, the interface must be selected:

- `CLI` → text-based interface
- `GUI` → graphical interface

To use the database mode, create and configure the following file:

```text
src/main/resources/db.properties
```
with the following content:
```text
db.url=jdbc:mysql://localhost:3306/doseguard
db.user.login=dg_login
db.user.login.password=dg_login
db.user.patient=dg_patient
db.user.patient.password=dg_patient
db.user.doctor=dg_doctor
db.user.doctor.password=dg_doctor
db.user.pharmacist=dg_pharmacist
db.user.pharmacist.password=dg_pharmacist
```
⚠️ The `db.properties` file includes database credentials.

## Demo credentials

| Role       | Email             | Password   |
|------------|-------------------|------------|
| Paziente   | `patient@demo`    | qualsiasi |
| Medico     | `doctor@demo`     | qualsiasi |
| Farmacista | `pharmacist@demo` | qualsiasi |


## Database credentials (modalità MySQL)

| Role       | Email                     | Password   |
|------------|---------------------------|------------|
| Pazziente  | `mario.rossi@test.com`    | password123 |
| Medico     | `dr.neri@test.com`        | password123 |
| Farmacista | `farmacia.russo@test.com` | password123 |


Other test accounts available, see `doseguard_db.sql` for the full list.
## Author
Evangelisti Francesco