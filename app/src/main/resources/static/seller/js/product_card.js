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

const addToPurchaseForm = document.getElementById('addToPurchaseForm');
addToPurchaseForm.addEventListener('submit', async function(event){
    event.preventDefault();
    const productId = addToPurchaseForm.dataset.productId
    const formData = new FormData(addToPurchaseForm);
    const jsonString = JSON.stringify(Object.fromEntries(formData));
    const response = await fetch(`/api/v1/purchases/product/${productId}`,{
        method : 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body : jsonString
    });
    if(response.ok) {
        showToast('Товар добавлен в корзину');
    }else if(response.status == 409){
        showToast('Нельзя добавить такое количество');
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
});