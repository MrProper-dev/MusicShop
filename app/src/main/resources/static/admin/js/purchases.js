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

fromInput.addEventListener('change', function(){
    location.href = `/admin/purchases?from=${fromInput.value}&to=${toInput.value}`
});

toInput.addEventListener('change', function(){
    location.href = `/admin/purchases?from=${fromInput.value}&to=${toInput.value}`
});