$(function() {

    $('#login-form-link').click(function(e) {
		$("#login-form").delay(100).fadeIn(100);
 		$("#register-form").fadeOut(100);
 		$("#changePassword-form").fadeOut(100);
		$('#register-form-link').removeClass('active');
		$('#changePassword-form-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});
	$('#changePassword-form-link').click(function(e) {
		$("#changePassword-form").delay(100).fadeIn(100);
 		$("#login-form").fadeOut(100);
 		$("#register-form").fadeOut(100);
		$('#login-form-link').removeClass('active');
		$('#register-form-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});
	$('#register-form-link').click(function(e) {
		$("#register-form").delay(100).fadeIn(100);
 		$("#login-form").fadeOut(100);
 		$("#changePassword-form").fadeOut(100);
		$('#login-form-link').removeClass('active');
		$('#changePassword-form-link').removeClass('active');
		$(this).addClass('active');
		e.preventDefault();
	});

});
