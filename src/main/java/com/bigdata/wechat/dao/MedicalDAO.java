package com.bigdata.wechat.dao;

import java.util.List;

import com.bigdata.wechat.dos.MedicalDO;

public interface MedicalDAO {
	public List<MedicalDO> queryWords(String word);
	
	public int countWords(String word);
}
