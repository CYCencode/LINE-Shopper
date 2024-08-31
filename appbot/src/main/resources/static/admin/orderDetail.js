document.addEventListener("DOMContentLoaded", () => {
    const orderDetailTable = document.querySelector("#orderDetailTable")
    const orderPaymentTable = document.querySelector("#orderPaymentTable")
    const orderLogisticTable = document.querySelector("#orderLogisticTable")
    const orderLogistic2Table = document.querySelector("#orderLogistic2Table")

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
                <td>${data[i].discountedPrice * data[i].quantity}</td>
            </tr>
            `
            orderDetailTable.innerHTML += tr
        }
    })

    fetch("/api/v1/orderPayment/search" + location.search)
    .then(r => r.json())
    .then(data => {
        orderPaymentTable.innerHTML = ""
        d = data.data
        if (data.data != null) {
            let tr = `
            <tr>
                <td>${d.recTradeId}</td>
                <td>${d.bankTransactionId}</td>
                <td>${d.method}</td>
                <td>${d.amount}</td>
                <td>${d.currency}</td>
                <td>${d.transactionTime}</td>
            </tr>
            `
            orderPaymentTable.innerHTML += tr
        }
    })

    fetch("/api/v1/orderLogistic/search" + location.search)
    .then(r => r.json())
    .then(data => {
        orderLogisticTable.innerHTML = ""
        orderLogistic2Table.innerHTML = ""
        d = data.data
        if (data.data != null) {
            let tr = `
            <tr>
                <td>${d.orderNo}</td>
                <td>${d.allPayLogisticId}</td>
                <td>${d.bookingNote}</td>
                <td>${d.shipping}</td>
                <td>${d.status}</td>
            </tr>
            `
            let tr2 = `
            <tr>
                <td>${d.receiverName}</td>
                <td>${d.receiverCellPhone}</td>
                <td>${d.receiverEmail}</td>
                <td>${d.receiverAddress}</td>
                <td>${d.receiverZipcode}</td>
            </tr>
            `

            orderLogisticTable.innerHTML += tr
            orderLogistic2Table.innerHTML += tr2
        }
    })

})