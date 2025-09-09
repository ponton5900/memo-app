const memoList = document.getElementById('memo-list');
const addBtn = document.getElementById('addBtn');

function getCsrfToken(){
    const match=document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
}

function fetchMemos() {
    fetch('/memos', {
        credentials: 'include'
    })
        .then(res => res.json())
        .then(data => {
            memoList.innerHTML = '';
            data.forEach((memo, index) => {
                const div = document.createElement('div');
                div.textContent = `${memo.title} : ${memo.content}`;
                memoList.appendChild(div);
            });
        });
}

addBtn.addEventListener('click', () => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    fetch('/memos', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': getCsrfToken()
        },
        credentials: 'include',
        body: JSON.stringify({ title, content })
    })
    .then(res => res.json())
    .then(() => fetchMemos());
});

//削除と編集
function fetchMemos(){
    fetch('/memos')
        .then(res=>res.json())
        .then(data=>{
            memoList.innerHTML='';
            data.forEach((memo,index)=>{
                const div=document.createElement('div');
                div.textContent=`${memo.title} : ${memo.content}`;

                const delBtn=document.createElement('button');
                delBtn.textContent='削除';
                delBtn.onclick=()=>{
                    fetch(`/memos/${index}`,{
                        method: 'DELETE',
                        headers:{
                            'X-XSRF-TOKEN': getCsrfToken()
                        },
                        credentials: 'include'//セッションを送信
                    }).then(()=>fetchMemos());
                };

                //編集ボタン
                const editBtn=document.createElement('button');
                editBtn.textContent='編集';
                editBtn.onclick=() =>{
                    div.innerHTML='';//中身リセット

                    const titleInput=document.createElement('input');
                    titleInput.value=memo.title;

                    const contentInput=document.createElement('input');
                    contentInput.value=memo.content;

                    const saveBtn=document.createElement('button');
                    saveBtn.textContent='保存';
                    saveBtn.onclick=()=>{
                        fetch(`/memos/${index}`,{
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json',
                                'X-XSRF-TOKEN': getCsrfToken()
                            },
                            credentials: 'include',//セッションを送信
                            body: JSON.stringify({
                                title: titleInput.value,
                                content:contentInput.value
                        
                            })
                        }).then(()=>fetchMemos());
                    };

                    div.appendChild(titleInput);
                    div.appendChild(contentInput);
                    div.appendChild(saveBtn);
                };

                div.appendChild(delBtn);
                div.appendChild(editBtn);
                memoList.appendChild(div);
            });
        });
}

fetchMemos();
