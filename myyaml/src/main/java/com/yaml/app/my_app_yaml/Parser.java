package com.yaml.app.my_app_yaml;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
public class Parser {
   private int index;
   private ArrayList<Token> tokens;
   private Token current;
   private TreeNode root;
   private boolean isArrayElement;

   private boolean ifValid = false;
   public boolean isIfValid() {
	   return ifValid;
   }

   public void setIfValid(boolean ifValid) {
	   this.ifValid = ifValid;
   }

   public Parser(String filepath){
	       Lexer le = new Lexer(filepath);
	        try {
				le.scanner();
				setIfValid(le.getIfValid());
				tokens = le.getTokens();
		        index = 0;
		        if (tokens.size() != 0){
		            current  = tokens.get(index);
					while (current.getType() == TokenType.COMMENT)
					    getnext();
		            root = Root();
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
	public ArrayList<Token> getTokens() {
	        return tokens;
	    }
	
	private void getnext()  {
	        if (index < tokens.size()){
	        	current = tokens.get(index);
	        	index++;
	        }
	    }
	private String json = "{";
	public String getJson() {
		return json+"}";
	}
	int num=0;
	
	public static void outFile(String s) {
		
        File file = new File("D:/Eclipse/my-app-yaml/sample.json");

        try (FileOutputStream fop = new FileOutputStream(file)) {

            if (!file.exists()) {
                file.createNewFile();
            }

            byte[] contentInBytes = s.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("sample.json");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
	public void PrintJson(TreeNode t) {
		if(t.getType()==ParserType.Root){
			PrintJson((TreeNode)t.child.get(0));
		}else if (t.getType()==ParserType.Yaml){
			for(int i = 0; i < t.child.size()-1; i++){
				PrintJson((TreeNode)t.child.get(i));
			}
		}else if(t.getType()==ParserType.EOF){
			return;
		}
		else{
			if(t.getType()==ParserType.Object && !t.getValue().equals("EOF")){
				if(t.getIsArrayElement()){
					json+="{";
				}
				json+="\"";
				PrintJson((TreeNode)t.child.get(0));
				json+="\":";
				PrintJson((TreeNode)t.child.get(1));
				if(t.getIsArrayElement()){
					json+="}";
				}
				json+=",";
			}else if(t.getType()==ParserType.Array){
				if(t.getValue().equals("default")){
					json+="[";
				}else{
					json+="\""+t.getValue()+"\":[";
				}
				for(int i = 0; i < t.child.size(); i++){
					PrintJson((TreeNode)t.child.get(i));
					if(i < t.child.size()-1){
						json+=",";
					}
				}
				if(json.charAt(json.length()-1)==','){
					json=json.substring(0, json.length()-1);
				}
				json+="]";
			}else {
				json +=t.getValue();
			}
		}
	}
	
	public void name() {
		
	}
	
	public TreeNode getRoot() {
	        return root;
	    }
		
	private TreeNode Root() {
	        TreeNode t = new TreeNode(ParserType.Root, "Root" , false);
	        t.child.add(Yaml());
	        return  t;
	    }

	private TreeNode Yaml() {
	        TreeNode t = new TreeNode(ParserType.Yaml, "Yaml", false);
	        while (index < tokens.size()){
	        	while (current.getType() == TokenType.COMMENT)
	        		getnext();
	        	TreeNode tmp;
	        	if(current.getType()==TokenType.ARRAY)
	        		tmp = Array();
	        	else 
	        		tmp = Object();
	        	if (tmp!= null) 
	        		t.child.add(tmp);
	        	else 
	        		getnext();
	        }
	        return t;
	    }

	private TreeNode Object() {
			while (current.getType() == TokenType.COMMENT)
				getnext();
	        TreeNode t = new TreeNode(ParserType.Object, "Object", current.getIsFromArr());
			if (current.getType() == TokenType.IDENTIFIER)
			{
				t.child.add(new TreeNode(ParserType.IDENTIFIER, current.getValue(), current.getIsFromArr()));
			    getnext();
			}else if(current.getType() == TokenType.EOF){
				t.child.add(new TreeNode(ParserType.EOF, current.getValue(), current.getIsFromArr()));
				return t;
			}
			t.child.add(Value());
			getnext();
			return t;
		}
			
	private TreeNode Array() {
	        TreeNode t = new TreeNode(ParserType.Array, current.getValue(), current.getIsFromArr());
	        getnext();
			while (current.getType() == TokenType.COMMENT)
				getnext();
			int lastlevel = current.getLevel();
			while (current.getType() == TokenType.COMMENT)
				getnext();
			while(lastlevel == current.getLevel())
			{
			    while (current.getType() == TokenType.COMMENT)
			       getnext();
				if (current.getType() == TokenType.IDENTIFIER)
				{
				   t.child.add(Object());
			    }
                else if (current.getType() == TokenType.ARRAY)
			    {
			       t.child.add(Array());
			    }
                else if(current.getType() == TokenType.KEY_VALUE_inArray)
			    {
                	getnext();
			    	t.child.add(Object());
			    }
                else
				{
			       t.child.add(Value());
			    }
			}
			return t;
		}
		
	private TreeNode Value(){
	        TreeNode t = new TreeNode(ParserType.Value, "Value", current.getIsFromArr());
			while (current.getType() == TokenType.COMMENT)
				getnext();
			if (current.getType() == TokenType.STRING){
	            t=new TreeNode(ParserType.String, current.getValue(), current.getIsFromArr());
	            getnext();
	        }else if(current.getType() == TokenType.INT ){
	            t=new TreeNode(ParserType.Integer, current.getValue(), current.getIsFromArr());
	            getnext();
	        }else if(current.getType() == TokenType.DOUBLE ){
	            t=new TreeNode(ParserType.Double, current.getValue(), current.getIsFromArr());
	            getnext();
	        }else if(current.getType() == TokenType.SCIENTIFIC ){
	            t=new TreeNode(ParserType.Scientfic, current.getValue(), current.getIsFromArr());
	            getnext();
	        }else if(current.getType() == TokenType.BOOL ){
	            t=new TreeNode(ParserType.BOOL, current.getValue(), current.getIsFromArr());
	            getnext();
	        }else if(current.getType() == TokenType.NULL ){
	            t=new TreeNode(ParserType.NULL, current.getValue(), current.getIsFromArr());
	            getnext();
			}else if(current.getType() == TokenType.ARRAY){
			    t=Array();
				getnext();
			}
			return t;
		}
}