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

const loginForm = document.querySelector('.login-form');
loginForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    const formData = new FormData(loginForm);
    const searchParams = new URLSearchParams(formData);
    const response = await fetch('/login', {
        method : 'POST',
        body : searchParams
    });
    const urlParams = new URLSearchParams(new URL(response.url).search);
    if(urlParams.has('error')){
        showToast('Неправильный логин или пароль');
    }else{
        window.location.href = response.url;
    }
});