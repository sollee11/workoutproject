import HttpClient from './module/HttpClient.js';
import UIManager from './module/UIManager.js';

const httpClient = new HttpClient();
const uiManager = new UIManager();

function saveCurrentPage(page) {
    localStorage.setItem('qnaCurrentPage', page.toString());
}

function loadCurrentPage() {
    const savedPage = localStorage.getItem('qnaCurrentPage');
    return savedPage ? parseInt(savedPage) : 1;
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);

    const year = date.getFullYear(); // 연도
    const month = date.getMonth() + 1; // 월
    const day = date.getDate(); // 일

    return `${year}년 ${month}월 ${day}일`;
}

export async function getQnaList(page = loadCurrentPage(), size = 10) {
    try {
        saveCurrentPage(page);

        const response = await httpClient.get(`/qna/api/list?page=${page}&size=${size}`);
        console.log('Fetched QnA List:', response);

        const qnaList = response.content;
        const totalPages = response.totalPages;
        const currentPage = response.number + 1;

        const tbody = document.getElementById('qnaListBody');
        if (!tbody) {
            console.error('qnaListBody 요소를 찾을 수 없습니다.');
            return;
        }

        // QnA 목록을 테이블에 렌더링
        tbody.innerHTML = qnaList.map(qna => `
            <tr>
                <td>${qna.qno}</td>
                <td><a href="/qna/view/${qna.qno}">${qna.title}</a></td>
                <td>${qna.writer}</td>
                <td>${formatDate(qna.regDate)}</td>
                <td>
                    <span class="status-badge ${qna.completed ? 'status-completed' : 'status-pending'}">
                        ${qna.completed ? '완료' : '진행중'}
                    </span>
                </td>
            </tr>
        `).join('');

        // 페이지네이션 렌더링 시에도 저장된 페이지 정보 활용
        uiManager.renderPagination(totalPages, currentPage, getQnaList);

    } catch (error) {
        console.error('QnA 목록을 가져오는 중 오류 발생:', error);
        return null;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // 저장된 페이지 번호로 시작
    const savedPage = loadCurrentPage();
    getQnaList(savedPage);
});