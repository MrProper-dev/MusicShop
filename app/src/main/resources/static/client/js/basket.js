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

document.querySelectorAll('.remove-btn').forEach(removeBtn => {
    removeBtn.addEventListener('click', async function (event) {
        const productId = removeBtn.dataset.productId;
        const response = await fetch(`/api/v1/orders/product/${productId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showToast('Товар удален из корзины');
        } else {
            showToast(`Ошибка сервера: ${response.status}`);
        }
    });
});