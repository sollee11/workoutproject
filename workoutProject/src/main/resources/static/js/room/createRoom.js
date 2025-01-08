(async function () {
    const createRoomBtn = document.getElementById("createRoomBtn");

    createRoomBtn.addEventListener("click", async () => {
        const roomName = document.getElementById("newRoomTitle").value.trim();
        const password = document.getElementById("newRoomPassword").value.trim();

        if (!currentUser || currentUser === 'guest') {
            alert("로그인한 사용자만 방을 생성할 수 있습니다. 로그인하세요.");
            window.location.href = "/login"; // 로그인 페이지로 리다이렉트
            return;
        }

        if (!roomName || !password) {
            alert("방 이름과 비밀번호를 입력해주세요.");
            return;
        }

        try {
            const response = await fetch("/api/rooms", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    title: roomName,
                    password: password,
                    host: currentUser,
                }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "방 생성에 실패했습니다.");
            }

            alert("방 생성 성공!");
            document.getElementById("createRoomModal").style.display = "none";
            location.reload();
        } catch (error) {
            console.error("방 생성 중 오류:", error);
            alert(error.message);
        }
    });
})();
