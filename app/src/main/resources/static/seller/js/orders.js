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

async function issue(issueBtn, orderCard, orderId){
    const response = await fetch(`/api/v1/orders/${orderId}/issue`, {
        method : 'PATCH'
    });
    if(response.ok){
        orderCard.remove();
        showToast('Заказ выдан');
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
}

async function collect(issueBtn, orderCard, orderId){
    const orderStatus = orderCard.querySelector('.order-status');
    const response = await fetch(`/api/v1/orders/${orderId}/collect`, {
        method : 'PATCH'
    });
    if(response.ok){
        issueBtn.textContent = 'Выдать заказ';
        issueBtn.dataset.status = 'READY';
        orderStatus.classList.replace('status-created', 'status-ready');
        orderStatus.textContent = 'Готов';
        issueBtn.onclick = () => issue(issueBtn, orderCard, orderId);
        showToast('Заказ собран');
    }else{
        showToast(`Ошибка сервера: ${response.status}`)
    }
}

const issueBtns = document.querySelectorAll('.btn-issue');
issueBtns.forEach(issueBtn => {
    const orderId = issueBtn.dataset.orderId;
    const status = issueBtn.dataset.status;
    const orderCard = issueBtn.closest('.order-card');
    if(status == 'CREATED'){
        issueBtn.onclick = () => collect(issueBtn, orderCard, orderId);
    }else{
        issueBtn.onclick = () => issue(issueBtn, orderCard, orderId);
    }
});