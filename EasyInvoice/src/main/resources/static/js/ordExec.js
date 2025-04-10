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

function checkTin(tin){
	if(tin!=null){
		$.ajax({
			url:'./checkTin',
			method:'POST',
			data:{
				tin
			},success:function(data){
				if(data.code=='1'){
					toastr.success("NIF reconnu par l'OBR");
					$('#cli_name').val(data.body[0]);
				}else{
					$('#cli_tin').val(''); //cli_tin
					toastr.error("Le NIF "+tin+" que vous avez entré n'est pas reconnu par l'OBR");
				}
			}
		});
	}
}
function deleteCarteElement(ref){
	var reference = ref.getAttribute('data-ref');
	var rubrique = ref.getAttribute('data-rubric');
	deleteArt(reference,rubrique);
}

function deleteArt(reference, rubrique){
	var conf = confirm("Voulez vous reellement suppprimer cet article dans la liste d'articles?");
	if(conf){
		$.ajax({
			url:'./deleteElementOnList',
			method:'POST',
			data:{
				reference,rubrique
			},success:function(data){
				if(data.code=='1'){
					if (data.code == '1') {
						var sales = data.body[0];
						$('#tbl_exec_article').empty();
						$('#exec_sub_tot').val('');
						$('#exec_tax_tot').val('');
						$('#exec_grand_total').val('');
						if (sales.length === 0) {
							$('#tbl_exec_article').append($('<tr>'))
							$('#tbl_exec_article').append($('<td colspan="10"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
							$('#tbl_exec_article').append($('</tr>'))
						}
						else {
							var total_amount = 0.0;
							var total_vat = 0.0;
							var total_vat_inc = 0.0;
							for (i = 0; i < sales.length; i++) {
								//var tot_price = sales[i].quantity * sales[i].unity_price;
								total_amount = parseInt(total_amount) + parseInt(sales[i].art_total_nvat);
								total_vat = parseInt(total_vat) + parseInt(sales[i].tax_amount);
								var art = sales[i].article;
								$('#tbl_exec_article').append('<tr>');
									$('#tbl_exec_article').append($('<td></td>').attr('th:text', i).text(sales[i].rubliquename))
									$('#tbl_exec_article').append($('<td></td>').attr('th:text',i).text(sales[i].article))
									$('#tbl_exec_article').append($('<td></td>').attr('th:text', i).text(sales[i].unity))
									$('#tbl_exec_article').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].quantity)))
									$('#tbl_exec_article').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].unity_price)))
									$('#tbl_exec_article').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].art_total_nvat)))
									$('#tbl_exec_article').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].tax)))
									$('#tbl_exec_article').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].tax_amount)))
									$('#tbl_exec_article').append($('<td id="tt'+i+'" style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].art_total)))
									$('#tbl_exec_article').append($('<td><button type="button" class="btn btn-sm btn-danger" id="'+i+'" onclick="deleteCarteElement( this )" data-ref="' + art + '" data-rubric="' + sales[i].rubliqueid + '"><i class="fa fa-trash"> </i></button></td>'))
								$('#tbl_exec_article').append('</tr>');
							}
							
							total_vat_inc = total_amount + total_vat;
							$('#exec_sub_tot').val(numberFormat(total_amount));
							$('#exec_tax_tot').val(numberFormat(total_vat));
							$('#exec_grand_total').val(numberFormat(total_vat_inc));
							//remove values in the fields
							//$('#exec_rubrique').children('option:selected').val();
							$('#exec_article').val('');
							$('#exec_unity').val('');
							$('#exec_quantity').val('');
							$('#exec_unity_price').val('');
							$('#exec_tax').val('');
							$('#exec_tax_amount').val('');
							$('#exec_art_total_nvat').val('');
							$('#exec_art_total').val('');
						}
					}
				}
			}
		});
	}
}

