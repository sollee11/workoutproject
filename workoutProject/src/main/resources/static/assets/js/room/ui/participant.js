
export class ParticipantUI {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
    }

    addParticipant(participant) {
        const element = this.createParticipantElement(participant);
        this.container.appendChild(element);
    }

    removeParticipant(participantId) {
        const element = document.getElementById(`participant-${participantId}`);
        element?.remove();
    }

    private createParticipantElement(participant) {
        // 참가자 UI 요소 생성 로직
    }
}