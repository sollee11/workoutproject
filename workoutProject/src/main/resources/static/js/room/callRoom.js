(function(){
    const roomId = window.ROOM_ID || 0;

    let socket = null;
    let peerConnection = null;
    let localStream = null;

    document.addEventListener('DOMContentLoaded', onPageLoad);


    function onPageLoad() {
        console.log('call-room page load, roomId=', roomId);
        initWebSocket();
        document.getElementById('startCallBtn').addEventListener('click', startCall);
    }

    function initWebSocket() {
        const wsUrl = `wss://${location.host}/ws/room/${roomId}`;
        socket = new WebSocket(wsUrl);

        socket.onopen = () => {
            console.log('WebSocket 연결 성공');
        };

        socket.onmessage = (event) => {
            console.log('Received message:', event.data);
            const msg = JSON.parse(event.data);
            handleSignalMessage(msg);
        };

        socket.onclose = () => console.log('WebSocket 연결 종료');
        socket.onerror = (err) => console.error('WebSocket 에러:', err);
    }

    function handleSignalMessage(msg) {
        switch(msg.type) {
            case 'OFFER':
                console.log('Offer received');
                handleOffer(msg.data.sdp);
                break;
            case 'ANSWER':
                console.log('Answer received');
                handleAnswer(msg.data.sdp);
                break;
            case 'ICE_CANDIDATE':
                console.log('ICE candidate:', msg.data);
                handleCandidate(msg.data.candidate);
                break;
            default:
                console.log('Etc message:', msg);
        }
    }

    // 콜 시작(Offer 생성)
    async function startCall() {
        try {
            localStream = await navigator.mediaDevices.getUserMedia({ video:true, audio:true });
            document.getElementById('localVideo').srcObject = localStream;

            createPeerConnection();

            // 로컬 트랙 추가
            localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

            // Offer 생성
            const offer = await peerConnection.createOffer();
            await peerConnection.setLocalDescription(offer);

            // 서버에 Offer 전송
            socket.send(JSON.stringify({
                type: "OFFER",
                data: { sdp: offer }
            }));
        } catch(e) {
            console.error('startCall error:', e);
            alert(e.message);
        }
    }

    function createPeerConnection() {
        peerConnection = new RTCPeerConnection({
            iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
        });

        peerConnection.onicecandidate = (event) => {
            if (event.candidate) {
                socket.send(JSON.stringify({
                    type: "ICE_CANDIDATE",
                    data: { candidate: event.candidate }
                }));
            }
        };

        peerConnection.ontrack = (event) => {
            console.log('Received remote track:', event.streams[0]);
            document.getElementById('remoteVideo').srcObject = event.streams[0];
        };
    }


    async function handleOffer(offer) {
        try {
            if (!peerConnection) {
                createPeerConnection(); // `peerConnection` 생성 함수
            }
            await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
            const answer = await peerConnection.createAnswer();
            await peerConnection.setLocalDescription(answer);

            // ANSWER 메시지 전송
            socket.send(JSON.stringify({
                type: 'ANSWER',
                data: { sdp: peerConnection.localDescription }
            }));
            console.log('Answer sent');
        } catch (error) {
            console.error('handleOffer error:', error);
        }
    }
    // Answer 수신
    async function handleAnswer(remoteAnswer) {
        if (!peerConnection) {
            console.warn('No peerConnection while handleAnswer');
            return;
        }
        try {
            await peerConnection.setRemoteDescription(new RTCSessionDescription(remoteAnswer));
            console.log('Remote answer set');
        } catch(e) {
            console.error('handleAnswer error:', e);
        }
    }

    // ICE Candidate 수신
    function handleCandidate(candidate) {
        if (!peerConnection) {
            console.warn('No peerConnection while handleCandidate');
            return;
        }
        peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
            .then(() => console.log('ICE candidate added'))
            .catch((err) => console.error('Error adding ICE candidate:', err));
    }
})();
