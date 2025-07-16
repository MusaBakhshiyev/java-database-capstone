function renderHeader() {
  const headerDiv = document.getElementById("header");

  // If on homepage root, clear session and show basic header only
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Basic header content (logo + nav start)
  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>`;

  // Check for invalid session - logged-in roles without token
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Role-based header content
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button id="homeBtn" class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="homeBtn" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" id="logoutLink">Logout</a>`;
  }

  headerContent += `</nav></header>`;

  headerDiv.innerHTML = headerContent;

  attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
  const logoutLink = document.getElementById("logoutLink");
  if (logoutLink) {
    logoutLink.addEventListener("click", (e) => {
      e.preventDefault();
      // For loggedPatient use logoutPatient, others use logout
      const role = localStorage.getItem("userRole");
      if (role === "loggedPatient") {
        logoutPatient();
      } else {
        logout();
      }
    });
  }

  const patientLoginBtn = document.getElementById("patientLogin");
  if (patientLoginBtn) {
    patientLoginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  const patientSignupBtn = document.getElementById("patientSignup");
  if (patientSignupBtn) {
    patientSignupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  const homeBtn = document.getElementById("homeBtn");
  if (homeBtn) {
    homeBtn.addEventListener("click", () => {
      // Depending on role, redirect or perform action
      const role = localStorage.getItem("userRole");
      if (role === "doctor") {
        window.location.href = "/pages/doctorDashboard.html";
      } else if (role === "loggedPatient") {
        window.location.href = "/pages/loggedPatientDashboard.html";
      }
    });
  }
}

// Clears all session data and redirects to root
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

// Logs out a loggedPatient but retains role as "patient"
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

// Call renderHeader immediately to set up the header on page load
renderHeader();
