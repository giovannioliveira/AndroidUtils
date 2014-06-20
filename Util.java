
import java.util.Calendar;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Util {

	static boolean isUpdatingPhone;
	static boolean isUpdatingCpf;
	static boolean userCpfStatus = false;
	
	public static boolean isCpfOk(){
		return userCpfStatus;
	}

	public static boolean isValidText(final String textName){
		boolean result = false;
		if (!textName.matches("[a-zA-Z &/]*")) {
			result = true;
		}
		return result;
	}
	
	public static String getAppVersion(Context context){
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo("com.dezinove.fnfusuario", 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public static String getOsVersion(){
		
		return String.valueOf(android.os.Build.VERSION.SDK_INT);
		
	}
	
	public static String getTimeStamp(){
		String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());
		return timeInMillis.substring(0, 10);
		
	}
		
	// Phone Mask.
	public static void addTextChangedListenerPhoneMask(final EditText edt){
    	edt.addTextChangedListener(new TextWatcher() {  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
    	    }  
    	      
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	      
    			// Avoid endless loop when text is changed  
    			if (isUpdatingPhone){
    				isUpdatingPhone = false;  
    				return;  
    			}  
    	      
    			// Remove o '-' da String  
    			String str = s.toString().replaceAll("[-]", "").replaceAll("[(]", "").replaceAll("[) ]", "");  
    	      
    			if (after > before) {  

    				str = '(' + str;  
    				
    				if (str.length() > 3) {  
    					str = str.substring(0,3) + ") " + str.substring(3);  
    				}  
    				
    				if (str.length() > 9) {  
    					str = str.substring(0,9) + '-' + str.substring(9);  
    				}  
    				
    				if (str.length() > 14) {
    					str = str.toString().replaceAll("[-]", "");
    					str = str.substring(0,10) + '-' + str.substring(10);  
    				}  
    				
    				// Seta a flag pra evitar chamada infinita  
    				isUpdatingPhone = true;  
    				
    				// seta o novo texto  
    				edt.setText(str);  
    				
    				// set cusros position  
    				if(start == 0 || start == 9 ){
        				edt.setSelection(start + 2);  
    				} else if (start == 3){
            				edt.setSelection(start + 3);  
    				}else{
        				edt.setSelection(start + 1);  
    				}
    	      
    			} else {  
    				isUpdatingPhone = true;  
    				
    				if(str.length() > 0){
        				str = '(' + str;
    				}
    				
    				if (str.length() > 3) {  
    					str = str.substring(0,3) + ") " + str.substring(3);  
    				}

    				if (str.length() > 9) {  
    					str = str.substring(0,9) + '-' + str.substring(9);  
    				}

    				edt.setText(str);  
    				
    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
    			}  
    		}  
    		
    	    public void afterTextChanged(Editable s) {  
    	    }  
        });
	}
	
	// CPF Mask
	public static void addTextChangedListenerCpfVerifierAndMask(final EditText edt){
		
		edt.addTextChangedListener(new TextWatcher() {  
    	    
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  
    	      
    		
    	    public void onTextChanged(CharSequence s, int start, int before, int after) {  
    	         
    			// Avoid endless loop when text is changed  
    			if (isUpdatingCpf){
    				isUpdatingCpf = false;  
    				return;  
    			}  
    	      
    			// Remove o '-'  e o '.'da String  
    			String str = s.toString().replaceAll("[-]", "").replaceAll("[.]", "");  
    			
    			if (after > before && before < 12) {  
    				
    				if (str.length() > 3) {  
    					str = str.substring(0,3) + '.' + str.substring(3);  
    				}  
    				
    				if (str.length() > 7) {  
    					str = str.substring(0,7) + '.' + str.substring(7);  
    				}  
    				
    				if (str.length() > 11) {  
    					str = str.substring(0,11) + '-' + str.substring(11);  
    				}  
    				
    				if (str.length() > 14) {  
    					str = str.substring(0,14);  
    				}
    				
    				// Seta a flag pra evitar chamada infinita  
    				isUpdatingCpf = true;  
    				
    				// seta o novo texto  
    				edt.setText(str);  
    				
    				// seta a posição do cursor  
    				if(start == 3 || start == 7 || start == 11 ){
    					edt.setSelection(start + 2);  
    				}else if (start == 14){
    					edt.setSelection(start);  
    				}else{
    					edt.setSelection(start + 1);  
    				}
    				
    			} else {  
    				isUpdatingCpf = true;  
    				
    				if (str.length() > 3) {  
    					str = str.substring(0,3) + '.' + str.substring(3);  
    				}
    				
    				if (str.length() > 7) {  
    					str = str.substring(0,7) + '.' + str.substring(7);  
    				}
    				
    				if (str.length() > 11) {  
    					str = str.substring(0,11) + '-' + str.substring(11);  
    				}
    				
    				if (str.length() > 18) {  
    					str = str.substring(0,18);  
    				}
    				
    				edt.setText(str);  
    				
    				// Se estiver apagando posiciona o cursor no local correto. Isso trata a deleção dos caracteres da máscara.  
    				edt.setSelection(Math.max(0, Math.min(start + 1 - before, str.length() ) ) ); 
    			}  
    		}
			
    	    public void afterTextChanged(Editable s) {  
    	    
    	    	if (s.length() == 14){
    	    		userCpfStatus = validateCpf(s.toString().replaceAll("[-]", "").replaceAll("[.]", ""));
    	    	}else {
    	    		userCpfStatus = false;
    	    	}
    		}  
        });
	}
	
	public static boolean validateCpf(String cpf) {  
        int     d1, d2;  
        int     digito1, digito2, resto;  
        int     digitoCPF;  
        String  nDigResult;  

        d1 = d2 = 0;  
        digito1 = digito2 = resto = 0;
        
        cpf = cpf.replaceAll("[-]", "").replaceAll("[.]", "");

        if (cpf.length() != 11 || cpf.equalsIgnoreCase("00000000000") || cpf.equalsIgnoreCase("11111111111") || cpf.equalsIgnoreCase("22222222222") || cpf.equalsIgnoreCase("33333333333") || cpf.equalsIgnoreCase("44444444444") ||
        	cpf.equalsIgnoreCase("55555555555") || cpf.equalsIgnoreCase("66666666666") || cpf.equalsIgnoreCase("77777777777") || cpf.equalsIgnoreCase("88888888888") || cpf.equalsIgnoreCase("99999999999")){
        	return false;
        }
        
        for (int nCount = 1; nCount < cpf.length() -1; nCount++)  
        {  
           digitoCPF = Integer.valueOf (cpf.substring(nCount -1, nCount)).intValue();  

           //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
           d1 = d1 + ( 11 - nCount ) * digitoCPF;  

           //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
           d2 = d2 + ( 12 - nCount ) * digitoCPF;  
        };  

        //Primeiro resto da divisão por 11.  
        resto = (d1 % 11);  

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
        if (resto < 2)  
           digito1 = 0;  
        else  
           digito1 = 11 - resto;  

        d2 += 2 * digito1;  

        //Segundo resto da divisão por 11.  
        resto = (d2 % 11);  

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
        if (resto < 2)  
           digito2 = 0;  
        else  
           digito2 = 11 - resto;  

        //Digito verificador do CPF que está sendo validado.  
        String nDigVerific = cpf.substring (cpf.length()-2, cpf.length());  

        //Concatenando o primeiro resto com o segundo.  
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);  

        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.  
        return nDigVerific.equals(nDigResult);  
     }
	
    public static boolean validateCnpj( String str_cnpj ) {  
        if (! str_cnpj.substring(0,1).equals("")){  
            try{
                str_cnpj=str_cnpj.replace('.',' ');
                str_cnpj=str_cnpj.replace('/',' ');
                str_cnpj=str_cnpj.replace('-',' ');
                str_cnpj=str_cnpj.replaceAll(" ","");
            	
                if(str_cnpj.length() != 14){
            		return false;
            	}
            	
                int soma = 0, aux, dig;  
                String cnpj_calc = str_cnpj.substring(0,12);  
                  
                if ( str_cnpj.length() != 14 )  
                    return false;  
                char[] chr_cnpj = str_cnpj.toCharArray();  
                /* Primeira parte */  
                for( int i = 0; i < 4; i++ )  
                    if ( chr_cnpj[i]-48 >=0 && chr_cnpj[i]-48 <=9 )  
                        soma += (chr_cnpj[i] - 48 ) * (6 - (i + 1)) ;  
                for( int i = 0; i < 8; i++ )  
                    if ( chr_cnpj[i+4]-48 >=0 && chr_cnpj[i+4]-48 <=9 )  
                        soma += (chr_cnpj[i+4] - 48 ) * (10 - (i + 1)) ;  
                dig = 11 - (soma % 11);  
                cnpj_calc += ( dig == 10 || dig == 11 ) ?  
                    "0" : Integer.toString(dig);  
                /* Segunda parte */  
                soma = 0;  
                for ( int i = 0; i < 5; i++ )  
                    if ( chr_cnpj[i]-48 >=0 && chr_cnpj[i]-48 <=9 )  
                        soma += (chr_cnpj[i] - 48 ) * (7 - (i + 1)) ;  
                for ( int i = 0; i < 8; i++ )  
                    if ( chr_cnpj[i+5]-48 >=0 && chr_cnpj[i+5]-48 <=9 )  
                        soma += (chr_cnpj[i+5] - 48 ) * (10 - (i + 1)) ;  
                dig = 11 - (soma % 11);  
                cnpj_calc += ( dig == 10 || dig == 11 ) ?  
                    "0" : Integer.toString(dig);  
                return str_cnpj.equals(cnpj_calc);  
            }catch (Exception e){  
                return false;  
            }  
        }else return false;  
          
    }
    
    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_DOUBLE, setando como Constantes.NULO_DOUBLE caso
     * verdadeiro
     * 
     * @param valor
     * @return
     */
    public static double verificarNuloDouble(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Constantes.NULO_DOUBLE;
		} else {
		    return Double.parseDouble(valor.trim());
		}
    }

    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_STRING, setando como Constantes.NULO_STRING caso
     * verdadadeiro
     * 
     * @param valor
     * @return
     */
    public static String verificarNuloString(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING) || valor.trim().equals("null")) {
		    return Constantes.NULO_STRING;
		} else {
		    return valor.trim();
		}
    }

    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_INT, setando como Constantes.NULO_INT caso
     * verdadadeiro
     * 
     * @param valor
     * @return
     */
    public static int verificarNuloInt(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Constantes.NULO_INT;
		} else {
		    return Integer.parseInt(valor.trim());
		}
    }

    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_INT, setando como Constantes.NULO_INT caso
     * verdadadeiro
     * 
     * @param valor
     * @return
     */
    public static long verificarNuloLong(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Constantes.NULO_INT;
		} else {
		    return Long.parseLong(valor.trim());
		}
    }

    /**
     * Verifica se o valor da String.trim() veio como null ou como
     * Constantes.NULO_STRING, setando como Constantes.NULO_INT caso
     * verdadadeiro
     * 
     * @param valor
     * @return
     */
    public static short verificarNuloShort(String valor) {
		if (valor == null || valor.trim().equals(Constantes.NULO_STRING)) {
		    return Constantes.NULO_SHORT;
		} else {
		    return Short.parseShort(valor.trim());
		}
    }

	public static String capitalizeString(String string) {
    	string = string.toLowerCase();
    	String[] palavras = string.split(" ");
    	String novaString = "";
    	
    	for (String palavra : palavras) {
    		if (palavra.equals("")){
    			continue;
    		}
			palavra = Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1);
			novaString += palavra + " ";
		}
    	
    	return novaString;
    }

}
