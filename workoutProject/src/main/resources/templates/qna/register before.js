// <script>
//     const token = document.querySelector("meta[name='_csrf']")?.content;
//     const header = document.querySelector("meta[name='_csrf_header']")?.content;
//     let currentUser = null;
//     let selectedFiles = new Map(); // 파일 관리를 위한 Map 객체
//
//     function getHeaders() {
//     const headers = {};
//     if (token && header) {
//     headers[header] = token;
// }
//     return headers;
// }
//
//     function showError(message) {
//     const errorDiv = document.getElementById('errorMessage');
//     errorDiv.textContent = message;
//     errorDiv.style.display = 'block';
//     setTimeout(() => {
//     errorDiv.style.display = 'none';
// }, 3000);
// }
//
//     async function loadCurrentUser() {
//     try {
//     const response = await fetch('/qna/api/user/current', {
//     headers: getHeaders()
// });
//     if (response.ok) {
//     currentUser = await response.json();
//     document.getElementById('userInfo').textContent = currentUser
//     ? `사용자: ${currentUser.username}`
//     : "로그인 필요";
// } else {
//     document.getElementById('userInfo').textContent = "로그인 필요";
// }
// } catch (error) {
//     showError('사용자 정보를 불러오는데 실패했습니다.');
// }
// }
//     function triggerFileInput() {
//     document.getElementById('imageFiles').click();
// }
//
//     // 이벤트 전파 중지를 위한 수정
//     const fileInput = document.getElementById('imageFiles');
//     fileInput.addEventListener('click', (e) => {
//     e.stopPropagation();
// });
//
//     function handleFileSelect(event) {
//     const files = Array.from(event.target.files);
//     addNewFiles(files);
// }
//
//     function addNewFiles(files) {
//     files.forEach(file => {
//         if (file.type.startsWith('image/')) {
//             const fileId = Date.now() + '-' + Math.random().toString(36).substr(2, 9);
//             selectedFiles.set(fileId, file);
//             displayImagePreview(file, fileId);
//         }
//     });
// }
//
//     function displayImagePreview(file, fileId) {
//     const reader = new FileReader();
//     reader.onload = function(e) {
//     const previewContainer = document.getElementById('imagePreview');
//     const previewItem = document.createElement('div');
//     previewItem.className = 'image-preview-item';
//     previewItem.setAttribute('data-file-id', fileId);
//
//     previewItem.innerHTML = `
//                 <img src="${e.target.result}" alt="Preview">
//                 <button class="remove-image" onclick="removeImage('${fileId}')">&times;</button>
//             `;
//
//     previewContainer.appendChild(previewItem);
// };
//     reader.readAsDataURL(file);
// }
//
//     function removeImage(fileId) {
//     selectedFiles.delete(fileId);
//     const previewItem = document.querySelector(`[data-file-id="${fileId}"]`);
//     if (previewItem) {
//     previewItem.remove();
// }
// }
//
//     async function submitQna() {
//     const title = document.getElementById('title').value.trim();
//     const content = document.getElementById('content').value.trim();
//
//     if (!currentUser || !currentUser.username) {
//     showError('로그인한 사용자 정보를 확인할 수 없습니다.');
//     return;
// }
//
//     if (!title || !content) {
//     showError('모든 필드를 입력해주세요.');
//     return;
// }
//
//     const formData = new FormData();
//     formData.append("writer", currentUser.username);
//     formData.append("title", title);
//     formData.append("questionText", content);
//
//     // 선택된 파일들을 FormData에 추가
//     selectedFiles.forEach(file => {
//     formData.append("imageFiles", file);
// });
//
//     try {
//     const response = await fetch('/qna/api/register', {
//     method: 'POST',
//     headers: getHeaders(),
//     body: formData
// });
//
//     if (!response.ok) {
//     const errorText = await response.text();
//     throw new Error(errorText);
// }
//
//     location.href = '/qna/list';
// } catch (error) {
//     showError(error.message);
// }
// }
//
//     // 드래그 앤 드롭 이벤트 처리
//     const dropZone = document.getElementById('dropZone');
//
//     dropZone.addEventListener('dragover', (e) => {
//     e.preventDefault();
//     dropZone.style.borderColor = '#666';
// });
//
//     dropZone.addEventListener('dragleave', (e) => {
//     e.preventDefault();
//     dropZone.style.borderColor = '#ccc';
// });
//
//     dropZone.addEventListener('drop', (e) => {
//     e.preventDefault();
//     dropZone.style.borderColor = '#ccc';
//     const files = Array.from(e.dataTransfer.files);
//     addNewFiles(files);
// });
//
//     window.onload = loadCurrentUser;
// </script>