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

const lessBtn = document.getElementById('less');
const moreBtn = document.getElementById('more');
const quantity = document.getElementById('quantity');

quantity.addEventListener('input', function(event) {
    if(!/^\d+$/.test(quantity.value) || quantity.value <= 0){
        quantity.value = 1;
    }
});

quantity.addEventListener('focus', function() {
    this.select();
});

lessBtn.addEventListener('click', function(event) {
    if(quantity.value > 1){
        quantity.value--;
    }
});

moreBtn.addEventListener('click', function(event) {
    quantity.value++;
});

const addBasketFrom = document.getElementById('add-basket-form');

addBasketFrom.addEventListener('submit', async function (event) {
    event.preventDefault();
    const formData = new FormData(addBasketFrom);
    const jsonString = JSON.stringify(Object.fromEntries(formData));
    const response = await fetch(`/api/v1/orders/product/${addBasketFrom.dataset.productId}`, {
        method : 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body : jsonString
    });

    if(response.redirected){
        window.location.href = response.url;
    }else if(response.ok) {
        showToast('Товар добавлен в корзину');
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
});