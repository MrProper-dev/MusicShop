const pagesLinks = document.querySelectorAll('.pagination-list a');
const currentUrl = new URL(window.location.href);

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