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

const profileForm = document.querySelector('.profile-form');
profileForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    const formData = new FormData(profileForm);
    const newPasswordInput = profileForm.querySelector('input[name="newPassword"]');
    const confirmPasswordInput = profileForm.querySelector('input[name="confirmPassword"]');
    if((formData.get('newPassword') === '' && formData.get('confirmPassword') !== '') || (formData.get('newPassword') !== '' && formData.get('confirmPassword') === '')){
        showToast('Одно из полей пароля не заполнено');
        return;
    }
    const jsonString = JSON.stringify(Object.fromEntries(formData));
    const response = await fetch('/api/v1/clients', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonString
    });
    
    if(response.ok) {
        showToast('Профиль успешно обновлен');
        newPasswordInput.value = '';
        confirmPasswordInput.value = '';
    }else if(response.status == 422) {
        showToast('Пароли не совпадают');
    }else{
        showToast(`Ошибка сервера: ${response.status}`);
    }
});

const cancelBtn = document.querySelector('.btn-cancel');
cancelBtn.addEventListener('click', function(event){
    profileForm.reset();
});