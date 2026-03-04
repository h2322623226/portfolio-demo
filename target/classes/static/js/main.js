// [JS] year
const yearEl = document.getElementById("year");
if (yearEl) yearEl.textContent = new Date().getFullYear();

// [JS] smooth scroll
document.querySelectorAll('a[href^="#"]').forEach(a => {
  a.addEventListener("click", (e) => {
    const id = a.getAttribute("href");
    if (id === "#") return;
    const el = document.querySelector(id);
    if (!el) return;
    e.preventDefault();
    el.scrollIntoView({ behavior: "smooth", block: "start" });
    history.pushState(null, "", id);
  });
});

// [JS] theme toggle
const KEY  = "site-theme";
const root = document.documentElement;
const btn  = document.getElementById("themeBtn");

function applyTheme(t) {
  root.setAttribute("data-theme", t);
  localStorage.setItem(KEY, t);
  if (btn) btn.textContent = t === "light" ? "🌙" : "☀️";
}
const stored = localStorage.getItem(KEY) || "dark";
applyTheme(stored);
if (btn) {
  btn.addEventListener("click", () => {
    const current = root.getAttribute("data-theme") || "dark";
    applyTheme(current === "light" ? "dark" : "light");
  });
}

// [JS] hamburger
const hamburger     = document.getElementById("navHamburger");
const mobileMenu    = document.getElementById("navMobileMenu");
if (hamburger && mobileMenu) {
  hamburger.addEventListener("click", () => {
    const open = mobileMenu.classList.toggle("open");
    hamburger.setAttribute("aria-expanded", open);
    hamburger.textContent = open ? "✕" : "☰";
  });

  mobileMenu.querySelectorAll("a").forEach(a => {
    a.addEventListener("click", () => {
      mobileMenu.classList.remove("open");
      hamburger.setAttribute("aria-expanded", false);
      hamburger.textContent = "☰";
    });
  });
}

// [JS] drawer
const drawerBtn   = document.getElementById("drawerBtn");
const drawerPanel = document.getElementById("drawerPanel");
if (drawerBtn && drawerPanel) {
  drawerBtn.addEventListener("click", () => {
    const isOpen = drawerPanel.classList.toggle("open");
    drawerBtn.setAttribute("aria-expanded", isOpen);
  });
  drawerPanel.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener("click", () => {
      drawerPanel.classList.remove("open");
      drawerBtn.setAttribute("aria-expanded", "false");
    });
  });
}

// [JS] user dropdown
const userDropdownBtn  = document.getElementById('userDropdownBtn');
const userDropdownMenu = document.getElementById('userDropdownMenu');
if (userDropdownBtn && userDropdownMenu) {
  userDropdownBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    const isOpen = userDropdownMenu.classList.toggle('open');
    userDropdownBtn.setAttribute('aria-expanded', isOpen);
  });
  document.addEventListener('click', () => {
    userDropdownMenu.classList.remove('open');
    userDropdownBtn.setAttribute('aria-expanded', 'false');
  });
}