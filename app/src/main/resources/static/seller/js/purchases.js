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

async function deletePurchase(purchaseId, purchase, purchases){
    const response = await fetch(`/api/v1/purchases/${purchaseId}`, {
        method : 'DELETE'
    });
    if(response.ok){
        showToast('Покупка удалена');
        purchase.remove();
        const index = purchases.indexOf(purchase);
        purchases.splice(index, 1);
        let count = 1;
        purchases.forEach(p => {
            p.querySelector('.purchase-number').textContent = `Покупка № ${count}`;
            count++;
        });
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
}

const purchasesList = document.querySelector('.purchases-list');
const purchases = Array.from(purchasesList.querySelectorAll('.purchase-card'));

const createBtn = document.querySelector('.btn-create');
createBtn.addEventListener('click', async function(){
    const response = await fetch('/api/v1/purchases', {
        method : 'POST'
    });
    if(response.ok){
        showToast('Новая покупка создана');
        const newPurchase = document.createElement('div');
        newPurchase.setAttribute('class', 'purchase-card');
        newPurchase.innerHTML = `
        <div class="purchase-header">
            <div class="purchase-number">Покупка № ${purchases.length + 1}</div>
        </div>
        <div class="purchase-info">
            <div class="purchase-actions">
                <button class="btn-delete-purchase">Удалить покупку</button>
            </div>
        </div>
        <div class="empty-products">
            Нет товаров. Добавьте товары из каталога.
        </div>
        `;
        purchasesList.appendChild(newPurchase);
        purchases.push(newPurchase);
        const deleteBtn = newPurchase.querySelector('.btn-delete-purchase');
        const purchaseId = await response.text();
        deleteBtn.addEventListener('click', () => deletePurchase(purchaseId, newPurchase, purchases));
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
});

function updatePrices(subTotalPrice, price, currentQuantityValue, totalPrice){
    let sTP = parseFloat(subTotalPrice.textContent.replace(/[^\d.]/g, ''));
    let tp = parseFloat(totalPrice.textContent.replace(/[^\d.]/g, ''));
    const p = parseFloat(price.textContent.replace(/[^\d.]/g, ''));
    tp -= sTP;
    sTP = p * currentQuantityValue;
    tp += sTP;
    totalPrice.textContent = tp.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$& ').replace(',', '.') + ' ₽';
    subTotalPrice.textContent = sTP.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$& ').replace(',', '.') + ' ₽';
}

purchases.forEach(purchase => {
    const products = Array.from(purchase.querySelectorAll('.product'));
    const totalPrice = purchase.querySelector('.purchase-total');
    const purchaseId = purchase.dataset.purchaseId;
    products.forEach(product => {
        let timeoutUpdate = null;
        const productId = product.dataset.productId;
        const quantity = product.querySelector('.quantity-input');
        const subTotalPrice = product.querySelector('.product-cost-cell');
        const price = product.querySelector('.product-price');
        let pastQuantityValue = quantity.value;
        const lessBtn = product.querySelector('.quantity-btn.less');
        const moreBtn = product.querySelector('.quantity-btn.more'); 
        const deleteBtn = product.querySelector('.btn-delete-product');
        quantity.addEventListener('input', async function(){
            clearTimeout(timeoutUpdate);
            if(!/^\d+$/.test(quantity.value) || quantity.value <= 0){
                quantity.value = 1;
            }
            timeoutUpdate = setTimeout(() => {
                fetch(`/api/v1/purchases/product/${productId}`, {
                    method : 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body : JSON.stringify({
                        quantity : quantity.value,
                        purchase_id : purchaseId
                    })
                }).then(response => {
                    if(response.ok){
                        pastQuantityValue = quantity.value;
                        updatePrices(subTotalPrice, price, quantity.value, totalPrice);
                    }else if (response.status == 409){
                        showToast('Нельзя добавить такое количество');
                        quantity.value = pastQuantityValue;
                    }else {
                        showToast(`Ошибка сервера: ${response.status}`);
                        quantity.value = pastQuantityValue;
                    }
                });
            }, 700);
        });
        lessBtn.addEventListener('click', function(event){
            quantity.value--;
            quantity.dispatchEvent(new Event('input'));
        });
        moreBtn.addEventListener('click', function(event){
            quantity.value++;
            quantity.dispatchEvent(new Event('input'));
        });
        quantity.addEventListener('focus', function() {
            this.select();
        });
        deleteBtn.addEventListener('click', async function(){
            const response = await fetch(`/api/v1/purchases/${purchaseId}/products/${productId}`, {
                method : 'DELETE'
            });
            if(response.ok){
                showToast('Продукт удален');
                product.remove();
                const index = products.indexOf(product);
                products.splice(index, 1);
                updatePrices(subTotalPrice, price, 0, totalPrice);
                if(products.length == 0){
                    const purchaseNumberText = purchase.querySelector('.purchase-number').textContent;
                    purchase.innerHTML = `
                    <div class="purchase-header">
                        <div class="purchase-number">${purchaseNumberText}</div>
                    </div>
                    <div class="purchase-info">
                        <div class="purchase-actions">
                            <button class="btn-delete-purchase">Удалить покупку</button>
                        </div>
                    </div>
                    <div class="empty-products">
                        Нет товаров. Добавьте товары из каталога.
                    </div>
                    `;
                }
            }else{
                showToast(`Ошибка сервера: ${response.status}`)
            }
        });
    });
    const deleteBtn = purchase.querySelector('.btn-delete-purchase');
    deleteBtn.addEventListener('click', () => deletePurchase(purchaseId, purchase, purchases));
    const checkoutBtn = purchase.querySelector('.btn-checkout-purchase');
    checkoutBtn.addEventListener('click', async function(){
        const response = await fetch(`/api/v1/purchases/${purchaseId}/checkout`, {
            method : 'POST'
        });
        if(response.ok){
            showToast('Покупка выполнена');
            purchase.remove();
            const index = purchases.indexOf(purchase);
            purchases.splice(index, 1);
        }else{
            showToast(`Ошибка сервера: ${response.status}`)
        }
    });
});