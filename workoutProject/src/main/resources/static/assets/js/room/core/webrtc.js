export class WebRTCManager {
    constructor(socketManager) {
        this.socketManager = socketManager;
        this.peerConnections = new Map();
        this.localStream = null;

        // WebSocket 메시지 핸들러 등록
        this.setupWebSocketHandlers();
    }

    setupWebSocketHandlers() {
        // Offer 수신 처리
        this.socketManager.on('SCREEN_OFFER', async (data) => {
            const { from, sdp } = data;
            let peerConnection = this.peerConnections.get(from);

            if (!peerConnection) {
                peerConnection = this.createPeerConnection(from);
            }

            await peerConnection.setRemoteDescription(new RTCSessionDescription(sdp));
            const answer = await peerConnection.createAnswer();
            await peerConnection.setLocalDescription(answer);

            this.socketManager.send('SCREEN_ANSWER', {
                to: from,
                sdp: peerConnection.localDescription
            });

            console.log('Answer sent to:', from);
        });

        // Answer 수신 처리
        this.socketManager.on('SCREEN_ANSWER', async (data) => {
            const { from, sdp } = data;
            const peerConnection = this.peerConnections.get(from);

            if (peerConnection) {
                await peerConnection.setRemoteDescription(new RTCSessionDescription(sdp));
                console.log('Answer set successfully for:', from);
            }
        });

        // ICE Candidate 수신 처리
        this.socketManager.on('ICE_CANDIDATE', async (data) => {
            const { from, candidate } = data;
            const peerConnection = this.peerConnections.get(from);

            if (peerConnection) {
                await peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
                console.log('ICE Candidate added for:', from);
            }
        });
    }

    createPeerConnection(participantId) {
        const peerConnection = new RTCPeerConnection({
            iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
        });

        peerConnection.onicecandidate = event => {
            if (event.candidate) {
                this.socketManager.send('ICE_CANDIDATE', {
                    to: participantId,
                    candidate: event.candidate
                });
            }
        };

        peerConnection.ontrack = event => {
            console.log('Received remote track from:', participantId);
            const videoElement = document.getElementById(`video-${participantId}`);
            if (videoElement && !videoElement.srcObject) {
                videoElement.srcObject = event.streams[0];
            }
        };

        this.peerConnections.set(participantId, peerConnection);
        return peerConnection;
    }

    async startScreenShare() {
        try {
            this.localStream = await navigator.mediaDevices.getDisplayMedia({ video: true });
            console.log('Local stream created:', this.localStream);
            this.localStream.getTracks().forEach(track => {
                this.peerConnections.forEach(pc => pc.addTrack(track, this.localStream));
            });
        } catch (error) {
            console.error('Error starting screen share:', error);
        }
    }
}
