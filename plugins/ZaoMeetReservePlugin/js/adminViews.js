var zaomeetreserve = zaomeetreserve || {};

(function (ns) {
  "use strict";

  /**
   * Ouvre/ferme le menu contextuel (bouton "...") d'une ligne du tableau.
   * @param {HTMLElement} btn le bouton .zaomeetreserve-row-menu-btn cliqué
   */
  ns.toggleRowMenu = function (btn) {
    var menu = btn.parentElement;
    var wasOpen = menu.classList.contains("zaomeetreserve-open");
    ns.closeAllRowMenus();
    if (!wasOpen) {
      menu.classList.add("zaomeetreserve-open");
    }
  };

  ns.closeAllRowMenus = function () {
    var openMenus = document.querySelectorAll(".zaomeetreserve-row-menu.zaomeetreserve-open");
    for (var i = 0; i < openMenus.length; i++) {
      openMenus[i].classList.remove("zaomeetreserve-open");
    }
  };

  document.addEventListener("click", function (evt) {
    if (!evt.target.closest || !evt.target.closest(".zaomeetreserve-row-menu")) {
      ns.closeAllRowMenus();
    }
  });

})(zaomeetreserve);