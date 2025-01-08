class ParticipantManager {
    constructor() {
        this.participants = new Map();
        this.initializeEventListeners();
    }

    initializeEventListeners() {
        // 방 나가기 버튼 이벤트
        document.getElementById('leaveRoom').addEventListener('click', () => this.leaveRoom());
    }

    addParticipant(participant) {
        // Map에 참가자 추가
        this.participants.set(participant.id, participant);
        this.updateParticipantsList();

        // 참가 알림
        this.showNotification(`${participant.username}님이 입장하셨습니다.`);
    }

    removeParticipant(participantId) {
        const participant = this.participants.get(participantId);
        if (participant) {
            this.participants.delete(participantId);
            this.updateParticipantsList();
            this.showNotification(`${participant.username}님이 퇴장하셨습니다.`);
        }
    }

    updateParticipantsList() {
        const participantsList = document.getElementById('participantsList');
        participantsList.innerHTML = ''; // 목록 초기화

        this.participants.forEach(participant => {
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.id = `participant-${participant.id}`;
            li.innerHTML = `
                <span>${participant.username}</span>
                ${participant.role === 'HOST' ? '<span class="badge bg-success">방장</span>' : ''}
            `;
            participantsList.appendChild(li);
        });
    }


    async leaveRoom() {
        try {
            const response = await fetch(`/api/rooms/${ROOM_DATA.roomId}/leave`, {
                method: 'POST'
            });

            if (response.ok) {
                window.location.href = '/rooms';
            } else {
                const error = await response.json();
                alert('방 나가기 실패: ' + error.message);
            }
        } catch (error) {
            console.error('방 나가기 중 오류:', error);
            alert('방 나가기 중 오류가 발생했습니다.');
        }
    }

    showNotification(message) {
        // Bootstrap Toast나 다른 알림 UI를 사용하여 메시지 표시
        const toast = document.createElement('div');
        toast.className = 'toast';
        toast.setAttribute('role', 'alert');
        toast.innerHTML = `
            <div class="toast-body">
                ${message}
            </div>
        `;

        document.body.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();

        // 토스트 자동 제거
        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove();
        });
    }
}