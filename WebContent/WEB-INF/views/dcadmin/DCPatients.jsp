<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Patients</title>
<jsp:include page="scripts.jsp" />
</head>

<body>
	<jsp:include page="nav.jsp" />


	<div align="center">
		<div class="col-md-9">
			<h3 align="center">Patients View</h3>
			<div class="row mt-4">
				<div class="col-md-6 offset-md-3">
					<div class="text-center">
						<h5>Test Wise</h5>
						<select class="form-control" id="testFilter"
							onchange="applyFilters3()">

							<option value=-1>All Tests</option>

							<!-- Add more test options as needed -->
						</select>

					</div>
				</div>
			</div>
			<div class="row mt-4">
				<div class="col-md-6 offset-md-3">
					<h5>Between Dates</h5>
					<div class="row">
						<div class="col-md-6">
							<input type="date" class="form-control" id="startDateFilter">
						</div>
						<div class="col-md-6">
							<input type="date" class="form-control" id="endDateFilter"
								onchange="applyFilters2()">
						</div>
					</div>
				</div>
			</div>
			<div class="row mt-4">
				<div class="col-md-12">
					<table class="table table-bordered" id="patientTable"
						style="display: none;">
						<thead>
							<tr>
								<th>Patient Id</th>
								<th>Patient Name</th>
								<th>Test Name</th>
								<th>Method</th>
								<th>Test category</th>


								<th>Price</th>
								<th>Date</th>
							</tr>
						</thead>
						<tbody id="patientTableBody">
							<!-- Table rows will be populated dynamically -->
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

	<script>
		$(document)
				.ready(
						function() {
							$.ajax({
								url : "./getalltests",
								type : "GET",
								// Pass the patientId
								success : function(response) {
									var data = JSON.parse(response);
									var testSelect = document
											.getElementById("testFilter");
									testSelect.innerHTML = "";

									for (var i = 0; i < data.length; i++) {
										var option = document
												.createElement("option");
										option.value = data[i].test_id;
										option.text = data[i].test_name;
										testSelect.appendChild(option);
									}

								},
								error : function(xhr, status, error) {
									console.log("Error: " + error);

								}

							});
							$
									.ajax({
										url : "./getalltestspatients",
										type : "GET",
										// Pass the patientId
										success : function(response) {
											var data = response;
											console.log(response);

											document
													.getElementById("patientTableBody").innerHTML = "";

											console.log("Hello");

											// Populate the table with filtered records
											for (var i = 0; i < data.length; i++) {
												var record = data[i];

												// Create a new table row
												var row = document
														.createElement("tr");

												// Create table cells and populate them with record data
												var serialNumberCell = document
														.createElement("td");
												serialNumberCell.innerHTML = record.patn_id;
												row
														.appendChild(serialNumberCell);

												var patientNameCell = document
														.createElement("td");
												patientNameCell.innerHTML = record.patn_name;
												row
														.appendChild(patientNameCell);

												var testNameCell = document
														.createElement("td");
												testNameCell.innerHTML = record.test_name;
												row.appendChild(testNameCell);

												var testMethod = document
														.createElement("td");
												testMethod.innerHTML = record.test_method;
												row.appendChild(testMethod);

												var testCategory = document
														.createElement("td");
												testCategory.innerHTML = record.test_category;
												row.appendChild(testCategory);

												var testPrice = document
														.createElement("td");
												testPrice.innerHTML = record.test_price;
												row.appendChild(testPrice);

												var dateCell = document
														.createElement("td");
												dateCell.innerHTML = record.dgbl_dates;
												row.appendChild(dateCell);

												// Add the row to the table body
												document.getElementById(
														"patientTableBody")
														.appendChild(row);
											}

											// Show the table if there are filtered records, hide it otherwise
											document
													.getElementById("patientTable").style.display = data.length > 0 ? "table"
													: "none";

										},
										error : function(xhr, status, error) {
											console.log("Error: " + error);

										}

									});
						});

		function applyFilters2() {
			var test = document.getElementById("testFilter").value;
			var date1 = document.getElementById("startDateFilter").value;
			var date2 = document.getElementById("endDateFilter").value;
			console.log(test);
			$
					.ajax({
						url : "./getalltestpatientdetails", // Specify the URL of the controller method
						type : "POST", // Use POST or GET depending on your server-side implementation
						// Pass the patientId as data to the server
						data : {
							date1 : date1,
							date2 : date2,
							test : test

						},

						success : function(response) {

							var data = response;
							console.log(response);

							document.getElementById("patientTableBody").innerHTML = "";

							console.log("Hello");

							// Populate the table with filtered records
							for (var i = 0; i < data.length; i++) {
								var record = data[i];

								// Create a new table row
								var row = document.createElement("tr");

								// Create table cells and populate them with record data
								var serialNumberCell = document
										.createElement("td");
								serialNumberCell.innerHTML = record.patn_id;
								row.appendChild(serialNumberCell);

								var patientNameCell = document
										.createElement("td");
								patientNameCell.innerHTML = record.patn_name;
								row.appendChild(patientNameCell);

								var testNameCell = document.createElement("td");
								testNameCell.innerHTML = record.test_name;
								row.appendChild(testNameCell);

								var testMethod = document.createElement("td");
								testMethod.innerHTML = record.test_method;
								row.appendChild(testMethod);

								var testCategory = document.createElement("td");
								testCategory.innerHTML = record.test_category;
								row.appendChild(testCategory);

								var testPrice = document.createElement("td");
								testPrice.innerHTML = record.test_price;
								row.appendChild(testPrice);

								var dateCell = document.createElement("td");
								dateCell.innerHTML = record.dgbl_dates;
								row.appendChild(dateCell);

								// Add the row to the table body
								document.getElementById("patientTableBody")
										.appendChild(row);
							}

							// Show the table if there are filtered records, hide it otherwise
							document.getElementById("patientTable").style.display = data.length > 0 ? "table"
									: "none";
						},
						error : function(xhr, status, error) {
							console.log("Error: " + error);

						}

					});
		}
		function applyFilters3() {

			var test = document.getElementById("testFilter").value;

			$
					.ajax({
						url : "./gettestwisepatients", // Specify the URL of the controller method
						type : "POST", // Use POST or GET depending on your server-side implementation
						// Pass the patientId as data to the server
						data : {
							test : test

						},

						success : function(response) {

							var data = response;
							console.log(response);

							document.getElementById("patientTableBody").innerHTML = "";

							console.log("Hello");

							// Populate the table with filtered records
							for (var i = 0; i < data.length; i++) {
								var record = data[i];

								// Create a new table row
								var row = document.createElement("tr");

								// Create table cells and populate them with record data
								var serialNumberCell = document
										.createElement("td");
								serialNumberCell.innerHTML = record.patn_id;
								row.appendChild(serialNumberCell);

								var patientNameCell = document
										.createElement("td");
								patientNameCell.innerHTML = record.patn_name;
								row.appendChild(patientNameCell);

								var testNameCell = document.createElement("td");
								testNameCell.innerHTML = record.test_name;
								row.appendChild(testNameCell);

								var testMethod = document.createElement("td");
								testMethod.innerHTML = record.test_method;
								row.appendChild(testMethod);

								var testCategory = document.createElement("td");
								testCategory.innerHTML = record.test_category;
								row.appendChild(testCategory);

								var testPrice = document.createElement("td");
								testPrice.innerHTML = record.test_price;
								row.appendChild(testPrice);

								var dateCell = document.createElement("td");
								dateCell.innerHTML = record.dgbl_dates;
								row.appendChild(dateCell);

								// Add the row to the table body
								document.getElementById("patientTableBody")
										.appendChild(row);
							}

							// Show the table if there are filtered records, hide it otherwise
							document.getElementById("patientTable").style.display = data.length > 0 ? "table"
									: "none";
						},
						error : function(xhr, status, error) {
							console.log("Error: " + error);

						}

					});
		}
	</script>
</body>
</html>