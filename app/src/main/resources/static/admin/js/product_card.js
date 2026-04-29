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

const mainImgDiv = document.querySelector('.primary-image');
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
            }else{
                const imgDiv = document.createElement('div');
                imgDiv.setAttribute('class', 'thumb-item');
                const img = document.createElement('img');
                img.setAttribute('class', 'thumb-img');
                img.src = reader.result;
                imgDiv.append(img);
                addInput.closest('.thumb-item').before(imgDiv);
            }
            addInput.setAttribute('form', 'productEditForm');
            addInput.classList.remove('input-img');
            const newAddInput = document.createElement('input');
            addInput.before(newAddInput);
            newAddInput.type = 'file';
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
    const formData = new FormData(productEditForm);
    const respose = await fetch(`/api/v1/products/${productId}`, {
        method : 'PUT',
        body : formData
    });
});