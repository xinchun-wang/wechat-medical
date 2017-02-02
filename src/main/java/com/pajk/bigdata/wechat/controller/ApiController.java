package com.pajk.bigdata.wechat.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pajk.bigdata.wechat.bos.ReceiveXmlEntity;
import com.pajk.bigdata.wechat.service.FormatService;
import com.pajk.bigdata.wechat.service.ReceiveService;

@Controller
@RequestMapping("/api")  
public class ApiController {
    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);   
    @Autowired
    private ReceiveService receiveService;
    @Autowired
    private FormatService formatService;
    
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String validate(
			@RequestParam("signature") String signature,  
			@RequestParam ("timestamp") String timestamp,
			@RequestParam ("nonce") String nonce,
			@RequestParam ("echostr") String echostr){
		
		LOG.info("signature = " + signature + ", timestamp = " + timestamp + ", nonce = " + nonce + ", echostr = " + echostr);
		return echostr;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void message(HttpServletRequest request, HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("utf-8");

        response.setContentType("text/xml;charset=utf-8");
        /** 读取接收到的xml消息 */  
        StringBuffer sb = new StringBuffer();  
        InputStream is = null;
		try {
			is = request.getInputStream();
		} catch (IOException e) {
			LOG.error("", e);
		}  
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");  
        BufferedReader br = new BufferedReader(isr);
        String s = "";  
        while ((s = br.readLine()) != null) {  
            sb.append(s);  
        }  
        String xml = sb.toString(); //次即为接收到微信端发送过来的xml数据  
        /** 解析xml数据 */  
        ReceiveXmlEntity xmlEntity = receiveService.getMsgEntity(xml);  
        LOG.info("receive frome : " + xmlEntity.getFromUserName() + ", content: " + xmlEntity.getContent());
        String result = formatService.formatXmlAnswer(
        		xmlEntity.getFromUserName(), 
        		xmlEntity.getToUserName(), 
        		xmlEntity.getContent());  

        try {  
            OutputStream os = response.getOutputStream();  
            os.write(result.getBytes("UTF-8"));  
            os.flush();  
            os.close();  
        } catch (Exception e) {  
			LOG.error("", e);
        }  
	}
}
