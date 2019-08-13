package com.yaml.app.my_app_yaml;

public class Token{
	private TokenType type;
	private String value;
    private int level;
    private int LineNum = 1;
    private int PosNum;
    private boolean isFromArr;
    
    public Token(TokenType type, int LineNum, int PosNum, int level, boolean isFromArr) {
        this.type = type;
        this.LineNum = LineNum;
        this.PosNum = PosNum;
        this.level = level;
        this.isFromArr = isFromArr;
        this.value="";
    } 

    public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLineNum() {
		return LineNum;
	}

	public void setLineNum(int lineNum) {
		LineNum = lineNum;
	}

	public int getPosNum() {
		return PosNum;
	}

	public void setPosNum(int posNum) {
		PosNum = posNum;
	}

	public boolean getIsFromArr() {
		return isFromArr;
	}

	public void setFromArr(boolean isFromArr) {
		this.isFromArr = isFromArr;
	}
	
	public String toString() {
		return this.getType() + " " + this.getValue() + " " + this.getLevel() + " " + this.getIsFromArr() + "\n";
	}

}