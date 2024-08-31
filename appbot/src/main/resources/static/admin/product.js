let productIndex = 0;

// 當DOM加載完成後執行
document.addEventListener('DOMContentLoaded', function() {
    // 添加第一個商品表單
    addProductForm();

    // 綁定登出按鈕事件
    document.getElementById('logoutBtn').addEventListener('click', logout);
});

// 添加新的商品表單
function addProductForm() {
    productIndex++;
    const productFormHtml = `
        <div class="product-form" id="product-form-${productIndex}">
            <div class="form-group">
                <label for="name-${productIndex}">商品名稱</label>
                <input type="text" id="name-${productIndex}" name="name-${productIndex}" required>
            </div>
            <div class="form-group">
                <label for="price-${productIndex}">價格</label>
                <input type="number" id="price-${productIndex}" name="price-${productIndex}" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="stock-${productIndex}">庫存</label>
                <input type="number" id="stock-${productIndex}" name="stock-${productIndex}" required>
            </div>
            <div class="form-group">
                <label for="category-${productIndex}">類別</label>
                <input type="text" id="category-${productIndex}" name="category-${productIndex}" required>
            </div>
            <div class="form-group">
                <label for="image-${productIndex}">商品圖片</label>
                <input type="file" id="image-${productIndex}" name="image-${productIndex}" accept="image/*" required>
            </div>
        </div>
    `;
    document.getElementById('productFormsContainer').insertAdjacentHTML('beforeend', productFormHtml);
}

// 提交表單
function submitForm() {
    const formData = new FormData();

    for (let i = 1; i <= productIndex; i++) {
        const name = document.getElementById(`name-${i}`).value;
        const price = document.getElementById(`price-${i}`).value;
        const stock = document.getElementById(`stock-${i}`).value;
        const category = document.getElementById(`category-${i}`).value;
        const image = document.getElementById(`image-${i}`).files[0];

        if (name && price && stock && category && image) {
            formData.append(`products[${i-1}].name`, name);
            formData.append(`products[${i-1}].price`, price);
            formData.append(`products[${i-1}].stock`, stock);
            formData.append(`products[${i-1}].category`, category);
            formData.append(`images`, image);
        }
    }

    fetch('/api/v1/product/create', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            alert('商品上傳成功');
            console.log(data);
            // 清空表單或重置頁面
            resetForms();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('商品上傳失敗：' + error.message);
        });
}

// 重置表單
function resetForms() {
    document.getElementById('productFormsContainer').innerHTML = '';
    productIndex = 0;
    addProductForm();
}

