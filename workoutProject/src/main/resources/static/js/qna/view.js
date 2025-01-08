import HttpClient from './module/HttpClient.js';
import UIManager from './module/UIManager.js';
import ReplyManager from './module/ReplyManager.js';
import PostManager from './module/PostManager.js';

class QnaView {
    constructor() {
        this.qno = window.location.pathname.split('/').pop();
        this.uiManager = new UIManager();
        this.httpClient = new HttpClient();

        this.replyManager = new ReplyManager(this.qno, this.httpClient, this.uiManager);
        this.postManager = new PostManager(this.qno, this.httpClient, this.uiManager, this.replyManager);
        this.initializeEventListeners();

    }


    initializeEventListeners() {
        const submitButton = document.querySelector('.reply-form button');
        if (submitButton) {
            submitButton.addEventListener('click', () => this.handleReplySubmit());
        }
    }

    async handleReplySubmit() {
        const replyText = document.getElementById('replyText').value;
        await this.replyManager.submitReply(replyText);
    }

    async initialize() {

        await this.postManager.loadPostData();

    }


}


window.addEventListener('DOMContentLoaded', () => {
    const qnaView = new QnaView();
    qnaView.initialize()
        .then(() => {
            console.log("초기화 완료");
        })
        .catch((error) => {
            console.error("초기화 중 오류 발생:", error);
        });
});
// window.addEventListener('DOMContentLoaded', async () => {
//     try {
//         const qnaView = new QnaView();
//         await qnaView.initialize();
//     } catch (error) {
//         console.error("초기화 중 오류 발생:", error);
//     }
// });
