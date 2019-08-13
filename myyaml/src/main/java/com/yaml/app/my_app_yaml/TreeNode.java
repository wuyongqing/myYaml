package com.yaml.app.my_app_yaml;
import java.util.ArrayList;
public class TreeNode {
	 	ArrayList child ;
	    private String value;
	    private boolean isArrayElement;
	    public ParserType getType() {
	        return type;
	    }

	    public String getValue() {
	        return value;
	    }
	    public boolean getIsArrayElement() {
			return isArrayElement;
		}
	    private ParserType type;

	    public TreeNode(ParserType type, String value, boolean isArrayElement) {
	        this.type = type;
	        this.value = value;
	        this.isArrayElement = isArrayElement;
	        child = new ArrayList<TreeNode>();
	    }

	    @Override
	    public String toString() {
	        return value;
	    }
}
