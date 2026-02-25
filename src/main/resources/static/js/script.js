document.addEventListener("DOMContentLoaded", function () {

  /* =========================
     RICH TEXT EDITOR
  ========================= */
  const editor = document.getElementById("aboutEditor");
  const toolbar = document.querySelector(".editor-toolbar");

  if (editor && toolbar) {
    toolbar.addEventListener("click", function (e) {
      const button = e.target.closest("button");
      if (!button) return;

      const command = button.dataset.command;
      if (!command) return;

      editor.focus();
      document.execCommand(command, false, null);
    });

    toolbar.addEventListener("change", function (e) {
      if (e.target.type === "color") {
        editor.focus();
        document.execCommand("foreColor", false, e.target.value);
      }
    });
  }

  /* =========================
     FORM SUBMIT – SYNC EDITOR
  ========================= */
  const form = document.querySelector("form");
  if (form && editor) {
    form.addEventListener("submit", function () {
      const hiddenInput = document.getElementById("aboutHidden");
      if (hiddenInput) hiddenInput.value = editor.innerHTML;
    });
  }

  /* =========================
     SPRING SECURITY LOGIN ERROR
  ========================= */
  const params = new URLSearchParams(window.location.search);
  if (params.has("error")) alert("Invalid username or password");

  /* =========================
     SIDEBAR TOGGLE
  ========================= */
  const sidebar = document.querySelector(".sidebar");
  const toggleBtn = document.querySelector(".fa-bars");
  const crossBtn = document.querySelector(".sidebar .cross-btn");
  const content = document.querySelector(".content");

  const openSidebar = () => {
      sidebar.classList.remove("collapsed");
      sidebar.classList.add("active");
      content.classList.remove("sidebar-collapsed");
      if (window.innerWidth <= 768) document.body.classList.add("sidebar-open");
  };

  const closeSidebar = () => {
      sidebar.classList.add("collapsed");
      sidebar.classList.remove("active");
      content.classList.add("sidebar-collapsed");
      document.body.classList.remove("sidebar-open");
  };

  if (toggleBtn) {
    toggleBtn.addEventListener("click", function () {
      if (sidebar.classList.contains("collapsed")) openSidebar();
      else closeSidebar();
    });
  }

  if (crossBtn) {
    crossBtn.addEventListener("click", closeSidebar);
  }

  document.addEventListener("click", function (e) {
    if (window.innerWidth > 768) return;
    if (sidebar.classList.contains("collapsed")) return;
    if (!sidebar.contains(e.target) && e.target !== toggleBtn) closeSidebar();
  });

  sidebar.addEventListener("click", function (e) {
    e.stopPropagation();
  });

  /* =========================
     SEARCH FUNCTION
  ========================= */
  const search = () => {
    let query = document.getElementById("search-input").value;

    if (query === "") {
      document.querySelector(".search-result").style.display = "none";
      return;
    }

    let url = `/user/search/${query}`;

    fetch(url)
      .then(response => response.json())
      .then(data => {
        let text = `<div class='list-group'>`;
        data.forEach(contact => {
          text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
        });
        text += `</div>`;
        const resultDiv = document.querySelector(".search-result");
        resultDiv.innerHTML = text;
        resultDiv.style.display = "block";
      })
      .catch(error => console.error(error));
  };

  window.search = search;
});