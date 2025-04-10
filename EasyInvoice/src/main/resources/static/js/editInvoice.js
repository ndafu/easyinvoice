function formatDate(date) {
	var d = new Date(date),
		month = '' + (d.getMonth() + 1),
		day = '' + d.getDate(),
		year = d.getFullYear();
	if (month.length < 2) month = '0' + month;
	if (day.length < 2) day = '0' + day;
	return [day, month, year].join('/');
}

function numberFormat(x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}


function deleteInvoiceArticle(id) {
	var res = confirm("Voulez-vous supprimer cet article de la liste des articles SVP?")
	if (res) {
		$.ajax({
			url: "./deleteInvoiceArticle",
			method: "POST",
			data: {
				id: id
			},
			success: function(dta) {
				var articles = dta.body;
				if (articles.lenght === 0) {

				} else {
					var articles = dta.body;
						if (articles.lenght === 0) {

						} else {
							$('#dir_inv_art_detail').removeAttr('hidden');
							$('#dir_inv_art_detail').empty();
							var sub_total = 0.0;
							var tax = 0.0;
							for (i = 0; i < articles.length; i++) {
								sub_total = sub_total + articles[i].totalNVat;
								tax = tax + articles[i].tax;
								$('#dir_inv_art_detail').append('<tr>');
								$('#dir_inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].quantity));
								$('#dir_inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].title));
								$('#dir_inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].unity));
								$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].unity_price)))
								$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].taxRate)));
								$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].totalNVat)));
								$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].tax)));
								$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].total)))
								$('#dir_inv_art_detail').append($('<td><button type="button" class="btn btn-sm btn-danger deleteitems" onclick="deleteInvoiceArticle(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
								$('#dir_inv_art_detail').append('</tr>');

							}
							var gdTotal = sub_total + tax;
							$('#inv_total').val(numberFormat(gdTotal));
							$('#inv_tax').val(numberFormat(tax));
							$('#inv_sub_tot').val(numberFormat(sub_total));
						}
				}
			}
		});
	}
}
function checkPassword(password){
	/**
		/^
		  (?=.*\d)          // should contain at least one digit
		  (?=.*[a-z])       // should contain at least one lower case
		  (?=.*[A-Z])       // should contain at least one upper case
		  [a-zA-Z0-9]{8,}   // should contain at least 8 from the mentioned characters
		$/
	 */
	 var reg = "^(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))(?=.{6,})";
	return password.test(reg);
}
function searchTransfert(code, phone) {
	$('#amount').val('');
	$('#currency').val('');
	$('#trans_date').val('');
	$('#recever').val('');
	$('#question').val('');
	$('#response').val('');
	$('#rate').val('');
	$('#currency').val('');
	$('#paid_amount').val('');

	$('#rate').attr('disabled', 'disabled');
	$('#new_currency').attr('disabled', 'disabled');
	$('#paid_amount').attr('disabled', 'disabled');

	$('#doc_id').attr('disabled', 'disabled');
	$('#doc_by').attr('disabled', 'disabled');
	$('#doc_date').attr('disabled', 'disabled');

	$.ajax({
		url: './searchTransfert',
		method: 'POST',
		data: {
			code: code,
			phone: phone
		}, success: function(dta) {
			var res = dta.body[0];
			$('#amount').val(res.amount);
			$('#currency').val(res.currency);
			$('#trans_date').val(formatDate(res.transferDate));
			$('#recever').val(res.recipient);
			$('#question').val(res.withdrawQuestion);
			$('#response').val(res.withdrawResponse);

			if (res.amount != '') {
				$('#rate').removeAttr('disabled');
				$('#new_currency').removeAttr('disabled');
				$('#paid_amount').removeAttr('disabled');

				$('#doc_id').removeAttr('disabled');
				$('#doc_by').removeAttr('disabled');
				$('#doc_date').removeAttr('disabled');
			}
		}
	});
}

//document.onkeydown = function(e) {
//    if (e.ctrlKey && (e.keyCode === 67 || e.keyCode === 86 || e.keyCode === 85 || e.keyCode === 117)) {//Alt+c, Alt+v will also be disabled sadly.
//       toastr.warning("You are not allow to use CTRL+U on this software");
//        
//    }
//    return false;
//};

