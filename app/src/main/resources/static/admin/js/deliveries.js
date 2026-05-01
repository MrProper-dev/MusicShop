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

const pagesLinks = document.querySelectorAll('.pagination-list a');
const currentUrl = new URL(window.location.href);
const fromInput = document.querySelector('input[name="from"]');
const toInput = document.querySelector('input[name="to"]');

fillFields();

pagesLinks.forEach(link => {
    link.addEventListener('click', function(event){
        event.preventDefault();
        const pageNumber = link.textContent - 1;
        const urlParams = currentUrl.searchParams;
        urlParams.set('page', pageNumber);
        const newUrl = currentUrl.pathname + '?' + urlParams.toString();
        window.location.href = newUrl;
    })
});

fromInput.addEventListener('change', function(){
    location.href = `/admin/deliveries?from=${fromInput.value}&to=${toInput.value}`
});

toInput.addEventListener('change', function(){
    location.href = `/admin/deliveries?from=${fromInput.value}&to=${toInput.value}`
});

function fillFields(){
    const urlParams = currentUrl.searchParams;
    const from = urlParams.get('from');
    const to = urlParams.get('to');
    if(from){
        fromInput.value = from;
    }
    if(to){
        toInput.value = to;
    }
}