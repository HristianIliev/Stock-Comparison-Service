$("#register-form").submit(function(event) {
	var confirmPasswordValue = $('input[name="confirm_password"]').val();
	var password = $('input[name="password"]').val();

	if (confirmPasswordValue !== password) {
		iziToast.error({
            title: 'Error',
            message: 'Passwords do not match',
			position: 'topRight'
        });

		event.preventDefault();

		return;
	}

	var username = $('input[name="username"]').val();
	var email = $('input[name="email"]').val();

	var user = {
		username: username,
		email: email,
		password: password
	}

	$.ajax({
      type: "POST",
      url: "http://localhost:3211/api/register",
      data: JSON.stringify(user),
	  contentType: "application/json",
      success: function (result) {
         window.location.replace("/login");
      },
      fail: function(xhr, textStatus, errorThrown){
		if (xhr.status == 409) {
			iziToast.error({
				title: 'Error',
				message: 'Username or email already exists',
                position: 'topRight'
			});
		}
      }
    });

	event.preventDefault();
});