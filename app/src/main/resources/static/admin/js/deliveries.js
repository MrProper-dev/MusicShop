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

const pagesLinks = document.querySelectorAll('.pagination-list a');
const currentUrl = new URL(window.location.href);
const fromInput = document.querySelector('input[name="from"]');
const toInput = document.querySelector('input[name="to"]');

fillFields();

pagesLinks.forEach(link => {
    link.addEventListener('click', function(event){
        event.preventDefault();
        const pageNumber = link.textContent - 1;
        const urlParams = currentUrl.searchParams;
        urlParams.set('page', pageNumber);
        const newUrl = currentUrl.pathname + '?' + urlParams.toString();
        window.location.href = newUrl;
    })
});

fromInput.addEventListener('change', function(){
    location.href = `/admin/deliveries?from=${fromInput.value}&to=${toInput.value}`
});

toInput.addEventListener('change', function(){
    location.href = `/admin/deliveries?from=${fromInput.value}&to=${toInput.value}`
});

function fillFields(){
    const urlParams = currentUrl.searchParams;
    const from = urlParams.get('from');
    const to = urlParams.get('to');
    if(from){
        fromInput.value = from;
    }
    if(to){
        toInput.value = to;
    }
}

const products = document.querySelectorAll('.product-list-item');
products.forEach(product => {
    const productId = product.dataset.productId;
    const delBtn = product.querySelector('.btn-delete-product');
    delBtn.addEventListener('click', async function(){
        const response = await fetch(`/api/v1/deliveries/product/${productId}`, {
            method : 'DELETE'
        });
        if(response.ok) {
            showToast('Товар удален из поставки');
            product.remove();
        }else{
            showToast(`Ошибка сервера: ${response.status}`)
        }
    });
    const quantityInput = product.querySelector('.quantity-input');
    const lessBtn = product.querySelector('.quantity-btn.less');
    const moreBtn = product.querySelector('.quantity-btn.more');
    let timeoutUpdate;
    lessBtn.addEventListener('click', function(event){
        quantityInput.value--;
        quantityInput.dispatchEvent(new Event('input'));
        updateTotalQuantity(quantityInput.value);
    });
    moreBtn.addEventListener('click', function(event){
        quantityInput.value++;
        quantityInput.dispatchEvent(new Event('input'));
        updateTotalQuantity(quantityInput.value);
    });
    quantityInput.addEventListener('focus', function() {
        this.select();
    });
    quantityInput.addEventListener('change', function(){
        updateTotalQuantity(quantityInput.value);
    });
    quantityInput.addEventListener('input', async function(event) {
        clearTimeout(timeoutUpdate);
        if(!/^\d+$/.test(quantityInput.value) || quantityInput.value <= 0){
            quantity.value = 1;
        }
        timeoutUpdate = setTimeout(() => {
            fetch(`/api/v1/deliveries/product/${productId}`, {
                method : 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body : JSON.stringify({
                    quantity : quantityInput.value
                })
            });
        }, 700);
    });
});

const totalQuantity = document.querySelector('.supply-total');
function updateTotalQuantity(){
    const quantities = document.querySelectorAll('.quantity-input');
    let count = 0;
    quantities.forEach(quantity => {
        count += parseInt(quantity.value);
    });
    totalQuantity.textContent = `Всего товаров: ${count} шт.`
}

const actualDelivery = document.querySelector('.create-supply-form');
const confirmDeliveryBtn = document.querySelector('.btn-submit');
if(confirmDeliveryBtn)
confirmDeliveryBtn.addEventListener('click', async function(){
    if(!await confirm('Вы точно хотите завершить прием поставки?')) return;
    const response = await fetch('/api/v1/deliveries/issue', {
        method : 'POST'
    });
    if(response.ok) {
        showToast('Поставка принята, данные о количестве товаров обновлены');
        actualDelivery.remove();
    }else if(response.status == 409){
        showToast('Поставка должна содержать хотя бы один товар');
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
});

const deliverierInput = document.querySelector('.deliverier');
let timeoutUpdateDeliverier; 
if(deliverierInput)
deliverierInput.addEventListener('input', async function(){
    clearTimeout(timeoutUpdateDeliverier);
    timeoutUpdateDeliverier = setTimeout(() => {
        fetch('/api/v1/deliveries/deliverier', {
            method : 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body : JSON.stringify({
                deliverier : deliverierInput.value
            })
        });
    }, 700)
});

