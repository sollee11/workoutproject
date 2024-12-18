async function checkId(mid){
    const result = await axios.get(`/check/${mid}`);
    return result.data;
}