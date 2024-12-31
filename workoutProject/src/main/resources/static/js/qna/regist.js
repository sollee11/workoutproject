import HttpClient from './module/HttpClient.js';
import UIManager from './module/UIManager.js';
import RegisterManager from './module/RegisterManager.js';

const httpClient = new HttpClient();
const uiManager = new UIManager();
const registerManager = new RegisterManager(httpClient, uiManager);

window.triggerFileInput = () => registerManager.triggerFileInput();
window.removeImage = (fileId) => registerManager.removeImage(fileId);
window.submitQna = () => registerManager.submitQna();

// 페이지 로드 시 현재 사용자 정보 로드
window.onload = () => registerManager.loadCurrentUser();