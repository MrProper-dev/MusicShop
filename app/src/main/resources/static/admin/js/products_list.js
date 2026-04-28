const modal = document.getElementById("modal");
const modalOk = modal.querySelector("#modalOk");
const modalCancel = modal.querySelector("#modalCancel");
const modalText  = modal.querySelector("#modalText");

function confirm(question){
    return new Promise((resolve) => {
        modalText.textContent = question;
        modal.showModal();

        const onConfirm = () => {
            modal.close();
            modalOk.removeEventListener("click", onConfirm);
            modalCancel.removeEventListener("click", onCancel);
            resolve(true);
        };

        const onCancel = () => {
            modal.close();
            modalOk.removeEventListener("click", onConfirm);
            modalCancel.removeEventListener("click", onCancel);
            resolve(false);
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

const products = document.querySelectorAll('.product-tile');
products.forEach(product => {
    const deleteBtn = product.querySelector('.delete-product-btn');
    const productId = product.dataset.productId;
    deleteBtn.addEventListener('click', async function(){
        if(!await confirm('Вы точно хотите удалить товар? Все, что с ним связано будет удалено.')){
            return;
        }
        const response = await fetch(`/api/v1/products/${productId}`, {
            method : 'DELETE'
        });
        if (response.ok) {
            showToast('Товар удален');
            product.remove();
        } else {
            showToast(`Ошибка сервера: ${response.status}`);
        }
    });
});

const cats = document.querySelectorAll('.category-item');
cats.forEach(cat => {
    const deleteBtn = cat.querySelector('.delete-category-btn');
    const catId = cat.dataset.catId;
    deleteBtn.addEventListener('click', async function(){
        if(!await confirm('Вы точно хотите удалить категорию? Все товары, связанные с ней будут удалены.')){
            return;
        }
        const response = await fetch(`/api/v1/categories/${catId}`, {
            method : 'DELETE'
        });
        if (response.ok) {
            showToast('Категория удалена');
            cat.remove();
        } else {
            showToast(`Ошибка сервера: ${response.status}`);
        }
    });
});

//TODO:доделать
const addCategoryBtn = document.querySelector('.add-category-btn');
addCategoryBtn.addEventListener('click', async function(){
    const response = await fetch(`/api/v1/categories/${catId}`, {
        method : 'POST'
    });
    if (response.ok) {
        showToast('Категория добавлена');
        cat.remove();
    } else {
        showToast(`Ошибка сервера: ${response.status}`);
    }
});