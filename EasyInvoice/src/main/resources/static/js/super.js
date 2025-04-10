function formatDate(date) {
	var d = new Date(date),
		month = '' + (d.getMonth() + 1),
		day = '' + d.getDate(),
		year = d.getFullYear();
	if (month.length < 2) month = '0' + month;
	if (day.length < 2) day = '0' + day;
	return [day, month, year].join('/');
}

function numberFormatfindClient(x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

function checkTin(tin, name){
	//alert('Name : '+name)
	if(tin!=null){
		$.ajax({
			url:'./checkTin',
			method:'POST',
			data:{
				tin
			},success:function(data){
				if(data.code=='1'){
					toastr.success("NIF reconnu par l'OBR");
					$('#'+name).val(data.body[0]);
				}else{
					$('#'+name).val(''); //cli_tin
					toastr.error("Le NIF "+tin+" que vous avez entré n'est pas reconnu par l'OBR");
				}
			}
		});
	}
}

function readURL(input) {
	var image_type=input.files[0].type;
	if(image_type!='image/png' && image_type!='image/jpg' && image_type!='image/jpeg'){
		toastr.error('Le logo doit avoir l\'extension png, jpg ou jpeg');
	}else{
		if(input.files[0].size>1000000){
			toastr.error('Le logo ne doit pas avoir une taille superieure 10 000 Octets');
		}else{
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function (e) {
	
					$('#logo_img64').attr('src', e.target.result);
					$('#logo_img64').removeAttr('hidden');
					$('#logo_img64').attr('data-ref', e.target.result);
					$('#logo_value').attr('value',e.target.result);
				}
				reader.readAsDataURL(input.files[0]); // convert to base64 string
			}
		}
	}
}

function readURLUpdate(input) {
	var image_type=input.files[0].type;
	if(image_type!='image/png' && image_type!='image/jpg' && image_type!='image/jpeg'){
		toastr.error('Le logo doit avoir l\'extension png, jpg ou jpeg');
	}else{
		if(input.files[0].size>1000000){
			toastr.error('Le logo ne doit pas avoir une taille superieure 10 000 Octets');
		}else{
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function (e) {
	
					$('#ed_logo_img64').attr('src', e.target.result);
					$('#ed_logo_img64').removeAttr('hidden');
					$('#ed_logo_img64').attr('data-ref', e.target.result);
					$('#ed_logo_value').attr('value',e.target.result);
				}
				reader.readAsDataURL(input.files[0]); // convert to base64 string
			}
		}
		
	}
	
}

function readCSVFile(input) {
	$('#input_name').val('');
	var image_type=input.files[0].type;
	if(image_type!='application/vnd.ms-excel'){
		toastr.error('Le type de fichier choisi n\'est pas supporté. Prière de bien vouloir choisir le type .xls');
		$(this).val('');
	}else{
		$('#input_name').html("<strong>Fichier / File :</strong><i> " +input.files[0].name+"</i>");
	}	
}

function generateHtmlTable(data){
	var html = '';
      if(typeof(data[0]) === 'undefined') {
        return null;
      } else {
		$.each(data, function( index, row ) {
		  //bind header
		  if(index == 0) {
			html += '';
			html += '';
			$.each(row, function( index, colData ) {
				html += '';
			});
			html += '';
			html += '';
			html += '';
		  } else {
			html += '';
			$.each(row, function( index, colData ) {
				html += '';
			});
			html += '';
		  }
		});
		html += '';
		html += '<table class="table table-condensed table-hover table-striped"><thead><tr><th>';
		html += colData;
		html += '</th></tr></thead><tbody><tr><td>';
		html += colData;
		html += '</td></tr></tbody></table>';
		alert(html);
		$('#csv-display').append(html);
	  }
}
function updateItemElement(id) {
	var rsp = confirm("Voulez-vous réellement modifier cet article sur cette facture");
	if (rsp) {
		$.ajax({
			url: "./updateArticleSession",
			method: "POST",
			data: {
				id: id
			},
			success: function(data) {
				if (data.code == '1') {
					var bo = data.body[0];
					$('#inv_art_quantity').val(bo.quantity);
					$('#inv_art_quantity_confirm').val(bo.quantity);
					$('#inv_art_art').val(bo.title);
					$('#inv_art_art_desc').val(bo.designation);
					$('#inv_art_unity_price').val(bo.unity_price);
					$('#inv_art_unity_price_confirm').val(bo.unity_price);
					$('#inv_art_total').val(bo.total);
					$('#inv_art_total_confirm').val(bo.total);
					$('#inv_art_id').val(bo.id);
					
					$('#inv_nvat_total').val(bo.totalNVat);
					$('#inv_tax_amnt').val(bo.tax);
					$('#inv_art_tax').val(bo.taxRate);
					$('#inv_art_tax_confirm').val(bo.taxRate);
					$('#inv_tax_amnt_confirm').val(bo.tax);
					

					$('#inv_art_art').attr('readonly', 'readonly');
					$('#inv_art_art_desc').attr('readonly', 'readonly');
					$('#inv_art_unity_price').attr('readonly', 'readonly');
				}
			}
		});
	}
}

function deleteInvoiceElement(id) {
	var res = confirm("Voulez-vous supprimer cet article de la liste des articles?")
	if (res) {
		$.ajax({
			url: "./deleteInvoiceElement",
			method: "POST",
			data: {
				id: id
			},
			success: function(dta) {
				
				var articles = dta.body;
				if (articles.lenght === 0) {

				} else {
					//$('#inv_art_part').removeAttr('hidden');
					$('#inv_art_detail').empty();
					var sub_total = 0.0;
					var tax = 0.0;	
					for (i = 0; i < articles.length; i++) {
						sub_total = sub_total + articles[i].totalNVat;
						tax = tax + articles[i].tax;
						$('#inv_art_detail').append('<tr>');
						$('#inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].quantity))
						$('#inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].title))
						$('#inv_art_detail').append($('<td style="text-align:right;"></td>').attr('th:text', articles[i].id).text(articles[i].unity_price))
						$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].taxRate)));
						$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].totalNVat)));
						$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].tax)));
						$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].total)))
						$('#inv_art_detail').append($('<td><button type="button" class="btn btn-warning updateitems" onclick="updateItemElement(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-edit"> </i></button><button type="button" class="btn btn-sm btn-danger deleteitems" onclick="deleteCartElement(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
						$('#inv_art_detail').append('</tr>');
					}
					var gdTotal = sub_total + tax;
					$('#inv_total').val(numberFormat(gdTotal));
					$('#inv_tax').val(numberFormat(tax));
					$('#inv_sub_tot').val(numberFormat(sub_total));
				}
			}
		});
	}
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

