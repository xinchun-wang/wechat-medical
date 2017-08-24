package com.bigdata.wechat.controller;

import com.bigdata.wechat.service.MedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/query")  
public class WordController {
	
    @Autowired
	private MedicalService medicalService;
    
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String validate(
			@RequestParam("word") String word){
		return medicalService.queryWords(word);
	}
}
