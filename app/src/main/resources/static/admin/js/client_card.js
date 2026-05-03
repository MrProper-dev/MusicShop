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

const cancelBtns = document.querySelectorAll('.btn-cancel');
cancelBtns.forEach(cancelBtn => {
    const orderCard = cancelBtn.closest('.order-card');
    const orderStatus = orderCard.querySelector('.order-status');
    const orderId = orderCard.dataset.orderId;
    cancelBtn.onclick = async function(){
        if(!await confirm('Вы точно хотите отменить заказ?')) return;
        const response = await fetch(`/api/v1/orders/${orderId}`, {
            method : 'DELETE'
        });
        if(response.ok) {
            showToast('Заказ отменен');
            orderStatus.setAttribute('class', 'order-status status-cancelled');
            orderStatus.textContent = 'Отменен';
            cancelBtn.setAttribute('disabled', '');
            cancelBtn.setAttribute('class', 'btn-cancel-disabled');
            cancelBtn.onclick = null;
        }else{
            showToast(`Ошибка сервера: ${response.status}`)
        }
    }
});