//$(document).on({
//    "contextmenu": function (e) {
//		toastr.warning("You are not allow to use context menu on this software");
//
//
//        // Stop the context menu
//        e.preventDefault();
//    },
//    "mousedown": function(e) { 
//
//    },
//    "mouseup": function(e) { 
//
//    }
//});
$(document).ready(function() {
	var url_glob = "EasyInvoice/";
	
	$('.editart').click(function(){
		var id = $(this).data('ref');
		var quantity = $(this).data('quantity'); //="${art.quantity}"
		$.ajax({
			url:'./editArticleInvoice',
			method:'POST',
			data:{
				id
			},success:function(res){
				if(res.code=='1'){
					console.log(res.body);
					let body = res.body[0];
					if(body.rubricName==''){
						$('#title_article').val(body.articleName);
						$('#title_article').removeAttr('readonly');
						$('#new_unity_price').removeAttr('readonly');
						$('#typeinvoice').val(2);
					}else{
						$('#title_article').val(body.rubricName);
						$('#typeinvoice').val(5);
						$('#new_unity_price').attr('readonly','readonly');
					}
					$('#art_quantity').val(quantity);
					$('#art_unity_price').val(body.unityprice);
					$('#art_avail_quantity').val(body.remquantity);
					$('#new_unity_price').val(body.unityprice);
					
					$('#articleid').val(id);
				}
			}
		});
		$('#editArticle').modal('show');
	});
	
	$('#new_quantity').change(function(){
		var newQty = $(this).val();
		var remQty = $('#art_avail_quantity').val();
		var typeinvoice = $('#typeinvoice').val();
		var takenQty = $('#art_quantity').val();
		
		
		var total = Number(takenQty)+Number(remQty);
		if(Number(newQty) > Number(total) && typeinvoice=='5'){
			toastr.error("Il est impossible de depasser les quantité disponible sur la fiche d'exécution");
			$(this).val('');
		}
	});
	
	$('.dirart').click(function(){
		var id = $(this).data('ref');
		let resp = confirm("Voulez-vous vraiment supprimer cet article sur cette facture?");
		if(resp){
			$.ajax({
				url:'./dirArtInvoice',
				method:'POST',
				data:{
					id
				},success:function(data){
					if(data.code=='1'){
						toastr.success("Suppression de l'article sur la facture effectuée avec succès.")
						location.reload();
					}
					
				}
			})
		}
	});
	
	$('#saveArticle').click(function(){
		var articleid 		= $('#articleid').val();
		var newQty 			= $('#new_quantity').val();
		var typeinvoice 	= $('#typeinvoice').val();
		var unityPrice 		= $('#new_unity_price').val();
		var articletitle 	= $('#title_article').val();
		var typeinvoice		= $('#typeinvoice').val();
		
		if(newQty==''){
			toastr.error("Prière de bien vouloir entrer la nouvelle quantité");
			$('#new_quantity').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#new_quantity').attr('class','form-control form-control-border is-valid');
		}
		
		if(unityPrice==''){
			toastr.error("Prière de bien vouloir entrer le nouveau prix unitaire");
			$('#new_unity_price').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#new_unity_price').attr('class','form-control form-control-border is-valid');
		}
		
		if(articletitle==''){
			toastr.error("Prière de bien vouloir entrer le nouveau prix unitaire");
			$('#title_article').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#title_article').attr('class','form-control form-control-border is-valid');
		}
		
		if(unityPrice!='' && newQty!='' && articletitle!=''){
			$.ajax({
				url:'./updateArticleInvoice',
				method:'POST',
				data:{
					articleid,newQty,unityPrice,articletitle,typeinvoice
				},success:function(data){
					if(data.code=='1'){
						toastr.success("Mise à jour de l'article a ete effectuée avec succès");
						location.reload();
					}
				}
			});
		}
	});
	$('#cancelsaveArticle').click(function(){
		$('#new_quantity').val('');
		$('#title_article').val('');
		$('#art_quantity').val('');
		$('#art_unity_price').val('');
		$('#art_avail_quantity').val('');
		$('#new_unity_price').val('');
		$('#new_unity_price').attr('readonly','readonly');
		
		$('#editArticle').modal('hide');
	});
	
	
	
	
});
