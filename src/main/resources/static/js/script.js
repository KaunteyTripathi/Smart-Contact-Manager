console.log("script.js loaded");

document.addEventListener("DOMContentLoaded", function () {

  /* =========================
     RICH TEXT EDITOR
  ========================= */

  const editor = document.getElementById("aboutEditor");
  const toolbar = document.querySelector(".editor-toolbar");

  if (editor && toolbar) {

    // Handle toolbar button clicks
    toolbar.addEventListener("click", function (e) {
      const button = e.target.closest("button");
      if (!button) return;

      const command = button.dataset.command;
      if (!command) return;

      editor.focus();
      document.execCommand(command, false, null);
    });

    // Handle color picker
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
      if (hiddenInput) {
        hiddenInput.value = editor.innerHTML;
      }
    });
  }

  /* =========================
     SPRING SECURITY LOGIN ERROR
  ========================= */

  const params = new URLSearchParams(window.location.search);
  if (params.has("error")) {
    alert("Invalid username or password");
  }

});

const toogleSidebar = () =>{
	
	if ($(".sidebar").is(":visible")
	){
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0");
	}else{
		
		$(".sidebar").css("display","block");
				$(".content").css("margin-left","20%");
	}
	
}
