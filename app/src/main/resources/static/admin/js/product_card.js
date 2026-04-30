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

let picDelIds = [];
const mainImgDiv = document.querySelector('.primary-image');
let pics = Array.from(document.querySelectorAll('.thumb-img'));

function deletePic(picDiv, pic){
    const mainImg = mainImgDiv.querySelector('.cross-img-main');
    const picId = picDiv.dataset.picId;
    picDelIds.push(picId);
    const index = pics.indexOf(pic);
    pics.splice(index, 1);
    if(pics.length == 0){
        mainImg.remove();
    }else if(index == 0){
        let inputPic = pics[0].closest('.thumb-item').querySelector('input');
        if(!inputPic){
            inputPic = document.createElement('input');
            inputPic.type = 'hiden';
            inputPic.style.display = 'none';
            inputPic.setAttribute('form', 'productEditForm');
            inputPic.setAttribute('value', pics[0].closest('.thumb-item').dataset.picId);
            pics[0].before(inputPic);
            inputPic.setAttribute('name', 'main-picture-id');
        }else{
            inputPic.setAttribute('name', 'main-picture');
        }
    }
    if(mainImg.src == pic.src){
        mainImg.src = '';
    }
    picDiv.remove();
}

pics.forEach(pic => {
    pic.addEventListener('click', function(){
        mainImgDiv.querySelector('.cross-img-main').src = pic.src;
    });
    const picDiv = pic.closest('.thumb-item');
    const delBtn = picDiv.querySelector('.delete-btn');
    delBtn.addEventListener('click', () => deletePic(picDiv, pic));
});

let addInput = document.querySelector('.input-img');
addInput.onchange = addPicture;
function addPicture(){
    const file = addInput.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            let mainImg = mainImgDiv.querySelector('.cross-img-main');
            if(mainImg == null){
                mainImg = document.createElement('img');
                mainImg.setAttribute('class', 'cross-img-main');
                mainImg.src = reader.result;
                mainImgDiv.append(mainImg);
                addInput.setAttribute('name', 'main-picture');
            }
            const imgDiv = document.createElement('div');
            imgDiv.setAttribute('class', 'thumb-item');
            const img = document.createElement('img');
            img.setAttribute('class', 'thumb-img');
            img.addEventListener('click', () => {
                mainImgDiv.querySelector('.cross-img-main').src = img.src;
            });
            img.src = reader.result;
            imgDiv.append(img);
            const deleteBtn = document.createElement('button');
            deleteBtn.setAttribute('class', 'delete-btn');
            deleteBtn.textContent = 'x';
            deleteBtn.addEventListener('click', () => deletePic(imgDiv, img));
            imgDiv.append(deleteBtn);
            pics.push(img);
            addInput.closest('.thumb-item').before(imgDiv);
            addInput.setAttribute('form', 'productEditForm');
            addInput.classList.remove('input-img');
            const newAddInput = document.createElement('input');
            addInput.before(newAddInput);
            addInput.remove();
            imgDiv.append(addInput);
            newAddInput.type = 'file';
            newAddInput.setAttribute('accept', 'image/*');
            newAddInput.setAttribute('name', 'picture');
            newAddInput.style.display = 'none';
            newAddInput.onchange = addPicture;
            addInput = newAddInput;
        }
        reader.readAsDataURL(file);
    }
}

const productEditForm = document.getElementById('productEditForm');
const productId = productEditForm.dataset.productId;
productEditForm.addEventListener('submit', async function(event){
    event.preventDefault();
    const deleteResponse = await fetch('/api/v1/pictures',{
        method : 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body : JSON.stringify(picDelIds)
    });
    if(!deleteResponse.ok){
        showToast(`Не удалось удалить картинки, ошибка сервера: ${deleteResponse.status}`);
        return;
    }
    const formData = new FormData(productEditForm);
    const putResponse = await fetch(`/api/v1/products/${productId}`, {
        method : 'PUT',
        body : formData
    });
    if(putResponse.ok){
        showToast('Даныне товара обновлены');
        const list = await putResponse.json(); 
        updatePicutures(list)
    }else if(putResponse.status == 400){
        showToast('Все поля должны быть заполнены');
    }else {
        showToast(`Ошибка сервера: ${putResponse.status}`);
    }
});

function updatePicutures(pictureList){
    let inner = '';
    pictureList.forEach(p => {
        inner += `
            <div class="thumb-item" data-pic-id="${p.id}">
                <img class="thumb-img" src="/pictures/${p.path}">
                <button class="delete-btn">x</button>
            </div>
        `;
    });
    inner += `
        <div class="thumb-item">
            <label class="upload-label">
                <input type="file" name="picture" style="display: none;" class="input-img" accept="image/*">
                <img class="img-cross" src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%23f9f5ef'/%3E%3Cline x1='50' y1='30' x2='50' y2='70' stroke='%238c7a67' stroke-width='6' stroke-linecap='round'/%3E%3Cline x1='30' y1='50' x2='70' y2='50' stroke='%238c7a67' stroke-width='6' stroke-linecap='round'/%3E%3C/svg%3E">
            </label>
        </div>
    `;
    document.querySelector('.thumb-row').innerHTML = inner;
    picDelIds = [];
    pics = Array.from(document.querySelectorAll('.thumb-img'));
    addInput = document.querySelector('.input-img');
    addInput.onchange = addPicture;
    pics.forEach(pic => {
        pic.addEventListener('click', function(){
            mainImgDiv.querySelector('.cross-img-main').src = pic.src;
        });
        const picDiv = pic.closest('.thumb-item');
        const delBtn = picDiv.querySelector('.delete-btn');
        delBtn.addEventListener('click', () => deletePic(picDiv, pic));
    });
}