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
		var totalNVAT = Number(quantity) * Number(price);
		if(taxrate=='1'){
			var totVat = 0;
		}else{
			var totVat = Number(totalNVAT)*Number(vatRate)/100;
		}
		
		var total = Number(totalNVAT)+Number(totVat);
		$('#exec_tax_amount').val(totVat);
		$('#exec_art_total_nvat').val(totalNVAT);
		$('#exec_art_total').val(total);
		
	}
}


function newcalculValue(){
	var quantity = $('#ed_exec_quantity').val();
	var price = $('#ed_exec_unity_price').val();
	var vatRate = $('#ed_exec_tax').val();
	var taxrate = $('#ed_exec_taxe').children('option:selected').val();
	if(taxrate==''){
		toastr.error("Bien vouloir choisir si la TVA est applicable ou pas dans la partie \"Information générale\"");
		$('#ed_exec_quantity').attr('class','form-control form-control-border is-invalid');
	   	$('#ed_exec_unity_price').attr('class','form-control form-control-border is-invalid');
		$('#ed_exec_tax').attr('class','form-control form-control-border is-invalid');
		$('#ed_exec_taxe').attr('class','form-control form-control-border is-invalid');
	}else if(quantity!='' && price!='' && vatRate!=''){
		$('#ed_exec_quantity').attr('class','form-control form-control-border');
	   	$('#ed_exec_unity_price').attr('class','form-control form-control-border');
		$('#ed_exec_tax').attr('class','form-control form-control-border');
		$('#ed_exec_taxe').attr('class','form-control form-control-border');
		var totalNVAT = Number(quantity) * Number(price);
		if(taxrate=='1'){
			var totVat = 0;
		}else{
			var totVat = Number(totalNVAT)*Number(vatRate)/100;
		}
		
		var total = Number(totalNVAT)+Number(totVat);
		$('#ed_exec_tax_amount').val(totVat);
		$('#ed_exec_art_total_nvat').val(totalNVAT);
		$('#ed_exec_art_total').val(total);
		
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
	
	
	$('.editworubric').click(function(){
		var id = $(this).data('ref');
		var name = $(this).data('name');
		$('#rubriqueid').val(id);
		$('#rubrique_old_name').val(name);
//		rubriqueid
//		new_rubrique_name
//		rubrique_old_name
		$('#editRubrique').modal('show');
//		let resp = confirm("Voulez-vous vraiment modifier l'intutilé de cette rubrique ? ");
//		if(resp){
//			$.ajax({
//				url:'./rubriqueModification',
//				method:'POST',
//				data:{
//					id
//				},success:function(data){
//					if(data.code=='1'){
//						toastr.success("Modification de la rubrique effectuée avec succès.")
//						location.reload();
//					}
//					
//				}
//			})
//		}
	});
	
	$('#saveRubrique').click(function(){
		var id = $('#rubriqueid').val();
		var name = $('#new_rubrique_name').val();
		
		if(name==''){
			toastr.error("Le nouveau nom nde doit pas etre vide. Priere de bien vouloir renseigner le nouveau nom de la rubrique.");
			$('#new_rubrique_name').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#new_rubrique_name').attr('class','form-control form-control-border is-valid');
			let resp = confirm("Voulez-vous vraiment modifier l'intutilé de cette rubrique ? ");
			if(resp){
				$.ajax({
					url:'./rubriqueModification',
					method:'POST',
					data:{
						id,name
					},success:function(data){
						if(data.code=='1'){
							toastr.success("Modification de la rubrique effectuée avec succès.")
							location.reload();
						}
						
					}
				})
			}
		}
	});
	
	
	$('#cancelsaveRubrique').click(function(){
		$('#rubriqueid').val('');
		$('#new_rubrique_name').val('');
		$('#rubrique_old_name').val('');
		$('#editRubrique').modal('hide');
	});
	
	
	$('.dirworubric').click(function(de){
		de.preventDefault();
		var id = $(this).data('ref');
		let conf = confirm("Etes-vous sûr de bien vouloir supprimer cette rubrique réellement?");
		if(conf){
			$.ajax({
				url:'./deleteRubrique',
				method:'POST',
				data:{
					id
				},success:function(data){
					if(data.code=='1'){
						//console.log(data);
						toastr.success("Suppression de la rubrique effectuée avec succès.");
						location.reload();
					}else{
						toastr.warning(data.description);
					}
				}
			});
		}
	});
	
	$('.addrubric').click(function(){
		//woid
		var id = $(this).data('ref');
		$('#woid').val(id);
		$('#addRubrique').modal('show');
	});
	
	
	$('#saveNewRubrique').click(function(){
		var id = $('#woid').val();
		var name = $('#rubrique_name').val();
		if(name==''){
			toastr.error("Le nom de la rubrique ne doit pas etre vide. Priere de bien vouloir renseigner le nom de la rubrique.");
			$('#rubrique_name').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#rubrique_name').attr('class','form-control form-control-border is-valid');
			$.ajax({
				url:'./addNewRubrique',
				method:'POST',
				data:{
					id,
					name
				},success:function(data){
					if(data.code=='1'){
						toastr.success('Enregistrement de la nouvelle rubrique effectué avec succès.');
						location.reload();
					}
				}
			})
		}
	});
	
	$('#cancelsaveNewRubrique').click(function(){
		$('#woid').val('');
		$('#rubrique_name').val('');
		$('#addRubrique').modal('hide');
	});
	
	
	$('.addwoitems').click(function(){
		var id = $(this).data('ref');
		$('#exec_rub_id').val(id);
		$('#addItems').modal('show');
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
	
	$('#saveNewItem').click(function(){
		var total = $('#exec_art_total').val();
		var totalnvat = $('#exec_art_total_nvat').val();
		var taxamnt = $('#exec_tax_amount').val();
		var taxrate = $('#exec_tax').val();
		var unityprice  = $('#exec_unity_price').val();
		var quantity = $('#exec_quantity').val();
		var unity = $('#exec_unity').val();
		var article = $('#exec_article').val();
		var rubid = $('#exec_rub_id').val();
		
		
		if(article==''){
			toastr.error("Le nom de l'article doit être donné. ");
			$('#exec_article').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#exec_article').attr('class','form-control form-control-border is-valid');
		}
		
		if(total==''){
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
		
		if(article!='' && total!='' && unity!=''){
			let conf = confirm("Voulez-vous enregistrer ce nouvel article ?");
			if(conf){
				$.ajax({
					url:'./addNewArticle',
					method:'POST',
					data:{
						total, 
						totalnvat,
						taxamnt,
						taxrate,
						unityprice,
						quantity,
						unity, 
						article, 
						rubid 
					},success:function(data){
						if (data.code == '1') {
							toastr.success("Enregistrement de l'article effectué avec succès.");
							location.reload();
						}
					}
				});
			}
		}
	})
	
	$('#cancelsaveNewItem').click(function(){
		$('#exec_art_total').val('');
		$('#exec_art_total_nvat').val('');
		$('#exec_tax_amount').val('');
		$('#exec_tax').val('');
		$('#exec_unity_price').val('');
		$('#exec_quantity').val('');
		$('#exec_unity').val('');
		$('#exec_article').val('');
		$('#exec_rub_id').val('');
		$('#addItems').modal('hide');
	})
	
	
	$('.editwoart').click(function(){
		var articleid = $(this).data('ref');
		
		
		var article = $(this).data('article');
		var unity = $(this).data('unity');
		var quantity = $(this).data('qty');
		var unityprice = $(this).data('pu');
		var vatrate = $(this).data('vatrate');
		var vatamount = $(this).data('vatamnt');
		var totatlnvat = $(this).data('totalnvat');
		var total = $(this).data('total');
		
		
		$('#ed_exec_art_total').val(total);
		$('#ed_exec_art_total_nvat').val(totatlnvat);
		$('#ed_exec_tax_amount').val(vatamount);
		$('#ed_exec_tax').val(vatrate);
		$('#ed_exec_unity_price').val(unityprice);
		$('#ed_exec_quantity').val(quantity);
		$('#ed_exec_unity').val(unity);
		$('#ed_exec_article').val(article);
		$('#article_id').val(articleid);
		
		
		$('#editArticle').modal('show');
	});
	

	
	
	
	
	$('#ed_exec_quantity').change(function(){
		newcalculValue();
	});
	
	$('#ed_exec_unity_price').change(function(){
		newcalculValue();
	});
	
	$('#ed_exec_tax').change(function(){
		newcalculValue();
	});
	
	$('#ed_exec_taxe').change(function(){
		newcalculValue();
	});
	
	$('#saveedArticle').click(function(){
		var total = $('#ed_exec_art_total').val();
		var totalnvat = $('#ed_exec_art_total_nvat').val();
		var taxamnt = $('#ed_exec_tax_amount').val();
		var taxrate = $('#ed_exec_tax').val();
		var unityprice  = $('#ed_exec_unity_price').val();
		var quantity = $('#ed_exec_quantity').val();
		var unity = $('#ed_exec_unity').val();
		var article = $('#ed_exec_article').val();
		var articleid = $('#article_id').val();
		
		
		if(article==''){
			toastr.error("Le nom de l'article doit être donné. ");
			$('#ed_exec_article').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_exec_article').attr('class','form-control form-control-border is-valid');
		}
		
		if(total==''){
			toastr.error("Les montants doivent etre calculee pour cet article.");
			$('#ed_exec_art_total').attr('class','form-control form-control-border is-invalid');
			$('#ed_exec_tax').attr('class','form-control form-control-border is-invalid');
			$('#ed_exec_unity_price').attr('class','form-control form-control-border is-invalid');
			$('#ed_exec_art_total_nvat').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_exec_art_total').attr('class','form-control form-control-border is-valid');
			$('#ed_exec_tax').attr('class','form-control form-control-border is-valid');
			$('#ed_exec_unity_price').attr('class','form-control form-control-border is-valid');
			$('#ed_exec_art_total_nvat').attr('class','form-control form-control-border is-valid');
		}
		
		if(unity==''){
			toastr.error("Bien vouloir preciser l'unité de cet article.");
			$('#ed_exec_unity').attr('class','form-control form-control-border is-invalid');
		}else{
			$('#ed_exec_unity').attr('class','form-control form-control-border is-valid');
		}
		
		if(article!='' && total!='' && unity!=''){
			let conf = confirm("Voulez-vous enregistrer les modifications pour cet article ?");
			if(conf){
				$.ajax({
					url:'./upArticle',
					method:'POST',
					data:{
						total, 
						totalnvat,
						taxamnt,
						taxrate,
						unityprice,
						quantity,
						unity, 
						article, 
						articleid 
					},success:function(data){
						if (data.code == '1') {
							toastr.success("Enregistrement des modifications sur l'article effectué avec succès.");
							location.reload();
						}
					}
				});
			}
		}
	});
	
	
	
	$('.dirwoart').click(function(){
		var id = $(this).data('ref');
		let conf = confirm("Etes-vous sûr(e) de vouloir supprimer cet article ?");
		if(conf){
			$.ajax({
				url:'/deleteItemOnWO',
				method:'POST',
				data:{
					id
				},success:function(data){
					if(data.code=='1'){
						toastr.success("Suppression d el'article effectué avec succès.");
						location.reload();
					}else{
						toastr.warning(data.description);
					}
				}
			})
		}
	});
	
	
	
	
	
	
});
