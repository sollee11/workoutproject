export const WebRTCHelper = {
    // 브라우저의 WebRTC 지원 여부 확인
    checkWebRTCSupport() {
        return !!(navigator.mediaDevices && navigator.mediaDevices.getDisplayMedia);
    },

    // 미디어 디바이스 권한 체크
    async checkPermissions() {
        try {
            await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
            return true;
        } catch (e) {
            return false;
        }
    },

    // WebRTC 연결 상태 문자열 변환
    getConnectionStateText(state) {
        const states = {
            'new': '연결 준비 중',
            'connecting': '연결 중',
            'connected': '연결됨',
            'disconnected': '연결 끊김',
            'failed': '연결 실패',
            'closed': '연결 종료'
        };
        return states[state] || state;
    }
};

// utils/message-formatter.js
export const MessageFormatter = {
    // 시간 포맷팅 (로그나 채팅에서 사용)
    formatTime(date) {
        return new Intl.DateTimeFormat('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
        }).format(date);
    }
};