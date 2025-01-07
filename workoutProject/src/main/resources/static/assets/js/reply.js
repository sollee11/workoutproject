
async function get1(bno){
    const result = await axios.get(`/replies/list/${bno}`)
    return result
}



// 댓글 목록
async function getList({bno, page, size, goLast}) {
    const response = await axios.get(`/replies/list/${bno}`, {params: {page, size}});
    return response.data;
}

// 댓글 등록
async function addReply(replyObj) {
    const response = await axios.post(`/replies/`, replyObj);
    return response.data;
}

async function getReply(rno){
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj){
    const response = await axios.put(`/replies/${replyObj.rno}`,replyObj)
    return response.data
}

// 댓글 삭제
async function removeReply(rno) {
    const response = await axios.delete(`/replies/${rno}`);
    return response.data;
}





