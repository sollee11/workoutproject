export default class RegisterManager {
    constructor(httpClient, uiManager) {
        this.httpClient = httpClient;
        this.uiManager = uiManager;
        this.currentUser = null;
        this.selectedFiles = new Map();

        this.initializeElements();
        this.setupEventListeners();
        this.loadCurrentUser();
    }

    initializeElements() {
        this.dropZone = document.getElementById('dropZone');
        this.fileInput = document.getElementById('imageFiles');
        this.titleInput = document.getElementById('title');
        this.contentInput = document.getElementById('content');
        this.imagePreview = document.getElementById('imagePreview');
    }

    setupEventListeners() {

        this.dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            this.dropZone.style.borderColor = '#666';
        });

        this.dropZone.addEventListener('dragleave', (e) => {
            e.preventDefault();
            this.dropZone.style.borderColor = '#ccc';
        });

        this.dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            this.dropZone.style.borderColor = '#ccc';
            const files = Array.from(e.dataTransfer.files);
            this.addNewFiles(files);
        });

        this.fileInput.addEventListener('click', (e) => {
            e.stopPropagation();
        });

        this.fileInput.addEventListener('change', (event) => {
            const files = Array.from(event.target.files);
            this.addNewFiles(files);
        });

        this.dropZone.addEventListener('click', () => {
            this.triggerFileInput();
        });


        document.getElementById('submitButton').addEventListener('click', () => {
            this.submitQna();
        });

        document.getElementById('listButton').addEventListener('click', () => {
            location.href = '/qna/list';
        });

        this.imagePreview.addEventListener('click', (e) => {
            if (e.target.closest('.remove-image')) {
                const fileId = e.target.closest('.remove-image').dataset.fileId;
                this.removeImage(fileId);
            }
        });
    }

    async loadCurrentUser() {
        try {
            this.currentUser = await this.httpClient.get('/qna/api/user/current');
            document.getElementById('userInfo').textContent = this.currentUser
                ? `사용자: ${this.currentUser.username}`
                : "로그인 필요";
        } catch (error) {
            this.uiManager.showError('사용자 정보를 불러오는데 실패했습니다.');
        }
    }

    triggerFileInput() {
        this.fileInput.click();
    }

    addNewFiles(files) {
        files.forEach(file => {
            if (file.type.startsWith('image/')) {
                const fileId = Date.now() + '-' + Math.random().toString(36).substr(2, 9);
                this.selectedFiles.set(fileId, file);
                this.displayImagePreview(file, fileId);
            }
        });
    }

    displayImagePreview(file, fileId) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const previewContainer = document.getElementById('imagePreview');
            const previewItem = document.createElement('div');
            previewItem.className = 'image-preview-item';
            previewItem.setAttribute('data-file-id', fileId);

            previewItem.innerHTML = `
                <img src="${e.target.result}" alt="Preview">
                <button class="remove-image" data-file-id="${fileId}">&times;</button>
            `;

            previewContainer.appendChild(previewItem);
        };
        reader.readAsDataURL(file);
    }

    removeImage(fileId) {
        this.selectedFiles.delete(fileId);
        const previewItem = document.querySelector(`[data-file-id="${fileId}"]`);
        if (previewItem) {
            previewItem.remove();
        }
    }

    async submitQna() {
        const title = this.titleInput.value.trim();
        const content = this.contentInput.value.trim();

        if (!this.currentUser || !this.currentUser.username) {
            this.uiManager.showError('로그인한 사용자 정보를 확인할 수 없습니다.');
            return;
        }

        if (!title || !content) {
            this.uiManager.showError('모든 필드를 입력해주세요.');
            return;
        }

        const formData = new FormData();
        formData.append("writer", this.currentUser.username);
        formData.append("title", title);
        formData.append("questionText", content);

        this.selectedFiles.forEach(file => {
            formData.append("imageFiles", file);
        });

        try {
            await this.httpClient.post('/qna/api/register', formData, true);
            location.href = '/qna/list';
        } catch (error) {
            this.uiManager.showError(error.message);
        }
    }
}