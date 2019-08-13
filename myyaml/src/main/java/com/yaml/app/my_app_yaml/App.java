package com.yaml.app.my_app_yaml;

import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String filepath = "";
        Parser parser;
        System.out.println(args[0]+" "+args[1]+" "+args[2]);
        if (args.length == 2 && args[0]=="yamlite"){
                filepath = args[1];
        }
        else if (args.length == 3 && args[0].equals("yamlite") && args[1].equals("-parse")){
            filepath = args[2].toString();
        }else if (args.length == 3 && args[0].equals("yamlite") && args[1].equals("-json")) {
        	filepath = args[2].toString();
		} 
        else{
            System.out.println("Usage: yamlite [option [value]] file");
            System.exit(0);
        }
        try {
            File file=new File(filepath);
            if(!file.isFile() || !file.exists()){
                System.out.println("No file exists!");
            }else{
            	if(args[1].equals("-parse")){
            		parser = new Parser(filepath);
            		if(parser.isIfValid()){
            			System.out.println("valid");
            		}
            	}else if(args[1].equals("-json")){
            		parser = new Parser(filepath);
            		if(parser.isIfValid()){
                    	parser.PrintJson(parser.getRoot());
                        parser.outFile(parser.getJson());
            		}
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
