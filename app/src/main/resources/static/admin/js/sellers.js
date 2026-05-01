const modal = document.getElementById("modal");
const modalOk = modal.querySelector("#modalOk");
const modalCancel = modal.querySelector("#modalCancel");
const modalText  = modal.querySelector("#modalText");
const modalInput = modal.querySelector('#modalInput');

function confirm(question, withInput){
    return new Promise((resolve) => {
        modalText.textContent = question;
        modalInput.style.display = 'none';
        modalInput.value = '';
        if(withInput == true){
            modalInput.style.display = 'inline-block';
        }
        modal.showModal();

        const onConfirm = () => {
            if(withInput == true && modalInput.value == ''){
                modalInput.setAttribute('placeholder', 'Поле должно быть заполнено');
                return;
            }
            modal.close();
            modalOk.removeEventListener("click", onConfirm);
            modalCancel.removeEventListener("click", onCancel);
            if(withInput == true){
                resolve({confirm : true, value : modalInput.value})
            }else{
                resolve(true);
            }
        };

        const onCancel = () => {
            modal.close();
            modalOk.removeEventListener("click", onConfirm);
            modalCancel.removeEventListener("click", onCancel);
            if(withInput == true){
                resolve({confirm : false, value : modalInput.value})
            }else{
                resolve(false);
            }
        };
        modalOk.addEventListener("click", onConfirm);
        modalCancel.addEventListener("click", onCancel);
    });
}

const toast = document.querySelector('.toast-message');
function showToast(info){
    toast.textContent = info;
    toast.style.opacity = 0.95;
    toast.style.visibility = 'visible';
    setTimeout(() => {
        toast.style.opacity = 0;
        setTimeout(() => {
            toast.style.visibility = 'hidden';
        }, 600);
    }, 1500);
}

const createForm = document.querySelector('#create-seller');
createForm.addEventListener('submit', async function(event){
    event.preventDefault();
    const formData = new FormData(createForm);
    const jsonString = JSON.stringify(Object.fromEntries(formData));
    const response = await fetch('/api/v1/sellers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonString
    });
    if(response.ok) {
        showToast('Профиль продавца успешно создан');
        const sellerId = await response.text();
        const sellersTable = document.querySelector('.sellers-list');
        const tr = document.createElement('tr');
        tr.setAttribute('class', 'seller');
        tr.setAttribute('data-sellet-id', sellerId);
        tr.innerHTML = `
            <td>${sellerId}</td>
            <td class="edit-field">
                <input style="display: none;" form="sellerForm${sellerId}" type="text" class="edit-input" name="fullName" value="${formData.get('fullName')}" required>
                <div class="info-value">${formData.get('fullName')}</div>
            </td>
            <td class="edit-field">
                <input style="display: none;" form="sellerForm${sellerId}" type="text" class="edit-input" name="login" value="${formData.get('login')}" required>
                <div class="info-value">${formData.get('login')}</div>
            </td>
            <td class="edit-field">
                <input style="display: none;" form="sellerForm${sellerId}" type="password" class="edit-input" name="password">
                <div class="info-value">••••••••</div>
            </td>
            <td class="edit-field">
                <input style="display: none;" form="sellerForm${sellerId}" type="email" class="edit-input" name="email" value="${formData.get('email')}" required>
                <div class="info-value">${formData.get('email')}</div>
            </td>
            <td class="edit-field">
                <input style="display: none;" form="sellerForm${sellerId}" type="tel" class="edit-input" name="phone" value="${formData.get('phone')}" required>
                <div class="info-value">${formData.get('phone')}</div>
            </td>
            <td>
                <div class="info-value"></div>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-edit">Редактировать</button>
                    <form id="sellerForm${sellerId}" style="display: none;"></form>
                    <button style="display: none;" form="sellerForm${sellerId}" type="submit" class="btn-save">Сохранить</button>
                    <button style="display: none;" class="btn-cancel">Отмена</button>
                    <button class="btn-delete">Удалить</button>
                </div>
            </td>
        `;
        sellersTable.prepend(tr);
        const editFields = tr.querySelectorAll('.edit-field');
        const editBtn = tr.querySelector('.btn-edit');
        const delBtn = tr.querySelector('.btn-delete');
        const saveBtn = tr.querySelector('.btn-save');
        const cancelBtn = tr.querySelector('.btn-cancel');
        const sellerForm = tr.querySelector(`#sellerForm${sellerId}`);
        
        editBtn.addEventListener('click', () => showEdit(editFields, editBtn, delBtn, saveBtn, cancelBtn));
        cancelBtn.addEventListener('click', () => cancelEdit(editFields, editBtn, delBtn, saveBtn, cancelBtn));
        delBtn.addEventListener('click', () => deleteSeller(sellerId, tr));
        sellerForm.addEventListener('submit', (event) => {
            event.preventDefault();
            saveChanges(sellerForm, sellerId, editFields, editBtn, delBtn, saveBtn, cancelBtn);
        });

        createForm.reset();
    }else if(response.status == 409){
        showToast('Продавец с таким логином уже существует');
    }else{
        showToast(`Ошибка сервера: ${response.status}`);
    }
});

