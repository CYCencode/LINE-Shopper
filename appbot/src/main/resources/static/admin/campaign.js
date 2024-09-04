let productContainer;
function searchProductById(id) {
    fetch(`/api/v1/product/${id}`)
    .then(r => r.json())
    .then(data => {
        d = data.data[0]
        productContainer.innerHTML = ""
        let tr = `
            <div class="product-image">
                <img src="${d.image}" alt="Product Image">
            </div>
            <div class="product-details">
                <p>ID: ${d.id}</p>
                <p>名稱: ${d.name}</p>
                <p>價格: ${d.price}</p>
                <p>庫存: ${d.stock}</p>
                <p>類別: ${d.category}</p>
            </div>
        `
        productContainer.innerHTML += tr
    })
}

document.addEventListener("DOMContentLoaded", () => {
    const campaignTable = document.querySelector("#campaignTable")
    productContainer = document.querySelector("#product-container")
    const logoutBtn = document.querySelector("#logoutBtn")

    const campaignNameInput = document.querySelector("#campaignNameInput")
    const createAtInput = document.querySelector("#createAtInput")
    const terminateAtInput = document.querySelector("#terminateAtInput")
    const discoutRateInput = document.querySelector("#discoutRateInput")
    const searchBtn = document.querySelector("#searchBtn")

    const cNameInput = document.querySelector("#cNameInput")
    const cProductIdInput = document.querySelector("#cProductIdInput")
    const cCreateAtInput = document.querySelector("#cCreateAtInput")
    const cTerminateAtInput = document.querySelector("#cTerminateAtInput")
    const cDiscoutRateInput = document.querySelector("#cDiscoutRateInput")
    const createBtn = document.querySelector("#createBtn")

    function searchCampaign() {
        fetch("/api/v1/campaign/search", {
            headers: {
                "Content-Type": "application/json"
            },
            method: "POST",
            body: JSON.stringify({
                "name": campaignNameInput.value,
                "createAt": createAtInput.value,
                "terminateAt": terminateAtInput.value,
                "discountRate": discoutRateInput.value
            })
        })
        .then(r => r.json())
        .then(data => {
            campaignTable.innerHTML = ""
            dlist = data.data
            dlist.forEach(d => {
                let tr = `
                    <tr>
                        <td>${d.id}</td>
                        <td>${d.name}</td>
                        <td><button type='button' onclick='searchProductById(${d.productId})'>${d.productId}</button></td>
                        <td>${d.createAt}</td>
                        <td>${d.terminateAt}</td>
                        <td>${d.discountRate * 100}%</td>
                    </tr>
                `
                campaignTable.innerHTML += tr
            })
        })
    }

    function getAllProduct() {
        fetch("/api/v1/product/searchAll")
        .then(r => r.json())
        .then(data => {
            dlist = data.data
            dlist.forEach(d => {
                cProductIdInput.innerHTML += `<option value="${d.id}">${d.name}</option>`
            })
            
            console.log(dlist)
        })
    }

    function validateCreate() {
        const productId = cProductIdInput.value.trim();
        const name = cNameInput.value.trim();
        const createAt = cCreateAtInput.value.trim();
        const terminateAt = cTerminateAtInput.value.trim();
        const discountRate = cDiscoutRateInput.value.trim();
    
        if (!productId || !name || !createAt || !terminateAt || !discountRate) {
            alert("所有欄位都必須填寫！");
            return false;
        }
        
        return true;
    }

    searchCampaign()
    getAllProduct()
    searchBtn.addEventListener("click",() =>{
        searchCampaign()
    })

    logoutBtn.addEventListener("click",() => {
        location.href = "/logout";
    })

    createBtn.addEventListener("click", () => {
        if(validateCreate() == false) {
            return;
        }

        fetch("/api/v1/campaign/create", {
            headers: {
                "Content-Type": "application/json"
            },
            method: "POST",
            body: JSON.stringify({
                "productId": cProductIdInput.value,
                "name": cNameInput.value,
                "createAt": cCreateAtInput.value,
                "terminateAt": cTerminateAtInput.value,
                "discountRate": cDiscoutRateInput.value
            })
        })
        .then(r => r.json())
        .then(data => {
            if (data.data) {
                alert(`已新增促銷活動ID:${data.data}`)
                searchCampaign()
            } else {
                alert(`${data.msg}`)
            }             
        })
    })

    cProductIdInput.addEventListener("change", () => {
        searchProductById(cProductIdInput.value)
    })
})