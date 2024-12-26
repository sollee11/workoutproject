
export default class HttpClient {
    constructor() {

        this.token = document.querySelector("meta[name='_csrf']")?.content;
        this.header = document.querySelector("meta[name='_csrf_header']")?.content;
    }


    getHeaders(isMultipart = false) {
        const headers = {};
        if (!isMultipart) {
            headers['Content-Type'] = 'application/json';
            headers['Accept'] = 'application/json';
        }
        if (this.token && this.header) {
            headers[this.header] = this.token;
        }
        return headers;
    }


    async get(url) {
        const response = await fetch (url, {
            headers: this.getHeaders()
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
        // const response = await axios.get(url);
        // return response.data;
    }


    async post(url, data, isMultipart = false) {
        const headers = this.getHeaders(isMultipart);
        const body = isMultipart ? data : JSON.stringify(data);

        const response = await fetch(url, {
            method: 'POST',
            headers,
            body
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    }

    async patch(url, data) {
        const response = await fetch(url, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    }

    async delete(url) {
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }


            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                return await response.json();
            }

            return null;
        } catch (error) {

            throw error;
        }
    }


}