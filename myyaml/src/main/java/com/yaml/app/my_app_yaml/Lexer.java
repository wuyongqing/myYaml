package com.yaml.app.my_app_yaml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Lexer{
	private String all = "";
	private int length;
	private boolean ifValid = false;
	private ArrayList<Token> tokens;
	private ArrayList<Error> errors;
	private String currLine="";
	private int index = 0;
	private int Tab = 4;

	public boolean getIfValid() {
		return ifValid;
	}
	public ArrayList<Token> getTokens() {
		return tokens;
	}

    public static String stringToUnicode(String s) {  
        try {  
            StringBuffer out = new StringBuffer("");  
            //直接获取字符串的unicode二进制  
            byte[] bytes = s.getBytes("unicode");  
            //然后将其byte转换成对应的16进制表示即可  
            for (int i = 0; i < bytes.length - 1; i += 2) {  
                out.append("\\u");  
                String str = Integer.toHexString(bytes[i + 1] & 0xff);  
                for (int j = str.length(); j < 2; j++) {  
                    out.append("0");  
                }  
                String str1 = Integer.toHexString(bytes[i] & 0xff);  
                out.append(str1);  
                out.append(str);  
            }  
            return out.toString();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
    public Lexer(String filePath) {
    	try {
            InputStream in = new FileInputStream(filePath);
            byte[] b=new byte[in.available()+1];
            in.read(b);
            in.close();
            b[b.length-1] = '\n';
            if(!Objects.equals(stringToUnicode(all), "UTF-8")){
            	all=new String(b, "UTF-8");
            }else{
                all=new String(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tokens = new ArrayList<Token>();
        errors = new ArrayList<Error>();
	}
    
    private String nextLine() throws IOException{ 
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(all.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));   
    	String line;    
	    StringBuffer strbuf=new StringBuffer();  
	    while ( (line = br.readLine()) != null ) {
	        if(!line.trim().equals("")){  
	            strbuf.append(line+"\r\n");  
	        }                         
	    }
	    return line;  
    }
    
    private int checkFormat(String content) throws IOException{
        if(content.matches("^(0|-?[1-9]\\d*)$")){//int
            return 0;
        }else if(content.matches("^((0\\.0)|-?[1-9]\\d*\\.\\d*)$")){//double
            return 1;
        }else if(content.matches("^(\\d\\.\\d*(e|E)(\\+|-)?\\d*)$")) {//scientific
            return 2;
        }else if(content.equals("true")||content.equals("false")){//bool
            return 3;
        }else{
            if(!content.startsWith("\"")||!content.endsWith("\"")){//string
                return -1;
            }
            return 4;
        }
    }
    private boolean KeyFromat(String key){
        if(key.matches("^[a-zA-Z](([0-9a-zA-Z_]*[0-9a-zA-Z])|[0-9a-zA-Z])?$")){
            return true;
        }
        return false;
    }
    private int countSpace(String s) {
    	int num=0;
    	boolean ifAllBlank = true;
    	for(int i=0;i<s.length();i++){
    		char c = s.charAt(i);
    		if(c==' '){
    			num++;
    		}else{
    			ifAllBlank = false;
    			break;
    		}
    	}
    	if(ifAllBlank){
    		return -1;
    	}else{
    		return num;
    	}
    	
	}
    public void scanner() throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(all.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));   
    	String line;    
    	int spaceNum = 0, level = 0, lineNum = 0, posNum = 0;
    	Token token;
    	Error error;
	    StringBuffer strbuf = new StringBuffer();
	    while ( (line = br.readLine()) != null ) {
	    	lineNum++;
	    	while (line.startsWith(" ")) {
	    		spaceNum++;
	    		posNum++;
				line = line.substring(1);
			}
	    	if(spaceNum%2!=0){
	    		error = new Error(lineNum, posNum, "Wrong number of indent!");
	    		errors.add(error);
	    	}else{
	    		level = spaceNum/2;
	    	}
	    	if(line.startsWith("#")){
	    		token = new Token(TokenType.COMMENT, lineNum, posNum, level, false);
	    		token.setValue(line);
	    		tokens.add(token);
	    	}else if(line.contains(":")){
	    		String[] key_value=line.split(":", 2);
	    		String key = key_value[0];
	    		String value = key_value[1];
	    		posNum+=key.length()+1;
	    		int temp = posNum;
	    		if(value.contains("#")){
	    			String[] value_comment=value.split("#", 2);
		    		token = new Token(TokenType.COMMENT, lineNum, posNum, level, false);
		    		token.setValue( value_comment[1]);
		    		tokens.add(token);
		    		value = value_comment[0];
	    		}
	    		if(countSpace(value) == 1){
	    			posNum+=1;
	    			if(value.trim().length()>0&&!value.trim().equals("\n")){
	    				if(KeyFromat(key)){
	    	    			if(tokens.get(tokens.size()-1).getValue().equals("default")){
	    	    				token = new Token(TokenType.KEY_VALUE_inArray, tokens.get(tokens.size()-1).getLineNum(), tokens.get(tokens.size()-1).getPosNum(), tokens.get(tokens.size()-1).getLevel(), true);
	    	    				token.setValue("key_value_inArray");
	    	    				tokens.remove(tokens.size()-1);
	    	    				tokens.add(token);
	    	    				level--;
	    	    			}
	    	    			token = new Token(TokenType.IDENTIFIER, lineNum, posNum, level, level > 0);
	    		    		token.setValue(key);
	    	    			tokens.add(token);
//	    	    			token = new Token(TokenType.COLON, lineNum, posNum, level, false);
//	    	    			token.setValue(":");
//	    	    			tokens.add(token);
	    	    		}else{
	    		    		error = new Error(lineNum, posNum, "Wrong key format!");
	    		    		errors.add(error);
	    	    		}
	    				value = value.trim();
	    				if(value.startsWith("\"")){
	    					if(value.endsWith("\"")){
	    						token = new Token(TokenType.STRING, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
	    					}else{
	    						posNum+=value.length()+1;
	    						error = new Error(lineNum, posNum, "expect <\">");
	    						errors.add(error);
	    					}
	    				}else if(value.equals("true")||value.equals("false")){
	    					token = new Token(TokenType.BOOL, lineNum, posNum, level, level > 0);
    						token.setValue(value);
	    	    			tokens.add(token);
	    				}else if (value.matches("^(0|-?[1-9]\\d*)$")) {
	    					token = new Token(TokenType.INT, lineNum, posNum, level, level > 0);
    						token.setValue(value);
	    	    			tokens.add(token);
						}else if(value.matches("^((0\\.0)|-?[1-9]\\d*\\.\\d*)$")){
	    					token = new Token(TokenType.DOUBLE, lineNum, posNum, level, level > 0);
    						token.setValue(value);
	    	    			tokens.add(token);
						}else if(value.matches("^(\\d\\.\\d*(e|E)(\\+|-)?\\d*)$")){
							token = new Token(TokenType.SCIENTIFIC, lineNum, posNum, level, level > 0);
    						token.setValue(value);
	    	    			tokens.add(token);
						}else if(value.endsWith("\"")){
							posNum++;
							error = new Error(lineNum, posNum, "expect <\">");
    						errors.add(error);
						}else{
							error = new Error(lineNum, posNum, "Wrong value type!");
							errors.add(error);
						}
	    			}
	    		}else if(countSpace(value) == 0){
	    			error = new Error(lineNum, posNum, "expect one blank sapce");
	    			errors.add(error);
	    		}else if(countSpace(value) > 1){
	    			error = new Error(lineNum, posNum, "only need one blank sapce");
	    			errors.add(error);
	    		}else{
					token = new Token(TokenType.ARRAY, lineNum, posNum, level, level>1);
					token.setValue(key);
					tokens.add(token);
				}
	    	}else if(line.startsWith("-")){
//    			token = new Token(TokenType.BAR, lineNum, posNum, level, true);
//				token.setValue("-");
//    			tokens.add(token);
	    		if(level > (tokens.get(tokens.size()-1).getLevel() + 1)){
		    		//System.out.println(tokens.get(tokens.size()-1).getValue()+"  "+tokens.get(tokens.size()-1).getType()+"  "+tokens.get(tokens.size()-1).getLineNum());
	    			error = new Error(lineNum, posNum, "expect exactly " + (tokens.get(tokens.size()-1).getLevel() + 1) * 2 + " blank space!");
	    			errors.add(error); 
	    		}
    			posNum++;
    			line = line.substring(1);
	    		if(line.trim().equals("")){
	    			token = new Token(TokenType.ARRAY, lineNum, posNum, level, true);
	    			token.setValue("default");
	    			tokens.add(token);
	    		}else{
	    			if(countSpace(line)==1){
	    				if(line.contains("#")){
			    			String value;
			    			String[] value_comment=line.split("#", 2);
				    		token = new Token(TokenType.COMMENT, lineNum, posNum, level, false);
			    			token.setValue(value_comment[1]);
				    		tokens.add(token);
				    		value = value_comment[0];
				    		value = value.trim();
		    				if(value.startsWith("\"")){
		    					if(value.endsWith("\"")){
		    						token = new Token(TokenType.STRING, lineNum, posNum, level, level > 0);
					    			token.setValue(value);
			    	    			tokens.add(token);
		    					}else{
		    						posNum+=value.length()+1;
		    						error = new Error(lineNum, posNum, "expect <\">");
		    						errors.add(error);
		    					}
		    				}else if(value.equals("true")||value.equals("false")){
		    					token = new Token(TokenType.BOOL, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
		    				}else if (value.matches("^(0|-?[1-9]\\d*)$")) {
		    					token = new Token(TokenType.INT, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
							}else if(value.matches("^((0\\.0)|-?[1-9]\\d*\\.\\d*)$")){
		    					token = new Token(TokenType.DOUBLE, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
							}else if(value.matches("^(\\d\\.\\d*(e|E)(\\+|-)?\\d*)$")){
								token = new Token(TokenType.SCIENTIFIC, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
							}else if(value.endsWith("\"")){
								posNum++;
								error = new Error(lineNum, posNum, "expect <\">");
	    						errors.add(error);
							}else{
								error = new Error(lineNum, posNum, "Wrong value type!");
								errors.add(error);
							}
			    		}else{
			    			String value = line.trim();
		    				if(value.startsWith("\"")){
		    					if(value.endsWith("\"")){
		    						token = new Token(TokenType.STRING, lineNum, posNum, level, level > 0);
		    						token.setValue(value);
			    	    			tokens.add(token);
		    					}else{
		    						posNum+=value.length()+1;
		    						error = new Error(lineNum, posNum, "expect <\">");
		    						errors.add(error);
		    					}
		    				}else if(value.equals("true")||value.equals("false")){
		    					token = new Token(TokenType.BOOL, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
		    				}else if (value.matches("^(0|-?[1-9]\\d*)$")) {
		    					token = new Token(TokenType.INT, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
							}else if(value.matches("^((0\\.0)|-?[1-9]\\d*\\.\\d*)$")){
		    					token = new Token(TokenType.DOUBLE, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
							}else if(value.matches("^(\\d\\.\\d*(e|E)(\\+|-)?\\d*)$")){
								token = new Token(TokenType.SCIENTIFIC, lineNum, posNum, level, level > 0);
	    						token.setValue(value);
		    	    			tokens.add(token);
							}else if(value.endsWith("\"")){
								posNum++;
								error = new Error(lineNum, posNum, "expect <\">");
	    						errors.add(error);
							}else{
								error = new Error(lineNum, posNum, "Wrong value type!");
								errors.add(error);
							}
			    		}
	    			}else if(countSpace(line)==0){
	    				error = new Error(lineNum, posNum, "expect one blank space!");
	    				errors.add(error);
	    			}else if(countSpace(line)>1){
	    				error = new Error(lineNum, posNum, "only need one blank space!");
	    				errors.add(error);
	    			}
	    			
	    		}
	    	}
//	    	token = new Token(TokenType.NEWLINE, lineNum, 0, 0, false);
//	    	tokens.add(token);
	    	spaceNum = 0;
	    	posNum = 0;
	    }
	    token = new Token(TokenType.EOF, lineNum, 0, 0, false);
	    token.setValue("EOF");
	    tokens.add(token);
	    if(errors.isEmpty()){
	    	ifValid = true;
	    }else{
	    	ifValid = false;
	    	for (Error e : errors) {
				System.out.println("line "+e.getLineNum()+", position "+e.getPosNum()+": "+e.getErrorMessage());
			}
	    }
    }
    
    public String printTokens() {
    	String string="";
		for (Token t : tokens) {
			string+=t.toString();
		}
		return string;
	}
 
}