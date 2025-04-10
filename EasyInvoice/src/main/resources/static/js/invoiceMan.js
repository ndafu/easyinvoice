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

	
	$('#dir_save_invoice').click(function(){
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
	
	
	$('.viewInvoice').click(function(ev) {
		ev.preventDefault();
		var id = $(this).data('ref');
		if(id!=''){
			$('body').attr('class','sidebar-mini layout-fixed sidebar-mini layout-fixed layout-navbar-fixed text-sm layout-footer-fixed sidebar-mini-xs sidebar-collapse');
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
							$('#quotation_print').removeAttr('hidden');
							var link = 'printI/' + id;
							$('#quotation_print').attr('href', link);
							$('#quotation_print').attr('data-ref', id);
							$('#quotation_print').attr('data-num', quot.referenceNumber);
							$('#quotation_print').attr('data-client', quot.cliName);
							$('#quotation_print').attr('data-date', formatDate(quot.invoiceDate));
							
							
						}else{
							$('#quotation_print').attr('hidden','hidden');
						}	
						$('#reg_date').text(quot.invoiceDate);
						$('#reg_number').text(quot.invoiceRef); 
						$('#reg_person').text(quot.registrationperson);
						if(quot.validatedDate!='' || quot.validatedDate!=null){
							$('#val_date').text(quot.validationdateDate);
						}
//						
						$('#val_number').text(quot.validationref);
						$('#val_person').text(quot.validationPerson);
						
						$('#canc_raison').text(quot.cancellationreason);
						$('#canc_person').text(quot.cancellationPerson);
						$('#canc_date').text(quot.cancallationDate);
						
						
						
						$('#ebms_id').text(quot.idEBMSSignature);
						$('#ebms_msg').text(quot.ebmsMsg);
						$('#ebms_ack').text(quot.ebmsACK);		
					}
					
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
						$('#inv_items_detail').append($('<td colspan="5" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("PRIX DE VENTE HTVA"));
						$('#inv_items_detail').append($('<td colspan="3" style="text-align:right;font-weight:bold; "></td>').attr('th:text', i).text(numberFormat(pvhtva)));
						$('#inv_items_detail').append($('</tr>'));

						$('#inv_items_detail').append($('<tr>'));
						$('#inv_items_detail').append($('<td colspan="5" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("TVA"));
						$('#inv_items_detail').append($('<td colspan="3" style="text-align:right;font-weight:bold;"></td>').attr('th:text', i).text(numberFormat(tva)));
						$('#inv_items_detail').append($('</tr>'));

						$('#inv_items_detail').append($('<tr>'));
						$('#inv_items_detail').append($('<td colspan="5" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text("TOTAL TVAC"));
						$('#inv_items_detail').append($('<td colspan="3" style="font-weight:bold; text-align:right;"></td>').attr('th:text', i).text(numberFormat(pvtvac)));
						$('#inv_items_detail').append($('</tr>'));
					}
				}
			});
		}
	});
	
	$('#quotation_print').click(function(d){
		d.preventDefault();
		var ref 		= $(this).data('ref');
		var num 		= $(this).data('num');
		var invdate 	= $(this).data('date');
		var invclient 	= $(this).data('client');
		
		$('#print_inv_id').val(ref);
		$('#print_inv').val(num);
		$('#print_inv_date').val(invdate);
		$('#print_inv_client').val(invclient);
		
		//print_avanc
		
		
		$('#printInvoice').modal('show');
	})

	$('#save_printM').click(function(){
		
		var invid = $('#print_inv_id').val();
		var avanc = '';
		var avancement = $('#print_avanc').is(':checked');
		
		if(avancement){
			avanc = 1;
		}else{
			avanc = 0;
		}
		
		window.open('./printI?iRef='+invid+'&avanc='+avanc);

		$('#print_inv_id').val('');
		$('#print_inv').val('');
		$('#print_inv_date').val('');
		$('#print_inv_client').val('');
		$('#printInvoice').modal('hide');

	});
	
	$('#cancel_save_printM').click(function(){
		
		$('#print_inv_id').val('');
		$('#print_inv').val('');
		$('#print_inv_date').val('');
		$('#print_inv_client').val('');
		
		$('#printInvoice').modal('hide');
	});
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
	$('#closesection').click(function(){
		$('body').attr('class','sidebar-mini layout-fixed sidebar-mini layout-fixed layout-navbar-fixed text-sm layout-footer-fixed sidebar-mini-xs');
		$('#quotation_list').attr('class', 'col-lg-12 connectedSortable');
		$('#quotation_det').attr('hidden','hidden');
	})
});
