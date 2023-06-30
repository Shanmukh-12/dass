<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ page import="java.util.*, spring.orm.model.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">


<title>Home</title>
<jsp:include page="scripts.jsp" />

 <link rel="stylesheet" type="text/css" href="./css/testspage.css">
</head>

<body>
 <!-- Buffering layer -->
    <div class="overlay" id="buffering-layer">
        <div class="spinner"></div>
    </div>

	<jsp:include page="nav.jsp" />
	<%
	List<TestModel> slist = (List<TestModel>) request.getAttribute("tests");
	%>

<h3 align="center">Test Details</h3>
	<div align="center">
		<div class="col-md-12">
			<div id="table">
				<table class="table mt-4">
					<thead>
						
						<tr>
							<th>Test Id</th>
							<th>Test Name</th>
							<th>Test Category</th>
							<th>Test Price</th>
							<th>Test Method</th>
							<th>From Range</th>
							<th>To Range</th>
							<th>Actions</th>


						</tr>
					</thead>


					<%
					for (TestModel s : slist) {
					%>
					<tbody>
						<tr>
							<td><%=s.getTest_id()%></td>
							<td><%=s.getTest_name()%></td>
							<td><%=s.getTest_category()%></td>
							<td><%=s.getTest_price()%></td>
							<td><%=s.getTest_method()%></td>
							<td><%=s.getTest_fromrange()%></td>
							<td><%=s.getTest_torange()%></td>


							<td>
								<button class="btn btn-primary"
									onclick="gettest('<%=s.getTest_id()%>')">Edit</button>
								<button class="btn btn-danger"
									onclick="deltest('<%=s.getTest_id()%>')">Delete</button>
							</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</div>
		</div>


		<div align="center">
			<button type="button" class="btn btn-primary" id="show-btn"
				onclick="onclickspec()">Add Test</button>
		</div>
		<div class="container">
			<div class="row justify-content-center">
				<div class="col-md-6">
					<div class="shadow p-3 mb-5 bg-white rounded"
						id="specializationForm" style="display: none;">
						<div align="center">
							<h1>New Test Details</h1>
						</div>

						<form action="./savetest"  method="post" id="edit-spec-form">




							<input id="test_id" name="test_id" type="text" hidden>

							<div class="form-group">
								<label for="test_name" class="form-label">Test Name</label> <input
									type="text" name="test_name" id="test_name"
									class="form-control" required>
							</div>

							<div class="form-group">
								<label for="test_category" class="form-label">Test
									category</label> <input type="text" name="test_category"
									id="test_category" class="form-control" required>
							</div>

							<div class="form-group">
								<label for="test_price" class="form-label">Test Price</label> <input
									type="text" name="test_price" id="test_price"
									class="form-control" required>
							</div>
							<input type="hidden" id="isDeleted" name="isDeleted" value="false">

							<div class="form-group">
								<label for="test_method" class="form-label">Method</label> <input
									type="text" name="test_method" id="test_method"
									class="form-control" required>
							</div>

							<div class="form-group">
								<label for="test_fromrange" class="form-label">Normal
									Range From</label> <input type="text" name="test_fromrange"
									id="test_fromrange" class="form-control" required>
							</div>
							<div class="form-group">
								<label for="test_torange" class="form-label">Normal
									Range To</label> <input type="text" name="test_torange"
									id="test_torange" class="form-control" required>
							</div>


							<button id="add-spec-btn" type="submit" class="btn btn-primary">Add</button>

						</form>


					</div>
				</div>
			</div>



		</div>
</div>
</body>
	<script src="js/TestPage.js">

	</html>