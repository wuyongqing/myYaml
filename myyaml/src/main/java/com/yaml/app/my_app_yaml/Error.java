package com.yaml.app.my_app_yaml;

public class Error{
		private int lineNum;
		private int posNum;
		private String errorMessage;
		public Error(int lineNum, int posNum, String errorMessage) {
			// TODO Auto-generated constructor stub
			this.lineNum = lineNum;
			this.posNum = posNum;
			this.errorMessage = errorMessage;
		}
		public int getLineNum() {
			return lineNum;
		}
		public int getPosNum() {
			return posNum;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
		public String toString() {
			return "line "+this.getLineNum()+", position "+this.getPosNum()+": "+this.getErrorMessage()+"\n";
		}
	}