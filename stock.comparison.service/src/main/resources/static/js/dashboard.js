$("#createNewDashboardButton").submit(function(event) {
    event.preventDefault();

    var dashboardName = $("#dashboardName").val();
    var dashboardDescription = $("#dashboardDescription").val();
    if (!dashboardName.length || !dashboardDescription.length) {
        iziToast.error({
            title: "ERROR",
            message: "There is empty input!",
            position: "topRight"
        });
    }

    var pathEntries = window.location.pathname.split('/');

    var dashboard = {
        name: dashboardName,
        description: dashboardDescription
    }

    $.ajax({
        url: "/api/users/" + pathEntries[2] + "/dashboards",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(dashboard),
        success: function(result) {
            iziToast.success({
                title: "Success",
                message: "Dashboard was created!",
                position: "topRight"
            });
        }
    });
});

$(".deleteDashboardBtn").on('click', function(event) {
    var dashboardId = $(this).attr('id');

    var toDelete = $(this).parent().parent().parent().parent();

    var pathEntries = window.location.pathname.split('/');

    $.ajax({
        url: "/api/users/" + pathEntries[2] + "/dashboards/" + dashboardId,
        method: "DELETE",
        success: function(result) {
            iziToast.success({
                title: "Success",
                message: "Dashboard was deleted!",
                position: "topRight"
            });

            $(toDelete).remove();
        }
    });
});