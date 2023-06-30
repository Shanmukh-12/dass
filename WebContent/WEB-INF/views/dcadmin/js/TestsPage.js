// Show buffering layer
function showBufferingLayer() {
    document.getElementById('buffering-layer').style.display = 'flex';
}

// Hide buffering layer
function hideBufferingLayer() {
    document.getElementById('buffering-layer').style.display = 'none';
}

     function onclickspec() {

		$('#id').val('');
		$('#test_name').val('');
		$('#test_category').val('');
		$('#test_price').val('');
		$('#test_method').val('');
		$('#test_torange').val('');
		$('#test_fromrange').val('');
		$('#add-spec-btn').text('Add');
		$('#specializationForm').toggle();
	}

	function gettest(specid) {
			  showBufferingLayer();

		$.ajax({
			url : "./gettest",
			method : "post",
			data : {
				id : specid
			},
			success : function(spec) {
				//spec=JSON.parse(spec);
				console.log(typeof (spec));
				console.log(spec);
				const form = $('#edit-spec-form');

				console.log("go" + spec.test_id);

				$('#test_id').val(spec.test_id);
				$('#test_name').val(spec.test_name);
				$('#test_category').val(spec.test_category);

				$('#test_price').val(spec.test_price);
				$('#test_method').val(spec.test_method);
				$('#test_fromrange').val(spec.test_fromrange);
				$('#test_torange').val(spec.test_torange);

				form.attr('action', './updatetest');
				form.show();

				$('#specializationForm').show();
				$('#add-spec-btn').text('update');
			}
		});
	hideBufferingLayer();
	}

	function deltest(specid) {
		$.confirm({
			title : 'Confirm!',
			content : 'Are you Sure to delete!',
			buttons : {
				confirm : function() {
					$.ajax({
						url : "./deltest",
						method : "post",
						data : {
							test_id : specid
						},
						success : function(data) {
							$.alert({
								title : 'Deleted!',
								content : 'The item has been deleted.',
								buttons : {
									confirm : {
										text : 'OK',
										action : function() {
											// Refresh the page
											location.reload();
										}
									}
								}
							});
						}
					});

				},
				cancel : function() {
					$.alert('Canceled!');
				},
			}
		});
	}
		setTimeout(hideBufferingLayer, 3000);
	