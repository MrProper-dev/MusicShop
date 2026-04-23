const lessBtn = document.getElementById('less');
const moreBtn = document.getElementById('more');
const quantity = document.getElementById('quantity');

quantity.addEventListener('input', function(event) {
    if(quantity.value <= 0){
        quantity.value = 1;
    }
});

lessBtn.addEventListener('click', function(event) {
    if(quantity.value > 1){
        quantity.value--;
    }
});

moreBtn.addEventListener('click', function(event) {
    quantity.value++;
});