const clearBtn = document.querySelector('.btn-cancel-form');
clearBtn.addEventListener('click', function(){
    createForm.reset();
});

const sellersTr = document.querySelectorAll('.seller');
sellersTr.forEach(sellerTr => {
    const sellerId = sellerTr.dataset.sellerId;
    const editFields = sellerTr.querySelectorAll('.edit-field');
    const editBtn = sellerTr.querySelector('.btn-edit');
    const delBtn = sellerTr.querySelector('.btn-delete');
    const saveBtn = sellerTr.querySelector('.btn-save');
    const cancelBtn = sellerTr.querySelector('.btn-cancel');
    const sellerForm = sellerTr.querySelector(`#sellerForm${sellerId}`);
    
    editBtn.addEventListener('click', () => showEdit(editFields, editBtn, delBtn, saveBtn, cancelBtn));

    cancelBtn.addEventListener('click', () => cancelEdit(editFields, editBtn, delBtn, saveBtn, cancelBtn));

    delBtn.addEventListener('click', () => deleteSeller(sellerId, sellerTr));

    sellerForm.addEventListener('submit', (event) => {
        event.preventDefault();
        saveChanges(sellerForm, sellerId, editFields, editBtn, delBtn, saveBtn, cancelBtn);
    });
});

async function saveChanges(sellerForm, sellerId, editFields, editBtn, delBtn, saveBtn, cancelBtn){
    const formData = new FormData(sellerForm);
    const jsonString = JSON.stringify(Object.fromEntries(formData));
    const response = await fetch(`/api/v1/sellers/${sellerId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonString
    });
    if(response.ok) {
        showToast('Профиль продавца успешно обновлен');
        editFields.forEach(field => {
            const sellerInfo = field.querySelector('.info-value');
            const sellerInput = field.querySelector('.edit-input');
            if(sellerInput.name == 'password'){
                sellerInfo.textContent = '••••••••';
                sellerInput.value = '';
            }else{
                sellerInfo.textContent = sellerInput.value;
            }
            sellerInfo.style.visibility = 'visible';
            sellerInput.style.display = 'none';
        });
        editBtn.style.display = 'inline-block';
        delBtn.style.display = 'inline-block';
        saveBtn.style.display = 'none';
        cancelBtn.style.display = 'none';
    }else if(response.status == 409){
        showToast('Продавец с таким логином уже существует');
    }else{
        showToast(`Ошибка сервера: ${response.status}`);
    }
}

async function deleteSeller(sellerId, sellerTr){
    if(!await confirm('Вы точно хотите удалить продавца?')){
    return;
    }
    const response = await fetch(`/api/v1/sellers/${sellerId}`, {
        method : 'DELETE'
    });
    if (response.ok) {
        showToast('Продавец удален');
        sellerTr.remove();
    } else {
        showToast(`Ошибка сервера: ${response.status}`);
    }
}

function cancelEdit(editFields, editBtn, delBtn, saveBtn, cancelBtn){
    editFields.forEach(field => {
        const sellerInfo = field.querySelector('.info-value');
        const sellerInput = field.querySelector('.edit-input');
        sellerInput.value = sellerInfo.textContent;
        sellerInfo.style.visibility = 'visible';
        sellerInput.style.display = 'none';
    });
    editBtn.style.display = 'inline-block';
    delBtn.style.display = 'inline-block';
    saveBtn.style.display = 'none';
    cancelBtn.style.display = 'none';
}

function showEdit(editFields, editBtn, delBtn, saveBtn, cancelBtn){
    editFields.forEach(field => {
        const sellerInfo = field.querySelector('.info-value');
        const sellerInput = field.querySelector('.edit-input');
        sellerInfo.style.visibility = 'hidden';
        sellerInput.style.display = 'inline-block';
    });
    editBtn.style.display = 'none';
    delBtn.style.display = 'none';
    saveBtn.style.display = 'inline-block';
    cancelBtn.style.display = 'inline-block';
}
