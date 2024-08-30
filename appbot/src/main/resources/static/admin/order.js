const orderStatus = ["未付款", "已付款", "已刪除"]

function formatDatetime(datetime){
    const date = new Date(datetime)
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
}

document.addEventListener("DOMContentLoaded", () => {
    const orderTable = document.querySelector("#orderTable")
    const searchInput = document.querySelector("#searchInput")
    const searchBtn = document.querySelector("#searchBtn")
    const logoutBtn = document.querySelector("#logoutBtn")

    function search(query) {
        fetch("/api/v1/order/search" + query)
        .then(r => r.json())
        .then(data => {
            orderTable.innerHTML = ""
            for(let i=0;i<data.length;i++) {
                let tr = `
                <tr>
                    <td>${data[i].orderNo == null ? "-" : data[i].orderNo}</td>
                    <td>${data[i].total}</td>
                    <td>${orderStatus[data[i].orderStatus]}</td>
                    <td>${formatDatetime(data[i].createAt)}</td>
                    <td>${formatDatetime(data[i].lastModifiedAt)}</td>
                </tr>
                `
                orderTable.innerHTML += tr
            }
        })
    }
    
    search("")

    searchBtn.addEventListener("click", () => {
        const orderNo = searchInput.value
        search(`?orderNo=${orderNo}`)
    })

    logoutBtn.addEventListener("click", () => {
        location.href="/logout"
    })
})