$(".form-inline").submit(function(event) {
  event.preventDefault();

  var periods = $("#periods").val();

  var pathEntries = window.location.pathname.split('/');

  window.location.href = "/users/" + pathEntries[2] + "/comparisons?periods=" + periods;
});

$(document).ready(function() {
  var periods = getParameterByName("periods");
  if (periods < 1) {
    $("#periods").val(200);
  } else {
    $("#periods").val(periods);
  }

});

function getParameterByName(name, url) {
  if (!url) url = window.location.href;
  name = name.replace(/[\[\]]/g, "\\$&");
  var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
    results = regex.exec(url);
  if (!results) return null;
  if (!results[2]) return "";
  return decodeURIComponent(results[2].replace(/\+/g, " "));
}
