## MySQL Database Design

---

### **Table: patients**

* `id`: INT, **Primary Key**, **AUTO\_INCREMENT**
* `full_name`: VARCHAR(100), **NOT NULL**
* `email`: VARCHAR(100), **UNIQUE**, **NOT NULL**
* `phone`: VARCHAR(15), **NOT NULL**
* `date_of_birth`: DATE
* `gender`: ENUM('Male', 'Female', 'Other')
* `address`: TEXT
* `created_at`: TIMESTAMP DEFAULT CURRENT\_TIMESTAMP

📝 **Notes:**

* Emails must be unique for login.
* Phone and email formats can be validated in code.
* If a patient is deleted, **appointments should also be deleted** (use `ON DELETE CASCADE` in FK).

---

### **Table: doctors**

* `id`: INT, **Primary Key**, **AUTO\_INCREMENT**
* `full_name`: VARCHAR(100), **NOT NULL**
* `email`: VARCHAR(100), **UNIQUE**, **NOT NULL**
* `phone`: VARCHAR(15)
* `specialization`: VARCHAR(100), **NOT NULL**
* `availability_schedule`: TEXT (or better stored in a separate availability table)
* `bio`: TEXT
* `created_at`: TIMESTAMP DEFAULT CURRENT\_TIMESTAMP

📝 **Notes:**

* Prevent overlapping appointments for doctors in application logic.
* Email must be unique.

---

### **Table: appointments**

* `id`: INT, **Primary Key**, **AUTO\_INCREMENT**
* `doctor_id`: INT, **Foreign Key** → `doctors(id)` **ON DELETE CASCADE**
* `patient_id`: INT, **Foreign Key** → `patients(id)` **ON DELETE CASCADE**
* `appointment_time`: DATETIME, **NOT NULL**
* `duration_minutes`: INT DEFAULT 60
* `status`: ENUM('Scheduled', 'Completed', 'Cancelled') DEFAULT 'Scheduled'
* `notes`: TEXT

📝 **Notes:**

* Application logic should prevent a doctor from having overlapping appointments.

---

### **Table: admin**

* `id`: INT, **Primary Key**, **AUTO\_INCREMENT**
* `username`: VARCHAR(50), **UNIQUE**, **NOT NULL**
* `email`: VARCHAR(100), **UNIQUE**, **NOT NULL**
* `password_hash`: VARCHAR(255), **NOT NULL**
* `role`: VARCHAR(50) DEFAULT 'admin'
* `created_at`: TIMESTAMP DEFAULT CURRENT\_TIMESTAMP

📝 **Notes:**

* Passwords should be hashed securely (e.g., using BCrypt).

---

### (Optional) **Table: clinic\_locations**

* `id`: INT, **Primary Key**, **AUTO\_INCREMENT**
* `name`: VARCHAR(100), **NOT NULL**
* `address`: TEXT, **NOT NULL**
* `phone`: VARCHAR(15)
* `email`: VARCHAR(100)

📝 **Notes:**

* Can be linked to doctors later if doctors work in specific locations.

---

### (Optional) **Table: payments**

* `id`: INT, **Primary Key**, **AUTO\_INCREMENT**
* `appointment_id`: INT, **Foreign Key** → `appointments(id)` **ON DELETE CASCADE**
* `amount`: DECIMAL(10,2), **NOT NULL**
* `payment_method`: ENUM('Cash', 'Card', 'Online'), **NOT NULL**
* `payment_date`: DATETIME DEFAULT CURRENT\_TIMESTAMP

📝 **Notes:**

* Tracks whether a patient paid for a visit or consultation.

---

##MongoDB Collection Design

MongoDB is used to store **prescriptions**, which often include flexible or optional fields like doctor notes, refill info, and pharmacy details. These are better suited to a NoSQL structure than rigid SQL tables.

### **Collection: prescriptions**

```json
{
  "_id": "ObjectId('64f7a1234a9cde001234abcd')",
  "patientId": 23,
  "doctorId": 9,
  "appointmentId": 102,
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Twice a day",
      "duration": "5 days"
    },
    {
      "name": "Ibuprofen",
      "dosage": "400mg",
      "frequency": "As needed",
      "notes": "Only if fever returns"
    }
  ],
  "issuedAt": "2025-07-16T10:30:00Z",
  "refillInfo": {
    "refillCount": 1,
    "lastRefillDate": null
  },
  "doctorNotes": "Patient recovering well. Review in one week.",
  "pharmacy": {
    "name": "HealthyCare Pharmacy",
    "contact": "+994-55-123-4567",
    "location": "Baku, Narimanov District"
  },
  "tags": ["infection", "antibiotic"],
  "attachments": [
    {
      "type": "pdf",
      "url": "https://clinic-files.com/prescriptions/102.pdf"
    }
  ]
}
```

---

### 📝 Notes:

* `patientId`, `doctorId`, and `appointmentId` reference the structured data in MySQL.
* The `medications` array allows multiple prescribed drugs in one document.
* `pharmacy`, `refillInfo`, and `attachments` are embedded documents for flexibility.
* `tags` can help filter prescriptions by keyword or condition.
* `doctorNotes` supports long-form, unstructured text.

---
