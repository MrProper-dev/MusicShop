document.querySelectorAll('.btn-cancel').forEach(button => {
    button.addEventListener('click', async function() {
        const orderId = button.dataset.orderId;
        const card = this.closest('.order-card');
        const status = card.querySelector('.order-status');
        
        const response = await fetch(`/api/v1/orders/${orderId}`, {
            method: 'DELETE'
        });
        
        if(response.ok){
            status.textContent = 'Отменен';
            status.className = 'order-status status-cancelled';
            button.setAttribute('disabled', '');
            button.className = 'btn-cancel-disabled';
        }
    });
});