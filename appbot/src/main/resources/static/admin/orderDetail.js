document.addEventListener("DOMContentLoaded", () => {
    const orderDetailTable = document.querySelector("#orderDetailTable")

    fetch("/api/v1/orderDetail/search" + location.search)
    .then(r => r.json())
    .then(data => {
        orderDetailTable.innerHTML = ""
        for(let i=0;i<data.length;i++) {
            let tr = `
            <tr>
                <td>${data[i].productName}</td>
                <td>${data[i].quantity}</td>
                <td>${data[i].originalPrice}</td>
                <td>${data[i].discountedPrice}</td>
            </tr>
            `
            orderDetailTable.innerHTML += tr
        }
    })

})