function updateItemElement(id) {
	var rsp = confirm("Voulez-vous rmodifier cet article sur cette facture");
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

function calculValue(){
	var quantity = $('#exec_quantity').val();
	var price = $('#exec_unity_price').val();
	var vatRate = $('#exec_tax').val();
	var taxrate = $('#exec_taxe').children('option:selected').val();
	if(taxrate==''){
		toastr.error("Bien vouloir choisir si la TVA est applicable ou pas dans la partie \"Information générale\"");
		$('#exec_quantity').attr('class','form-control form-control-border is-invalid');
	   	$('#exec_unity_price').attr('class','form-control form-control-border is-invalid');
		$('#exec_tax').attr('class','form-control form-control-border is-invalid');
		$('#exec_taxe').attr('class','form-control form-control-border is-invalid');
	}else if(quantity!='' && price!='' && vatRate!=''){
		$('#exec_quantity').attr('class','form-control form-control-border');
	   	$('#exec_unity_price').attr('class','form-control form-control-border');
		$('#exec_tax').attr('class','form-control form-control-border');
		$('#exec_taxe').attr('class','form-control form-control-border');
		var totalNVAT = quantity * price;
		if(taxrate=='1'){
			var totVat = 0;
		}else{
			var totVat = totalNVAT*vatRate/100;
		}
		
		var total = totalNVAT+totVat;
		$('#exec_tax_amount').val(totVat);
		$('#exec_art_total_nvat').val(totalNVAT);
		$('#exec_art_total').val(total);
		
	}
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
	
	
	//End tax management
	// Article management
	$('#add_article').click(function(ev) {
		ev.preventDefault();
		$('#new_article').modal('show');
	});
	$('#saveArticle').click(function(e) {
		//recuperer les valeurs
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
			toastr.error("Pride bien vouloir renseigner le nom de l'article'")
		} else {
			$('#art_title').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_unity_price == '') {
			$('#art_unity_price').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Pride bien vouloir donner le prix unitaire de l'article'")
		} else {
			$('#art_unity_price').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_description == '') {
			$('#art_description').attr('class', 'form-control form-control-border is-invalid');
			toastr.error("Pride bien vouloir donner la description de l'article'")
		} else {
			$('#art_description').attr('class', 'form-control form-control-border is-valid');
		}
		if (art_title != '' && art_unity_price != '' && art_description != '' && art_unity!='') {
			$('#art_description').attr('class', 'form-control form-control-border is-valid');
			$('#art_unity_price').attr('class', 'form-control form-control-border is-valid');
			$('#art_title').attr('class', 'form-control form-control-border is-valid');
			var resp = confirm("Voulez-vous renregistrer cet article ?");
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
							toastr.success('Enregistrement effectuavec succ');
							location.reload();
						}
					}
				});
			}
		}
		// enregistrer
	});

	//End client management

	$('#add_client').click(function(){
		$('#new_client').modal('show');
	});
	
	$('#cancelsaveClient').click(function(ev) {
		ev.preventDefault();
		$('#cli_name').val('');
		$('#cli_contact_person').val('');
		$('#cli_tin').val('');
		$('#cli_telephon').val('');
		$('#cli_email').val('');
		$('#cli_address').val('');
		//$('#checkboxDanger1').is(':unchecked');
		$('#cli_province').val('');
		$('#cli_commune').val('');
		$('#cli_district').val('');
		$('#new_client').modal('hide');
	});

	$('#cli_tin').change(function(){
		var tin = $(this).val();
		checkTin(tin);
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
		
		if (cli_name == '') {
			$('#cli_name').attr('class', 'form-control form-control-border is-invalid');
			toastr.error('Pride fournir le nom du nouveau client');
		} else {
			$('#cli_name').attr('class', 'form-control form-control-border is-valid');
		}

		if (cli_name != '') {
			$('#cli_name').attr('class', 'form-control form-control-border is-valid');
			$('#cli_tin').attr('class', 'form-control form-control-border is-valid');
			$('#cli_telephon').attr('class', 'form-control form-control-border is-valid');
			$('#cli_contact_person').attr('class', 'form-control form-control-border is-valid');
			var resp = confirm("Voulez-vous enregistrer ce nouveau client?")
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
						district:district
					}, success: function(dta) {
						if (dta.code == '1') {
							toastr.success('Enregistrement du nouveau client effectué avec succès. Il a été ajoute a la liste des clients.');
							var client = dta.body[0];
							console.log(client);
							$('#new_client').modal('hide');
							$('#exec_client').append('<option value="'+client.id+'">'+client.name+'</option>');
							
						}
					}
				});
			}
		}

	});
	
	$('#add_rubrique').click(function(){
		let rubrique = $('#rubr_name').val();
		if(rubrique==''){
			$('#rubr_name').attr('class','form-control form-control-border is-invalid');
			toastr.success('Priere de bien vouloir donner le nom de la rubrique avant de cliquer sur ce bouton.');
		}else{
			$('#rubr_name').attr('class','form-control form-control-border is-valid');
			let res = confirm('Voulez-vous enregister cette rubrique SVP?');
			if(res){
				//$(this).attr('disabled','disabled');
				$.ajax({
					url:'./manageRubriqueSession',
					method:'POST',
					data:{
						rubriquename:rubrique
					},success:function(dat){
						if(dat.code=='1'){
							var bo = dat.body[0];
							console.log(bo)
							$('#exec_rubrique').append('<option value="'+bo.num+'">'+bo.name+'</option>');
							$('#rubr_name').val('');
							toastr.success('La rubrique a été ajoutée avec succès.');
						}else{
							toastr.error(dat.description);
						}
					}
				});
			}
			
		}
	});
	
	$('#exec_add_article').click(function(){
		var rubrique 		= $('#exec_rubrique').children('option:selected').val();
		var article			= $('#exec_article').val();
		var unity			= $('#exec_unity').val();
		var quantity		= $('#exec_quantity').val();
		var unity_price		= $('#exec_unity_price').val();
		var tax				= $('#exec_tax').val();
		var tax_amount		= $('#exec_tax_amount').val();
		var art_total_nvat	= $('#exec_art_total_nvat').val();
		var art_total		= $('#exec_art_total').val();
		if(rubrique==''){
			toastr.error("Il faut choisir la rubrique pour cet article.");
			$('#exec_rubrique').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_rubrique').attr('class','form-control form-control-border is-valid');
		}
		
		if(article==''){
			toastr.error("Le nom de l'article doit être donné. ");
			$('#exec_article').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_article').attr('class','form-control form-control-border is-valid');
		}
		
		if(art_total==''){
			toastr.error("Les montants doivent etre calculee pour cet article.");
			$('#exec_art_total').attr('class','form-control form-control-border is-invalid');
			$('#exec_tax').attr('class','form-control form-control-border is-invalid');
			$('#exec_unity_price').attr('class','form-control form-control-border is-invalid');
			$('#exec_art_total_nvat').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_art_total').attr('class','form-control form-control-border is-valid');
			$('#exec_tax').attr('class','form-control form-control-border is-valid');
			$('#exec_unity_price').attr('class','form-control form-control-border is-valid');
			$('#exec_art_total_nvat').attr('class','form-control form-control-border is-valid');
		}
		
		if(unity==''){
			toastr.error("Bien vouloir preciser l'unité de cet article.");
			$('#exec_unity').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_unity').attr('class','form-control form-control-border is-valid');
		}
		
		if(rubrique!='' && article!='' && art_total!='' && unity!=''){
			let conf = confirm("Voulez vous ajouter cet article à la liste d'article?");
			if(conf){
				$.ajax({
					url:'./manageArticleSession',
					method:'POST',
					data:{
						rubrique,
						article,
						unity,
						quantity,
						unity_price,
						tax,
						tax_amount,
						art_total_nvat,
						art_total
					},success:function(data){
						console.log(data);
						//tbl_exec_article
						if (data.code == '1') {
							var sales = data.body[0];
							$('#tbl_exec_article').empty();
							$('#exec_sub_tot').val('');
							$('#exec_tax_tot').val('');
							$('#exec_grand_total').val('');
							if (sales.length === 0) {
								$('#tbl_exec_article').append($('<tr>'))
								$('#tbl_exec_article').append($('<td colspan="10"><i></i></td>').attr('th:text', 1).text('Pas de stock pur ce produit.'))
								$('#tbl_exec_article').append($('</tr>'))
							}
							else {
								var total_amount = 0.0;
								var total_vat = 0.0;
								var total_vat_inc = 0.0;
								for (i = 0; i < sales.length; i++) {
									//var tot_price = sales[i].quantity * sales[i].unity_price;
									total_amount = parseInt(total_amount) + parseInt(sales[i].art_total_nvat);
									total_vat = parseInt(total_vat) + parseInt(sales[i].tax_amount);
									var art = sales[i].article;
									$('#tbl_exec_article').append('<tr>');
										$('#tbl_exec_article').append($('<td></td>').attr('th:text', i).text(sales[i].rubliquename))
										$('#tbl_exec_article').append($('<td></td>').attr('th:text',i).text(sales[i].article))
										$('#tbl_exec_article').append($('<td></td>').attr('th:text', i).text(sales[i].unity))
										$('#tbl_exec_article').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].quantity)))
										$('#tbl_exec_article').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].unity_price)))
										$('#tbl_exec_article').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].art_total_nvat)))
										$('#tbl_exec_article').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].tax)))
										$('#tbl_exec_article').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].tax_amount)))
										$('#tbl_exec_article').append($('<td id="tt'+i+'" style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].art_total)))
										$('#tbl_exec_article').append($('<td><button type="button" class="btn btn-sm btn-danger" id="'+i+'" onclick="deleteCarteElement( this )" data-ref="' + art + '" data-rubric="' + sales[i].rubliqueid + '"><i class="fa fa-trash"> </i></button></td>'))
									$('#tbl_exec_article').append('</tr>');
								}
								
								total_vat_inc = total_amount + total_vat;
								$('#exec_sub_tot').val(numberFormat(total_amount));
								$('#exec_tax_tot').val(numberFormat(total_vat));
								$('#exec_grand_total').val(numberFormat(total_vat_inc));
								//remove values in the fields
								//$('#exec_rubrique').children('option:selected').val();
								$('#exec_article').val('');
								$('#exec_unity').val('');
								$('#exec_quantity').val('');
								$('#exec_unity_price').val('');
								$('#exec_tax').val('');
								$('#exec_tax_amount').val('');
								$('#exec_art_total_nvat').val('');
								$('#exec_art_total').val('');
							}
						}else{
							toastr.error(data.description);
						}
					}
				});
			}
		}
	});
	
	$('.deleteCartElement').click(function(){
		var article = $(this).data('ref');
		var rubrique = $(this).data('rubric');
		deleteArt(article, rubrique); 
	});
	$('#exec_quantity').change(function(){
		calculValue();
	});
	
	$('#exec_unity_price').change(function(){
		calculValue();
	});
	
	$('#exec_tax').change(function(){
		calculValue();
	});
	
	$('#exec_taxe').change(function(){
		calculValue();
	});


	$('#exec_save').click(function(){
		 var nature 		= $('#exec_natur').val();
		 var client 		= $('#exec_client').val();
		 var currency 		= $('#exec_currency').children('option:selected').val();
		 var total_amount 	= $('#exec_grand_total').val();
		 var exec_tax_tot	= $('#exec_tax_tot').val();
		 var subtotal 		= $('#exec_sub_tot').val();
		 if(nature==''){
			toastr.error("Il faut renseigner la nature des prestations ( tel que mentionnee dans votre contrat de prestation)");
			$('#exec_natur').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_natur').attr('class','form-control form-control-border is-valid');
		}
		if(client==''){
			toastr.error("Il faut renseigner le client sinon si il n'est pas l'enregistrer d'abord puis le selectionner");
			$('#exec_client').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_client').attr('class','form-control form-control-border is-valid');
		}
		if(currency==''){
			toastr.error("Il faut selectionner la monnaie de facturation SVP");
			$('#exec_currency').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_currency').attr('class','form-control form-control-border is-valid');
		}
		if(total_amount==''){
			toastr.error("Il faut renseigner les articles sur lesquelles la facturation sera faite.");
			$('#exec_grand_total').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_grand_total').attr('class','form-control form-control-border is-valid');
		}
		if(total_amount!='' && currency!='' && client!='' && nature!=''){
			var res = confirm ("Voulez vous enregistrer cette execution des travaux ?");
			if(res){
				//$('#exec_save').attr('disabled','disabled');
				$.ajax({
					url:'./saveExecution',
					method:'POST',
					data:{
						 type:nature,
						 client:client ,
						 currency:currency,
						 total_amount:total_amount,
						 subtotal:subtotal,
						 exec_tax_tot:exec_tax_tot
					}, success:function(dta){
						if(dta.code=='1'){
							toastr.success("Enregistrement effectuee avec succes.");
							location.reload();
						}
					}
				});
			}
		}
		
	});

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

	$('#exec_currency').change(function(){
		var currency = $(this).children('option:selected').val();
		if(currency!=''){
			$.ajax({
				url:'./sessionCurrencyManage',
				method:'POST',
				data:{
					currency
				},success:function(data){
					
				}
			})
		}
	});
	
	$('#exec_taxe').change(function(){
		var tax = $(this).children('option:selected').val();
		if(tax!=''){
			$.ajax({
				url:'./sessionTaxManage',
				method:'POST',
				data:{
					tax
				},success:function(data){
					
				}
			})
		}
	});
	
	$('#exec_client').change(function(){
		var client = $(this).children('option:selected').val();
		if(client!=''){
			$.ajax({
				url:'./sessionClientManage',
				method:'POST',
				data:{
					client
				},success:function(data){
					
				}
			})
		}
	});
	
	$('#exec_natur').change(function(){
		var type = $(this).val();
		if(type!=''){
			$.ajax({
				url:'./sessionTypeManage',
				method:'POST',
				data:{
					type
				},success:function(data){
					
				}
			})
		}
	});
	
	$('.editworubric').click(function(){
		alert('tegdetet');
	});
	
});
