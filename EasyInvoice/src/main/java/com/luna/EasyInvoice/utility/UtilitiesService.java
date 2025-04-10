package com.luna.EasyInvoice.utility;

import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.luna.EasyInvoice.entities.Organisation;
import com.luna.EasyInvoice.entities.User;
import com.luna.EasyInvoice.service.implementation.OrganisationServImpl;
import com.luna.EasyInvoice.service.implementation.UserPrincipal;

@Service
public class UtilitiesService {
	
	@Autowired
	OrganisationServImpl organisationservice;
	
	private static int randomStringLength = 25 ;
    private static boolean allowSpecialCharacters = true ;
    private static String specialCharacters = "!@$%*-_+:";
    private static boolean allowDuplicates = false ;

    private static boolean isAlphanum = false;
    private static boolean isNumeric = false;
    private static boolean isAlpha = false;
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private static boolean mixCase = false;
    private static final String capAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String num = "0123456789";
	
	
	public String detuuuidCode() {
		UUID.randomUUID();
		String code = UUID.randomUUID().toString();
		return code;
	}
	
	public Date dateAddition(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);  
        return cal.getTime();
	}
	
	public String getUsername() {
		String username = "";
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername();
		} else {
			username = principal.toString();
		}
		return username;
	}
	
	
	public Date getdateFromString(String date) {
		Date date1 = null;
	    try {
	    	date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);  
		    return date1;
	    }catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return date1;
	}
	
	
	
	public static String generateRandomCode(int lenght) {
		String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
		String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz"; 
		String numbers = "0123456789"; 
	    // create a super set of all characters 
		String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase + numbers; 
	    // initialize a string to hold result 
		StringBuffer randomString = new StringBuffer(); 
	    // loop for 10 times 
		for (int i = 0; i < lenght; i++) { 
			// generate a random number between 0 and length of all characters 
			int randomIndex = (int)(Math.random() * allCharacters.length()); 
			// retrieve character at index and add it to result 
			randomString.append(allCharacters.charAt(randomIndex)); 
		} 
		return randomString.toString(); 
	} 

    private static StringBuffer buildList() {
        StringBuffer list = new StringBuffer(0);
        if (isNumeric || isAlphanum) {
            list.append(num);
        }
        if (isAlpha || isAlphanum) {
            list.append(alphabet);
            if (mixCase) {
                list.append(capAlpha);
            }
        }
        if (allowSpecialCharacters)
        {
            list.append(specialCharacters);
        }
        int currLen = list.length();
        String returnVal = "";
        for (int inx = 0; inx < currLen; inx++) {
            int selChar = (int) (Math.random() * (list.length() - 1));
            returnVal += list.charAt(selChar);
            list.deleteCharAt(selChar);
        }
        list = new StringBuffer(returnVal);
        return list;
    }   
	
    
    public String dateToString(Date date, String format) {
    	DateFormat dateFormat = new SimpleDateFormat(format);  
    	return dateFormat.format(date);  
    }
    
   // inv.setInvoiceDate("2022-05-15 12:02:14");
    
    public boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
    
    public Organisation getOrganisationFromConnectedUser() {
    	Organisation organisation=new Organisation();
    	UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		String org = princ.getOrganisation();
		if(this.isStringInt(org)) {
			organisation = this.organisationservice.findSingleOrganisation(Long.valueOf(org));
		}
		else organisation=null;
		return organisation;
    }
    
    public User getConnectedUserInfo() {
    	UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		return princ.getUser();
    }
    
    public boolean checkIfHasRole(String role) {
    	UserPrincipal princ = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  
		//System.out.println(princ.getAuthorities());
		if (princ != null && princ.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role))) {
			return true;
		}else {
			return false;
		}
    }
    
    public boolean checkIfConnected() {
    	Object princi = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
    	if(princi.getClass().equals(String.class)) {
    		if(princi.toString().equalsIgnoreCase("anonymousUser")) {
        		return false;
        	}else {
        		return true;
        	}
    	}else {
    		return true;
    	}
    	
    }

}
