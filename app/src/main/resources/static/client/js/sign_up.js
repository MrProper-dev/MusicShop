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

const registerForm = document.querySelector('.register-form');
registerForm.addEventListener('change', function (){
    const phoneInput = registerForm.querySelector('input[name="phone"]');
    let phone = phoneInput.value.replace(/\D/g, '');
    console.log(phone);
    if(phone.length != 11){
        phoneInput.value = '';
        showToast('Номер телефона введен некорректно');
    }else{
        if(phone[0] == '7') phone = '8' + phone.slice(1);
        phoneInput.value = phone.replace(/(\d)(\d{3})(\d{3})(\d{2})(\d{2})/, '$1 $2 $3 $4 $5');
    }
});
registerForm.addEventListener('submit', async function(event){
    event.preventDefault();
    const formData = new FormData(registerForm);
    const jsonString = JSON.stringify(Object.fromEntries(formData));
    const response = await fetch('/api/v1/clients/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonString
    });
    if(response.ok) {
        window.location.href = '/login';
    }else if(response.status == 422){
        showToast('Такой номер телефона уже зарегистрирован');
    }else if(response.status == 400) {
        showToast('Пароли не совпадают');
    }else{
        showToast(`Ошибка сервера: ${response.status}`);
    }
});