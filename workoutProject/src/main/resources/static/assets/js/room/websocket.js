class RoomWebSocket {
    constructor(roomId, joinRequest) {
        this.roomId = roomId;
        this.joinRequest = joinRequest;
        this.socket = null;
        this.connect();
    }

    connect() {
        this.socket = new WebSocket(`ws://${window.location.host}/ws/room/${this.roomId}`);

        this.socket.onopen = () => {
            console.log('WebSocket 연결 성공');
            this.send('JOIN', { username: this.joinRequest.username });
        };

        this.socket.onmessage = (event) => {
            try {
                console.log('Received message:', event.data);
                const message = JSON.parse(event.data);
                this.handleMessage(message);
            } catch (error) {
                console.error('Message parsing error:', error);
            }
        };

        this.socket.onclose = () => {
            console.log('WebSocket 연결 종료');
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket 에러:', error);
        };
    }

    handleMessage(message) {
        console.log('Received message:', message);

        switch (message.type) {
            case 'PARTICIPANT_JOIN':
                participantManager.addParticipant({
                    id: message.data.id,
                    username: message.data.username
                });
                break;

            case 'PARTICIPANT_LEAVE':
                participantManager.removeParticipant(message.data);
                break;

            case 'YOUR_ID':
                ROOM_DATA.myParticipantId = message.data.id;
                console.log('Your ID:', ROOM_DATA.myParticipantId);
                break;

            case 'SCREEN_OFFER':
                console.log('Screen offer received:', message.data);

                screenShare.handleOffer(message.data.from, message.data.sdp);
                break;

            case 'SCREEN_ANSWER':
                console.log('Screen answer received:', message.data);
                screenShare.handleAnswer(message.data.from, message.data.sdp);
                break;

            case 'ICE_CANDIDATE':
                console.log('ICE candidate received:', message.data);
                screenShare.handleIceCandidate(message.data.from, message.data.candidate);
                break;

            case 'SCREEN_SHARE_START':
                console.log(`Screen share started for room: ${message.data.roomId}`);
                alert(`Participant started sharing their screen in room ${message.data.roomId}`);
                break;

            case 'SCREEN_SHARE_STOP':
                console.log('Screen share stopped for room:', message.data.roomId);
                // 화면 공유 중지 UI 처리
                break;

            default:
                console.warn('Unknown message type:', message.type);
        }
    }

    send(type, data) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            const message = JSON.stringify({ type, data });
            console.log('Sending message:', message);
            this.socket.send(message);
        }
    }
}