function deleteCartElement(id) {
	var res = confirm("Voulez-vous supprimer cet article de la liste des articles?")
	if (res) {
		$.ajax({
			url: "./deleteElementcart",
			method: "POST",
			data: {
				id: id
			},
			success: function(dta) {
				var sales = dta.body;
				$('#quotation_detail').empty();
				if (sales.length === 0) {
					$('#quotation_detail').append($('<tr>'))
//					$('#quotation_detail').append($('<td colspan="7"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
					$('#quotation_detail').append($('</tr>'))
				}
				else {
					var total_amount = 0.0;
					var total_vat = 0.0;
					var total_vat_inc = 0.0;
					for (i = 0; i < sales.length; i++) {
								var tot_price = sales[i].quantity * sales[i].unity_price;
								total_amount = total_amount + tot_price;
								total_vat = total_vat + sales[i].totalVAT;
								total_vat_inc = total_vat_inc+sales[i].totalIncVAT;
								
								$('#quotation_detail').append('<tr>');
									$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].quantity))
									$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].title))
									$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].unity_price)))
									$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].tax)))
									$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalNVAT)))
									$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalVAT)))
									$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalIncVAT)))
									$('#quotation_detail').append($('<td><button type="button" class="btn btn-sm btn-danger updatesaleschart" onclick="deleteCartElement(' + sales[i].id + ')" th:data-ref="' + sales[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
								$('#quotation_detail').append('</tr>');
							}
							$('#quotation_detail').append($('<tr>'));
								$('#quotation_detail').append($('<td colspan="4" style="text-align:right;font-weight: bold;"><strong></strong></td>').attr('th:text', 1).text('TOTAL'));
								$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;"></td>').attr('th:text', 1).text(numberFormat(total_amount)));
								$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;"></td>').attr('th:text', 1).text(numberFormat(total_vat)))
								$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;" colspan="2"></td>').attr('th:text', 1).text(numberFormat(total_vat_inc)))
							$('#quotation_detail').append($('</tr>'))
							$('#quo_sub_tot').val(numberFormat(total_amount));
							var rate = $('#tax_rate').val();
							var taxx = 0.0;
							if (rate != '') {
								taxx = total_amount * rate / 100;
							}
							$('#quo_tax').val(numberFormat(taxx));
							var total = total_amount + taxx;
							$('#quo_total').val(numberFormat(total));
							//							//remove values in the fields
							$('#quo_quantity').val('');
							$('#quo_art').val('');
							$('#quo_art_desc').val('');
							$('#quo_unity_price').val('');
							$('#quo_art_tax').val('');
							$('#quo_art_total').val('');
							$('#quo_tax_amnt').val('');
							$('#quo_art_vat_total').val('');
							
							
							$('#quo_sub_tot').val(numberFormat(total_amount));
							$('#quo_tax').val(numberFormat(total_vat));
							$('#quo_total').val(numberFormat(total_vat_inc));
							
							
//					for (i = 0; i < sales.length; i++) {
//						var tot_price = sales[i].quantity * sales[i].unity_price;
//						total_amount = total_amount + tot_price;
//						$('#quotation_detail').append('<tr>');
//						$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].quantity))
//						$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].title))
//						$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].designation))
//						$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(sales[i].unity_price))
//						$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(sales[i].tax))
//						$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(tot_price))
//						$('#quotation_detail').append($('<td><button type="button" class="btn btn-danger updatesaleschart" onclick="deleteCartElement(' + sales[i].id + ')" th:data-ref="' + sales[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
//						$('#quotation_detail').append('</tr>');
//					}
//					$('#quotation_detail').append($('<tr>'))
//					$('#quotation_detail').append($('<td colspan="5"><strong></strong></td>').attr('th:text', 1).text('TOTAL'))
//					$('#quotation_detail').append($('<td style="text-align:right;" colspan="2"m ><strong></strong></td>').attr('th:text', 1).text(total_amount))
//					$('#quotation_detail').append($('</tr>'))
//					//
//					$('#quo_sub_tot').val(total_amount);
//
//					var rate = $('#tax_rate').val();
//					var taxx = 0.0;
//					if (rate != '') {
//						taxx = total_amount * rate / 100;
//					}
//					$('#quo_tax').val(taxx);
//					var total = total_amount + taxx;
//					$('#quo_total').val(total);
//					//remove values in the fields
//					$('#quo_quantity').val('');
//					$('#quo_art').val('');
//					$('#quo_art_desc').val('');
//					$('#quo_unity_price').val('');
//					$('#quo_art_tax').val('');
//					$('#quo_art_total').val('');
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
	$('#addusr').click(function(e) {
		e.preventDefault();
		$('#adduser').modal('show');
	})
	
	function isOBRCheck(url,username,password){
		if(url!='' && username!='' && password!=''){
			$.ajax({
				url:'./isOBRChecked',
				method:'POST',
				cache: false,
        		async: true,
				data:{
					url:url,
					username:username,
					password:password
				},success:function(dta){
					if(dta.code=='1'){
						toastr.success("Vérification des paramètres de connection effectuée avec succès.");
						return true;
					}else{
						toastr.error("Impossible de vérifier les paramètres de connection fournis!");
						return false;
					}
				},error: function() { 
					return false;
            	},complete: function() { 
                	// make sure that you are no longer handling the submit event; clear handler
                	this.off('submit');
                	// actually submit the form
               		this.submit();
             	}
			});
		}else{
			return false;
		}
	}
	$('#com_logo').change(function(ed){
		//$('#imgBase64').val();
		//alert($('#imgBase64').val())
		//alert("test");
		readURL(this);
		//alert($('#imgBase64').val())
	})
	
	$('#ed_com_logo').change(function(ed){
		readURLUpdate(this);
	})
	
	$('.profil').click(function(ev){
		ev.preventDefault();
		$.ajax({
			url:'./getProfil',
			method:'POST',
			data:{
				
			},success:function(data){
				var bod = data.body[0];
				$('#prof_password_confirm').val('');
				$('#prof_password').val('');
				$('#prof_phone').val(bod.telephone);
				$('#prof_email').val(bod.email);
				$('#prof_organisation').val(bod.organisation);
				$('#prof_fst_name').val(bod.firstName);
				$('#prof_lst_name').val(bod.lastName);
				$('#prof_username').val(bod.username);
			}
		});
		$('#updateProfil').modal('show');
	});
	
	$('#prof_saveuser').click(function(){
		var pass_confirm = $('#prof_password_confirm').val();
		var pass = $('#prof_password').val();
		var phone = $('#prof_phone').val();
		var email = $('#prof_email').val();
		var organisation = $('#prof_organisation').val();
		
		//alert(checkPassword(pass));
		var first_name = $('#prof_fst_name').val();
		var last_name = $('#prof_lst_name').val();
		var username = $('#prof_username').val();
		
		
		if(pass!='' && pass!=pass_confirm){
			toastr.error("Prière de bien vouloir confirmer votre mot de passe");
			$('#prof_password').attr('class','form-control form-control-sm form-control-border is-invalid');
			$('#prof_password_confirm').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#prof_password').attr('class','form-control form-control-sm form-control-border is-valid');
			$('#prof_password_confirm').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(phone==''){
			toastr.error('Bien vouloir fournir votre numéro de téléphone');
			$('#prof_phone').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#prof_phone').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(email==''){
			toastr.error('Le champ email est obligatoire');
			$('#prof_email').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#prof_email').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(first_name==''){
			toastr.error('Votre prénom doit etre renseigné');
			$('#prof_fst_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#prof_fst_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(last_name==''){
			toastr.error('Votre nom doit etre renseigné');
			$('#prof_lst_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#prof_lst_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if((last_name!='' && first_name!='' &&  email!='' &&  phone!='' )&& (pass=='' || (pass!='' && pass==pass_confirm))){
			$.ajax({
				url:'./updateProfil',
				method:'POST',
				data:{
					username:username,
					last_name:last_name,
					first_name:first_name,
					email:email,
					phone:phone,
					pass:pass
				},success:function(data){
					if(data.code=='1'){
						toastr.success("Mise jour de votre profil effectué avec succès.Si vous avez modifié votre mot de passe, vous devez utiliser le nouveau mot de passe lors de votre nouvelle connexion.");
						$('#prof_password_confirm').val('');
						$('#prof_password').val('');
						$('#prof_phone').val('');
						$('#prof_email').val('');
						$('#prof_organisation').val('');
						$('#prof_fst_name').val('');
						$('#prof_lst_name').val('');
						$('#prof_username').val('');
						$('#updateProfil').modal('hide');
					}
				}
			});
		}
		
	});
	
	$('#prof_see_pass').click(function(){
		$('#prof_hide_pass').removeAttr('hidden');
		$(this).attr('hidden','hidden');
		$('#prof_password').attr('type','text');
		$('#prof_password_confirm').attr('type','text');
	});
	
	$('#prof_hide_pass').click(function(){
		$('#prof_see_pass').removeAttr('hidden');
		$(this).attr('hidden','hidden');
		$('#prof_password').attr('type','password');
		$('#prof_password_confirm').attr('type','password');
	});
	
	$('#prof_cancel_save').click(function(){
		$('#prof_password_confirm').val('');
		$('#prof_password').val('');
		$('#prof_phone').val('');
		$('#prof_email').val('');
		$('#prof_organisation').val('');
		$('#prof_fst_name').val('');
		$('#prof_lst_name').val('');
		$('#prof_username').val('');
		$('#updateProfil').modal('hide');
	});
	
	$('#saveuser').click(function(ev) {
		ev.preventDefault();
		var stock = $('#stock_manager').is(':checked');
		var admin = $('#admin').is(':checked')
		var email = $('#email').val()
		var password = $('#password').val()
		var organization = $('#organization').val()
		var fst_name = $('#fst_name').val()
		var lst_name = $('#lst_name').val()
		var username = $('#username').val()
		var phone = $('#phone').val()
		var passwordstatus = $('#passwordstatus').is(':checked');
		var userstatus = $('#userstatus').is(':checked');
		
		var com_manager = $('#com_manager').is(':checked');
		var com_head_admin = $('#com_head_admin').is(':checked');
		var com_head_fina = $('#com_head_fina').is(':checked');
		var com_log_officer = $('#com_log_officer').is(':checked');
		var com_hr_off = $('#com_hr_off').is(':checked');
		var com_cash = $('#com_cash').is(':checked');
		var com_account = $('#com_account').is(':checked')
		
		if (username == '' ) {
			toastr.error("Le nom d'utilisateur est obligatoire");
			$('#username').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#username').attr('class','form-control form-control-border is-valid');
		}
		if(fst_name == ''){
			toastr.error("Le prdu nouvel utilisateur est obligatoire");
			$('#fst_name').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#fst_name').attr('class','form-control form-control-border is-valid');
		}
		
		if(lst_name == ''){
			toastr.error("Le nom du nouvel utilisateur est obligatoire");
			$('#lst_name').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#lst_name').attr('class','form-control form-control-border is-valid');
		}
		
		if(password == ''){
			toastr.error("Le mot de passe est obligatoire");
			$('#password').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#password').attr('class','form-control form-control-border is-valid');
		}
		
		if(organization==''){
			toastr.error("Prière de bien séléctionner l'entreprise employeur du nouvel utilisateur");
			$('#organization').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#organization').attr('class','form-control form-control-border is-valid');
		}
		
		if (!admin && !com_manager && !com_head_admin && !com_head_fina && !com_log_officer && !com_hr_off && !com_cash && !com_account) {
			toastr.error("Au moins un role doit etre choisi parmi les roles proposés")
			$('#com_manager').attr("class","form-control is-invalid");
			$('#com_head_admin').attr("class","form-control is-invalid");
			$('#com_head_fina').attr("class","form-control is-invalid");
			$('#com_log_officer').attr("class","form-control is-invalid");
			$('#com_hr_off').attr("class","form-control is-invalid");
			$('#com_cash').attr("class","form-control is-invalid");
			$('#com_account').attr("class","form-control is-invalid");
		} 
		else if(username!='' && fst_name!='' && lst_name!='' && password!='' && organization!='') {
			$.ajax({
				url: "./saveuser",
				method: "POST",
				data: {
					username: username,
					status: "1",
					password: password,
					firstName: fst_name,
					lastName: lst_name,
					organisation: organization,
					telephone: phone,
					email: email,
					enabled: userstatus,
					tokenExpired: passwordstatus,
					adminrole: admin,
					com_manager:com_manager,
					com_head_admin:com_head_admin,
					com_head_fina:com_head_fina,
					com_log_officer:com_log_officer,
					com_hr_off:com_hr_off,
					com_cash:com_cash,
					com_account:com_account
				},
				success: function(dta) {
					toastr.success("User saved successfully")
					$('#adduser').modal('hide');
					location.reload();
				}
			});
		}
	})


	$('#cancelprovsave').click(function(ev) {
		ev.preventDefault
		$('#name').val('');
		$('#tin').val('')
		$('#phone').val('')
		$('#address').val('')
		$('#provider').modal('hide')
	})

	$('#cancel_save').click(function(ev) {
		$('#stock_manager').prop('checked', false)
		$('#admin').prop('checked', false)
		$('#passwordstatus').prop('checked', false)
		$('#userstatus').prop('checked', false)
		$('#stock_manager').val('false')
		$('#admin').val('false')
		$('#email').val('')
		$('#password').val('')
		$('#organization').val('')
		$('#fst_name').val('')
		$('#lst_name').val('')
		$('#username').val('')
		$('#adduser').modal('hide');
	})


	$('.edituser').click(function(ev) {
		ev.preventDefault()
		//var id = 
		$('#ed_email').val('')
		$('#ed_password').val('')
		$('#ed_organization').val('')
		$('#ed_fst_name').val('')
		$('#ed_lst_name').val('')
		$('#ed_username').val('')
		$('#ed_phone').val('')
		$('#ed_stock_manager').prop('checked', false)
		$('#ed_admin').prop('checked', false)
		$('#ed_passwordstatus').prop('checked', false)
		$('#ed_userstatus').prop('checked', false)

		var id = $(this).data('ref')
		if (id != '') {
			$.ajax({
				url: './finduser',
				method: 'POST',
				data: { id: id },
				success: function(dta) {
					if (dta.code == '1') {
						var us = dta.body[0];
						var item = dta.body[0].roles;
						$('#ed_email').val(us.email);
						$('#ed_password').val('');
						$('#ed_organization').val(us.organisation);
						$('#ed_fst_name').val(us.firstName);
						$('#ed_lst_name').val(us.lastName);
						$('#ed_username').val(us.username);
						
						$('#ed_ref').val(us.id);
											
						if(us.username==''){
							$('#ed_username').removeAttr('readonly');
						}
						
						if(us.organisation!=''){
							$('#ed_organization').attr('disabled','disabled');
						}else{
							$('#ed_organization').removeAttr('disabled');
						}

						

						$('#ed_phone').val(us.telephone)
						for (i = 0; i < item.length; i++) {
							if (item[i].name == 'ROLE_COM_ACOUNTANT') {
								$('#ed_com_account').prop('checked', true)
							} else if (item[i].name == 'ROLE_ADMIN') {
								$('#ed_admin').prop('checked', true)
							}else if(item[i].name == 'ROLE_COM_CASH'){
								$('#ed_com_cash').prop('checked', true)
							}else if(item[i].name == 'ROLE_COM_HR_OFF'){
								$('#ed_com_hr_off').prop('checked', true)
							}else if(item[i].name == 'ROLE_COM_LOG_OFF'){
								$('#ed_com_log_officer').prop('checked', true)
							}else if(item[i].name == 'ROLE_COM_HEAD_FINA'){
								$('#ed_com_head_fina').prop('checked', true)
							}else if(item[i].name == 'ROLE_COM_HEAD_ADMIN'){
								$('#ed_com_head_admin').prop('checked', true)
							}else if(item[i].name == 'ROLE_COM_MANAGER'){
								$('#ed_com_manager').prop('checked', true)
							}							
						}
						$('#ed_passwordstatus').prop('checked', us.tokenExpired)
						$('#ed_userstatus').prop('checked', us.enabled)
						$('#edituser').modal('show')
					} else {
						//alert(dta.description)
						$('#edituser').modal('hide')
					}

				}
			})
		}
	})
	// tax management
	$('#addtaxx').click(function(ev) {
		ev.preventDefault();
		$('#addtax').modal('show');
	});
	$('#savetax').click(function(ev) {
		ev.preventDefault();
		var tax_comment = $('#tax_comment').val();
		var tax_rate = $('#tax_rate').val();
		var tax_abbr = $('#tax_abbr').val();
		var tax_title = $('#tax_title').val();
		var tax_status = $('#checkboxDanger1').is(':checked');
		if (tax_title == '') {
			$('#tax_title').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Prière de bien vouloir entrer le titre de cette taxe');
		} else {
			$('#tax_title').attr('class', 'form-control form-control-border is-valid');
		}

		if (tax_abbr == '') {
			$('#tax_abbr').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Prière de bien vouloir indiquer l'abbreviation pour cette taxe");
		} else {
			$('#tax_abbr').attr('class', 'form-control form-control-border is-valid');
		}

		if (tax_rate == '') {
			$('#tax_rate').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Prière de bien vouloir entrer le taux de cette taxe');
		} else {
			$('#tax_rate').attr('class', 'form-control form-control-border is-valid');
		}
		if (tax_rate != '' && tax_abbr != '' && tax_title != '') {
			$('#tax_rate').attr('class', 'form-control form-control-border is-valid');
			$('#tax_abbr').attr('class', 'form-control form-control-border is-valid');
			$('#tax_title').attr('class', 'form-control form-control-border is-valid');
			$.ajax({
				url: './saveTax',
				method: 'POST',
				data: {
					tax_comment: tax_comment,
					tax_rate: tax_rate,
					tax_abbr: tax_abbr,
					tax_status: tax_status,
					tax_title: tax_title
				}, success: function(dta) {
					if (dta.code == '1') {
						toastr.success('Enregistrement de la nouvelle taxe effectué avec succès');
						location.reload();
					}
				}
			});
		}
	});

	$('#cancelsavetax').click(function(ev) {
		ev.preventDefault();
		$('#addtax').modal('hide');
	});
	//End tax management
	// Article management
	$('#add_article').click(function(ev) {
		ev.preventDefault();
		$('#new_article').modal('show');
	});
	$('#saveArticle').click(function(e) {
		//recuperer les valeurs
		//alert('Re');
		var art_title = $('#art_title').val();
		var art_description = $('#art_description').val();
		var art_unity_price = $('#art_unity_price').val();
		var art_tax = $('#art_tax').val();
		var art_status = $('#checkboxDanger1').is(':checked');
		var art_unity = $('#art_unity').val();
		//faire les controls necessaires
		if(art_unity==''){
			$('#art_unity').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Il faut renseigner l'unité de mesure pour cet article")
		}else{
			$('#art_unity').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_title == '') {
			$('#art_title').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Prière de bien vouloir renseigner le nom de l'article'")
		} else {
			$('#art_title').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_unity_price == '') {
			$('#art_unity_price').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Prière de bien vouloir donner le prix unitaire de l'article'")
		} else {
			$('#art_unity_price').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_description == '') {
			$('#art_description').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Prière de bien vouloir donner la description de l'article'")
		} else {
			$('#art_description').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_title != '' && art_unity_price != '' && art_description != '' && art_unity!='') {
			$('#art_description').attr('class', 'form-control form-control-border is-valid');
			$('#art_unity_price').attr('class', 'form-control form-control-border is-valid');
			$('#art_title').attr('class', 'form-control form-control-border is-valid');
			var resp = confirm("Voulez-vous enregistrer cet article ?");
			if (resp) {
				$('#saveArticle').attr('disabled', 'disabled');
				$.ajax({
					url: './saveArticle',
					method: 'POST',
					data: {
						art_title: art_title,
						art_description: art_description,
						art_unity_price: art_unity_price,
						art_tax: art_tax,
						art_status: art_status,
						art_unity:art_unity
					}, success: function(dta) {
						if (dta.code == '1') {
							toastr.success('Enregistrement effectué avec succès');
							location.reload();
						}
					}
				});
			}
		}
		// enregistrer
	});

	$('#cancelsaveArticle').click(function(e) {
		e.preventDefault();
		$('#new_article').modal('hide');
	});

	$('.updateArticle').click(function(ev) {
		var ref = $(this).data('ref');
		$.ajax({
			url:'./findArticle',
			method:'POST',
			data:{
				id:ref
			},success:function(data){
				var body = data.body[0];
				$('#id').val(body.id);
				$('#ed_art_title').val(body.title);
				$('#ed_art_description').val(body.description);
				$('#ed_art_unity_price').val(body.unityPrice);
				$('#ed_art_tax').val(body.tax.id);
				if(body.status){
					$('#ed_checkboxDanger1').attr('checked','checked');
				}else{
					$('#ed_checkboxDanger1').removeAttr('checked');
				}
			}
		})
		$('#ed_article').modal('show');
	});

	$('#ed_saveArticle').click(function(ev){
		var id = $('#id').val();
		var title = $('#ed_art_title').val();
		var description = $('#ed_art_description').val();
		var unityPrice = $('#ed_art_unity_price').val();
		var tax = $('#ed_art_tax').val();
		var artStatus = $('#ed_checkboxDanger1').is(':checked');
		var unity = $('#ed_art_unity').val();
		if(title==''){
			toastr.error("Le titre de l'article est exigé.");
			$('#ed_art_title').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_art_title').attr('class','form-control form-control-sm form-control-border is-valid');
		}

		if(description==''){
			toastr.error("La description de l'article est exigée.");
			$('#ed_art_description').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_art_description').attr('class','form-control form-control-sm form-control-border is-valid');
		}

		if(unityPrice==''){
			toastr.error("Le prix unitaire de l'article est exigé.");
			$('#ed_art_unity_price').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_art_unity_price').attr('class','form-control form-control-sm form-control-border is-valid');
		}

		if(tax==''){
			toastr.error("Au moins une taxe doit être sélectionné dans la liste.");
			$('#ed_art_tax').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_art_tax').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(unity==''){
			$('#ed_art_unity').attr('class','form-control form-control-sm form-control-border is-invalid');
			toastr.error("Priere de bien renseigner l'unite pour cet article.");
		}else{
			
		}

		if(tax!='' && unityPrice!='' && description!='' && title!='' && unity!=''){
			$.ajax({
				url:'./updateArticle',
				method:'POST',
				data:{
					id:id,
					title:title,
					unityPrice:unityPrice,
					description:description,
					tax:tax,
					status:artStatus,
					unity:unity
				},success:function(dta){
					if(dta.code=='1'){
						$('#id').val('');
						$('#ed_art_title').val('');
						$('#ed_art_description').val('');
						$('#ed_art_unity_price').val('');
						$('#ed_art_tax').val('');
						$('#ed_checkboxDanger1').val(false);
						$('#ed_article').modal('hide');
						location.reload();
					}
				},error:function(da){
					toastr.error(da);
				}
			})
		}
	});

	$('#ed_cancelsaveArticle').click(function(ed){
		$('#id').val('');
		$('#ed_art_title').val('');
		$('#ed_art_description').val('');
		$('#ed_art_unity_price').val('');
		$('#ed_art_tax').val('');
		$('#ed_checkboxDanger1').val(false);
		$('#ed_article').modal('hide');
	});

	$('#import_article').click(function(ed){
		$('#importArticles').modal('show');
	});

	$('#csv_import').change(function(ed){
		readCSVFile(this);
	});

	$('#cancel_sub_file').click(function(ev){
		$('#csv_import').val('');
		$('#importArticles').modal('hide');
	});



	//End article management

	//Client management
	$('#add_client').click(function(e) {
		e.preventDefault();
		$('#new_client').modal('show');
	});

	$('.cli_row').click(function(ev){
		var id=$(this).data('ref');
		$.ajax({
			url:'./findInv',
			method:'POST',
			data:{
				id:id
			},success:function(data){
				//loastr.log(data);
			},error:function(err){
			}
		});
	})

	$('.updclients').click(function(){
		let ref = $(this).data('ref');
		if(ref!=''){
			$.ajax({
				url:'./findClient',
				data:{
					id:ref
				},method:'POST',
				success:function(data){
					if(data.code=='1'){
						let bod=data.body[0];
						console.log(bod);
						$('#ed_client').modal('show');
						$('#ed_id').val(bod.id);
						$('#ed_vat_subj').prop('checked', bod.vatSubject);
						$('#ed_cli_address').val(bod.addInfos);
						$('#ed_cli_email').val(bod.email);
						$('#ed_cli_telephon').val(bod.telephone);
						$('#ed_cli_tin').val(bod.tin);
						
						
						$('#ed_cli_province').val(bod.province);
						$('#ed_cli_commune').val(bod.commune);
						$('#ed_cli_district').val(bod.quartier);
						
						$('#ed_cli_type').val(bod.typeClient);
						$('#ed_cli_contact_person').val(bod.contactPerson);
						$('#ed_cli_name').val(bod.name);
						
					}else{
						$('#ed_vat_subj').prop('checked', false);
						$('#ed_cli_address').val('');
						$('#ed_cli_email').val('');
						$('#ed_cli_telephon').val('');
						$('#ed_cli_tin').val('');
						$('#ed_cli_contact_person').val('');
						$('#ed_cli_type').val('');
						$('#ed_cli_name').val('');
						toastr.warning("Impossible de trouver le client avec l'erreur suivante : "+data.description);
					}
				}
			});
		}
	});
	
	$('#ed_cli_tin').change(function(){
		let tin = $(this).val();
		checkTin(tin, "ed_cli_tin");
	});
	
	$('#ed_saveClient').click(function(){
		let client_id= $('#ed_id').val();
		let vat_subj = $('#ed_vat_subj').is(':checked');
		let address = $('#ed_cli_address').val();
		let email = $('#ed_cli_email').val();
		let telephone = $('#ed_cli_telephon').val();
		let tin = $('#ed_cli_tin').val();
		let contact_person = $('#ed_cli_contact_person').val();
		let nam = $('#ed_cli_name').val();
		let province = $('#ed_cli_province').val();
		let commune = $('#ed_cli_commune').val();
		let district = $('#ed_cli_district').val();
		let typeClient = $('#ed_cli_type').val();
		let sigle = $('#ed_cli_abbr').val();
		
		let typeValid = false;
		
		
		if(typeClient==''){
			toastr.error("Le champ type de client est obligatoire.");
			$('#ed_cli_type').attr('class','form-control form-control-border is-invalid');
			typeValid = false;
		}else{
			if(typeClient=='entr' && tin==''){
				toastr.error("Le Nif est obligatoire pour les client de type Entreprise");
				$('#ed_cli_type').attr('class','form-control form-control-border is-invalid');
				$('#ed_cli_tin').attr('class','form-control form-control-border is-invalid');
				$('#ed_cli_type').val('');
				typeValid = false;
			}else{
				typeValid=true;
			}
		}
		
		
		
		
		if(commune==""){
			toastr.error("Le nom de la commune ou reside la commune ou reside le client.");
			$('#ed_cli_commune').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_cli_commune').attr('class','form-control form-control-border is-valid');
		}
		
		if(province ==""){
			toastr.error("Donner le nom de la province ou le client reside.");
			$('#ed_cli_province').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_cli_province').attr('class','form-control form-control-border is-valid');
		}
		
		if(district==""){
			toastr.error("Donner le quartier ou reside le client.");
			$('#ed_cli_district').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_cli_district').attr('class','form-control form-control-border is-valid');
		}
		if(nam==''){
			toastr.error("Le nom doit être donné.");
			$('#ed_cli_name').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_cli_name').attr('class', 'form-control form-control-border is-valid');
		}
		if(telephone==''){
			toastr.error("Prière de bien vouloir renseigner le numéro de téléphone");
			$('#ed_cli_telephon').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_cli_telephon').attr('class','form-control form-control-border is-valid');
		}
		
		if(commune!='' && district!='' && province!='' && telephone!='' && nam!='' && typeValid){
			$.ajax({
				url:'./updateClient',
				method:'POST',
				data:{
					client_id:client_id,
					vat_subj:vat_subj,  
					email:email, 
					telephone:telephone, 
					tin:tin, 
					contact_person:contact_person, 
					nam:nam,
					commune:commune,
					province:province,
					district:district,
					typeClient,
					sigle
				},success:function(dta){
					if(dta.code=='1'){
						location.reload();
						$('#ed_client').modal('hide');
					}
				}
			});
		}
	});
	
	$('#ed_cancelsaveClient').click(function(){
		$('#ed_vat_subj').prop('checked', false);
		$('#ed_cli_address').val('');
		$('#ed_cli_email').val('');
		$('#ed_cli_telephon').val('');
		$('#ed_cli_tin').val('');
		$('#ed_cli_contact_person').val('');
		$('#ed_cli_name').val('');
		$('#ed_client').modal('hide');
	});
	
	$('#cancelsaveClient').click(function(ev) {
		ev.preventDefault();
		$('#new_client').modal('hide');
	});
	
	$('#cli_tin').change(function(){
		var tin = $(this).val();
		checkTin(tin,"cli_tin");
	});

	$('#saveClient').click(function(ev) {
		ev.preventDefault();
		var cli_name = $('#cli_name').val();
		var cli_contact_person = $('#cli_contact_person').val();
		var cli_tin = $('#cli_tin').val();
		var cli_telephon = $('#cli_telephon').val();
		var cli_email = $('#cli_email').val();
		var cli_address = $('#cli_address').val();
		var cli_vat_sub = $('#checkboxDanger1').is(':checked');
		let province = $('#cli_province').val();
		let commune = $('#cli_commune').val();
		let district = $('#cli_district').val();
		let type = $('#cli_type').val();
		let sigle = $('#cli_abbr').val();
		
		let typeValid = false;
		
		
		
		
		if(type==""){
			toastr.error("Le type de client doit etre choisi entre personne physique et entreprise.");
			$('#cli_type').attr('class','form-control form-control-border is-invalid');
			typeValid = false;
		}else{
			if(type=='entr' && cli_tin==''){
				toastr.error("Le Nif est obligatoire pour les client de type Entreprise");
				$('#cli_type').attr('class','form-control form-control-border is-invalid');
				$('#cli_tin').attr('class','form-control form-control-border is-invalid');
				$('#cli_type').val('');
				typeValid = false;
			}else{
				$('#cli_type').attr('class','form-control form-control-border is-valid');
				$('#cli_tin').attr('class','form-control form-control-border is-valid');
				typeValid=true;
			}
			
		}
		
		
		if(commune==""){
			toastr.error("Le nom de la commune ou reside la commune ou reside le client.");
			$('#cli_commune').attr('class','form-control form-control-border is-invalid');
		}else{
			
			$('#cli_commune').attr('class','form-control form-control-border is-valid');
		}
		
		if(province ==""){
			toastr.error("Donner le nom de la province ou le client reside.");
			$('#cli_province').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#cli_province').attr('class','form-control form-control-border is-valid');
		}
		
		if(district==""){
			toastr.error("Donner le quartier ou reside le client.");
			$('#cli_district').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#cli_district').attr('class','form-control form-control-border is-valid');
		}

		if (cli_name == '') {
			$('#cli_name').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride fournir le nom du nouveau client');
		} else {
			$('#cli_name').attr('class', 'form-control form-control-border is-valid');
		}

		if (cli_telephon == '') {
			$('#cli_telephon').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride fournir le Numde tdu nouveau client');
		} else {
			$('#cli_telephon').attr('class', 'form-control form-control-border is-valid');
		}

		if (cli_contact_person == '') {
			$('#cli_contact_person').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride fournir la personne de contact du nouveau client');
		} else {
			$('#cli_contact_person').attr('class', 'form-control form-control-border is-valid');
		}

		if (commune!='' && province!='' && district!='' && cli_name != '' && cli_telephon != '' && cli_contact_person != '' && typeValid) {
			$('#cli_name').attr('class', 'form-control form-control-border is-valid');
			$('#cli_tin').attr('class', 'form-control form-control-border is-valid');
			$('#cli_telephon').attr('class', 'form-control form-control-border is-valid');
			$('#cli_contact_person').attr('class', 'form-control form-control-border is-valid');
			var resp = confirm("Voulez-vous enregistrer ce nouveau client de?")
			if (resp) {
				$('#saveClient').attr('disabled', 'disabled');
				$.ajax({
					url: './saveClient',
					method: 'POST',
					data: {
						cli_name: cli_name,
						cli_contact_person: cli_contact_person,
						cli_tin: cli_tin,
						cli_telephon: cli_telephon,
						cli_email: cli_email,
						cli_address: cli_address,
						cli_vat_sub: cli_vat_sub,
						commune:commune,
						province:province,
						district:district,
						type,sigle
					}, success: function(dta) {
						if (dta.code == '1') {
							toastr.success('Enregistrement du nouveau client effectuavec succès');
							location.reload();
						}
					}
				});
			}
		}

	});
	//End client management


	$('#btnedituser').click(function(ev) {
		ev.preventDefault
		var ed_email = $('#ed_email').val()
		var ed_password = $('#ed_password').val()
		var ed_organization = $('#ed_organization').children('option:selected').val()
		var ed_fst_name = $('#ed_fst_name').val()
		var ed_lst_name = $('#ed_lst_name').val()
		var ed_username = $('#ed_username').val()
		var ed_phone = $('#ed_phone').val()
		var ed_id = $('#ed_ref').val();
		//var stock = $('#ed_stock_manager').is(':checked')
		var admin = $('#ed_admin').is(':checked');
		var com_manager = $('#ed_com_manager').is(':checked');
		var com_head_admin = $('#ed_com_head_admin').is(':checked');
		var com_head_fina = $('#ed_com_head_fina').is(':checked');
		var com_log_officer = $('#ed_com_log_officer').is(':checked');
		var com_hr_off = $('#ed_com_hr_off').is(':checked');
		var com_cash = $('#ed_com_cash').is(':checked');
		var com_account = $('#ed_com_account').is(':checked');
		
		var ed_passwordstatus = $('#ed_passwordstatus').is(':checked')
		var ed_userstatus = $('#ed_userstatus').is(':checked')
		
		if(ed_username == ''){
			toastr.error("Le nom d'utilisateur est obligatoire");
			$('#ed_username').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_username').attr('class','form-control form-control-sm form-control-border is-valid');
		}

		if(ed_fst_name == ''){
			toastr.error("Le nom est obligatoire");
			$('#ed_fst_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_fst_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(ed_lst_name == ''){
			toastr.error("Le prest obligatoire");
			$('#ed_lst_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_lst_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(ed_phone==''){
			toastr.error("Le prest obligatoire");
			$('#ed_phone').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_phone').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		
		if(ed_email==''){
			toastr.error("L'email est obligatoire");
			$('#ed_email').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_email').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if (!com_manager && !com_head_admin && !com_head_fina && !com_log_officer && !com_hr_off && !com_cash && !com_account) {
			toastr.error("Au moins un role doit etre choisi parmi les roles proposés")
			$('#ed_com_manager').attr("class","form-control is-invalid");
			$('#ed_com_head_admin').attr("class","form-control is-invalid");
			$('#ed_com_head_fina').attr("class","form-control is-invalid");
			$('#ed_com_log_officer').attr("class","form-control is-invalid");
			$('#ed_com_hr_off').attr("class","form-control is-invalid");
			$('#ed_com_cash').attr("class","form-control is-invalid");
			$('#ed_com_account').attr("class","form-control is-invalid");
		}
		else if(ed_email!='' && ed_phone!='' && ed_lst_name!='' && ed_fst_name!='' && ed_username!=''){
			//alert('test');
			$.ajax({
				url: './edituser',
				method: 'POST',
				data: {
					id:ed_id,
					username: ed_username,
					status: "1",
					password: ed_password,
					firstName: ed_fst_name,
					lastName: ed_lst_name,
					organisation: ed_organization,
					telephone: ed_phone,
					email: ed_email,
					enabled: ed_userstatus,
					tokenExpired: ed_passwordstatus,
					adminrole: admin,
					com_manager:com_manager, 
					com_head_admin :com_head_admin, 
					com_head_fina:com_head_fina, 
					com_log_officer :com_log_officer, 
					com_hr_off:com_hr_off, 
					com_cash:com_cash, 
					com_account:com_account
				},
				success: function(dta) {
					if(dta.code=='1'){
						toastr.success("Mise jour de l'utilisateur effectuavec succès");
						$('#edituser').modal('hide');
						location.reload();
					}
				}
			})
		}
		//$('#edituser').modal('hide')
		//$('#edituser').modal('hide')
	})

	$('#edit_cancel_save').click(function(ev) {
		ev.preventDefault
		$('#ed_email').val('')
		$('#ed_password').val('')
		$('#ed_organization').val('')
		$('#ed_fst_name').val('')
		$('#ed_lst_name').val('')
		$('#ed_username').val('')
		$('#ed_phone').val('')
		$('#ed_stock_manager').prop('checked', false)
		$('#ed_admin').prop('checked', false)
		$('#ed_passwordstatus').prop('checked', false)
		$('#ed_userstatus').prop('checked', false)
		$('#edituser').modal('hide')
	})

	$('#addclient').click(function(e) {
		e.preventDefault;
		$('#client').modal('show');
	});

	// Quotation management
	$('#quo_client').change(function() {
		var client_id = $(this).children('option:selected').val();
		var same_res = $('#checkboxDanger1').is(':checked');
		//alert(same_res);
		if (client_id != '') {
			$.ajax({
				url: './findClient',
				method: 'POST',
				data: {
					id: client_id
				}, success: function(res) {
					if (res.code == '1') {
						var body = res.body[0];
						var tva_payer = 'NO';
						if (body.vatSubject) {
							tva_payer = 'YES/OUI';
						} else {
							tva_payer = 'NO/NON';
						}
						var address = body.contactPerson + '\n' + body.addInfos + '\nE-mail : ' + body.email + '\nTel : ' + body.telephone + '\nTIN/NIF : ' + body.tin + '\nVAT Subj./Assuj.TVA : ' + tva_payer;
						$('#quo_address').val(address);
						if (same_res) {
							$('#quo_destination').val(address);
							$('#quo_destination').attr('readonly', 'readonly');
						}
					}

				}
			})
		} else {
			$('#quo_address').val('');
			if (same_res) {
				$('#quo_destination').val('');
			} else {
				$('#quo_destination').removeAttr('readonly');
			}
		}
	});

	$('#checkboxDanger1').change(function(ev) {
		var same = $(this).is(':checked');
		if (same) {
			$('#quo_destination').attr('readonly', 'readonly');
			$('#quo_destination').val($('#quo_address').val());
		} else {
			$('#quo_destination').removeAttr('readonly');
		}
	});

	$('#quo_unity_price').change(function(){
		var unitPrice = $(this).val();
		var qty = $('#quo_quantity').val();
		var quo_taxe=$('#quo_taxe').children('option:selected').val();
		var quo_art_tax = $('#quo_art_tax').val();
		if(qty!=''){
			
			var totHTVA = '';
			var totTaxe = 0;
			var totAmount = '';
			var totHTVA = '';
			totHTVA = unitPrice * qty;
			//*
			if(quo_taxe=='2'){
				totTaxe = totHTVA*quo_art_tax/100;
			}
			var totAmount = totHTVA + totTaxe;
			$('#quo_tax_amnt').val(totTaxe);
			$('#quo_art_total').val(totHTVA);
			$('#quo_art_vat_total').val(totAmount);
		}
	});


	$('#quo_quantity').change(function(){
		var unitPrice = $('#quo_unity_price').val();
		var qty = $(this).val();
		var quo_taxe=$('#quo_taxe').children('option:selected').val();
		var quo_art_tax = $('#quo_art_tax').val();
		if(unitPrice!=''){
			var totHTVA = '';
			var totTaxe = 0;
			var totAmount = '';
			var totHTVA = '';
			totHTVA = unitPrice * qty;
			//*
			if(quo_taxe=='2'){
				totTaxe = totHTVA*quo_art_tax/100;
			}
			var totAmount = totHTVA + totTaxe;
			$('#quo_tax_amnt').val(totTaxe);
			$('#quo_art_total').val(totHTVA);
			$('#quo_art_vat_total').val(totAmount);
		}
	});
//*
	$('#quo_art').change(function(){
		var unitPrice = $('#quo_unity_price').val();
		var qty = $('#quo_quantity').val();
		var quo_taxe=$('#quo_taxe').children('option:selected').val();
		var quo_art_tax = $('#quo_art_tax').val();
		if(qty!=''){
			var totHTVA = '';
			var totTaxe = 0;
			var totAmount = '';
			var totHTVA = '';
			totHTVA = unitPrice * qty;
			
			if(quo_taxe=='2'){
				totTaxe = totHTVA*quo_art_tax/100;
			}
			var totAmount = totHTVA + totTaxe;
			$('#quo_tax_amnt').val(totTaxe);
			$('#quo_art_total').val(totHTVA);
			$('#quo_art_vat_total').val(totAmount);
		}
	}); //*/


	$('#add_article_quo').click(function(ev) {
		ev.preventDefault();
		var quo_quantity = $('#quo_quantity').val();
		var quo_art = $('#quo_art').val();
		var quo_art_desc = $('#quo_art_desc').val();
		var quo_unity_price = $('#quo_unity_price').val();
		var quo_art_tax = $('#quo_art_tax').val();
		var quo_art_total = $('#quo_art_total').val();
		var quo_tax_amnt = $('#quo_tax_amnt').val();
		var quo_taxe=$('#quo_taxe').children('option:selected').val();
		
		//alert(quo_taxe);
		if(quo_taxe==''){
			$('#quo_taxe').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Bien vouloir choisir le mode de calcul de TVA');
		}else{
			$('#quo_taxe').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(quo_tax_amnt=='' && quo_taxe=='2'){
			$('#quo_tax_amnt').attr('class', 'form-control form-control-border is-invalid');
			//toastr.error('Bien vouloir choisir le mode de calcul de TVA');
		}else{
			$('#quo_tax_amnt').attr('class', 'form-control form-control-border is-valid');
		}
		
		if (quo_quantity == '') {
			$('#quo_quantity').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride bien vouloir renseigner les quantitpour cet article');
		} else {
			$('#quo_quantity').attr('class', 'form-control form-control-border is-valid');
		}

		if (quo_unity_price == '') {
			$('#quo_unity_price').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride bien vouloir renseigner le prix unitaire pour cet article');
		} else {
			$('#quo_unity_price').attr('class', 'form-control form-control-border is-valid');
		}

		if (quo_art == '') {
			$('#quo_art').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride bien vouloir renseigner l\'article ajouter dans la liste des articles');
		} else {
			$('#quo_art').attr('class', 'form-control form-control-border is-valid');
		}
		if (quo_quantity != '' && quo_unity_price != '' && quo_art != '' && quo_taxe!='') {
			$('#quo_art').attr('class', 'form-control form-control-border is-valid');
			$('#quo_unity_price').attr('class', 'form-control form-control-border is-valid');
			$('#quo_quantity').attr('class', 'form-control form-control-border is-valid');
			$('#quo_tax_amnt').attr('class', 'form-control form-control-border is-valid');
			$('#quo_art_vat_total').attr('class','form-control form-control-border is-valid');
			//
			var total = quo_quantity * quo_unity_price;
			var taxAmnt = 0.0;
			if(quo_taxe=='2'){
				taxAmnt = total*quo_art_tax/100;
			}else{
				$('#quo_art_tax').val(0.0);
				taxAmnt = 0.0;
			}
			var totall = total+taxAmnt;
			$('#quo_art_total').val(total);
			$('#quo_tax_amnt').val(taxAmnt);
			$('#quo_art_vat_total').val(totall);
			$.ajax({
				url: './cartManagement',
				method: 'POST',
				data: {
					quantity: quo_quantity,
					art: quo_art,
					art_desc: "",
					isTaxable:quo_taxe,
					unity_price: quo_unity_price,
					art_tax: quo_art_tax,
					art_is_vat:quo_taxe,
					art_total: quo_art_total
				}, success: function(dta) {
					if (dta.code == '1') {
						var sales = dta.body;
						$('#quotation_detail').empty();
						if (sales.length === 0) {
							$('#quotation_detail').append($('<tr>'))
							$('#quotation_detail').append($('<td colspan="7"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
							$('#quotation_detail').append($('</tr>'))
						}
						else {
							var total_amount = 0.0;
							var total_vat = 0.0;
							var total_vat_inc = 0.0;
							for (i = 0; i < sales.length; i++) {
								var tot_price = sales[i].quantity * sales[i].unity_price;
								total_amount = total_amount + tot_price;
								total_vat = total_vat + sales[i].totalVAT;
								total_vat_inc = total_vat_inc+sales[i].totalIncVAT;
								
								$('#quotation_detail').append('<tr>');
									$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].quantity))
									$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].title))
									$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].unity_price)))
									$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].tax)))
									$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalNVAT)))
									$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalVAT)))
									$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalIncVAT)))
									$('#quotation_detail').append($('<td><button type="button" class="btn btn-sm btn-danger updatesaleschart" onclick="deleteCartElement(' + sales[i].id + ')" th:data-ref="' + sales[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
								$('#quotation_detail').append('</tr>');
							}
							$('#quotation_detail').append($('<tr>'));
								$('#quotation_detail').append($('<td colspan="4" style="text-align:right;font-weight: bold;"><strong></strong></td>').attr('th:text', 1).text('TOTAL'));
								$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;"></td>').attr('th:text', 1).text(numberFormat(total_amount)));
								$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;"></td>').attr('th:text', 1).text(numberFormat(total_vat)))
								$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;" colspan="2"></td>').attr('th:text', 1).text(numberFormat(total_vat_inc)))
							$('#quotation_detail').append($('</tr>'))
							$('#quo_sub_tot').val(numberFormat(total_amount));
							
							
							$('#quo_sub_tot').val(numberFormat(total_amount));
							$('#quo_tax').val(numberFormat(total_vat));
							$('#quo_total').val(numberFormat(total_vat_inc));
							
							
							//							//remove values in the fields
							$('#quo_quantity').val('');
							$('#quo_art').val('');
							$('#quo_art_desc').val('');
							$('#quo_unity_price').val('');
							$('#quo_art_tax').val('');
							$('#quo_art_total').val('');
							$('#quo_tax_amnt').val('');
							$('#quo_art_vat_total').val('');
						}
					}
				}
			});
		}
	});

	$('#quo_art').change(function(ev) {
		ev.preventDefault();
		var id = $(this).children('option:selected').val();
		if (id != '') {
			$.ajax({
				url: './findArticle',
				method: 'POST',
				data: {
					id: id
				}, success: function(ef) {
					var article = ef.body[0];
					$('#quo_unity_price').val(article.unityPrice);
					$('#quo_art_desc').val(article.description);
					$('#quo_art_tax').val(article.tax.rate);
				}
			});
		} else {
			$('#quo_unity_price').attr('class', 'form-control form-control-border is-invalid');
		}
	});

	$('#save_quotation').click(function(ev) {
		ev.preventDefault();
		var quo_client = $('#quo_client').children('option:selected').val();
		var quo_date = $('#quo_date').val();
		var quo_sub_tot = $('#quo_sub_tot').val();
		var quo_tax = $('#quo_tax').val();
		var quo_total = $('#quo_total').val();

		var tax_rate = $('#tax_rate').val();
		var quo_taxe = $('#quo_taxe').val();
		var quo_destination = $('#quo_destination').val();
		var quo_address = $('#quo_address').val();
		var quo_comment = $('#quo_comment').val();
		//voir total
		var quo_currency = $('#quo_currency').val();
		//voir sub total

		//voir date quotation
		
		if(quo_currency==''){
			toastr.error('Pride bien vouloir la monnaie de facturation.');
			$('#quo_currency').attr('class', 'form-control form-control-border is-invalid');
		}else{
			$('#quo_currency').attr('class', 'form-control form-control-border is-valid');
		}
		
		if (quo_date == '') {
			toastr.error('Pride bien vouloir donner la date de facturation.');
			$('#quo_date').attr('class', 'form-control form-control-border is-invalid');
		} else {
			$('#quo_date').attr('class', 'form-control form-control-border is-valid');
		}
		//Voir client
		if (quo_client == '') {
			toastr.error('Pride bien vouloir choisir le client pour ce devis.');
			$('#quo_client').attr('class', 'form-control form-control-border is-invalid');
		} else {
			$('#quo_client').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(quo_taxe==''){
			toastr.error('Pride bien vouloir choisir le mode d\'application de la TVA');
			$('#quo_taxe').attr('class', 'form-control form-control-border is-invalid');
		}else{
			$('#quo_taxe').attr('class', 'form-control form-control-border is-valid');
		}

		if (quo_sub_tot == '' || quo_total == '') {
			toastr.error('Pride bien charger la liste des articles pour ce devis.');
			$('#quo_sub_tot').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_tax').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_total').attr('class', 'form-control form-control-border is-invalid');

			$('#quo_quantity').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_art').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_art_desc').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_unity_price').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_art_tax').attr('class', 'form-control form-control-border is-invalid');
			$('#quo_art_total').attr('class', 'form-control form-control-border is-invalid');
		} else {
			$('#quo_sub_tot').attr('class', 'form-control form-control-border is-valid');
			$('#quo_tax').attr('class', 'form-control form-control-border is-valid');
			$('#quo_total').attr('class', 'form-control form-control-border is-valid');
			$('#quo_quantity').attr('class', 'form-control form-control-border is-valid');
			$('#quo_art').attr('class', 'form-control form-control-border is-valid');
			$('#quo_art_desc').attr('class', 'form-control form-control-border is-valid');
			$('#quo_unity_price').attr('class', 'form-control form-control-border is-valid');
			$('#quo_art_tax').attr('class', 'form-control form-control-border is-valid');
			$('#quo_art_total').attr('class', 'form-control form-control-border is-valid');
		}

		if (quo_date != '' && quo_client != '' && quo_sub_tot != '' && quo_total != '' && quo_currency!='') {
			var resp = confirm("Voulez vous renregistrer cette demande de cotation?");
			if (resp) {
				$('#save_quotation').attr('disabled', 'disabled');
				$.ajax({
					url: './saveQuotation',
					method: 'POST',
					data: {
						quo_client: quo_client,
						quo_date: quo_date,
						quo_sub_tot: quo_sub_tot,
						quo_tax: quo_tax,
						quo_total: quo_total,
						quo_currency:quo_currency,
						tax_rate: '',
						quo_taxe: quo_taxe,
						quo_destination: quo_destination,
						quo_address: quo_address,
						quo_comment: quo_comment
					}, success: function(dta) {
						if (dta.code == '1') {
							toastr.success("Enregistrement de la demande de cotation effectuavec succès");
							location.reload();
						}
					}
				});
			}
		}
	});
	
	
	$('.deleteQuotation').click(function(){
		var ref = $(this).data('ref');
		if(ref!=''){
			$.ajax({
				url:'./deleteQuotation',
				method:'POST',
				data:{
					id:ref
				},success:function(dta){
					if(dta.code=='1'){
						toastr.success('Désactivation de la facture proforma effectuée avec succès.');
						location.reload();
					}else{
						toastr.success('Impossible de désactiver la facture proforma car elle est déjà utilisé sur un bon de commande.');
					}
				},error:function(){
					toastr.error('Une erreur à été detecté lors de la Désactivation de la facture proforma. Veuillez réessayer plus tard, mais si l\'erreur persiste, contacter l\'administrateur de l\'application');
					//alert("Error");
				}
			});
		}
	});

	$('.viewQuotation').click(function() {
		var ref = $(this).data('ref');
		if (ref != '') {
			$.ajax({
				url: './searchQuotation',
				method: 'POST',
				data: {
					id: ref
				}, success: function(dta) {
					if (dta.code == '1') {
						var quot = dta.body[0];
						var org = quot.organisation;
						//organisation population
						$('#quota_logo').attr('src',org.logo);
						
						$('#org_name').html(org.name);
						var adr = org.addressNumber+','+org.addressAvenue+', '+org.addressQuartier;
						$('#org_address_gen').html(adr); 
						var adrrr = org.addressProvince+'-'+org.addressCommune;
						$('#org_address_prov').html(adrrr); 
						$('#org_pobox').html(org.postalNumber); 
						$('#org_telephone').html(org.telephone); 
						$('#org_tin').html(org.tin); 
						$('#org_trade_number').html(org.tradeNumber); 
						$('#org_fiscal_center').html(org.fiscalCenter); 
						$('#org_juris_form').html(org.juridictionForm); 
						if(org.vatpayer){
							$('#org_isTva').html('OUI/YES'); //   
						}else{
							$('#org_isTva').html('NON/NO'); // 
						}
						//
						$('#org_activity_secter').html(org.activitySecter); 
						//end organisation population
						$('#quotation_list').attr('class', 'col-lg-5 connectedSortable');
						$('#quotation_det').removeAttr('hidden');
						$('#quot_ref').text(quot.reference);
						$('#quot_date').text(formatDate(quot.quotationDate));
						$('#quo_currency').text(quot.currency);
						$('#cli_raison').text(': '+quot.client.name);
						$('#cli_NIF').text(': '+quot.client.tin);
						$('#quo_taxable').text(': '+quot.isTaxable);
						$('#cli_adresse').text(': '+quot.client.addInfos+', T: '+quot.client.telephone+', e-mail : '+quot.client.email);
						
						$('#stamp').attr('src','../img/cancelled.jpg');

						if(quot.status=='0'){
							$('#stamp').attr('src','../img/cancelled.jpg');
						}else if(quot.status=='1'){
							$('#stamp').attr('src','../img/accepted_1.jpg');
						}else if(quot.status=='2'){
							$('#stamp').attr('src','../img/accepted.jpg');
						}
						

						if (quot.client.vatSubject) {
							$('#cli_isvatpayer').text(': OUI/YES');
						} else {
							$('#cli_isvatpayer').text(': NON/NO');
						}
						var link = './printQ/' + ref;
						$('#quotation_print').attr('href', link);		
						
						
						var art = quot.articles;
						$('#quo_items_detail').empty();
						if (art.length === 0) {
							$('#quo_items_detail').append($('<tr>'))
							$('#quo_items_detail').append($('<td colspan="4"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
							$('#quo_items_detail').append($('</tr>'))
						}
						else {
							var pvhtva = 0.0;
							var tva = 0.0;
							for (i = 0; i < art.length; i++) {
								
								$('#quo_items_detail').append($('<tr>'));
									$('#quo_items_detail').append($('<td></td>').attr('th:text', i).text(art[i].quantity));
									$('#quo_items_detail').append($('<td></td>').attr('th:text', i).text(art[i].title));
									
									$('#quo_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].unityPrice))));
									$('#quo_items_detail').append($('<td></td>').attr('th:text', i).text((art[i].vat)));
									$('#quo_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].totalNVAT))));
									$('#quo_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].totalVat))));
									$('#quo_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].totalWVAT))));
								$('#quo_items_detail').append($('</tr>'));
							}
							
							$('#quo_items_detail').append($('<tr>'));
							$('#quo_items_detail').append($('<td colspan="4" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("PRIX DE VENTE HTVA"));
							$('#quo_items_detail').append($('<td  colspan="3" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text(numberFormat(quot.subTotal)));
							$('#quo_items_detail').append($('</tr>'));
	
							$('#quo_items_detail').append($('<tr>'));
							$('#quo_items_detail').append($('<td colspan="4" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("TVA"));
							$('#quo_items_detail').append($('<td  colspan="3" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text(numberFormat(quot.totalTax)));
							$('#quo_items_detail').append($('</tr>'));
	
							$('#quo_items_detail').append($('<tr>'));
							$('#quo_items_detail').append($('<td colspan="4" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("TOTAL TVAC"));
							$('#quo_items_detail').append($('<td colspan="3" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text(numberFormat(quot.grandTotal)));
							$('#quo_items_detail').append($('</tr>'));
							$('#qrcode').empty();
							
						}
						
						
						
							
					}
				}
			});
		}
		//window.addEventListener("load", window.print());
	});
	$('.updateQuotation').click(function(){
		var id = $(this).data('ref');
		if(id!=''){
			$.ajax({
				url:'./searchQuotation',
				method:'POST',
				data:{
					id:id
				},success:function(dta){
					$('#editQuotation').modal('show');
				},error:function(dta){
					toastr.error(dta);
					//alert('Error');
				}
			});
		}
	});
//	$('#quotation_print').click(function(t) {
//		var id = $('#quotation_id').val();
//		//alert("To print the quotation : " + id)
//	});
	// Quotation management
	$('#quo_taxe').change(function() {
		var conf = confirm("Voulez vous rchanger le mode de calcul de la TVA?");
		if(conf){
			var id = $(this).children('option:selected').val();		
			if (id != '') {
				$.ajax({
					url: './taxCalcul',
					method: 'POST',
					data: {
						typ:id
					}, success: function(res) {
						if (res.code == '1') {
							var sales = res.body;
							$('#quotation_detail').empty();
							if (sales.length === 0) {
								$('#quotation_detail').append($('<tr>'))
								$('#quotation_detail').append($('<td colspan="7"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
								$('#quotation_detail').append($('</tr>'))
							}
							else {
								var total_amount = 0.0;
								var total_vat = 0.0;
								var total_vat_inc = 0.0;
								for (i = 0; i < sales.length; i++) {
									var tot_price = sales[i].quantity * sales[i].unity_price;
									total_amount = total_amount + tot_price;
									total_vat = total_vat + sales[i].totalVAT;
									total_vat_inc = total_vat_inc+sales[i].totalIncVAT;
									
									$('#quotation_detail').append('<tr>');
										$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].quantity))
										$('#quotation_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].title))
										$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].unity_price)))
										$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].tax)))
										$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalNVAT)))
										$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalVAT)))
										$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalIncVAT)))
										$('#quotation_detail').append($('<td><button type="button" class="btn btn-sm btn-danger updatesaleschart" onclick="deleteCartElement(' + sales[i].id + ')" th:data-ref="' + sales[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
									$('#quotation_detail').append('</tr>');
								}
								$('#quotation_detail').append($('<tr>'));
									$('#quotation_detail').append($('<td colspan="4" style="text-align:right;font-weight: bold;"><strong></strong></td>').attr('th:text', 1).text('TOTAL'));
									$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;"></td>').attr('th:text', 1).text(numberFormat(total_amount)));
									$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;"></td>').attr('th:text', 1).text(numberFormat(total_vat)))
									$('#quotation_detail').append($('<td style="text-align:right;font-weight: bold;" colspan="2"></td>').attr('th:text', 1).text(numberFormat(total_vat_inc)))
								$('#quotation_detail').append($('</tr>'))
								$('#quo_sub_tot').val(numberFormat(total_amount));
								
								
								$('#quo_sub_tot').val(numberFormat(total_amount));
								$('#quo_tax').val(numberFormat(total_vat));
								$('#quo_total').val(numberFormat(total_vat_inc));
								
								
								//							//remove values in the fields
								$('#quo_quantity').val('');
								$('#quo_art').val('');
								$('#quo_art_desc').val('');
								$('#quo_unity_price').val('');
								$('#quo_art_tax').val('');
								$('#quo_art_total').val('');
								$('#quo_tax_amnt').val('');
								$('#quo_art_vat_total').val('');
							}
						}
					}
				});
			} else {
				$('#tax_rate').val('');
			}
		}
		
	});
	//organisation management
	$('#add_company').click(function(ev) {
		$('#new_company').modal('show');
	});

	$('#saveCompany').click(function() {
		var isVatSubjected = $('#checkboxDanger1').is(':checked');
		var com_more_info = $('#com_more_info').val();
		var com_jur_form = $('#com_jur_form').val();
		var com_activity_sector = $('#com_activity_sector').val();
		var com_fiscal_center = $('#com_fiscal_center').val();
		var com_tin = $('#com_tin').val();
		var com_name = $('#com_name').val();
		var lv_subj = $('#lv_subj').is(':checked');
		var tc_subj = $('#tc_subj').is(':checked');
		var account_title=$('#account_title').val();
		var bank_account=$('#bank_account').val();
		var bank_name=$('#bank_name').val();
		var com_repr_name=$('#com_repr_name').val();
		var com_repr_position=$('#com_repr_position').val();
		var image_logo = $('#logo_value').val();
		var trade_num = $('#trade_num').val();
		var com_province = $('#com_province').val();
		var com_commune = $('#com_commune').val();
		var com_district = $('#com_district').val();
		var com_street = $('#com_street').val();
		var com_street_number = $('#com_street_number').val();
		var com_postal_number = $('#com_postal_number').val();
		var com_telephone = $('#com_telephone').val();
		//
		var status =$('#obr_status').is(':checked');
		var url = $('#obr_url').val();
		var username = $('#obr_username').val();
		var password = $('#obr_password').val();
		
		//alert(image_logo);
		if(image_logo==''){
			$('#com_logo').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Un logo de la sociest requis");
		}else{
			$('#com_logo').attr('class', 'form-control form-control-border is-valid');
		}
		if(trade_num==''){
			$('#trade_num').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Le RC doit etre complété");
		}else{
			$('#trade_num').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(com_province==''){
			$('#com_province').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Le province etre complété");
		}else{
			$('#com_province').attr('class', 'form-control form-control-border is-valid');
		}
		
		
		if(com_commune==''){
			$('#com_commune').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Le nom de la commune est obligatoire");
		}else{
			$('#com_commune').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(com_district==''){
			$('#com_district').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Le nom du quartier est obligatoire");
		}else{
			$('#com_district').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(com_telephone==''){
			$('#com_telephone').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Le numde test obligatoire");
		}else{
			$('#com_telephone').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(bank_name==''){
			$('#bank_name').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Le nom de votre banque est requis avant d'enregistrer votre société");
		}else{
			$('#bank_name').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(bank_account==''){
			$('#bank_account').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Veuillez renseigner votre numde compte SVP!");
		}else{
			$('#bank_account').attr('class', 'form-control form-control-border is-valid');
		}
		if(account_title==''){
			$('#account_title').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Veuillez fournir l'intitulde votre compte bancaire");
		}else{
			$('#account_title').attr('class', 'form-control form-control-border is-valid');
		}
		
		if (com_more_info == '') {
			$('#com_more_info').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Pride bien vouloir donner les informations en dcomme l'avenue, le rue, le Tetc.");
		} else {
			$('#com_more_info').attr('class', 'form-control form-control-border is-valid');
		}

		if (com_jur_form == '') {
			$('#com_jur_form').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Pride bien vouloir donner la forme juridique de la société");
		} else {
			$('#com_jur_form').attr('class', 'form-control form-control-border is-valid');
		}

		if (com_activity_sector == '') {
			$('#com_activity_sector').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Bien vouloir renseigner le secteur d'activitde la société");
		} else {
			$('#com_activity_sector').attr('class', 'form-control form-control-border is-valid');
		}

		if (com_fiscal_center == '') {
			$('#com_fiscal_center').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Bien vouloir renseigner le centre fiscal de la société");
		} else {
			$('#com_fiscal_center').attr('class', 'form-control form-control-border is-valid');
		}

		if (com_tin == '') {
			$('#com_tin').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Bien vouloir renseigner le Numd'identification fiscale de la société");
		} else {
			$('#com_tin').attr('class', 'form-control form-control-border is-valid');
		}

		if(com_repr_name==''){
			$('#com_repr_name').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Bien vouloir renseigner le nom du reprde la société");
		}else{
			$('#com_repr_name').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(com_repr_position==''){
			$('#com_repr_position').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Bien vouloir renseigner la position (si DG par exemple) du reprde la société");
		}else{
			$('#com_repr_position').attr('class', 'form-control form-control-border is-valid');
		}
		if (com_name == '') {
			$('#com_name').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Bien vouloir renseigner le nom de la société");
		} else {
			$('#com_name').attr('class', 'form-control form-control-border is-valid');
		}

		if(status &&(url=='' || username=='' || password=='')){
			$('#obr_url').attr('class','form-control form-control is-invalid');
			$('#obr_username').attr('class','form-control form-control is-invalid');
			$('#obr_password').attr('class','form-control form-control is-invalid');
		}else{
			if (image_logo!='' && trade_num!='' && com_province!='' && com_commune!='' && com_district!='' && com_telephone!='' && com_name != '' && com_tin != '' && com_fiscal_center != '' && com_activity_sector != '' && com_jur_form != '' && com_more_info != '' && account_title!='' && bank_account!='' && bank_name!='') {
			$('#com_name').attr('class', 'form-control form-control-border is-valid');
			$('#com_tin').attr('class', 'form-control form-control-border is-valid');
			$('#com_fiscal_center').attr('class', 'form-control form-control-border is-valid');
			$('#com_activity_sector').attr('class', 'form-control form-control-border is-valid');
			$('#com_jur_form').attr('class', 'form-control form-control-border is-valid');
			$('#com_more_info').attr('class', 'form-control form-control-border is-valid');
			$('#trade_num').attr('class', 'form-control form-control-border is-valid');
			$('#com_province').attr('class', 'form-control form-control-border is-valid');
			$('#com_commune').attr('class', 'form-control form-control-border is-valid');
			$('#com_district').attr('class', 'form-control form-control-border is-valid');
			$('#com_street').attr('class', 'form-control form-control-border is-valid');
			$('#com_street_numbe').attr('class', 'form-control form-control-border is-valid');
			$('#com_postal_number').attr('class', 'form-control form-control-border is-valid');
			$('#com_telephone').attr('class', 'form-control form-control-border is-valid');
			//*
			$.ajax({
				url: './saveCompany',
				method: 'POST',
				data: {
					isVatSubjected: isVatSubjected,
					com_more_info: com_more_info,
					com_jur_form: com_jur_form,
					com_activity_sector: com_activity_sector,
					com_fiscal_center: com_fiscal_center,
					com_tin: com_tin,
					status:status,
					url:url,
					username:username,
					password:password,
					com_name: com_name,
					account_title: account_title,
					bank_account:bank_account, 
					bank_name:bank_name,
					lv_subj:lv_subj,
					tc_subj:tc_subj,
					com_repr_name:com_repr_name,
					com_repr_position:com_repr_position,
					trade_num:trade_num, 
					com_province:com_province,
					com_commune:com_commune, 
					com_district:com_district, 
					com_street:com_street, 
					com_street_number:com_street_number, 
					com_postal_number:com_postal_number,
					com_telephone:com_telephone,
					logo:image_logo 
				}, success: function(data) {
					if (data.code == '1') {
						$('#com_more_info').val('');
						$('#com_jur_form').val('');
						$('#com_activity_sector').val('');
						$('#com_fiscal_center').val('');
						$('#com_tin').val('');
						$('#com_name').val('');
						$('#logo_value').val('');
						
						$('#account_title').val('');
						$('#bank_account').val('');
						$('#bank_name').val('');
						$('#com_repr_name').val('');
						$('#com_repr_position').val('');
						$('#logo_value').val('');
						$('#trade_num').val('');
						$('#com_province').val('');
						$('#com_commune').val('');
						$('#com_district').val('');
						$('#com_street').val('');
						$('#com_street_number').val('');
						$('#com_postal_number').val('');
						$('#com_telephone').val('');
						$('#logo_img64').attr('src','#');
						$('#logo_img64').attr('hidden','hidden');
						
						$('#new_company').modal('hide');
						location.reload();
					}
				}
			}); //*/
		}

			/*
			if(status && !isOBRCheck(url,username,password)){//if(status && !isOBRCheck(url,username,password)){
				alert("Test yahomba ......");
			}else{
				alert("EGO ......");
			}//*/
		}
		
	});
	
	$('.com_edit').click(function(ev){ 
		ev.preventDefault();
		var ref = $(this).data('ref');
		$.ajax({
			url:'./getOrganisation',
			method:'POST',
			data:{
				id:ref
			},success:function(dta){
				var org = dta.body[0];
				var interco = org.intercons;
				$('#ed_com_more_info').val(org.adress);
				$('#ed_com_jur_form').val(org.juridictionForm);
				$('#ed_com_activity_sector').val(org.activitySecter);
				$('#ed_com_fiscal_center').val(org.fiscalCenter);
				$('#ed_com_tin').val(org.tin);
				$('#ed_com_name').val(org.name);
				$('#ed_account_title').val(org.accountTitle);
				$('#ed_bank_account').val(org.bankAccount);
				$('#ed_bank_name').val(org.bankName);
				$('#ed_com_repr_name').val(org.representativeName);
				$('#ed_com_repr_position').val(org.representativePosition);
				$('#ed_trade_num').val(org.tradeNumber);
				$('#ed_com_province').val(org.addressProvince);
				$('#ed_com_commune').val(org.addressCommune);
				$('#ed_com_district').val(org.addressQuartier);
				$('#ed_com_street').val(org.addressAvenue);
				$('#ed_com_street_number').val(org.addressNumber);
				$('#ed_com_postal_number').val(org.postalNumber);
				$('#ed_com_telephone').val(org.telephone);
				$('#com_id').val(org.id);
				$('#ed_vat_subj').prop('checked', org.vatpayer);
				$('#ed_lv_subj').prop('checked', org.lvpayer);
				$('#ed_tc_subj').prop('checked', org.ctpayer);
				if(interco.length>0){
					$('#ed_obr_status').prop('checked', true);
					$('#ed_obr_url').val(interco[0].url);
					$('#ed_obr_username').val(interco[0].username);
					$('#ed_obr_password').val(interco[0].password);
				}else{
					$('#ed_obr_status').prop('checked', false);
					$('#ed_obr_url').val('');
					$('#ed_obr_username').val('');
					$('#ed_obr_password').val('');
				}
				if(org.logo!=''){
					$('#ed_logo_img64').attr('src',org.logo);
					$('#ed_logo_img64').removeAttr('hidden');
					$('#ed_logo_value').val(org.logo);
				}else{
					$('#ed_logo_img64').attr('src','#');
					$('#ed_logo_img64').attr('hidden','hidden');
					$('#ed_logo_value').val('');
				}
			}
		})
		$('#edit_company').modal('show');
	});
	
	$('#ed_cancelsaveCompany').click(function(){
		$('#ed_com_more_info').val('');
		$('#ed_com_jur_form').val('');
		$('#ed_com_activity_sector').val('');
		$('#ed_com_fiscal_center').val('');
		$('#ed_com_tin').val('');
		$('#ed_com_name').val('');
		$('#ed_account_title').val('');
		$('#ed_bank_account').val('');
		$('#com_id').val('');
		$('#ed_bank_name').val('');
		$('#ed_com_repr_name').val('');
		$('#ed_com_repr_position').val('');
		$('#ed_trade_num').val('');
		$('#ed_com_province').val('');
		$('#ed_com_commune').val('');
		$('#ed_com_district').val('');
		$('#ed_com_street').val('');
		$('#ed_com_street_number').val('');
		$('#ed_com_postal_number').val('');
		$('#ed_com_telephone').val('');
		$('#ed_vat_subj').prop('checked', false);
		$('#ed_lv_subj').prop('checked', false);
		$('#ed_tc_subj').prop('checked', false);
		$('#ed_logo_img64').attr('src','#');
		$('#ed_logo_img64').attr('hidden','hidden');
		$('#ed_logo_value').val('');
		$('#edit_company').modal('hide');
	});
	
	$('#ed_saveCompany').click(function(ev){
		//alert("Testttttt");
		//ev.preventDefault();
		var com_more_info = $('#ed_com_more_info').val();
		var com_jur_form = $('#ed_com_jur_form').val();
		var com_activity_sector = $('#ed_com_activity_sector').val();
		var com_fiscal_center = $('#ed_com_fiscal_center').val();
		var com_tin = $('#ed_com_tin').val();
		var com_name = $('#ed_com_name').val();
		var account_title = $('#ed_account_title').val();
		var bank_account = $('#ed_bank_account').val();
		var bank_name = $('#ed_bank_name').val();
		var com_repr_name = $('#ed_com_repr_name').val();
		var com_repr_position = $('#ed_com_repr_position').val();
		var trade_num = $('#ed_trade_num').val();
		var com_province = $('#ed_com_province').val();
		var com_commune = $('#ed_com_commune').val();
		var com_district = $('#ed_com_district').val();
		var com_street = $('#ed_com_street').val();
		var com_street_num = $('#ed_com_street_number').val();
		var com_postal_num = $('#ed_com_postal_number').val();
		var com_telephone = $('#ed_com_telephone').val();
		var com_logo = $('#ed_logo_value').val();
		var com_id = $('#com_id').val();
		
		var vat_subj = $('#ed_vat_subj').is(':checked');
		var lv_subj = $('#ed_lv_subj').is(':checked');
		var tc_subj = $('#ed_tc_subj').is(':checked');

		var status = $('#ed_obr_status').is(':checked');
		var url=$('#ed_obr_url').val();
		var username=$('#ed_obr_username').val();
		var password=$('#ed_obr_password').val();
		
		if(com_more_info==''){
			toastr.error("Le champs les informations supplest obligatoire");
			$('#ed_com_more_info').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_more_info').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_jur_form==''){
			toastr.error("Veuillez rensigner la form juridique de l'organisation");
			$('#ed_com_jur_form').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_jur_form').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_activity_sector==''){
			toastr.error("Le champ secteur d'activitest obligatoire");
			$('#ed_com_activity_sector').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_activity_sector').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_fiscal_center==''){
			toastr.error("Le champ centre fiscal est obligatoire");
			$('#ed_com_fiscal_center').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_fiscal_center').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_tin==''){
			toastr.error("Le NIF est obligatoire");
			$('#ed_com_tin').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_tin').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_name==''){
			toastr.error("Le nom de l'organisation est obligatoire");
			$('#ed_com_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(account_title==''){
			toastr.error("L'intituldu compte est obligatoire");
			$('#ed_account_title').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_account_title').attr('class','form-control form-control-sm form-control-border is-invalid');
		}
		if(bank_account==''){
			toastr.error("le numdu compte est obligatoire");
			$('#ed_bank_account').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_bank_account').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(bank_name==''){
			toastr.error("le nom de la banque est obligatoire");
			$('#ed_bank_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_bank_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_repr_name==''){
			toastr.error("Le reprde l'organisation est obligatoire");
			$('#ed_com_repr_name').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_repr_name').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_repr_position==''){
			toastr.error("La position du reprdde l'organisation est obligatoire");
			$('#ed_com_repr_position').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_repr_position').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(trade_num==''){
			toastr.error("Le numdu registre de commerce est attendu");
			$('#ed_trade_num').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_trade_num').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_province==''){
			toastr.error("La province ouu se trouve l'organisation est obligatoire");
			$('#ed_com_province').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_province').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_commune==''){
			toastr.error("La commune ouu se trouve l'organisation est obligatoire");
			$('#ed_com_commune').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_commune').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_district==''){
			toastr.error("Le quartier ou se trouve l'organisation est obligatoire");
			$('#ed_com_district').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_district').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_street==''){
			toastr.error("L'avenue/boulevard est obligatoire");
			$('#ed_com_street').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_street').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_street_num==''){
			toastr.error("Lu numero sur le boulevard, avenue est obligatoire");
			$('#ed_com_street_number').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_street_number').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_postal_num==''){
			toastr.error("Le numero de la boite postal est necessaire");
			$('#ed_com_postal_number').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_com_postal_number').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_telephone==''){
			toastr.error("Le numero du test obligatoire");
			$('#ed_com_telephone').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{	
			$('#ed_com_telephone').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(com_logo==''){
			toastr.error("Le logo de l'organisation est obligatoire");
			$('#ed_logo_value').attr('class','form-control form-control-sm form-control-border is-invalid');
		}else{
			$('#ed_logo_value').attr('class','form-control form-control-sm form-control-border is-valid');
		}
		if(status &&(url=='' || username=='' || password=='')){
			$('#ed_obr_url').attr('class','form-control form-control is-invalid');
			$('#ed_obr_username').attr('class','form-control form-control is-invalid');
			$('#ed_obr_password').attr('class','form-control form-control is-invalid');
		}else{
			if(com_logo!='' && com_telephone !='' && com_postal_num !='' && com_street_num!='' && com_street!='' && com_district!='' && com_commune!='' && com_province !='' && trade_num!='' && com_repr_position!='' && com_repr_name!='' && bank_name!='' && bank_account!='' && account_title!='' && com_name!='' && com_tin!='' && com_fiscal_center!='' && com_activity_sector!='' && com_jur_form!='' && com_more_info!=''){
				$.ajax({
					url:'./updateO',
					method:'POST',
					data:{
						com_id:com_id,
						com_more_info:com_more_info,
						com_jur_form:com_jur_form,
						com_activity_sector:com_activity_sector,
						com_fiscal_center:com_fiscal_center,
						com_tin:com_tin,
						com_name:com_name,
						account_title:account_title,
						bank_account:bank_account,
						bank_name:bank_name,
						status:status,
						url:url,
						username:username,
						password:password,
						com_repr_name:com_repr_name,
						com_repr_position:com_repr_position,
						trade_num:trade_num,
						com_province:com_province,
						com_commune:com_commune,
						com_district:com_district,
						com_street:com_street,
						com_street_num:com_street_num,
						com_postal_num:com_postal_num,
						com_telephone:com_telephone,
						com_logo:com_logo,
						vat_subj:vat_subj,
						lv_subj:lv_subj,
						tc_subj:tc_subj
					},success:function(dta){
						if(dta.code=="1"){
							location.reload();
						}
					}
				});
			}
		}
		
	});
	
	
	$('.com_view').click(function(ev){
		ev.preventDefault();
		var ref = $(this).data('ref');
		//alert(ref);
		
		//$('#editcategory').modal('show');
	});
	
	$('#cancelsaveCompany').click(function() {
		$('#com_more_info').val('');
		$('#com_jur_form').val('');
		$('#com_activity_sector').val('');
		$('#com_fiscal_center').val('');
		$('#com_tin').val('');
		$('#com_name').val('');
		$('#logo_value').val('');
		$('#account_title').val('');
		$('#bank_account').val('');
		$('#bank_name').val('');
		$('#com_repr_name').val('');
		$('#com_repr_position').val('');
		$('#logo_value').val('');
		$('#trade_num').val('');
		$('#com_province').val('');
		$('#com_commune').val('');
		$('#com_district').val('');
		$('#com_street').val('');
		$('#com_street_number').val('');
		$('#com_postal_number').val('');
		$('#com_telephone').val('');
		$('#logo_img64').attr('src','#');
		$('#logo_img64').attr('hidden','hidden');
		$('#new_company').modal('hide');
	});
	//end organisation management
	//Order management
	$('#order_quotation').change(function() {
		var id = $(this).children('option:selected').val();
		//alert('here we are : ' + id);
		if (id != '') {
			$.ajax({
				url: './searchQuotation',
				method: 'POST',
				data: {
					id: id
				}, success: function(data) {
					if (data.code == '1') {
						var body = data.body[0];
						$('#order_client').val(body.client.name);
						$('#quo_address').val(body.invoiceAddress);
						$('#quo_destination').val(body.delivaryAddress);
						$('#quo_taxe').val(1);
						$('#quo_date').val(formatDate(body.quotationDate));
						$('#quo_sub_tot').val(numberFormat(body.subTotal));
						$('#quo_tax').val(numberFormat(body.totalTax));
						$('#quo_total').val(numberFormat(body.grandTotal));
						$('#quo_comment').val(body.comments);

						//$('#order_detail').empty();
						$('#order_detail').empty();
						$('#quo_detail_display').removeAttr('hidden');
						var arts = body.articles;
						if (arts.length === 0) {
							
							$('#quo_detail_display').attr('hidden', 'hidden');
							$('#order_detail').append($('<tr>'))
							$('#order_detail').append($('<td colspan="5"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
							$('#order_detail').append($('</tr>'))
						}
						else {
							$('#quo_detail_display').removeAttr('hidden');
							var total_amount = 0.0;
							for (i = 0; i < arts.length; i++) {
								var tot_price = arts[i].quantity * arts[i].unityPrice;
								total_amount = total_amount + tot_price;
								$('#order_detail').append('<tr>');
									$('#order_detail').append($('<td></td>').attr('th:text', arts[i].id).text(numberFormat(arts[i].quantity)))
									$('#order_detail').append($('<td></td>').attr('th:text', arts[i].id).text(arts[i].title))
									$('#order_detail').append($('<td></td>').attr('th:text', arts[i].id).text(arts[i].unityPrice))
									$('#order_detail').append($('<td></td>').attr('th:text', arts[i].id).text(numberFormat(arts[i].vat)))
									$('#order_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', arts[i].id).text(numberFormat(arts[i].totalNVAT)))
									$('#order_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', arts[i].id).text(numberFormat(arts[i].totalVat)))
									$('#order_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', arts[i].id).text(numberFormat(arts[i].totalWVAT)))
								$('#order_detail').append('</tr>');
							}
							$('#order_detail').append($('<tr>'))
								$('#order_detail').append($('<td colspan="4" style="text-align:right;font-weight:bold;"><strong></strong></td>').attr('th:text', 1).text('PVHTVA'))
								$('#order_detail').append($('<td style="text-align:right;font-weight:bold;" colspan="3"><strong></strong></td>').attr('th:text', 1).text(numberFormat(body.subTotal)))
							$('#order_detail').append($('</tr>'))
							$('#order_detail').append($('<tr>'))
								$('#order_detail').append($('<td colspan="4" style="text-align:right;font-weight:bold;"><strong></strong></td>').attr('th:text', 1).text('TVA/VAT'))
								$('#order_detail').append($('<td style="text-align:right;font-weight:bold;" colspan="3"><strong></strong></td>').attr('th:text', 1).text(numberFormat(body.totalTax)))
							$('#order_detail').append($('</tr>'))
							$('#order_detail').append($('<tr>'))
								$('#order_detail').append($('<td colspan="4" style="text-align:right;font-weight:bold;"><strong></strong></td>').attr('th:text', 1).text('TOTAL'))
								$('#order_detail').append($('<td style="text-align:right;font-weight:bold;" colspan="3"><strong></strong></td>').attr('th:text', 1).text(numberFormat(body.grandTotal)))
							$('#order_detail').append($('</tr>'))
							
						}
					}
				}
			});
		} else {
			$('#order_client').val('');
			$('#quo_address').val('');
			$('#quo_destination').val('');
			$('#quo_taxe').val('');
			$('#quo_date').val(formatDate(''));
			$('#quo_sub_tot').val(numberFormat(''));
			$('#quo_tax').val(numberFormat(''));
			$('#quo_total').val(numberFormat(''));
			$('#quo_comment').val('');

			//$('#order_detail').empty();
			$('#order_detail').empty();
			$('#quo_detail_display').attr('hidden', 'hidden');
		}
	});

	$('#save_order').click(function() {
		var order_date = $('#order_date').val();
		var order_ref = $('#order_ref').val();
		var order_quot = $('#order_quotation').children('option:selected').val();
		var order_client = $('#order_client').val();
		if (order_date == '') {
			$('#order_date').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride completer avec la date du bon de commande');
		} else {
			$('#order_date').attr('class', 'form-control form-control-border is-valid');
		}
		if (order_ref == '') {
			$('#order_ref').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride complla reference du bon de commande');
		} else {
			$('#order_ref').attr('class', 'form-control form-control-border is-valid');
		}
		if (order_date != '' && order_ref != '' && order_quot != '' && order_client != '') {
			var resp = confirm('Voulez-vous enregistrer ce bon de commande ?');
			if (resp) {
				$.ajax({
					url: './saveO',
					method: 'POST',
					data: {
						quotation: order_quot,
						order_date: order_date,
						order_ref: order_ref
					}, success: function(dta) {
						if (dta.code == '1') {
							$('#order_client').val('');
							$('#quo_address').val('');
							$('#quo_destination').val('');
							$('#quo_taxe').val('');
							$('#quo_date').val(formatDate(''));
							$('#quo_sub_tot').val(numberFormat(''));
							$('#quo_tax').val(numberFormat(''));
							$('#quo_total').val(numberFormat(''));
							$('#quo_comment').val('');

							//$('#order_detail').empty();
							$('#order_detail').empty();
							$('#quo_detail_display').attr('hidden', 'hidden');

							location.reload();
							toastr.success('Enregistrement de la bon de commande effectuavec succès');
						}
					}
				});
			}
		}
	});

	$('.viewOrder').click(function() {
		var id = $(this).data('ref');
		$.ajax({
			url: './viewO',
			method: 'POST',
			data: {
				id: id
			}, success: function(data) {
				if (data.code == '1') {
					var body = data.body[0];
					var organisation = body.organisation;
					$('#order_logo').attr('src',organisation.logo);
					$('#ord_org_name').html(organisation.name);
					var addr = organisation.addressNumber+','+organisation.addressAvenue+', '+organisation.addressQuartier;
					$('#ord_org_address1').html(addr);
					$('#ord_org_address2').html(organisation.addressProvince+' - '+organisation.addressCommune);
					$('#ord_pobox').html(organisation.postalNumber);
					$('#ord_org_telephone').html(organisation.telephone);
					$('#ord_org_tin').html(organisation.tin);
					$('#ord_org_rc').html(organisation.tradeNumber);
					$('#ord_org_fiscal').html(organisation.fiscalCenter);
					$('#ord_org_jur').html(organisation.juridictionForm);
					$('#ord_org_sector').html(organisation.activitySecter);
					
					if(organisation.isVATPayer){
						$('#ord_org_vat').html('OUI/YES');
					}else{
						$('#ord_org_vat').html('NON/NO');
					}
					
					
					
					
					
					$('#order_det').removeAttr('hidden');
					$('#order_list').attr('class', 'col-lg-5 connectedSortable');
					$('#client_name').html(body.client.name);
					$('#cli_nif').text(body.client.tin);
					var cli_addr = body.client.addInfos + "<br/>" + body.client.email + '<br/>' + body.client.telephone
					$('#client_address').html(cli_addr);
					$('#bc_ref').text(body.ref);
					$('#bc_date').text(formatDate(body.orderDate));

					$('#bc_thtva').val(numberFormat(body.subTotal));
					$('#bc_tva').val(numberFormat(body.totalTax));
					$('#bc_tvac').val(numberFormat(body.grandTotal));
					if(body.isTaxable=="2"){
						$('#bc_vat_applicatble').html('PAYABLE');
					}else{
						$('#bc_vat_applicatble').html('EXEMPTED/EXONEREE');
					}
					if(body.vatSubject){
						$('#cli_isvatpayer').text('OUI/YES')
					}else{
						$('#cli_isvatpayer').text('NON/NO')
					}
					$('#order_items_detail').empty();
					$('#bc_currency').html(body.currency);
					var sales = body.articles;

					if (sales.length === 0) {
						$('#order_items_detail').append($('<tr>'))
						$('#order_items_detail').append($('<td colspan="4"><i></i></td>').attr('th:text', 1).text('Pas d\'articles trouvpour ce Bon de Commande.'))
						$('#order_items_detail').append($('</tr>'))
					}
					else {
						var tva_amt = 0.0;
						var total_amount = 0.0;
						for (i = 0; i < sales.length; i++) {
							var tot_price = sales[i].quantity * sales[i].unityPrice;
							var pvt=tot_price+sales[i].totalVat;
							total_amount = total_amount + tot_price;
							tva_amt = tva_amt+sales[i].totalVat;
							$('#order_items_detail').append('<tr>');
								$('#order_items_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].quantity));
								$('#order_items_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].title));
								$('#order_items_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].unityPrice)));
								
								$('#order_items_detail').append($('<td></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].vat)+'%'));
								$('#order_items_detail').append($('<td></td>').attr('th:text', sales[i].id).text(numberFormat(tot_price)));
								$('#order_items_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalVat)));
								$('#order_items_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(pvt)));
							$('#order_items_detail').append('</tr>');
						}

						var gdtotal = total_amount + tva_amt;

						$('#order_items_detail').append($('<tr>'));
							$('#order_items_detail').append($('<td style="text-align:right; font-weight:bold;" colspan="4"></td>').attr('th:text', 1).text('TOTAL HTVA'));
							$('#order_items_detail').append($('<td style="text-align:right;font-weight:bold;" colspan="3"><strong></strong></td>').attr('th:text', 1).text(numberFormat(total_amount)));
						$('#order_items_detail').append($('</tr>'));

						$('#order_items_detail').append($('<tr>'));
							$('#order_items_detail').append($('<td colspan="4" style="text-align:right; font-weight:bold;"></td>').attr('th:text', 1).text('TVA'));
							$('#order_items_detail').append($('<td style="text-align:right;font-weight:bold;" colspan="3"><strong></strong></td>').attr('th:text', 1).text(numberFormat(tva_amt)));
						$('#order_items_detail').append($('</tr>'));

						$('#order_items_detail').append($('<tr>'));
							$('#order_items_detail').append($('<td colspan="4" style="text-align:right; font-weight:bold;"></td>').attr('th:text', 1).text('GRAND TOTAL'));
							$('#order_items_detail').append($('<td style="text-align:right;font-weight:bold;" colspan="3"><strong></strong></td>').attr('th:text', 1).text(numberFormat(gdtotal)));
						$('#order_items_detail').append($('</tr>'));

					}
				}
			}
		});
	});

	//end order management

	//Invoice management
	
	$('.cancelInvoice').click(function(){
		var id = $(this).data('ref');
		$('#cancelInvoice').modal('show');
		$.ajax({
			url:'./findInvoice',
			method:'POST',
			data:{
				id:id
			},success:function(dta){
				var ref =dta.body[0];
				var invDate = dta.body[1];
				var client = dta.body[2];
				$('#inv_id').val(id);
				$('#canc_inv').val(ref);
				$('#canc_inv_date').val(invDate);
				$('#canc_inv_client').val(client);
			}
		});
	});
	
	$('#save_cancel_invoice').click(function(){
		var id = $('#inv_id').val();
		var motif = $('#canc_invoice_message').val();
		if(motif==''){
			toastr.error("Priere de donner le motif de l 'annulation de la facture");
			$('#canc_invoice_message').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#canc_invoice_message').attr('class','form-control form-control-border is-valid');
			var res = confirm("Voulez-vous réellement annuler cette facture ?");
			if(res){
				$.ajax({
					url:'./cancelInv',
					method:'POST',
					data:{
						id,
						motif
					},success:function(dta){
						location.reload();
						toastr.success("Annulation de la facture effectuee avec succes");
					}
				});
			}
		}	
	});
	
	$('#save_cancel_cancel_invoice').click(function(){
		$('#cancelInvoice').modal('hide');
	});
	
	$('#inv_client').change(function() {
		var client = $('#inv_client').children('option:selected').val();
		$('#inv_order_frame').removeAttr('hidden');
		$('#inv_total').val('');
		$('#inv_tax').val('');
		$('#inv_sub_tot').val('');
		$('#inv_art_detail').empty();
		$('#inv_order').empty();
		
		$('#inv_address').val('');
		$('#inv_destination').val('');
		$('#inv_taxe').empty();
		//$('#inv_pyt_mod').empty();
		if (client != '') {
			$.ajax({
				url: './searchOByC',
				method: 'POST',
				data: {
					client: client
				}, success: function(data) {
					$('#inv_order').empty();
					$('#inv_order').append('<option value=""></option>');
					if (data.code == '1') {
						var order = data.body[0];
						if (order.length === 0) {
							//alert('Lenght is zero')
						}
						else {
							for (i = 0; i < order.length; i++) {
								$('#inv_order').append('<option value="' + order[i].id + '">' + order[i].ref + '</option>');
							}
						}
					}
				}
			});
		} else {

		}
	});

	$('#inv_order').change(function(ev) { //inv_order
		ev.preventDefault();
		//alert('inside');
		$('#inv_order').removeAttr('hidden');
		var order = $(this).children('option:selected').val();
		$('#inv_address').val('');
		$('#inv_destination').val('');
		$('#inv_taxe').empty();
		//$('#inv_pyt_mod').empty();
		if (order != '') {
			$.ajax({
				url: './orderManagement',
				method: 'POST',
				data: {
					id: order
				}, success: function(data) {
					var bo = data.body[0];
					$('#inv_address').val(bo.invoiceAddress);
					$('#inv_destination').val(bo.delivaryAddress);
					$('#inv_taxe').val(1);
					var articles = data.body[1];
					$('#inv_art_detail').empty();
					if (articles.lenght === 0) {
						$('#inv_address').val('');
						$('#inv_destination').val('');
					} else {
						$('#inv_art_part').removeAttr('hidden');
						
						var sub_total = 0.0;
						var tax = 0.0;
						for (i = 0; i < articles.length; i++) {
							
							var total = articles[i].quantity * articles[i].unity_price;
							
							sub_total = sub_total + articles[i].tax;
							
							tax =tax+articles[i].tax;
							sub_total = sub_total + total;
							$('#inv_art_detail').append('<tr>');
							$('#inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].quantity));
							$('#inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].title));
							$('#inv_art_detail').append($('<td style="text-align:right;"></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].unity_price)));
							$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].taxRate)));
							$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].totalNVat)));
							$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].tax)));
							$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].total)));
							
							
							$('#inv_art_detail').append($('<td><button type="button" class="btn btn-sm btn-warning updateitems" onclick="updateItemElement(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-edit"> </i></button><button type="button" class="btn btn-sm btn-danger deleteitems" onclick="deleteInvoiceElement(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
							$('#inv_art_detail').append('</tr>');
						}

						var gdTotal = sub_total + tax;

						$('#inv_total').val(numberFormat(gdTotal));
						$('#inv_tax').val(numberFormat(tax));
						$('#inv_sub_tot').val(numberFormat(sub_total));
					}
				}
			});
		} else {
			$('#inv_total').val('');
			$('#inv_tax').val('');
			$('#inv_sub_tot').val('');
			$('#inv_art_detail').empty();
		}
	});
	
	$('#inv_pyt_mod').change(function(){
		var pyt_mod = $(this).children('option:selected').val();
		if(pyt_mod=='2'){
			$('#pyt_detail').removeAttr('hidden');
		}else{
			$('#pyt_detail').attr('hidden','hidden');
		}
	});

	$('#save_invoice').click(function() {
		var inv_order = $('#inv_order').children('option:selected').val();
		var client = $('#inv_client').children('option:selected').val();
		var pay_mode = $('#inv_pyt_mod').children('option:selected').val();
		var vat = $('#inv_tax').val();
		var subtotal = $('#inv_sub_tot').val();
		var total = $('#inv_total').val();
		if (client == '') {
			toastr.error("Pride bien vouloir choisir le client facturer");
			$('#inv_client').attr('class', 'form-control form-control-border is-invalid');
		} else {
			$('#inv_client').attr('class', 'form-control form-control-border is-valid');
		}
		if (inv_order == '') {
			toastr.error("Pride bien vouloir choisir le bon de commande imputer");
			$('#inv_order').attr('class', 'form-control form-control-border is-invalid');
		}else {
			$('#inv_order').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(pay_mode==''){
			toastr.error("Pride bien vouloir choisir le mode de paiement de cette facture !");
			$('#inv_pyt_mod').attr('class', 'form-control form-control-border is-invalid');
		}else{
			$('#inv_pyt_mod').attr('class', 'form-control form-control-border is-valid');
		}
		if (inv_order != '' && client != '' && pay_mode!='') {
			var resp = confirm("Voulez-vous vraiment sauvegarder cette facture");
			if (resp) {
				//if()
				$.ajax({
					url: './saveInvoice',
					method: 'Post',
					data: {
						id: inv_order,
						vat: vat,
						subTotal: subtotal,
						pyt_mode:pay_mode,
						total: total
					}, success: function(data) {
						if (data.code == '1') {
							location.reload();
							toastr.success("Sauvegarde de la facture effectuavec succ!");
						}
					}
				});
			}
		}

		//alert('Invoice order : ' + inv_order);

	});

	$('#inv_art_quantity').change(function() {
		var value = $('#inv_art_quantity').val();
		var total = value * $('#inv_art_unity_price').val();
		$('#inv_art_total').val(total);
	});

	$('#add_article_inv_art').click(function() {
		var qte = $('#inv_art_quantity').val();
		//var qte_confirm = $('#inv_art_quantity_confirm').val();

		var tax_confirm = $('#inv_art_tax').val();
		var tax = $('#inv_art_tax').val();
		
		var tax_amnt = $('#inv_tax_amnt').val();
		var tax_amnt_confirm = $('#inv_tax_amnt_confirm').val();
		
		//alert(tax_amnt_confirm);
		
		//alert(tax_confirm);
		var order_id = $('#inv_order').children('option:selected').val();
		var art = $('#inv_art_art').val();
		var desc = $('#inv_art_art_desc').val();
		var uprice = $('#inv_art_unity_price').val();
		var uprice_cofirm = $('#inv_art_unity_price_confirm').val();
		var total = $('#inv_art_total').val();
		var total_confirm = $('#inv_art_total_confirm').val();
		var id = $('#inv_art_id').val();
		if (qte == '') {
			toastr.error("Pride bien vouloir choisir un article a modifier");
			$('#inv_art_quantity').attr('class', 'form-control form-control-border is-invalid');
		} else {
			//toastr.error("Pride bien vouloir choisir un article a modifier");
			$('#inv_art_quantity').attr('class', 'form-control form-control-border is-valid');
		}

		if (total == '') {
			$('#inv_art_total').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride refaire les calculs pour cet article en cours de modification');
		} else {
			$('#inv_art_total').attr('class', 'form-control form-control-border is-valid');
		}
		
		if(order_id==''){
			$('#inv_order').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pribien vouloir choisir le bon de commande');
		}else{
			$('#inv_order').attr('class', 'form-control form-control-border is-valid');
		}
		if (total != '' && qte != '' && order_id!='') {
			if (total == total_confirm) {
				var conf = confirm("On remarque qu'il n'y a pas de modification pour cet article.Voulez vous continuer ou annuler les modifications ? Cliquer sur OK pour continuer sinon sur ANNULER/CANCEL pour annuler")
				if (conf) {

				} else {
					$('#inv_art_quantity').val('');
					$('#inv_art_quantity_confirm').val('');
					$('#inv_art_art').val('');
					$('#inv_art_art_desc').val('');
					$('#inv_art_unity_price').val('');
					$('#inv_art_unity_price_confirm').val('');
					$('#inv_art_total').val('');
					$('#inv_art_total_confirm').val('');
					$('#inv_art_id').val('');

					$('#inv_art_quantity').attr('class', 'form-control form-control-border');
					$('#inv_art_total').attr('class', 'form-control form-control-border');
				}
			} else {
				//mise a jour de l'article dans la liste des paniels'
				
				$.ajax({
					url: './updatecartsElement',
					method: 'POST',
					data: {
						id: id,
						qte: qte,
						total: total,
						tax:tax, 
						order_id:order_id,
						tax_confirm:tax_confirm,
						tax_amnt_confirm:tax_amnt_confirm
					}, success: function(data) {
						//var bo = data.body;
						if(data.code=='2'){
							toastr.error(data.description);
						}else{
							toastr.success(data.description);
							var articles = data.body;
							if (articles.lenght === 0) {
	
							} else {
								$('#inv_art_part').removeAttr('hidden');
								$('#inv_art_detail').empty();
								var sub_total = 0.0;
								
								var tax = 0.0;
								
								for (i = 0; i < articles.length; i++) {
									
									sub_total = sub_total + articles[i].totalNVat;
									tax = tax + articles[i].tax;
									
									$('#inv_art_detail').append('<tr>');
									$('#inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].quantity));
									$('#inv_art_detail').append($('<td></td>').attr('th:text', articles[i].id).text(articles[i].title));
									$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].unity_price)))
									
									
									$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].taxRate)));
									$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].totalNVat)));
									$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].tax)));
									
									$('#inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', articles[i].id).text(numberFormat(articles[i].total)))
									
									
									$('#inv_art_detail').append($('<td><button type="button" class="btn btn-sm btn-warning updateitems" onclick="updateItemElement(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-edit"> </i></button><button type="button" class="btn btn-sm btn-danger deleteitems" onclick="deleteInvoiceElement(' + articles[i].id + ')" th:data-ref="' + articles[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
									$('#inv_art_detail').append('</tr>');
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

		//if()
	});

	$('.validateInvoice').click(function(ev){
		ev.preventDefault();
		var conf = confirm("Voulez-vous vraiment valider cette facture. Soyez informer qu'après cette opelle sera envoyé à l'OBR.");
		if(conf){
			var id = $(this).data('ref');
			if(id!=''){
				$.ajax({
					url:'./validateI',
					method:'POST',
					data:{
						id:id
					},success:function(data){
						if(data.code=='1'){
							toastr.success("Validation de la facture effectué avec succès.");
							location.reload();
						}
					}
				});
			}
		}
		
	});
	
	$('#dir_cli_telephone').change(function(){
		
		var telephone = $(this).val();
		if(telephone!=''){
			$.ajax({
				url:'./getClientByTelephone',
				method:'POST',
				data:{
					telephone
				},success:function(data){
					if(data.code=='1'){
						//alert("Test");
						toastr.warning("Un client avec le meme numero de telephone existe deja");
						var cli = data.body[0];
						console.log(cli);
						$('#dir_cli_address').removeAttr('hidden');
						$('#dir_cli_address_new').attr('hidden','hidden');
						$('#dir_cli_name').val(cli.name);
						$('#dir_cli_nif').val(cli.tin);
						$('#dir_cli_province').val(cli.province);
						$('#dir_cli_commune').val(cli.commune);
						$('#dir_cli_district').val(cli.quartier);
						$('#dir_cli_contact_persone').val(cli.contactPerson);
						$('#dir_cli_telephone').val(cli.telephone);
						
						
						$('#dir_cli_address').attr('readonly','readonly');
						$('#dir_cli_name').attr('readonly','readonly');
						$('#dir_cli_nif').attr('readonly','readonly');
						$('#dir_cli_contact_persone').attr('readonly','readonly');
						$('#dir_cli_province').attr('readonly','readonly');
						$('#dir_cli_commune').attr('readonly','readonly');
						$('#dir_cli_district').attr('readonly','readonly');
						
					}else{
						toastr.warning("Pas de client trouvee avec les criteres entrees.");
						$('#dir_cli_address').attr('hidden','hidden');
						$('#dir_cli_address_new').removeAttr('hidden');
						$('#dir_cli_address').removeAttr('readonly');
						$('#dir_cli_name').removeAttr('readonly');
						$('#dir_cli_nif').removeAttr('readonly');
						$('#dir_cli_contact_persone').removeAttr('readonly');
						$('#dir_cli_address_text').removeAttr('readonly');
						$('#dir_cli_province').removeAttr('readonly');
						$('#dir_cli_commune').removeAttr('readonly');
						$('#dir_cli_district').removeAttr('readonly');
					}
				}
			})
		}
	});
	
	
	$('#dir_cli_name').change(function(){
		var name = $(this).val();
		if(name!=''){
			$.ajax({
				url:'./getClientByName',
				method:'POST',
				data:{
					name:name
				},success:function(data){
					if(data.code=='1'){
						//toastr.warning("Un client avec le meme numero de telephone existe deja");
						var cli = data.body[0];
						console.log(cli);
						$('#dir_cli_address').removeAttr('hidden');
						$('#dir_cli_address_new').attr('hidden','hidden');
						//$('#dir_cli_name').val(cli.name);
						$('#dir_cli_nif').val(cli.tin);
						$('#dir_cli_contact_persone').val(cli.contactPerson);
						$('#dir_cli_address_text').val(cli.addInfos);
						
						$('#dir_cli_province').val(cli.province);
						$('#dir_cli_commune').val(cli.commune);
						$('#dir_cli_district').val(cli.quartier);
						$('#dir_cli_contact_persone').val(cli.contactPerson);
						$('#dir_cli_telephone').val(cli.telephone);
						//disable field
						$('#dir_cli_address').attr('readonly','readonly');
						$('#dir_cli_telephone').attr('readonly','readonly');
						$('#dir_cli_nif').attr('readonly','readonly');
						$('#dir_cli_contact_persone').attr('readonly','readonly');
						$('#dir_cli_address_text').attr('readonly','readonly');
						$('#dir_cli_province').attr('readonly','readonly');
						$('#dir_cli_commune').attr('readonly','readonly');
						$('#dir_cli_district').attr('readonly','readonly');
						
					}else{
						toastr.warning("Pas de client trouvee avec les criteres entrees.");
						$('#dir_cli_address').attr('hidden','hidden');
						$('#dir_cli_address_new').removeAttr('hidden');
						//enable field
						$('#dir_cli_address').removeAttr('readonly');
						$('#dir_cli_name').removeAttr('readonly');
						$('#dir_cli_nif').removeAttr('readonly');
						$('#dir_cli_contact_persone').removeAttr('readonly');
						$('#dir_cli_address_text').removeAttr('readonly');
						
						$('#dir_cli_province').removeAttr('readonly');
						$('#dir_cli_commune').removeAttr('readonly');
						$('#dir_cli_district').removeAttr('readonly');
						$('#dir_cli_telephone').removeAttr('readonly');
					}
				}
			})
		}
	});
	
	
	$('#dir_inv_art_art').change(function(){
		var art = $(this).children('option:selected').val()
		if(art==''){
			$('#dir_inv_art_unity_price').val('');
			$('#dir_inv_art_tax').val('');
			$('#dir_inv_nvat_total').val('');
			$('#dir_inv_art_total').val('');
			$('#quo_unity').val('');
		}else{
			$.ajax({
				url:'./findArticle',
				method:'POST',
				data:{
					id:art
				},success:function(dat){
					var art_det = dat.body[0];
					var qty = $('#dir_inv_art_quantity').val();
					var tax_rate = art_det.tax.rate;
					var unity_price = art_det.unityPrice;
					var tot_tax = unity_price*tax_rate/100;
					$('#dir_inv_art_unity_price').val(unity_price);
					$('#dir_inv_art_tax').val(tax_rate);
					$('#quo_unity').val(art_det.unity);
					if(qty!=''){
						var tote = tot_tax*qty;
						$('#dir_inv_tax_amnt').val(tote);
						var total_wt = unity_price*qty;
						var total = total_wt+tote
						$('#dir_inv_nvat_total').val(total_wt);
						$('#dir_inv_art_total').val(total);
					}
				}
			});
		}
	});
	
	$('#dir_inv_pyt_mod').change(function(e){
		var valeur=$(this).children('option:selected').val();
		if(valeur=='2'){
			$('#dir_pyt_detail').removeAttr('hidden');
		}else{
			$('#dir_pyt_detail').attr('hidden','hidden');
		}
		//alert(valeur);
	});
	
	$('#dir_save_invoice').click(function(){
		//alert('test .......')
		var isEntreprise = $('#is_entreprise').is(':checked');
		var is_vat_subj = $('#is_vat_subj').is(':checked');
		var cli_name = $('#dir_cli_name').val();
		var cli_telephone = $('#dir_cli_telephone').val();
		var cli_nif = $('#dir_cli_nif').val();
		var cli_contact = $('#dir_cli_contact_persone').val();
		var cli_addr = $('#dir_cli_addr').val();
		var cli_prov = $('#dir_cli_province').val();
		var cli_commune = $('#dir_cli_commune').val();
		var cli_district = $('#dir_cli_district').val();
		var quo_taxe = $('#dir_quo_taxe').children('option:selected').val();
		var quo_currency = $('#dir_quo_currency').children('option:selected').val();
		var inv_pyt_mod = $('#dir_inv_pyt_mod').children('option:selected').val();
		var inv_tax = $('#inv_tax').val();
		var inv_sub_tot = $('#inv_sub_tot').val();
		var inv_total = $('#inv_total').val();
		var quo_comment = $('#quo_comment').val();
		
		//debut control
		if(cli_name==''){
			toastr.error("Prière de compléter le nom du client");
			$('#dir_cli_name').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_cli_name').attr('class','form-control form-control-border is-valid');
		}
		if(cli_telephone==''){
			toastr.error("Prière de compléter numero de téléphone");
			$('#dir_cli_telephone').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_cli_telephone').attr('class','form-control form-control-border is-valid');
		}
		if(cli_addr==''){
			toastr.error("L'adresse de facturation doit etre renseignée");
			$('#dir_cli_addr').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_cli_addr').attr('class','form-control form-control-border is-valid');
		}
		if(cli_prov==''){
			toastr.error("La valeur pour la province doit etre renseignee");
			$('#dir_cli_province').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_cli_province').attr('class','form-control form-control-border is-valid');
		}
		if(cli_commune==''){
			toastr.error("Le champ de la commune doit etre renseignée");
			$('#dir_cli_commune').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_cli_commune').attr('class','form-control form-control-border is-valid');
		}
		
		if(cli_district==''){
			toastr.error("Le champ quartier doit etre renseignee");
			$('#dir_cli_district').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_cli_district').attr('class','form-control form-control-border is-valid');
		}
		
		if(quo_taxe==''){
			toastr.error("Priere de bien vouloir indiquer comment facturer");
			$('#dir_quo_taxe').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_quo_taxe').attr('class','form-control form-control-border is-valid');
		}
		
		if(quo_currency==''){
			toastr.error("Une monnaie de facturation doit etre renseignée");
			$('#dir_quo_currency').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_quo_currency').attr('class','form-control form-control-border is-valid');
		}
		
		if(inv_pyt_mod==''){
			toastr.error("Prichoisir le mode de paiement cette fcture");
			$('#dir_inv_pyt_mod').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_inv_pyt_mod').attr('class','form-control form-control-border is-valid');
		}
		if(inv_pyt_mod!='' && quo_currency!='' && quo_taxe!='' && cli_district!='' && cli_commune!='' && cli_prov!='' && cli_addr!='' && cli_telephone!='' && cli_name!=''){
			if(isEntreprise && (cli_contact=='' || cli_nif=='')){
				toastr.error("Priere de bien vouloir completer le nom de la personne de contact ainsi que le NIF");
				$('#dir_cli_contact_persone').attr('class','form-control form-control-border is-invalid');
				$('#dir_cli_nif').attr('class','form-control form-control-border is-invalid');
			}else{
				$('#dir_cli_contact_persone').attr('class','form-control form-control-border is-valid');
				$('#dir_cli_nif').attr('class','form-control form-control-border is-valid');
				//continuer avec l'enregistrement
				var resp = confirm("Voulez-vous vraiment sauvegarder cette facture?");
				if (resp) {
					$('#dir_save_invoice').attr('disabled','disabled');
					$.ajax({
						url:'./direSaveInvoice',
						method:'POST',
						data:{
							isEntreprise: isEntreprise, 
							cli_name:cli_name, 
							cli_telephone:cli_telephone, 
							is_vat_subj:is_vat_subj,
							cli_nif:cli_nif, 
							cli_contact:cli_contact, 
							cli_addr:cli_addr, 
							cli_prov:cli_prov, 
							cli_commune:cli_commune, 
							cli_district:cli_district,
							quo_taxe:quo_taxe, 
							quo_currency:quo_currency, 
							inv_pyt_mod:inv_pyt_mod,
							inv_tax:inv_tax,
							inv_sub_tot:inv_sub_tot, 
							inv_total: inv_total, 
							quo_comment:quo_comment
						},
						success:function(dta){
							location.reload();
							toastr.success('Enregistrement de la nouvelle facture effectué avec succès.')
						},error:function(){
							$('#dir_save_invoice').removeAttr('disabled');
							toastr.error('Une erreur est survenue lors de la sauvegarde de la facture! Prière de refaire l\'opération mais si l\'erreur persiste, veuillez contacter votre support technique.')
						}
					});
				}
			}
		}
		
	});
	
	$('#dir_inv_art_quantity').change(function(){
		var qty = $(this).val();
		
		var unity_price = $('#dir_inv_art_unity_price').val();
		var tax_rate = $('#dir_inv_art_tax').val();
		var tot_tax = unity_price*tax_rate/100;
		var total_wt = unity_price*qty;
		
		var tote = tot_tax*qty;
		var total = total_wt+tote;
		$('#dir_inv_tax_amnt').val(tote);
		$('#dir_inv_nvat_total').val(total_wt);
		$('#dir_inv_art_total').val(total);
	});
	
	$('#dir_add_article_inv_art').click(function(){
		//alert('testttttttt')
		var qty = $('#dir_inv_art_quantity').val();
		var art = $('#dir_inv_art_art').val();
		var unityP = $('#dir_inv_art_unity_price').val();
		var taxRate = $('#dir_inv_art_tax').val();
		var taxAmnt = $('#dir_inv_tax_amnt').val();
		var quo_unity = $('#quo_unity').val();
		var totWT = $('#dir_inv_nvat_total').val();
		var total = $('#dir_inv_art_total').val();
		var calc_vat = $('#dir_quo_taxe').children('option:selected').val();	
		if(calc_vat==''){
			toastr.error('Bien vouloir choisir le mode de calcul de la TVA avant d\'ajouter cet article à la liste des articles!');
			$('#dir_quo_taxe').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_art_quantity').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_art_art').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_art_unity_price').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_art_tax').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_tax_amnt').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_nvat_total').attr('class','form-control form-control-border is-invalid');
			$('#dir_inv_art_total').attr('class','form-control form-control-border is-invalid');
		}else{
				$('#dir_quo_taxe').attr('class','form-control form-control-border is-valid');
				if(qty=='' || art=='' || unityP=='' || taxRate=='' ||taxAmnt=='' || totWT=='' || total==''){
					toastr.error('Prière de bien vouloir completer les champs obligatoires qui sont souligné');
					$('#dir_inv_art_quantity').attr('class','form-control form-control-border is-invalid');
					$('#dir_inv_art_art').attr('class','form-control form-control-border is-invalid');
					$('#dir_inv_art_unity_price').attr('class','form-control form-control-border is-invalid');
					$('#dir_inv_art_tax').attr('class','form-control form-control-border is-invalid');
					$('#dir_inv_tax_amnt').attr('class','form-control form-control-border is-invalid');
					$('#dir_inv_nvat_total').attr('class','form-control form-control-border is-invalid');
					$('#dir_inv_art_total').attr('class','form-control form-control-border is-invalid');

				}else{
					//save to 
					$.ajax({
						url: './addArticleToSession',
						method: 'POST',
						data: {
							qty:qty, 
							art:art, 
							unityP:unityP, 
							taxRate:taxRate, 
							taxAmnt:taxAmnt, 
							totWT:totWT, 
							total:total,
							unity:quo_unity,
							calc_vat:calc_vat
						}, success: function(data) {
							if(data.code=='2'){
								toastr.error(data.description);
								var articles = data.body;
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
							}else{
								toastr.success(data.description);
								var articles = data.body;
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
					$('#dir_inv_art_quantity').val('');
					$('#dir_inv_art_art').val('');
					$('#dir_inv_art_unity_price').val('');
					$('#dir_inv_art_tax').val('');
					$('#dir_inv_tax_amnt').val('');
					$('#quo_unity').val('');
					$('#dir_inv_nvat_total').val('');
					$('#dir_inv_art_total').val('');
					$('#dir_inv_art_quantity').attr('class','form-control form-control-border');
					$('#dir_inv_art_art').attr('class','form-control form-control-border');
					$('#dir_inv_art_unity_price').attr('class','form-control form-control-border');
					$('#dir_inv_art_tax').attr('class','form-control form-control-border');
					$('#dir_inv_tax_amnt').attr('class','form-control form-control-border');
					$('#dir_inv_nvat_total').attr('class','form-control form-control-border');
					$('#dir_inv_art_total').attr('class','form-control form-control-border');
				}
		}
		
		
	});
	
	$("#typ_billing_2").click(function() {
		$('#inv_order_frame').attr('hidden','hidden');
		$('#total_section').removeAttr('hidden');
		$('#select_typ').attr('class','col-lg-8 connectedSortable');
		$('#dir_cli_save_btn').removeAttr('hidden');
		$('#cli_save_btn').attr('hidden', 'hidden');
		$('#dir_inv_art_part').removeAttr('hidden');
		$('#inv_art_part').attr('hidden','hidden');
		$('#inv_order_direct').removeAttr('hidden');
	});

	$("#typ_billing_1").click(function() {
		$('#inv_order_frame').removeAttr('hidden');
		$('#total_section').removeAttr('hidden');
		$('#dir_cli_save_btn').attr('hidden', 'hidden');
		$('#cli_save_btn').removeAttr('hidden');
		$('#inv_art_part').attr('hidden','hidden');
		$('#dir_inv_art_part').attr('hidden','hidden');
		$('#select_typ').attr('class','col-lg-8 connectedSortable');
		$('#inv_order_direct').attr('hidden','hidden');
	});
	
	$('.viewInvoice').click(function(ev) {
		ev.preventDefault();
		var id = $(this).data('ref');
		if(id!=''){
			
			$.ajax({
				url: './searchInvoice',
				method: 'POST',
				data: {
					id: id
				}, success: function(dta) {
					if (dta.code == '1') {
						var quot = dta.body[0];
						$('#entr_logo').attr('src',quot.organisation.logo);
						$('#quotation_list').attr('class', 'col-lg-5 connectedSortable');
						$('#quotation_det').removeAttr('hidden');
						$('#quot_ref').text(quot.referenceNumber);
						$('#quot_date').text(formatDate(quot.invoiceDate));

						$('#cli_raison').html(quot.cliName);
						$('#inv_bc_ref').html(quot.ordRef);
						$('#inv_bc_date').html(formatDate(quot.ordDate));
						$('#cli_NIF').html(quot.cliTin);
						$('#cli_adresse').html(quot.cliAdress);
						$('#inv_signature').text(quot.signature);
						if(quot.vatPayable=='2'){
							$('#inv_taxable').html('PAYABLE');
						}else{
							$('#inv_taxable').html('NO PAYABLE');
						}
						if(quot.paymentMode=='1')
						{
							$('#inv_payment_mode').html('Paiement en espèce');
							$('#bankInfo').attr('hidden','hidden');
						}else if(quot.paymentMode=='2'){
							$('#inv_payment_mode').html('Paiement par Banque');
							$('#bankInfo').removeAttr('hidden');
						}else if(quot.paymentMode=='3'){
							$('#inv_payment_mode').html('Paiement crédit');
							$('#bankInfo').attr('hidden','hidden');
						}else if(quot.paymentMode=='4'){
							$('#inv_payment_mode').html('Autre');
							$('#bankInfo').attr('hidden','hidden');
						}
						$('#inv_currency').html(quot.currency);
						if (quot.cliIsVAT) {
							$('#cli_isvatpayer').html('OUI/YES');
						} else {
							$('#cli_isvatpayer').html('NON/NO');
						}
						if(quot.status==2 || quot.status==0 || quot.status==3 || quot.status==5 || quot.status==1){
							console.log('rdersede')
							$('#quotation_print').removeAttr('hidden');
							var link = 'printI/' + id;
							$('#quotation_print').attr('href', link);
						}else{
							$('#quotation_print').attr('hidden','hidden');
						}			
					}
					console.log(">>>>>>>>>>>>>>>>>>>>>> : "+quot)
					var art = quot.articleInvoicePojo;
					
					$('#inv_items_detail').empty();
					//$('#body_stock_detail').empty();
					if (art.length === 0) {
						$('#inv_items_detail').append($('<tr>'))
						$('#inv_items_detail').append($('<td colspan="4"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
						$('#inv_items_detail').append($('</tr>'))
					}
					else {
						var pvhtva = 0.0;
						var pvtvac = 0.0;
						var tva = 0.0;
						for (i = 0; i < art.length; i++) {
							pvtvac = pvtvac + art[i].total;
							pvhtva = pvhtva + art[i].totalNVat;
							tva = tva+ art[i].tax;
							$('#inv_items_detail').append($('<tr>'));
							$('#inv_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(art[i].quantity)));
							$('#inv_items_detail').append($('<td></td>').attr('th:text', i).text(art[i].title));
							$('#inv_items_detail').append($('<td></td>').attr('th:text', i).text(art[i].unity));
							$('#inv_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].unity_price))));
							
							$('#inv_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].taxRate))));
							$('#inv_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].totalNVat))));
							$('#inv_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].tax))));
							
							$('#inv_items_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text((numberFormat(art[i].total))));
							$('#inv_items_detail').append($('</tr>'));
						}
						$('#inv_items_detail').append($('<tr>'));
						$('#inv_items_detail').append($('<td colspan="4" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("PRIX DE VENTE HTVA"));
						$('#inv_items_detail').append($('<td colspan="3" style="text-align:right;font-weight:bold; "></td>').attr('th:text', i).text(numberFormat(pvhtva)));
						$('#inv_items_detail').append($('</tr>'));

						$('#inv_items_detail').append($('<tr>'));
						$('#inv_items_detail').append($('<td colspan="4" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("TVA"));
						$('#inv_items_detail').append($('<td colspan="3" style="text-align:right;font-weight:bold;"></td>').attr('th:text', i).text(numberFormat(tva)));
						$('#inv_items_detail').append($('</tr>'));

						$('#inv_items_detail').append($('<tr>'));
						$('#inv_items_detail').append($('<td colspan="4" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("TOTAL TVAC"));
						$('#inv_items_detail').append($('<td colspan="3" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text(numberFormat(pvtvac)));
						$('#inv_items_detail').append($('</tr>'));
					}
				}
			});
		}
	});

	$('#dir_quo_taxe').change(function(data){
		var conf = confirm("Voulez vous réellement changer le mode de calcul de la TVA?");
		if(conf){
			var id = $(this).children('option:selected').val();		
			if (id != '') {
				$.ajax({
					url: './taxCalcul2',
					method: 'POST',
					data: {
						typ:id
					}, success: function(res) {
						if (res.code == '1') {
							var sales = res.body;
							$('#quotation_detail').empty();
							if (sales.length === 0) {
								
							}
							else {
								$('#dir_inv_art_detail').removeAttr('hidden');
								$('#dir_inv_art_detail').empty();
								var sub_total = 0.0;
								var tax = 0.0;
								for (i = 0; i < sales.length; i++) {
									sub_total = sub_total + sales[i].totalNVat;
									tax = tax + sales[i].tax;
									$('#dir_inv_art_detail').append('<tr>');
									$('#dir_inv_art_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].quantity));
									$('#dir_inv_art_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].title));
									$('#dir_inv_art_detail').append($('<td></td>').attr('th:text', sales[i].id).text(sales[i].unity));
									$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].unity_price)))
									$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].taxRate)));
									$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].totalNVat)));
									$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].tax)));
									$('#dir_inv_art_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', sales[i].id).text(numberFormat(sales[i].total)))
									$('#dir_inv_art_detail').append($('<td><button type="button" class="btn btn-sm btn-danger deleteitems" onclick="deleteInvoiceArticle(' + sales[i].id + ')" th:data-ref="' + sales[i].id + '"><i class="fa fa-trash"> </i></button></td>'))
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
			} else {
				$('#dir_quo_taxe').val('');

			}
		}
	});
	//End invoice management
	//Begin action controlling
	
	$('#newaction').click(function(){
		$('#addaction').modal('show');
	});
	
	$('#rec_invoice').change(function(){
		var invoice = $(this).children('option:selected').val();
		if(invoice==''){
			$('#rec_client').val('');
			$('#rec_inv_amount').val('');
		}else{
			//get the invoice with id
			$.ajax({
				url:'./searchDebtInvoice',
				method:'POST',
				data:{
					id:invoice
				},success:function(res){
					if(res.code=='1'){
						console.log(res);
						$('#rec_client').val(res.body[1]);
						$('#rec_inv_amount').val(res.body[0]);
					}
				}
			})
		}
	});
	
	
	
	$('#saverecovery').click(function(){
		var invoice = $('#rec_invoice').children('option:selected').val();
		var client = $('#rec_client').val();
		var amount = $('#rec_inv_amount').val();
		var rec_date = $('#rec_date').val();
		var means = $('#rec_means').children('option:selected').val();
		var contactPerson = $('#rec_contactPerson').val();
		var telephone = $('#rec_telephone').val();
		var email = $('#rec_email').val();
		var result = $('#rec_result').val();
		var comments = $('#rec_comment').val();
		var next = $('#rec_next').val();
		var recover = $('#rec_recover').val();
		if(invoice==''){
			toastr.error("Une facture doit etre selectionnee avant d'enregistrer une action de recouvrement.");
			$('#rec_invoice').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_invoice').attr('class','form-control form-control-border is-valid');
		}
		if(rec_date==''){
			toastr.error("La date ou cette action de recouvrement a ete effectuee est attendue.");
			$('#rec_date').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_date').attr('class','form-control form-control-border is-valid');
		}
		if(contactPerson==''){
			toastr.error("Le nom de la personne contactee est obligatoire.");
			$('#rec_contactPerson').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_contactPerson').attr('class','form-control form-control-border is-valid');
		}
		if(means==''){
			toastr.error("Il faut indiquer le moyen utilisee pour cette action de recouvrement.");
			$('#rec_means').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_means').attr('class','form-control form-control-border is-valid');
		}
		
		if(result==''){
			toastr.error("Un resultat faisant etat d'appreciation est attandu.");
			$('#rec_result').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_result').attr('class','form-control form-control-border is-valid');
		}
		
		if(comments==''){
			toastr.error("Un commentaire est obligatoire ou on doit faire mention de la decision prise pour cette action.");
			$('#rec_comment').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_comment').attr('class','form-control form-control-border is-valid');
		}
		if(recover==''){
			toastr.error("L'agent de recouvrement qui a effectuee cette mission doit etre rensiegnee.");
			$('#rec_recover').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rec_recover').attr('class','form-control form-control-border is-valid');
		}
		if(comments!='' && result!='' && means!='' && contactPerson!='' && rec_date!='' && invoice !='' && recover!=''){
			$(this).attr('disabled','disabled');
			$.ajax({
				url:'./saveAction',
				method:'POST',
				data:{
					invoice:invoice,
					client:client,
					amount:amount,
					rec_date:rec_date,
					means:means,
					contactPerson:contactPerson,
					telephone:telephone,
					email:email,
					result:result,
					comments:comments,
					next:next,
					recover:recover
				},success:function(res){
					if(res.code=='1'){
						toastr.success("Enregistrement de la nouvelle action effectuée avec succès");
						location.reload();
					}
				},error:function(){
					$(this).removeAttr('disabled');
				}
			})

		}
	});
	$('#cancelsaverecovery').click(function(){
		$('#rec_invoice').val('');
		$('#rec_client').val('');
		$('#rec_inv_amount').val('');
		$('#rec_date').val('');
		$('#rec_means').val('');
		$('#rec_contactPerson').val('');
		$('#rec_telephone').val('');
		$('#rec_email').val('');
		$('#rec_result').val('');
		$('#rec_comment').val('');
		$('#rec_next').val('');
		$('#rec_recover').val('')
		$('#addaction').modal('hide');
	});
	
	//End action controlling
	
	// Begin payment controller
	$('#newpayment').click(function(){
		$('#addpayment').modal('show');
	});
	
	$('#pay_invoice').change(function(){
		var invoice = $(this).children('option:selected').val();
		if(invoice==''){
			$('#pay_client').val('');
			$('#pay_inv_amount').val('');
		}else{
			//get the invoice with id
			$.ajax({
				url:'./searchDebtInvoice',
				method:'POST',
				data:{
					id:invoice
				},success:function(res){
					if(res.code=='1'){
						console.log(res);
						$('#pay_client').val(res.body[1]);
						$('#pay_inv_amount').val(res.body[0]);
					}
				}
			})
		}
	});
	
	$('#savepayment').click(function(){
		var pay_invoice = $('#pay_invoice').children('option:selected').val();
		var pay_date = $('#pay_date').val();
		var pay_type = $('#pay_type').children('option:selected').val();
		var pay_ref = $('#pay_reference').val();
		var pay_bank = $('#pay_bank').val();
		var pay_bank_account = $('#pay_bank_account').val();
		var pay_comment = $('#pay_comment').val();
		var pay_amount = $('#pay_amount').val();
		if(pay_invoice==''){
			toastr.error("La facture a payer doit etre choisi dans la liste des factures.");
			$('#pay_invoice').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#pay_invoice').attr('class','form-control form-control-border is-valid');
		}
		if(pay_date==''){
			toastr.error("Une date de paiement est obligatoire.");
			$('#pay_date').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#pay_date').attr('class','form-control form-control-border is-valid');
		}
		if(pay_ref==''){
			toastr.error("Une reference pour ce paiement doit etre donnees.");
			$('#pay_reference').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#pay_reference').attr('class','form-control form-control-border is-valid');
		}
		if(pay_type==''){
			toastr.error("Le type de paiement doit etre renseignee en le choisissant dans la liste donnee.");
			$('#pay_type').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#pay_type').attr('class','form-control form-control-border is-valid');
		}
		if(pay_amount==''){
			toastr.error("Le montant qui a ete payee doit etre complete");
			$('#pay_amount').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#pay_amount').attr('class','form-control form-control-border is-valid');
		}
		if(pay_type!='' && pay_ref!='' && pay_date!='' && pay_invoice!='' && pay_amount!=''){
			$(this).attr('disabled','disabled');
			$.ajax({
				url:'./savepayment',
				method:'POST',
				data:{
					pay_invoice:pay_invoice,
					pay_date:pay_date,
					pay_type:pay_type,
					pay_reference:pay_ref,
					pay_bank:pay_bank,
					pay_bank_account:pay_bank_account,
					pay_comment:pay_comment,
					pay_amount:pay_amount
				},success:function(res){
					if(res.code=='1'){
						toastr.success("Enregistrement du nouveau paiement effectué avec succès");
						location.reload();
					}
				},error:function(){
					$(this).removeAttr('disabled');
				}
			})
		}
		
	});
	$('#cancelsavepayment').click(function(){
		$('#pay_invoice').val('');
		$('#pay_date').val('');
		$('#pay_type').val('');
		$('#pay_reference').val('');
		$('#pay_bank').val('');
		$('#pay_bank_account').val('');
		$('#pay_comment').val('');
		$('#pay_amount').val('');
		$('#addpayment').modal('hide');
		
		$('#pay_client').val('');
		$('#pay_inv_amount').val('');
	});
	
	
	
	// End payment controller
});
