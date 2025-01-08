document.addEventListener("DOMContentLoaded", () => {
    const modals = document.querySelectorAll(".modal");
    const closeButtons = document.querySelectorAll(".close");
    const openCreateRoomModal = document.getElementById("openCreateRoomModal");
    const createRoomModal = document.getElementById("createRoomModal");

    closeButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const modalId = button.getAttribute("data-modal");
            const modal = document.getElementById(modalId);
            if (modal) modal.style.display = "none";
        });
    });

    window.addEventListener("click", (event) => {
        modals.forEach((modal) => {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });
    });

    if (openCreateRoomModal) {
        openCreateRoomModal.addEventListener("click", () => {
            createRoomModal.style.display = "block";
        });
    }
});