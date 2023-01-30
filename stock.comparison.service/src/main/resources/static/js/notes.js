$(".delete-note-button").on("click", function(event) {
    var noteId = $(this).attr('id');

    var toDelete = $(this).parent().parent();

    var pathEntries = window.location.pathname.split('/');

    $.ajax({
        url: "/api/users/" + pathEntries[2] + "/notes/" + noteId,
        method: "DELETE",
        success: function(result) {
            $(toDelete).remove();

            iziToast.success({
                title: "Success",
                message: "Note was successfuly deleted!",
                position: "topRight"
            });
        }
    });
});