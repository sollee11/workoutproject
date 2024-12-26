
export default class UIManager {
    constructor() {
        this.actionsContainer = document.getElementById('postActions');
        this.getQnaList = document.getElementById('getQnaList');
        this.paginationContainer = document.getElementById('pagination'); // 페이징 컨테이너 초기화
    }

    triggerReflow(element) {
        element.style.display = 'none';
        element.offsetHeight;
        element.style.display = '';
    }

    showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        if (!errorDiv) return;
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 3000);
    }

    showSuccess(message) {
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = message;
        document.body.appendChild(successDiv);

        setTimeout(() => {
            successDiv.style.opacity = '0';
            setTimeout(() => document.body.removeChild(successDiv), 500);
        }, 2000);
    }

    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        }).format(date);
    }

    createButton(text, className, onClick) {
        const button = document.createElement('button');
        button.textContent = text;
        button.className = className;
        button.onclick = onClick;
        return button;
    }

    updateActionButtons(postData, isAdmin, currentUser, postWriter, toggleCallback, deleteCallback) {
        if (!this.actionsContainer) {
            console.error('actionsContainer not found');
            return;
        }

        this.actionsContainer.innerHTML = '';

        if (isAdmin || currentUser === postWriter && !postData.completed) {
            const toggleButton = this.createButton(
                postData.completed ? '진행중으로 변경' : '완료 처리',
                `button primary-button ${postData.completed ? 'status-pending' : 'status-completed'}`,
                () => toggleCallback()
            );

            this.actionsContainer.appendChild(toggleButton);
        }

        if (isAdmin || (currentUser === postWriter && !postData.completed)) {
            const deleteButton = this.createButton('삭제', 'button delete-button', () => {
                if (confirm('정말 삭제하시겠습니까?')) {
                    deleteCallback();
                }
            });

            this.actionsContainer.appendChild(deleteButton);
        }

        this.triggerReflow(this.actionsContainer);
    }

    renderPagination(totalPages, currentPage, getQnaListCallback) {
        if (!this.paginationContainer) {
            console.error('Pagination 요소를 찾을 수 없습니다.');
            return;
        }

        this.paginationContainer.innerHTML = '';

        for (let page = 1; page <= totalPages; page++) {
            const button = document.createElement('button');
            button.textContent = page;
            button.className = page === currentPage ? 'active' : '';
            button.addEventListener('click', () => {
                if (typeof getQnaListCallback === 'function')
                    getQnaListCallback(page);
                else {
                    console.error('getQnaListCallback is not a function');
                }
            });

            this.paginationContainer.appendChild(button);
        }
    }
}
