// doctorCard.js
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../components/loggedPatient.js";

export function createDoctorCard(doctor) {
  // Create main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Get user role from localStorage
  const role = localStorage.getItem("userRole");

  // Create doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  // assuming doctor.availability is an array of strings
  availability.textContent = `Availability: ${doctor.availability.join(", ")}`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // Create action buttons container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Admin role: delete button
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmed) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("You must be logged in as admin to delete.");
        return;
      }

      try {
        const result = await deleteDoctor(doctor.id, token);
        if (result.success) {
          alert("Doctor deleted successfully.");
          card.remove();
        } else {
          alert("Failed to delete doctor: " + result.message);
        }
      } catch (error) {
        alert("Error deleting doctor: " + error.message);
      }
    });
    actionsDiv.appendChild(removeBtn);
  }
  // Patient (not logged in)
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Please login to book an appointment.");
    });
    actionsDiv.appendChild(bookNow);
  }
  // Logged-in patient
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
      }
      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        alert("Failed to fetch patient data. Please try again.");
      }
    });
    actionsDiv.appendChild(bookNow);
  }

  // Assemble card
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
