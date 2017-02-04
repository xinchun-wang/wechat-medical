package com.pajk.bigdata.wechat.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pajk.bigdata.wechat.dao.MedicalDAO;
import com.pajk.bigdata.wechat.dos.MedicalDO;
import com.pajk.bigdata.wechat.util.Constants;

@Service
public class MedicalService {

	private static final String ENTER = "\n";
	
	@Autowired
	private MedicalDAO medicalDAO;
	
	@Autowired
	private YoudaoService youdaoService;
	
	public String queryWords(String word){
		int counter = medicalDAO.countWords(word);
		if(counter == 0){
			return "没有查询结果。";
		}
		List<MedicalDO> wordsList = medicalDAO.queryWords(word);
		
		StringBuilder builder = new StringBuilder();
		builder.append("共有").append(counter).append("条结果，显示前5条记录。").append(ENTER);
		builder.append("--------------").append(ENTER);
		int i = 1;
		for(MedicalDO medicalDO : wordsList){
			builder.append(i++).append(". ").append(medicalDO.getWord()).append(ENTER);
			Map<String, String> explainMap = youdaoService.query(medicalDO.getWord());
			if(!explainMap.isEmpty()){
				builder.append("英 [").append(explainMap.get(Constants.uk_phonetic)).append("]").append(ENTER);
				builder.append("美 [").append(explainMap.get(Constants.us_phonetic)).append("]").append(ENTER);
				builder.append("含义：\t").append(explainMap.get(Constants.explains)).append(ENTER);
			}
			builder.append("Rank:\t").append(medicalDO.getRank()).append(ENTER);
			builder.append("词频:\t").append(medicalDO.getFrequency()).append(ENTER);
			builder.append("............").append(ENTER);
		}
		return builder.toString();
	}
}
