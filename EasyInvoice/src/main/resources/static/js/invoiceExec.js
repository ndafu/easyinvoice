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

function calcul (){
	var avanc_based = $('#avanc_based').is(':checked');
	var quantity 	= $('#quo_quantity').val();
	let actRate 	= $('#quo_act_avan').val();
	var taxrate 	= $('#quo_tax_rate').val();
	var qty 		= $('#quo_qte_total').val();
	var unityPrice 	= $('#quo_unity_price').val();
	let remrate 	= $('#quo_remrate').val();
	var remqty 		= $('#qty_rem').val();		
	
	if(avanc_based){
		$('#quo_quantity').attr('readonly','readonly');
		$('#quo_act_avan').removeAttr('readonly');
		if(Number(actRate) > Number(remrate)){
			toastr.error("Le taux à facturer ["+actRate+"] ne doivent pas être supérieur au taux restantes["+remrate+"]");
			$('#quo_act_avan').attr('class','form-control form-control-border is-invalid');
			$('#quo_remrate').attr('class','form-control form-control-border is-invalid');
			$('#quo_tax_amnt').val('');
			$('#quo_quantity').val('');
			$('#quo_art_total').val('');
			$('#quo_art_vat_total').val('')
		}else{
			$('#quo_act_avan').attr('class','form-control form-control-border');
			$('#quo_remrate').attr('class','form-control form-control-border');
			var actQty = actRate*qty/100;
			var tot1  = actQty*unityPrice;
			var tax2pay = 0.0;
			
			if(+taxrate>0){
				tax2pay = tot1*taxrate/100;
			}
			var total = tot1 + tax2pay;
			$('#quo_tax_amnt').val(Math.round(tax2pay));
			$('#quo_quantity').val(actQty);
			$('#quo_art_total').val(Math.round(tot1));
			$('#quo_art_vat_total').val(Math.round(total));
		}
		
	}else{
		$('#quo_quantity').removeAttr('readonly');
		$('#quo_act_avan').attr('readonly','readonly');
		
		var rate = Math.round(quantity*100/qty);
		
		if(Number(rate)>Number(remrate)){
			toastr.error("Les quantité à facturer ["+quantity+"] ne doivent pas être supérieur aux quantités restantes["+remqty+"] ");
			$('#quo_quantity').attr('class','form-control form-control-border is-invalid');
			//$('#quo_remrate').attr('class','form-control form-control-border is-invalid');
			
			$('#quo_tax_amnt').val('');
			$('#quo_act_avan').val('');
			$('#quo_art_total').val('');
			$('#quo_art_vat_total').val('')
		}else{
			$('#quo_quantity').attr('class','form-control form-control-border');
			var tot1  = quantity*unityPrice;
			var tax2pay = 0.0;
			if(taxrate>0){
				tax2pay = tot1*taxrate/100;
			}
			
			var total = tot1 + tax2pay;
			$('#quo_tax_amnt').val(Math.round(tax2pay));
			$('#quo_act_avan').val(rate);
			$('#quo_art_total').val(Math.round(tot1));
			$('#quo_art_vat_total').val(Math.round(total));
		}
		
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
	
	
	$('#quo_client').change(function(){
		var client_id = $(this).children('option:selected').val();
		$('#quo_projet').empty();
		$('#quo_projet').append('<option></option>');
		if(client_id!=''){
			$.ajax({
				url:'./searchProject',
				method:'POST',
				data:{
					client_id
				},success:function(data){
					var bdo = data.body;
					if(bdo.length>0){
						for (i = 0; i < bdo.length; i++) {
							$('#quo_projet').append('<option value="'+bdo[i].id+'" >'+bdo[i].nature+'</option>');
						}
					}
				}
			})
		}else{
			$('#quo_projet').empty();
			$('#quo_rubrics').empty();
		}
	});
	
	$('#quo_projet').change(function(){
		var projectid = $(this).children('option:selected').val();
		if(projectid!=''){
			$('#quo_rubrics').empty();
			$('#quo_rubrics').append('<option></option>');
			$.ajax({
				url:'./searchRubrics',
				method:'POST',
				data:{
					projectid
				},success:function(data){
					if(data.code=='1'){
						var bdo = data.body;
						if(bdo.length>0){
							for (i = 0; i < bdo.length; i++) {
								$('#quo_rubrics').append('<option value="'+bdo[i].id+'" >'+bdo[i].name+'</option>');
							}
						}
					}
				}
			})
		}else{
			$('#quo_rubrics').empty();
		}
	});
	
	$('#quo_rubrics').change(function(){
		var rubricid = $(this).children('option:selected').val();
		if(rubricid!=''){
			$('#quo_articles').empty();
			$('#quo_articles').append('<option></option>');
			$.ajax({
				url:'./searchRubricsItems',
				method:'POST',
				data:{
					rubricid
				},success:function(dta){
					var bdo = dta.body;
					if(bdo.length>0){
						for (i = 0; i < bdo.length; i++) {
							$('#quo_articles').append('<option value="'+bdo[i].id+'" >'+bdo[i].article+'</option>');
						}
					}
				}
			})
		}else{
			$('#quo_articles').empty();
			$('#quo_articles').append('<option></option>');
		}
	});
	
	$('#quo_articles').change(function(){
		var articleid = $(this).children('option:selected').val();
		if(articleid!=''){
			$.ajax({
				url:'./getArticleDetails',
				method:'POST',
				data:{
					articleid
				},success:function(data){
					if(data.code=='1'){
						var bdo = data.body[0];
						
						var rate = bdo.totalTax;
						if(rate > 0){
							$('#quo_tax_rate').val(bdo.taxRate);
						}else{
							$('#quo_tax_rate').val(0.0);
						}
						$('#art_name').val(bdo.articleName);
						$('#quo_takrate').val(bdo.takrate);
						$('#quo_qte_taken').val(bdo.takquantity);
						$('#quo_qte_total').val(bdo.totquantity);
						$('#quo_unity_price').val(bdo.unityprice);
						$('#quo_unity').val(bdo.unity);
						$('#quo_remrate').val(bdo.remrate);
						$('#qty_rem').val(bdo.remquantity);		
						$('#tax_rate').val(bdo.taxRate);
						$('#tax_amount').val(bdo.totalTax);
						$('#art_rubric_name').val(bdo.rubricName);
					}
					
					console.log(data);
				}
			})
		}
	});
	
	$('#avanc_based').change(function(){
		var avanc_based = $('#avanc_based').is(':checked');
		if(avanc_based){
			$('#quo_quantity').attr('readonly','readonly');
			$('#quo_act_avan').removeAttr('readonly');
		}else{
			$('#quo_quantity').removeAttr('readonly');
			$('#quo_act_avan').attr('readonly','readonly');
		}
	});
	
	$('#quo_act_avan').change(function(){
		calcul();		
	});
	
	$('#quo_quantity').change(function(){
		calcul();
	});
	
	$('#inv_restourne_pourcentage').change(function(){
		var ristRate = $(this).val();
		var returnAmount = 0.0;
		let res = 0.0;
		var inv_total = $('#quo_total').val();
		var inv_tax = $('#quo_tax').val();
		var inv_sub_tot = $('#quo_sub_tot').val();
		res = $('#inv_restourne').val();
		let sub = inv_sub_tot.replace(/ /g,'');
		let tot = inv_total.replace(/ /g,'');
		let restou = res.replace(/ /g,'');
		let tax = inv_tax.replace(/ /g,'');
		if(restou==''){
			restou=0.0;
		}
		//alert('Subtotal : '+sub+' and the previous rest : '+restou);
		
		let base = parseFloat(sub)-parseFloat(restou);
		let rate = parseFloat(tax) / parseFloat(base);
		let newRistourne = parseFloat(ristRate)*parseFloat(sub)/100;
		let newBase = parseFloat(sub) - parseFloat(newRistourne);
		let newTax = parseFloat(rate)*parseFloat(newBase);
		let newTotal = newBase + newTax;
		
		$('#inv_restourne').val('');
		
		$('#quo_tax').val('');
		$('#quo_total').val('');
		
		
		$('#inv_restourne').val(numberFormat(newRistourne));
		$('#quo_tax').val(numberFormat(newTax));
		$('#quo_total').val(numberFormat(newTotal));
	});
	
	
	$('#add_article_quo').click(function(){
		var restournerate = $('#inv_restourne_pourcentage').val();
		var quantity = $('#quo_quantity').val();
		var taxrate = $('#quo_tax_rate').val();
		var taxamount = $('#quo_tax_amnt').val();
		var subtotat = $('#quo_art_total').val();
		var grandtotal = $('#quo_art_vat_total').val();
		var progressRate = $('#quo_act_avan').val();
		var unity = $('#quo_unity').val();
		var unityPrice = $('#quo_unity_price').val();
		var articleid = $('#quo_articles').children('option:selected').val();
		var taxmount = $('#tax_amount').val();
		var rubricname = $('#art_rubric_name').val();
		var articlename = $('#art_name').val();
		var previousProgressRate = $('#quo_takrate').val();
		
		$.ajax({
			url:'./managerExecInvoiceSession',
			method:'POST',
			data:{
				quantity,
				taxrate,
				taxamount,
				subtotat,
				grandtotal,
				progressRate,
				unity,
				unityPrice,
				articleid,
				taxmount,
				rubricname,
				articlename,
				previousProgressRate
			},success:function(data){
				//quotation_detail
				if (data.code == '1') {
					var sales = data.body[0];
					$('#quotation_detail').empty(); //quo_sub_tot , quo_tax , quo_total
					$('#quo_sub_tot').val('');
					$('#quo_tax').val('');
					$('#quo_total').val('');
					
					if (sales.length === 0) {
						$('#quotation_detail').append($('<tr>'))
						$('#quotation_detail').append($('<td colspan="12"><i></i></td>').attr('th:text', 1).text(''))
						$('#quotation_detail').append($('</tr>'))
					}
					else {						
						var total_amount = 0.0;
						var total_vat = 0.0;
						var total_vat_inc = 0.0;
						for (i = 0; i < sales.length; i++) {
							total_amount = parseInt(total_amount) + parseInt(sales[i].totalwvat);
							total_vat = parseInt(total_vat) + parseInt(sales[i].vatamount);
							
							$('#quotation_detail').append('<tr>');
								$('#quotation_detail').append($('<td></td>').attr('th:text', i).text(sales[i].rubricname))
								$('#quotation_detail').append($('<td></td>').attr('th:text',i).text(sales[i].articlename))
								$('#quotation_detail').append($('<td></td>').attr('th:text', i).text(sales[i].unity))
								$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].quantity)))
								$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].unityPrice)))
								
								$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].previousRate)))
								$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].currentRate)))
								
								$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].totalwvat)))
								$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].vatamount)))
								$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].totalvatinc)))
								$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].totalvatinc)))
								$('#quotation_detail').append($('<td><button type="button" class="btn btn-sm btn-danger updatesaleschart" onclick="deleteCartElement(' + sales[i].article + ')" th:data-ref="' + sales[i].article + '"><i class="fa fa-trash"> </i></button></td>'))
							$('#quotation_detail').append('</tr>');
						}
						
						
						var returnAmount = 0.0;
						if(restournerate!=''){
							returnAmount = parseInt(total_amount)*parseInt(restournerate)/100;
							total_vat = parseInt(total_vat)-(parseInt(total_vat) * parseInt(restournerate)/100);
						}
						$('#inv_restourne').val(numberFormat(returnAmount));
						
						total_vat_inc = total_amount - returnAmount + total_vat; 
						
						$('#quo_sub_tot').val(numberFormat(total_amount));
						$('#quo_tax').val(numberFormat(total_vat));
						$('#quo_total').val(numberFormat(total_vat_inc));
						
						$('#quo_quantity').val('');
						$('#quo_tax_rate').val('');
						$('#quo_tax_amnt').val('');
						$('#quo_art_total').val('');
						$('#quo_art_vat_total').val('');
						$('#quo_act_avan').val('');
						$('#quo_unity').val('');
						$('#quo_unity_price').val('');
						$('#quo_articles').prop('selectedIndex', 0);
						$('#tax_amount').val('');
						$('#art_rubric_name').val('');
						$('#art_name').val('');
						$('#quo_takrate').val('');
						$('#quo_remrate').val('');
						$('#quo_qte_taken').val('');
						$('#quo_qte_total').val('');
						$('#qty_rem').val('');
						$('#tax_rate').val('');
						
					}
				}else{
					toastr.error(data.description);
					var sales = data.body;
					
					console.log(sales);
					$('#quotation_detail').empty(); //quo_sub_tot , quo_tax , quo_total
					$('#quo_sub_tot').val('');
					$('#quo_tax').val('');
					$('#quo_total').val('');
					var total_amount = 0.0;
					var total_vat = 0.0;
					var total_vat_inc = 0.0;
					for (i = 0; i < sales.length; i++) {
						total_amount = parseInt(total_amount) + parseInt(sales[i].totalwvat);
						total_vat = parseInt(total_vat) + parseInt(sales[i].vatamount);
						
						$('#quotation_detail').append('<tr>');
							$('#quotation_detail').append($('<td></td>').attr('th:text', i).text(sales[i].rubricname))
							$('#quotation_detail').append($('<td></td>').attr('th:text',i).text(sales[i].articlename))
							$('#quotation_detail').append($('<td></td>').attr('th:text', i).text(sales[i].unity))
							$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].quantity)))
							$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].unityPrice)))
							
							$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].previousRate)))
							$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].currentRate)))
							
							$('#quotation_detail').append($('<td style="text-align:right;" ></td>').attr('th:text', i).text(numberFormat(sales[i].totalwvat)))
							$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].vatamount)))
							$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].totalvatinc)))
							$('#quotation_detail').append($('<td style="text-align:right;"></td>').attr('th:text', i).text(numberFormat(sales[i].totalvatinc)))
							$('#quotation_detail').append($('<td><button type="button" class="btn btn-sm btn-danger updatesaleschart" onclick="deleteCartElement(' + sales[i].article + ')" th:data-ref="' + sales[i].article + '"><i class="fa fa-trash"> </i></button></td>'))
						$('#quotation_detail').append('</tr>');
					}
					
					
					total_vat_inc = total_amount + total_vat;
					
					$('#quo_sub_tot').val(numberFormat(total_amount));
					$('#quo_tax').val(numberFormat(total_vat));
					$('#quo_total').val(numberFormat(total_vat_inc));
					
					$('#quo_quantity').val('');
					$('#quo_tax_rate').val('');
					$('#quo_tax_amnt').val('');
					$('#quo_art_total').val('');
					$('#quo_art_vat_total').val('');
					$('#quo_act_avan').val('');
					$('#quo_unity').val('');
					$('#quo_unity_price').val('');
					$('#quo_articles').prop('selectedIndex', 0);
					$('#tax_amount').val('');
					$('#art_rubric_name').val('');
					$('#art_name').val('');
					$('#quo_takrate').val('');
					$('#quo_remrate').val('');
					$('#quo_qte_taken').val('');
					$('#quo_qte_total').val('');
					$('#qty_rem').val('');
					$('#tax_rate').val('');
				}
			}
		});
		
		
	});
	
	$('#save_exec_invoice').click(function(){
		var ristourneamount = $('#inv_restourne').val();
		var ristournepourcentage = $('#inv_restourne_pourcentage').val();	
		var client_id 	= $('#quo_client').children('option:selected').val();
		var contrat_id 	= $('#quo_projet').children('option:selected').val();
		var subTotal 	= $('#quo_sub_tot').val();
		var totalVat 	= $('#quo_tax').val();
		var grandtotal 	= $('#quo_total').val();
		var pyt_mod		= $('#dir_inv_pyt_mod').children('option:selected').val();
		
		
		if(client_id==''){
			toastr.error('Prière de bien vouloir choisir un client pour la facturation');
			$('#quo_client').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#quo_client').attr('class','form-control form-control-border is-valid');
		}
		if(contrat_id==''){
			toastr.error('Prière de bien vouloir choisir le contrat/projet que vous executez pour le compte du client');
			$('#quo_projet').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#quo_projet').attr('class','form-control form-control-border is-valid');
		}
		if(pyt_mod==''){
			toastr.error('Prière de bien vouloir choisir le mode de paiement');
			$('#dir_inv_pyt_mod').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#dir_inv_pyt_mod').attr('class','form-control form-control-border is-valid');
		}
		
		if(grandtotal =='' || subTotal=='' ||totalVat==''){
			toastr.error('Prière de bien charger la liste des articles/travaux à facturer');
			$('#quo_total').attr('class','form-control form-control-border is-invalid');
			$('#quo_tax').attr('class','form-control form-control-border is-invalid');
			$('#quo_sub_tot').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#quo_total').attr('class','form-control form-control-border is-valid');
			$('#quo_tax').attr('class','form-control form-control-border is-valid');
			$('#quo_sub_tot').attr('class','form-control form-control-border is-valid');
		}
		//alert("testt");
		if(client_id!='' && contrat_id!='' && subTotal!='' && totalVat !='' && grandtotal !='' && pyt_mod!=''){
			var res = confirm("Voulez vous enregistrer cette facture?");
			if(res){
				$.ajax({
					url:'./saveExecInvoice',
					method:'POST',
					data:{
						client_id,contrat_id, subTotal,totalVat,grandtotal,pyt_mod, ristourneamount,ristournepourcentage
					},
					success:function(dta){
						if(dta.code=='1'){
							toastr.success("Enregistrement de la facture effectuee avec succes.");
							location.reload();
						}
					},error:function(){
						toastr.error("Une erreur est survenu lors de l'enregistrement de cette facture.");
					}
				})
			}
		}
	})
	
	
});
