$(document).ready(function () {
    initialLoad();
    loadOrders();

});

function initialLoad() {
    var ajaxConfig;
    if ($("#search").val().trim().length === 0) {
        ajaxConfig = {
            method: 'GET',
            url: 'http://localhost:8080/orders/',
            async: true
        };
    } else {
        ajaxConfig = {
            method: 'GET',
            url: 'http://localhost:8080/orders/' + $("#search").val().trim(),
            async: true
        };
    }
    $.ajax(ajaxConfig).done(function (orders, textStatus, jqxhr) {
        $("table tbody tr").remove();

        orders.forEach(function (order) {
            var html = "<tr>" +
                "<td>" + order.id + "</td>" +
                "<td>" + order.custId + "</td>" +
                "<td>" + order.date + "</td>" +
                "<td>" + order.total + "</td>" +
                "</tr>"
            $("#search-table").append(html);
        });
    }).fail(function (jqxhr, textStatus, errorThrown) {
        console.log("Please call DEP")
    });

}
function loadOrders() {
    $("#search").keyup(function () {
        initialLoad();
    });

}
