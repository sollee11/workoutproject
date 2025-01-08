async function loadRooms() {
    try {
        console.log("방 목록 불러오기 시작...");
        const response = await fetch('/api/rooms');
        console.log("응답 상태:", response.status, response.ok);

        if (!response.ok) {
            const errorData = await response.json().catch(() => "JSON 파싱 실패");
            console.error("API 오류 응답:", errorData);
            throw new Error('방 목록을 불러오는데 실패했습니다.');
        }

        const rooms = await response.json();
        console.log("방 목록 데이터:", rooms);

        const roomListElement = document.querySelector('.room-list');
        roomListElement.innerHTML = '';

        rooms.forEach(room => {
            const roomElement = createRoomElement(room);
            roomListElement.appendChild(roomElement);
        });
    } catch (error) {
        console.error('방 목록 로딩 중 오류:', error);

        const roomListElement = document.querySelector('.room-list');
        if (roomListElement.children.length > 0) {
            alert('방 목록을 새로고침하는데 실패했습니다.');
        }
    }
}

function formatDate(dateString) {
    try {
        const date = new Date(dateString);

        if (isNaN(date.getTime())) {
            console.log('Invalid date:', dateString);
            return '날짜 정보 없음';
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    } catch (error) {
        console.error('Date formatting error:', error);
        return '날짜 정보 없음';
    }
}

function createRoomElement(room) {
    const roomDiv = document.createElement('div');
    roomDiv.className = 'room-card';
    roomDiv.setAttribute('data-room-name', room.title);
    roomDiv.setAttribute('data-room-id', room.id);

    const statusText = {
        'OPEN': '참여 가능',
        'CLOSED': '종료됨',
        'IN_PROGRESS': '진행 중'
    }[room.status] || room.status;

    roomDiv.innerHTML = `
        <div class="thumbnail"></div>
        <div class="room-info">
            <h4>${room.title}</h4>
            <div class="room-details">
                <span class="host">방장: ${room.host || '정보 없음'}</span>
                <span class="participants">참가자: ${room.currentParticipants || 0}명</span>
                <span class="status">상태: ${statusText}</span>
            </div>
            <div class="created-at">생성: ${formatDate(room.createdAt)}</div>
        </div>
        <button class="enter-room-btn" data-room-name="${room.title}" data-room-id="${room.id}">입장</button>
    `;

    const enterButton = roomDiv.querySelector('.enter-room-btn');
    enterButton.addEventListener('click', () => {
        const enterRoomModal = document.getElementById('enterRoomModal');
        const selectedRoomName = document.getElementById('selectedRoomName');
        // room.id도 함께 저장
        enterRoomModal.setAttribute('data-room-id', room.id);
        selectedRoomName.textContent = `방 이름: ${room.title}`;
        enterRoomModal.style.display = 'block';
        // 이전 입력값 초기화
        document.getElementById('roomPasswordInput').value = '';
    });

    return roomDiv;
}

document.addEventListener('DOMContentLoaded', () => {
    loadRooms().then(r => console.log('방 목록 로딩 완료'));

    setInterval(loadRooms, 30000);

    const createRoomBtn = document.getElementById('createRoomBtn');
    if (createRoomBtn) {
        createRoomBtn.addEventListener('click', async () => {
            setTimeout(loadRooms, 2000);
        });
    }

    const confirmEnterRoomBtn = document.getElementById("confirmEnterRoomBtn");
    if (confirmEnterRoomBtn) {
        confirmEnterRoomBtn.addEventListener("click", async () => {
            console.log("입장 버튼 클릭됨");

            const enterRoomModal = document.getElementById("enterRoomModal");
            const roomId = enterRoomModal.getAttribute('data-room-id');
            const roomName = document.getElementById("selectedRoomName").textContent.replace("방 이름: ", "").trim();
            const password = document.getElementById("roomPasswordInput").value.trim();

            console.log('입장 시도:', {roomId, roomName, password});

            if (!password) {
                alert("비밀번호를 입력해주세요.");
                return;
            }

            try {
                const enterResponse = await fetch(`/api/rooms/${roomId}/enter`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        password: password,
                    }),
                });

                if (!enterResponse.ok) {
                    const errorData = await enterResponse.json();
                    throw new Error(errorData.message || "방 입장에 실패했습니다.");
                }

                alert("방 입장 성공!");
                enterRoomModal.style.display = "none";
                window.location.href = `/room/call/${roomId}`;
            } catch (error) {
                console.error("방 입장 중 오류:", error);
                alert(error.message);
            }
        });
    } else {
        console.error("confirmEnterRoomBtn을 찾을 수 없습니다.");
    }
});

document.addEventListener('DOMContentLoaded', () => {
    loadRooms().then(r => console.log('방 목록 로딩 완료'));

    setInterval(loadRooms, 30000);
});

document.getElementById('createRoomBtn').addEventListener('click', async () => {
    setTimeout(loadRooms, 2000);
});