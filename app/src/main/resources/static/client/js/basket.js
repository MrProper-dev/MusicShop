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

const totalPrice = document.querySelector('.total-price');
function updatePrices(subTotalPrice, price, currentValue){
    let sTP = parseFloat(subTotalPrice.textContent.replace(/\s/g, ''));
    let tp = parseFloat(totalPrice.textContent.replace(/\s/g, ''));
    const p = parseFloat(price.replace(/\s/g, ''));
    tp -= sTP;
    sTP = p * currentValue;
    tp += sTP;
    totalPrice.textContent = tp.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$& ').replace(',', '.') + ' ₽';
    subTotalPrice.textContent = sTP.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$& ').replace(',', '.') + ' ₽';
}
document.querySelectorAll('.quantity-control').forEach(quantityBox =>{
    const trBox = quantityBox.closest('tr');
    const lessBtn = quantityBox.querySelector('[data-action="decrement"]');
    const moreBtn = quantityBox.querySelector('[data-action="increment"]');
    const quantity = quantityBox.querySelector('.quantity-input');
    const productId = quantityBox.dataset.productId;
    const subTotalPrice = trBox.querySelector('.item-subtotal');
    const price = trBox.querySelector('.price').textContent;
    let timeoutUpdate = null;

    lessBtn.addEventListener('click', function(event){
        quantity.value--;
        quantity.dispatchEvent(new Event('input'));
        updatePrices(subTotalPrice, price, quantity.value);
    });

    moreBtn.addEventListener('click', function(event){
        quantity.value++;
        quantity.dispatchEvent(new Event('input'));
        updatePrices(subTotalPrice, price, quantity.value);
    });

    quantity.addEventListener('focus', function() {
        this.select();
    });

    quantity.addEventListener('change', function(){
        updatePrices(subTotalPrice, price, quantity.value);
    });

    quantity.addEventListener('input', async function(event) {
        clearTimeout(timeoutUpdate);
        if(!/^\d+$/.test(quantity.value) || quantity.value <= 0){
            quantity.value = 1;
        }
        timeoutUpdate = setTimeout(() => {
            fetch(`/api/v1/orders/product/${productId}`, {
                method : 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body : JSON.stringify({
                    quantity : quantity.value
                })
            });
        }, 700);
    });
});

document.querySelectorAll('.remove-btn').forEach(removeBtn => {
    removeBtn.addEventListener('click', async function (event) {
        const productId = removeBtn.dataset.productId;
        const product = removeBtn.closest('tr');
        const response = await fetch(`/api/v1/orders/product/${productId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showToast('Товар удален из корзины');
            product.remove();
        } else {
            showToast(`Ошибка сервера: ${response.status}`);
        }
    });
});

const trs = document.querySelectorAll('.product-row');
document.querySelector('.checkout-btn').addEventListener('click', async function(){
    const orderId = this.dataset.orderId;
    const cartContent = document.querySelector('.cart-content');
    const response = await fetch(`/api/v1/orders/${orderId}/checkout`, {
        method : 'POST'
    });
    if(response.ok){
        const data = await response.json();
        if(data.length !== 0){
            trs.forEach(tr => {
                if(data.includes(parseInt(tr.dataset.productId))){
                    tr.style.backgroundColor = '#f8d7d4';
                }
            });
            showToast('Выделенные товары не могут быть добавлены в заказ, проверьте их наличие');
        }else{
            cartContent.innerHTML = '<h1 class="page-title">Корзина</h1>';
            showToast('Заказ оформлен');
        }
    }else{
        showToast(`Ошибка сервера: ${response.status}`);
    }
});