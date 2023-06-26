<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<title>New Appointment Form</title>
<jsp:include page="scripts.jsp" />
<!-- Include Bootstrap CSS -->

</head>
<body>
	<jsp:include page="nav.jsp" />
	<div class="container mx-auto">
		<div class="row justify-content-center">
			<div class="col-md-5 ">
				<div class="shadow p-3  bg-white rounded">

					<h2>New Appointment Form</h2>
					<form id="appointmentForm" action="./newappointment/create"
						method="post">
						<!-- Specialization -->
						<div class="form-group">
							<label for="specialization">Specialization:</label> <select
								class="form-control" id="specialization" name="specialization"
								required>
								<option value="">Select Specialization</option>
								<c:forEach items="${speclist}" var="spec">
									<option value="${spec.id}">${spec.title}</option>
								</c:forEach>
							</select>
						</div>
						<input id="existingPatientid" name="existingPatientid" type="text"
							value="${patientSession.id}" hidden> <input
							id="appnrefer" name="appnrefer" type="text" hidden> <input
							id="appnrefer" name="appnrefer" type="text" hidden>
						<!-- Date -->
						<jsp:useBean id="now" class="java.util.Date" />
						<fmt:formatDate value="${now}" pattern="yyyy-MM-dd"
							var="formattedDate" />
						<div class="form-group">
							<label for="appointmentDate">Date:</label> <input type="date"
								class="form-control" id="appointmentDate" name="appointmentDate"
								min="${formattedDate}"required>
						</div>

						<!-- Doctor -->
						<div class="form-group">
							<label for="doctor">Doctor:</label> <select class="form-control"
								id="doctor" name="doctor" required>
								<option value="">Select Doctor</option>
								<!-- Dynamically populated based on selected specialization -->
							</select>

						</div>


						<!-- Available Slots -->
						<div class="form-group">
							<label for="slots">Available Slots:</label> <select
								class="form-control" id="slots" name="slots" required>
								<option value="">Select Slot</option>
								<!-- Dynamically populated based on selected doctor and date -->
							</select>
						</div>

						<!-- Radio Button: Self or Family -->
						<div class="form-group">
							<label for="bookingType">Booking Type:</label><br>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="bookingType"
									id="selfBooking" value="SELF" checked> <label
									class="form-check-label" for="selfBooking">Self</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="bookingType"
									id="familyBooking" value="FAMILY"> <label
									class="form-check-label" for="familyBooking">Family</label>
							</div>
						</div>

						<!-- Family Members (Hidden by default) -->
						<div class="form-group" id="familyMembersGroup"
							style="display: none;">
							<label for="familyMembers">Family Members:</label> <select
								class="form-control" id="familyMembers" name="familyMembers">
								<option value="">Select Family Member</option>
								<!-- Dynamically populated based on available family members -->
								<c:forEach items="${fam }" var="f">
									<option value="${f.fam_patni_id }">${f.fam_patn_name}</option>
								</c:forEach>
							</select>
						</div>
						<input type="numeric" value="" hidden id="appnfee" name="appnfee">
						<!-- Submit Button -->
						<button type="button" class="btn btn-success"
							onclick="previewBooking()">Preview Booking</button>
					</form>
				</div>
			</div>
			<div class="col-md-5">
				<div class="form-group" align="center">
					<div id="docdetails" style="display: none;">
						<div class="card" style="width: 18rem;">
							<img id="doc_img" src="..." class="card-img-top" alt="...">
							<div class="card-body">
								<h5 id="doc_name" class="card-title">Card title</h5>
								<h6 id="doc_qual" class="card-text"></h6>
								<h6 id="doc_exp" class="card-text"></h6>
								<h6 id="doc_fee" class="card-text"></h6>
								<h6 id="doc_exp" class="card-text"></h6>

							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- Preview Booking Modal -->
			<div class="modal fade" id="previewModal" tabindex="-1" role="dialog"
				aria-labelledby="previewModalLabel" aria-hidden="true">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title" id="previewModalLabel">Preview
								Booking</h5>
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body" id="bookingDetails">
							<!-- Dynamically populated with booking details -->
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary"
								data-dismiss="modal">Cancel</button>
							<button type="button" class="btn btn-primary" onclick="payment()">Confirm
								Booking</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Include Bootstrap JS -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

	<script>
		// Function to populate doctors based on selected specialization
		$(document)
				.ready(
						function() {
							$('#doctor')
									.change(
											function() {
												$('#docdetails').hide();
												var id = $('#doctor').val();
												$
														.ajax({
															url : './fetchdoctor',
															type : 'GET',
															data : {
																id : id
															},
															success : function(
																	doc) {
																/* doc = JSON
																		.parse(doc); */

																console
																		.log(doc);
																$('#doc_name')
																		.text(
																				"Name: "
																						+ doc.doctName);
																$("#doc_qual")
																		.text(
																				"Qualification: "
																						+ doc.doctQual);
																$("#doc_exp")
																		.text(
																				"Experience: "
																						+ doc.doctExp);
																$("#doc_fee")
																		.text(
																				"Base Fee "
																						+ doc.doctCfee);
																$("#doc_img")
																		.attr(
																				"src",
																				"data:image/png;base64,"
																						+ doc.doctPhoto);
																$('#appnfee')
																		.val(
																				Number(doc.doctCfee));
																$('#docdetails')
																		.show();
															}
														});

											});
						});

		$(document).ready(function() {
			$('#doctor').change(function() {
				var id = $('#doctor').val();
				var appointmentDate = $('#appointmentDate').val();
				$.ajax({
					url : './fetchtimeSlots',
					type : 'GET',
					data : {
						id : id,
						date : appointmentDate
					},
					success : function(timeslots) {
						/* timeslots = JSON.parse(timeslots); */
						console.log(timeslots);
						$('#slots').empty();
						for (var i = 0; i < timeslots.length; i++) {
							var option = $('<option/>');
							option.attr({
								'value' : timeslots[i]
							}).text(timeslots[i]);
							$('#slots').append(option);
						}
					}
				});

			});
		});

		$(document)
				.ready(
						function() {
							$('#appointmentDate')
									.change(
											function() {
												var appointmentDate = $(this)
														.val();
												var specialization = $(
														'#specialization')
														.val();
												// Perform an AJAX request to fetch doctors based on specialization
												$
														.ajax({
															url : './fetchBySpecialization',
															type : 'GET',
															data : {
																specialization : specialization,
																appointmentDate : appointmentDate
															},
															success : function(
																	response) {
																// Clear previous options
																/* response = JSON
																		.parse(response); */

																$('#doctor')
																		.empty();
																$('#doctor')
																		.append(
																				'<option value="">Select Doctor</option>');
																// Append new options based on response
																$
																		.each(
																				response,
																				function(
																						index,
																						doctor) {
																					$(
																							'#doctor')
																							.append(
																									'<option value="' + doctor.doctId + '">'
																											+ doctor.doctName

																											+ '</option>');
																				});
															},
															error : function(
																	xhr,
																	status,
																	error) {
																console
																		.log(error);
															}
														});
											});
						});

		// Function to populate available slots based on selected doctor and date
		/*		$(document)
		 .ready(
		 function() {
		 $('#doctor, #appointmentDate')
		 .change(
		 function() {
		 var doctorId = $('#doctor')
		 .val();
		 var appointmentDate = $(
		 '#appointmentDate')
		 .val();
		 // Perform an AJAX request to fetch available slots based on doctor and date
		 $
		 .ajax({
		 url : '/slots/fetchByDoctorAndDate',
		 type : 'GET',
		 data : {
		 doctorId : doctorId,
		 appointmentDate : appointmentDate
		 },
		 success : function(
		 response) {
		 // Clear previous options
		 $('#slots')
		 .empty();
		 $('#slots')
		 .append(
		 '<option value="">Select Slot</option>');
		 // Append new options based on response
		 $
		 .each(
		 response,
		 function(
		 index,
		 slot) {
		 $(
		 '#slots')
		 .append(
		 '<option value="' + slot.id + '">'
		 + slot.time
		 + '</option>');
		 });
		 },
		 error : function(
		 xhr,
		 status,
		 error) {
		 console
		 .log(error);
		 }
		 });
		 });
		 });
		 */
		// Function to toggle display of family members dropdown based on booking type
		$(document).ready(function() {

			$('input[name="bookingType"]').change(function() {
				var bookingType = $(this).val();
				if (bookingType === 'FAMILY') {
					$('#familyMembersGroup').show();
					$('#existingPatientid').val('');
				} else {
					$('#familyMembersGroup').hide();
					$('#existingPatientid').val('');
				}
			});
		});

		$('#familyMembers').change(function() {
			console.log("hello");

			$('#existingPatientid').val($('#familyMembers').val());

		});

		// Function to preview the booking details in the modal
		function previewBooking() {
			var specialization = $('#specialization').val();
			var appointmentDate = $('#appointmentDate').val();
			var doctor = $('#doctor option:selected').text();
			var slot = $('#slots option:selected').text();
			var bookingType = $('input[name="bookingType"]:checked').val();
			var familyMembers = $('#familyMembers option:selected').text();
			var appnfee = $('#appnfee').val();

			var bookingDetails = '<p><strong>Specialization:</strong> '
					+ specialization + '</p>';
			bookingDetails += '<p><strong>Date:</strong> ' + appointmentDate
					+ '</p>';
			bookingDetails += '<p><strong>Doctor:</strong> ' + doctor + '</p>';
			bookingDetails += '<p><strong>Slot:</strong> ' + slot + '</p>';

			if (bookingType === 'FAMILY') {
				bookingDetails += '<p><strong>Booking Type:</strong> Family</p>';
				bookingDetails += '<p><strong>Family Member:</strong> '
						+ familyMembers + '</p>';
			} else {
				bookingDetails += '<p><strong>Booking Type:</strong> Self</p>';
			}
			bookingDetails += '<p><strong>Booking Fee:</strong>'
					+ $('#appnfee').val() + '</p>';

			$('#bookingDetails').html(bookingDetails);
			$('#previewModal').modal('show');
		}

		// Function to confirm the booking and submit the form
		function confirmBooking() {
			$('#appointmentForm').submit();
		}
	</script>

	<script type="text/javascript">
		function payment() {
			var amount = $('#appnfee').val()

			$
					.ajax({
						url : "./appnpayment",
						type : "POST",
						data : {

							amount : amount,
							currency : "$"

						},

						success : function(response) {
							console.log("in pay");
							if (response.status == 'created') {
								var options = {
									"key" : "rzp_test_j9AU4Na98kCuvD",
									"amount" : response.amount,
									"currency" : "INR",
									"name" : "RaphaHospital",
									"description" : "Test Transaction",
									"image" : "https://static.vecteezy.com/system/resources/thumbnails/006/817/240/small/creative-abstract-modern-clinic-hospital-logo-design-colorful-gradient-clinic-logo-design-free-vector.jpg",
									"order_id" : response.id,
									"handler" : function(response) {

										payment_id = response.razorpay_payment_id
												.toString();
										console
												.log(response.razorpay_payment_id);
										console.log(response.razorpay_order_id);
										console
												.log(response.razorpay_signature);

										$('#appnrefer').val(
												response.razorpay_payment_id);
										$('#appnmode').val("card");
										console.log($('#appnrefer').val());
										console.log($('#appnmode').val());
										confirmBooking();

										console.log("confirmed");
										alert("Success");

									},
									"prefill" : {
										"name" : "",
										"email" : "",
										"contact" : ""
									},
									"notes" : {
										"address" : "Razorpay Corporate Office"
									},
									"theme" : {
										"color" : "#3399cc"
									}
								};

								var rzp1 = new Razorpay(options);
								rzp1
										.on(
												'payment.failed',
												function(response) {
													console
															.log(response.error.code);
													console
															.log(response.error.description);
													console
															.log(response.error.source);
													console
															.log(response.error.step);
													console
															.log(response.error.reason);
													console
															.log(response.error.metadata.order_id);
													console
															.log(response.error.metadata.payment_id);
													alert("Failed");

													window.location.href = "home";
												});

								rzp1.open();
							}
						},
						error : function(xhr, status, error) {
							// Handle the error response here
							console.log(xhr.responseText);
						}
					});
		}

		$(document).ready(function() {
			$('#appointmentForm').submit(function(event) {
				event.preventDefault(); // Prevent form submission

				var formData = $(this).serialize(); // Serialize the form data

				$.ajax({
					type : 'POST',
					url : './newappointment/create',
					data : formData,
					success : function(response) {
						// Handle successful response from the server
						console.log(response);
						$('#previewModal').modal('hide');
						window.location.reload();
					},
					error : function(xhr, status, error) {
						// Handle error response from the server
						console.log(error);
					}
				});
			});
		});
	</script>
</body>
</html>