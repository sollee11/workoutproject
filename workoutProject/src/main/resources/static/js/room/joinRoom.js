(async function () {
    const confirmEnterRoomBtn = document.getElementById("confirmEnterRoomBtn");

    confirmEnterRoomBtn.addEventListener("click", async () => {
        const enterRoomModal = document.getElementById("enterRoomModal");
        const roomId = enterRoomModal.getAttribute('data-room-id');
        const roomName = document.getElementById("selectedRoomName").textContent.replace("방 이름: ", "").trim();
        const password = document.getElementById("roomPasswordInput").value.trim();

        console.log('입장 시도:', { roomId, roomName, password });  // 디버깅용 로그

        if (!roomName || !password) {
            alert("비밀번호를 입력해주세요.");
            return;
        }

        try {
            console.log(`요청 URL: /api/rooms/${roomId}/enter`);  // URL 확인용 로그
            const enterResponse = await fetch(`/api/rooms/${roomId}/enter`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    password: password,
                }),
            });

            console.log('서버 응답:', enterResponse);

            if (!enterResponse.ok) {
                const errorData = await enterResponse.json();
                throw new Error(errorData.message || "방 입장에 실패했습니다.");
            }

            alert("방 입장 성공!");
            enterRoomModal.style.display = "none";
            console.log(`이동할 URL: /room/call/${roomId}`);
            window.location.href = `/room/call/${roomId}`;
        } catch (error) {
            console.error("방 입장 중 오류:", error);
            alert(error.message);
        }
    });
})();