export class SocketManager {
    constructor(roomId) {
        this.roomId = roomId;
        this.socket = null;
        this.eventHandlers = new Map();
    }

    connect() {
        this.socket = new WebSocket(`ws://${window.location.host}/ws/room/${this.roomId}`);
        this.setupEventListeners();
    }

    on(event, handler) {
        this.eventHandlers.set(event, handler);
    }

    send(type, data) {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify({ type, data }));
        }
    }

    private setupEventListeners() {
        this.socket.onmessage = (event) => {
            const { type, data } = JSON.parse(event.data);
            const handler = this.eventHandlers.get(type);
            if (handler) handler(data);
        };
    }
}