package com.pajk.bigdata.wechat.dos;

public class MedicalDO {
	private int id;
	private int rank;
	private int frequency;
	private String word;
	private int type;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Word:" + word + ",\tRank:" + rank + ",\tFrequency:" + frequency +",\tType=" + type;
	}
	
}
