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

const products = Array.from(document.querySelectorAll('.product-tile'));

async function deleteProduct(product){
    const productId = product.dataset.productId;
    if(!await confirm('Вы точно хотите удалить товар? Все, что с ним связано будет удалено.', false)){
        return;
    }
    const response = await fetch(`/api/v1/products/${productId}`, {
        method : 'DELETE'
    });
    if (response.ok) {
        showToast('Товар удален');
        product.remove();
        const index = products.indexOf(product);
        products.splice(index, 1);
    } else {
        showToast(`Ошибка сервера: ${response.status}`);
    }
}

products.forEach(product => {
    const deleteBtn = product.querySelector('.delete-product-btn');
    deleteBtn.addEventListener('click', () => deleteProduct(product));
});

async function deleteCat(cat){
    const catId = cat.dataset.catId;
    if(!await confirm('Вы точно хотите удалить категорию? Все товары, связанные с ней будут удалены.', false)){
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
}

const cats = document.querySelectorAll('.category-item');
cats.forEach(cat => {
    const deleteBtn = cat.querySelector('.delete-category-btn');
    deleteBtn.addEventListener('click', () => deleteCat(cat));
});

const addCategoryBtn = document.querySelector('.add-category-btn');
addCategoryBtn.addEventListener('click', async function(){
    const result = await confirm('Введите название категории', true);
    if(!result.confirm){
        return;
    }
    const response = await fetch(`/api/v1/categories`, {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body : JSON.stringify({
            name : result.value
        })
    });
    if (response.ok) {
        showToast('Категория добавлена');
        const newCatId = await response.text();
        const newCat = document.createElement('div');
        newCat.setAttribute('class', 'category-item');
        newCat.setAttribute('data-cat-id', newCatId);
        newCat.innerHTML = `
            <label class="category-name">
                <input type="checkbox" name="cat-id" th:value="${newCatId}">${result.value}
            </label>
            <button type="button" class="delete-category-btn">Удалить</button>
        `;
        newCat.querySelector('.delete-category-btn').addEventListener('click', () => deleteCat(newCat));
        addCategoryBtn.closest('h4').after(newCat);
    } else {
        showToast(`Ошибка сервера: ${response.status}`);
    }
});

const addProductBtn = document.querySelector('.add-product-btn');
addProductBtn.addEventListener('click', async function(){
    const result = await confirm('Введите название товара', true);
    if(!result.confirm){
        return;
    }
    const response = await fetch(`/api/v1/products`, {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body : JSON.stringify({
            name : result.value
        })
    });
    if (response.ok) {
        showToast('Товар добавлен');
        const newProductId = await response.text();
        const newProduct = document.createElement('div');
        newProduct.setAttribute('class', 'product-tile');
        newProduct.setAttribute('data-product-id', newProductId);
        newProduct.innerHTML = `
            <div class="image-block">
                <img class="img-cross" src="" alt="${result.value}">
            </div>
            <div class="product-details">
                <div class="product-name">${result.value}</div>
                <div class="product-cost">0.00 ₽</div>
                <div class="product-stock-row">Остаток: <span class="stock-value">0 шт.</span></div>
                <div class="admin-actions">
                    <button class="delete-product-btn">Удалить</button>
                </div>
                <a href="/admin/products/${newProductId}" class="detail-link">Подробнее</a>
            </div>
        `;
        newProduct.querySelector('.delete-product-btn').addEventListener('click', () => deleteProduct(newProduct));
        products[0].before(newProduct);
        products.unshift(newProduct);
    } else {
        showToast(`Ошибка сервера: ${response.status}`);
    }
});