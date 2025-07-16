# User Story Template
**Admin User Stories**
---

### **User Story 1**

**Title:**
*As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely.*

**Acceptance Criteria:**

1. Admin can access the login page.
2. Admin must provide valid credentials to log in.
3. On successful login, admin is redirected to the dashboard.

**Priority:** High
**Story Points:** 3
**Notes:**

* Implement authentication using Spring Security with JWT tokens.
* Show error on invalid credentials.

---

### **User Story 2**

**Title:**
*As an admin, I want to log out of the portal, so that I can protect system access.*

**Acceptance Criteria:**

1. Admin sees a "Logout" button on the dashboard.
2. Clicking "Logout" invalidates the session or JWT token.
3. Admin is redirected to the login screen.

**Priority:** High
**Story Points:** 2
**Notes:**

* Token invalidation can be handled client-side for JWT.
* Logout button should be available on every authenticated page.

---

### **User Story 3**

**Title:**
*As an admin, I want to add doctors to the portal, so that new medical staff can be onboarded easily.*

**Acceptance Criteria:**

1. Admin can access a form to enter doctor details (name, specialization, contact).
2. On form submission, a new doctor record is added to the database.
3. Success message is shown after adding a doctor.

**Priority:** High
**Story Points:** 5
**Notes:**

* Use form validation for required fields.
* Doctor creation should trigger a welcome email (optional extension).

---

### **User Story 4**

**Title:**
*As an admin, I want to delete a doctor’s profile from the portal, so that I can manage inactive or departed staff.*

**Acceptance Criteria:**

1. Admin can view a list of doctors.
2. Each doctor has a "Delete" option.
3. On confirmation, the doctor’s profile is permanently removed.

**Priority:** Medium
**Story Points:** 3
**Notes:**

* Confirm deletion with a modal prompt.
* Check if the doctor has upcoming appointments before deletion.

---

### **User Story 5**

**Title:**
*As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per month, so that I can track usage statistics.*

**Acceptance Criteria:**

1. Stored procedure exists and accepts required date range parameters.
2. Running the procedure returns monthly appointment counts.
3. Output includes month names and appointment totals.

**Priority:** Medium
**Story Points:** 3
**Notes:**

* Procedure should use GROUP BY on month and year.
* Could be later exposed through an admin dashboard or report API.

---



**Patient User Stories**

---

### **User Story 1**

**Title:**
*As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering.*

**Acceptance Criteria:**

1. Patient can access a "Doctors" page without authentication.
2. The page lists doctors with basic details (name, specialization).
3. Each doctor profile includes a "Book Now" or "Register to Book" button.

**Priority:** High
**Story Points:** 3
**Notes:**

* Consider caching or optimizing this endpoint for anonymous users.
* Do not show sensitive doctor info (email/phone).

---

### **User Story 2**

**Title:**
*As a patient, I want to sign up using my email and password, so that I can book appointments.*

**Acceptance Criteria:**

1. Patient sees a registration form asking for email, password, and name.
2. Upon submission, an account is created and stored in the database.
3. Patient is redirected to login or logged in automatically.

**Priority:** High
**Story Points:** 3
**Notes:**

* Validate password strength.
* Consider email verification (optional).

---

### **User Story 3**

**Title:**
*As a patient, I want to log into the portal to manage my bookings, so that I can view or change appointments.*

**Acceptance Criteria:**

1. Patient can access login page.
2. On entering valid credentials, patient is redirected to their dashboard.
3. Login is protected using JWT.

**Priority:** High
**Story Points:** 3
**Notes:**

* Display login error on failed attempts.
* Store token in localStorage or cookie securely.

---

### **User Story 4**

**Title:**
*As a patient, I want to log out of the portal, so that I can secure my account.*

**Acceptance Criteria:**

1. A "Logout" button is available in the patient dashboard.
2. On clicking logout, token is removed and user is redirected.
3. No further actions can be taken without re-authentication.

**Priority:** High
**Story Points:** 2
**Notes:**

* Logout must clear session/token completely.
* Ensure protected routes are inaccessible after logout.

---

### **User Story 5**

**Title:**
*As a patient, I want to book an hour-long appointment with a doctor, so that I can consult for my health needs.*

**Acceptance Criteria:**

1. Patient selects a doctor and views available time slots.
2. Patient chooses a date and one-hour slot.
3. Appointment is stored and visible under upcoming bookings.

**Priority:** High
**Story Points:** 5
**Notes:**

* Prevent double-booking a slot.
* Add validation to restrict bookings to working hours.

---

### **User Story 6**

**Title:**
*As a patient, I want to view my upcoming appointments, so that I can prepare accordingly.*

**Acceptance Criteria:**

1. Patient dashboard lists upcoming appointments.
2. Each appointment includes date, time, and doctor info.
3. Patient can cancel or reschedule (if allowed).

**Priority:** Medium
**Story Points:** 3
**Notes:**

* Display a message if there are no upcoming appointments.
* Sort by nearest date first.

---

