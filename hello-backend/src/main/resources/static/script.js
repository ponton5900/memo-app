const memoList = document.getElementById('memo-list');
const addBtn = document.getElementById('addBtn');

// CSRFトークンをCookieから取得
function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
}

// メモ一覧を取得して描画
function fetchMemos() {
    fetch('/memos', { credentials: 'include' })
        .then(res => res.json())
        .then(data => {
            memoList.innerHTML = '';
            data.forEach((memo, index) => {
                const div = document.createElement('div');
                div.textContent = `${memo.title} : ${memo.content}`;

                // 削除ボタン
                const delBtn = document.createElement('button');
                delBtn.textContent = '削除';
                delBtn.onclick = () => sendRequest('DELETE', `/memos/${index}`, null);

                // 編集ボタン
                const editBtn = document.createElement('button');
                editBtn.textContent = '編集';
                editBtn.onclick = () => renderEdit(div, memo, index);

                div.appendChild(delBtn);
                div.appendChild(editBtn);
                memoList.appendChild(div);
            });
        });
}

// POST/PUT/DELETE を送る共通関数
function sendRequest(method, url, body) {
    const token = getCsrfToken();
    if (!token) {
        console.error('CSRF token not found!');
        return;
    }

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': token
        },
        credentials: 'include',
        body: body ? JSON.stringify(body) : null
    })
    .then(res => {
        if (!res.ok) console.error('Request failed', res.status);
        return res.json();
    })
    .then(() => fetchMemos());
}

// 編集用UIレンダリング
function renderEdit(div, memo, index) {
    div.innerHTML = '';
    const titleInput = document.createElement('input');
    titleInput.value = memo.title;
    const contentInput = document.createElement('input');
    contentInput.value = memo.content;
    const saveBtn = document.createElement('button');
    saveBtn.textContent = '保存';
    saveBtn.onclick = () => sendRequest('PUT', `/memos/${index}`, {
        title: titleInput.value,
        content: contentInput.value
    });
    div.appendChild(titleInput);
    div.appendChild(contentInput);
    div.appendChild(saveBtn);
}

// 追加ボタン
addBtn.addEventListener('click', () => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    sendRequest('POST', '/memos', { title, content });
});

// ページロード時にメモ取得
window.addEventListener('load', fetchMemos);
