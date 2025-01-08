export class ScreenUI {
    constructor(videoElementId) {
        this.videoElement = document.getElementById(videoElementId);
    }

    displayStream(stream) {
        this.videoElement.srcObject = stream;
    }

    clearStream() {
        this.videoElement.srcObject = null;
    }

    updateControls(isSharing) {
        document.getElementById('shareScreen').classList.toggle('d-none', isSharing);
        document.getElementById('stopShare').classList.toggle('d-none', !isSharing);
    }
}