class ScreenShare {
    constructor() {
        this.peerConnections = new Map();
        this.isSharing = false;
        this.stream = null;
        this.initializeButtons();
    }

    initializeButtons() {
        const shareButton = document.getElementById('shareScreen');
        const stopButton = document.getElementById('stopShare');

        shareButton.addEventListener('click', () => this.startSharing());
        stopButton.addEventListener('click', () => this.stopSharing());
    }

    async startSharing() {
        try {
            this.stream = await navigator.mediaDevices.getDisplayMedia({ video: true });
            console.log('Screen share stream created:', this.stream);


            const videoElement = document.getElementById('screenVideo');
            videoElement.srcObject = this.stream;


            websocket.send('SCREEN_SHARE_START', { roomId: ROOM_DATA.roomId });
            for (const [id, participant] of participantManager.participants) {

                if (id !== ROOM_DATA.myParticipantId) {
                    const pc = await this.createPeerConnection(id);

                    // 4) 스트림 트랙 추가
                    this.stream.getTracks().forEach(track => {
                        pc.addTrack(track, this.stream);
                    });

                    // 5) Offer 생성/전송
                    const offer = await pc.createOffer();
                    await pc.setLocalDescription(offer);

                    // 6) SCREEN_OFFER 메시지
                    websocket.send('SCREEN_OFFER', {
                        to: id,
                        sdp: offer
                    });
                    console.log('Sent SCREEN_OFFER to:', id);
                }
            }

            this.stream.getVideoTracks()[0].addEventListener('ended', () => this.stopSharing());
            this.updateButtons(true);

        } catch (error) {
            console.error('Error starting screen sharing:', error);
        }
    }

    stopSharing() {
        if (this.stream) {
            this.stream.getTracks().forEach(track => track.stop());
            this.stream = null;
            document.getElementById('screenVideo').srcObject = null;

            websocket.send('SCREEN_SHARE_STOP', { roomId: ROOM_DATA.roomId });
            this.updateButtons(false);
        }
    }

    updateButtons(isSharing) {
        document.getElementById('shareScreen').classList.toggle('d-none', isSharing);
        document.getElementById('stopShare').classList.toggle('d-none', !isSharing);
    }

    async handleOffer(from, sdp) {
        let peerConnection = this.peerConnections.get(from);
        if (!peerConnection) {
            peerConnection = this.createPeerConnection(from);
        }
        try {
            console.log('Setting remote description for offer from:', from);
            await peerConnection.setRemoteDescription(new RTCSessionDescription(sdp));

            const answer = await peerConnection.createAnswer();
            await peerConnection.setLocalDescription(answer);

            // Answer 송신
            websocket.send('SCREEN_ANSWER', {
                to: from,
                sdp: peerConnection.localDescription
            });
            console.log('Answer sent to:', from);
        } catch (error) {
            console.error('Error handling offer:', error);
        }
    }

    handleAnswer(from, sdp) {
        const peerConnection = this.peerConnections.get(from);
        if (!peerConnection) {
            console.warn('PeerConnection not found for:', from);
            return;
        }
        try {
            console.log('Setting remote description for answer from:', from);
            peerConnection.setRemoteDescription(new RTCSessionDescription(sdp));
        } catch (error) {
            console.error('Error setting remote description for answer:', error);
        }
    }

    async handleIceCandidate(from, candidate) {
        const peerConnection = this.peerConnections.get(from);
        if (!peerConnection) {
            console.warn('PeerConnection not found for:', from);
            return;
        }
        try {
            console.log('Adding ICE Candidate for:', from);
            await peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
        } catch (error) {
            console.error('Error adding ICE Candidate:', error);
        }
    }

    createPeerConnection(participantId) {
        if (this.peerConnections.has(participantId)) {
            return this.peerConnections.get(participantId);
        }
        const peerConnection = new RTCPeerConnection({
            iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
        });

        peerConnection.onicecandidate = event => {
            if (event.candidate) {
                console.log('Generated ICE Candidate for:', participantId);
                websocket.send('ICE_CANDIDATE', {
                    to: participantId,
                    from: ROOM_DATA.myParticipantId,
                    candidate: event.candidate
                });
            }
        };

        peerConnection.ontrack = event => {
            console.log('Received remote track from:', participantId);

            let videoElement = document.getElementById(`video-${participantId}`);
            if (!videoElement) {
                videoElement = document.createElement('video');
                videoElement.id = `video-${participantId}`;
                videoElement.autoplay = true;
                videoElement.playsInline = true;
                document.getElementById('videoGrid').appendChild(videoElement);
            }
            videoElement.srcObject = event.streams[0];
        };

        this.peerConnections.set(participantId, peerConnection);
        return peerConnection;
    }
}
