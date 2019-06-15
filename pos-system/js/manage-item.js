$(document).ready(function () {
    $("#description").focus();
    loadNewItem();
    idGenerator();
    loadItem();
    validateFunction();

    $(document).on("click", ".delete-item", function () {
        var itemId = $($(this).parent().children()[0]).text();
        var selectedRow = $(this);
        var response = confirm("Are you Want To Delete");
        if (response) {
        var ajaxConfig = {
            method: 'DELETE',
            url: 'http://localhost:8080/item/' + itemId,
            async: true,
        };
        $.ajax(ajaxConfig).done(function (response) {

            if (response) {
                selectedRow.parent().remove();
                newCustomer();
                alert("Successfully Deleted The Item");
            }
            else {
                alert("Error");
            }
        }).fail(function (jqxhr, textStatus, errorThrown) {
            console.log("Please call DEP");
            newItem();
            alert(jqxhr.responseText);
        });
    }else{
            alert("OK");
        }
    });

});

function validateFunction() {


    $("#description").on("keyup", function () {
        var description = $(this).val().trim();
        var checker = /^[A-Za-z\s]*$/;
        if (description.match(checker) && (description.length > 0)) {
            // $("#address").focus();
            $(this).css('border-color', '#ced4da');
        } else if (description.length > 0) {
            $(this).focus();
            $(this).select();
            $(this).css('border-color', 'red');
        } else if (description.length == 0) {
            $(this).css('border-color', '#ced4da');
        }
    });


    $("#quantity").on("keyup", function () {
        var quantity = $(this).val().trim();
        var checker = /^[0-9]*$/;
        if (quantity.match(checker) && (quantity.length > 0)) {
            // $("#address").focus();
            $(this).css('border-color', '#ced4da');
        } else if (quantity.length > 0) {
            $(this).focus();
            $(this).select();
            $(this).css('border-color', 'red');
        } else if (quantity.length == 0) {
            $(this).css('border-color', '#ced4da');
        }
    });

    $("#unitprice").on("keyup", function () {
        var unitprice = $(this).val().trim();
        var checker = /^[0-9]*$/;
        if (unitprice.match(checker) && (unitprice.length > 0)) {
            // $("#address").focus();
            $(this).css('border-color', '#ced4da');
        } else if (unitprice.length > 0) {
            $(this).focus();
            $(this).select();
            $(this).css('border-color', 'red');
        } else if (unitprice.length == 0) {
            $(this).css('border-color', '#ced4da');
        }
    });

}


function idGenerator() {


    var ajaxConfig = {
        method: 'GET',
        url: 'http://localhost:8080/item',
        async: true
    };

    var numOfItems;
    $.ajax(ajaxConfig).done(function (items, textStatus, jqxhr) {
        numOfItems = items.length;
        console.log(numOfItems);
        var idsubs = parseInt(items[numOfItems - 1].code.substr(1, 3));
        if (idsubs < 9) {
            $("#id").val("P00" + ++idsubs);
        } else if (idsubs < 99) {
            $("#id").val("P0" + ++idsubs);
        } else if (idsubs > 999) {
            $("#id").val("P" + ++idsubs);
        }

    }).fail(function (jqxhr, textStatus, errorThrown) {
        console.log("Please call DEP");
        alert(jqxhr.responseText);
    });


}

function newItem() {
    idGenerator();
    $("#description").val("");
    $("#quantity").val("");
    $("#unitprice").val("");
}

function saveItem() {

    var itemObject = {
        code: $("#id").val(),
        description: $("#description").val(),
        qtyOnHand: parseInt($("#quantity").val()),
        unitPrice: parseFloat($("#unitprice").val())
    };
    var itemData = JSON.stringify(itemObject);

    var ajaxConfig = {
        method: 'POST',
        url: 'http://localhost:8080/item',
        async: true,
        data: itemData,
        contentType: "application/json"
    };

    $.ajax(ajaxConfig).done(function (data) {
        console.log(data);
        if (data) {

            html = '<tr>' +
                '<td>' + $("#id").val() + '</td>' +
                '<td>' + $("#description").val() + '</td>' +
                '<td>' + $("#quantity").val() + '</td>' +
                '<td>' + $("#unitprice").val() + '</td>' +
                '<td class="delete-item">' + '<button class="btn"></button>' + '</td>'
                + '</tr>';
            $("#tbl-item").append(html);
            $("tbody tr:last-child").click(function (e) {
                console.log($("tbody tr").index(this));
                $("#id").val($($(this).children()[0]).text());
                $("#description").val($($(this).children()[1]).text());
                $("#quantity").val($($(this).children()[2]).text());
                $("#unitprice").val($($(this).children()[3]).text());

            });
            newItem();
            alert("Successfully Saved The Item");
        } else {
            alert("Customer Not Saved");
        }

    }).fail(function (jqxhr, textStatus, errorThrown) {
        console.log("Please call DEP");
        alert(jqxhr.responseText);
    });
}

function loadNewItem() {
    $("#newItem").on('click', function () {
        newItem();
    });
}

$("#btn-save").click(function () {
    var update = false;
    if ($("#description").val().trim().length > 0 || $("#quantity").val().trim().length > 0 || $("#unitprice").val().trim().length > 0) {
        $("tbody tr td:first-child").each(function (index, obj) {
            if ($(this).text() === $("#id").val()) {
                rowUpdate($(this).parent());
                update = true;
            }
        });
        if (!update) {
            saveItem();
        }
    } else {
        alert("Please Enter All Fields Correctly");
    }
});

function loadItem() {

    var ajaxConfig = {
        method: 'GET',
        url: 'http://localhost:8080/item',
        async: true
    };
    $.ajax(ajaxConfig).done(function (items, textStatus, jqxhr) {
        $("table tbody tr").remove();

        items.forEach(function (item) {
            var html = "<tr>" +
                "<td>" + item.code + "</td>" +
                "<td>" + item.description + "</td>" +
                "<td>" + item.qtyOnHand + "</td>" +
                "<td>" + item.unitPrice + "</td>" +
                '<td class="delete-item">' + '<button class="btn"></button>' + '</td>' +
                // "<td>" + customer.salary + "</td>" +
                "</tr>"
            $("#tbl-item").append(html);
            $("tbody tr:last-child").click(function (e) {
                console.log($("tbody tr").index(this));
                $("#id").val($($(this).children()[0]).text());
                $("#description").val($($(this).children()[1]).text());
                $("#quantity").val($($(this).children()[2]).text());
                $("#unitprice").val($($(this).children()[3]).text());

            });
        });
        $('#tbl-item').DataTable(
            {   searching: false,
                pageLength:10
            }
        );
    }).fail(function (jqxhr, textStatus, errorThrown) {
        console.log("Please call DEP");
        alert(jqxhr.responseText);
    });


}

function rowUpdate(row) {
    row.find("td:nth-child(2)").text($("#description").val());
    row.find("td:nth-child(3)").text($("#quantity").val());
    row.find("td:nth-child(4)").text($("#unitprice").val());


    var itemObject = {
        description: $("#description").val(),
        unitPrice: parseFloat($("#unitprice").val()),
        qtyOnHand: parseInt($("#quantity").val())
    }
    var itemData = JSON.stringify(itemObject);

    var ajaxConfig = {
        method: 'PUT',
        url: 'http://localhost:8080/item/' + $("#id").val(),
        async: true,
        data: itemData,
        contentType: "application/json"
    };
    $.ajax(ajaxConfig).done(function (response) {
        console.log(response);
    }).fail(function (jqxhr, textStatus, errorThrown) {
        console.log("Please call DEP");
        alert(jqxhr.responseText);
    });


}

