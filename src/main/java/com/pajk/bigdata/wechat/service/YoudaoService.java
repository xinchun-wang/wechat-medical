package com.pajk.bigdata.wechat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pajk.bigdata.wechat.util.Constants;

@Service
public class YoudaoService {
    private static final Logger LOG = LoggerFactory.getLogger(YoudaoService.class);   

	@Autowired
	private RESTClientService<Map<String, ?>> restClientService;
	
	private String baseUrl = "http://fanyi.youdao.com/openapi.do?"
			+ "keyfrom=MedicalEnglishHere&key=226698417&type=data&doctype=json&version=1.1&q=";
	
	
	public Map<String, String> query(String word){
		Map<String, String> youdaoResult = new HashMap<String, String>();
		try {
			Map<String, ?> result = restClientService.get(baseUrl + word);
			if(result.containsKey("basic")){
				Map<String, ?> basicMap = (Map<String, ?>)result.get("basic");
				youdaoResult.put(Constants.uk_phonetic, (String)basicMap.get(Constants.uk_phonetic));
				youdaoResult.put(Constants.us_phonetic, (String)basicMap.get(Constants.us_phonetic));
				List<String> explainList = (List<String>)basicMap.get(Constants.explains);
				StringBuilder explains = new StringBuilder();
				int i = 0;
				for(String explain : explainList){
					if(i != 0){
						explains.append("; ");
					}
					explains.append(explain);
					i++;
				}
				youdaoResult.put(Constants.explains, explains.toString());
			}
			
			
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		return youdaoResult;
	}
}
