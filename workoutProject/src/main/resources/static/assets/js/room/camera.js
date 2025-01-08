class CameraShare {
    constructor() {
        this.peerConnections = new Map();
        this.stream = null;
        this.initializeButtons();
    }

    initializeButtons() {
        const startButton = document.getElementById('startCamera');
        const stopButton = document.getElementById('stopCamera');

        startButton.addEventListener('click', () => this.startCamera());
        stopButton.addEventListener('click', () => this.stopCamera());
    }

    async startCamera() {
        try {
            this.stream = await navigator.mediaDevices.getUserMedia({
                video: true,
                audio: true
            });

            // 로컬 비디오 표시
            const videoGrid = document.getElementById('videoGrid');
            const videoElement = this.createVideoElement(ROOM_DATA.currentUserId, true);
            videoGrid.appendChild(videoElement);
            videoElement.srcObject = this.stream;

            // 다른 참가자들과 연결
            participantManager.participants.forEach((participant, id) => {
                if (id !== ROOM_DATA.myParticipantId) {
                    this.createPeerConnection(id);
                }
            });

            this.updateButtons(true);
        } catch (error) {
            console.error('카메라 시작 오류:', error);
            alert('카메라를 시작할 수 없습니다.');
        }
    }

    createVideoElement(participantId, isLocal = false) {
        const div = document.createElement('div');
        div.className = 'video-item';
        div.id = `video-container-${participantId}`;

        const video = document.createElement('video');
        video.id = `video-${participantId}`;
        video.autoplay = true;
        video.playsInline = true;
        if (isLocal) video.muted = true;

        div.appendChild(video);
        return div;
    }

    createPeerConnection(participantId) {
        const peerConnection = new RTCPeerConnection({
            iceServers: [{urls: 'stun:stun.l.google.com:19302'}]
        });

        this.stream.getTracks().forEach(track => {
            peerConnection.addTrack(track, this.stream);
        });

        peerConnection.onicecandidate = event => {
            if (event.candidate) {
                websocket.send('ICE_CANDIDATE', {
                    to: participantId,
                    candidate: event.candidate
                });
            }
        };

        peerConnection.ontrack = event => {
            const videoGrid = document.getElementById('videoGrid');
            const videoElement = this.createVideoElement(participantId);
            videoGrid.appendChild(videoElement);
            videoElement.srcObject = event.streams[0];
        };

        this.peerConnections.set(participantId, peerConnection);
        return peerConnection;
    }


    stopCamera() {
        if (this.stream) {
            this.stream.getTracks().forEach(track => track.stop());
            document.getElementById('mainVideo').srcObject = null;
            this.updateButtons(false);
            this.isSharing = false;
            this.stream = null;

            // 다른 참가자들에게 카메라 공유 중단을 알림
            websocket.send('CAMERA_STOP', {roomId: ROOM_DATA.roomId});
        }
    }

    updateButtons(isSharing) {
        document.getElementById('startCamera').classList.toggle('d-none', isSharing);
        document.getElementById('stopCamera').classList.toggle('d-none', !isSharing);
    }
}