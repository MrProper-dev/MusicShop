const pagesLinks = document.querySelectorAll('.pagination-list a');
const currentUrl = new URL(window.location.href);

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
    const filterForm = document.getElementById('filterForm');
    const categoriesInput = filterForm.querySelectorAll('input[name="cat-id"]');
    const actualCategories = urlParams.getAll('cat-id');
    categoriesInput.forEach(checkbox => {
        if (actualCategories.includes(checkbox.value)) {
            checkbox.checked = true;
        }
    });
    const priceFrom = filterForm.querySelector('input[name="price_from"]');
    const priceTo = filterForm.querySelector('input[name="price_to"]');
    const actualPriceFrom = urlParams.get('price_from');
    const actualPriceTo = urlParams.get('price_to');
    if(actualPriceFrom !== null){
        priceFrom.value = actualPriceFrom;
    }
    if(actualPriceTo !== null){
        priceTo.value = actualPriceTo;
    }
    const search = document.querySelector('input[name="search"]');
    const actualSearch = urlParams.get('search');
    if(actualSearch !== null){
        search.value = actualSearch;
    }
}