package com.canyugan.util;

import java.util.Map;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
@Service
public class MailSender implements InitializingBean 
{
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;
    @Autowired
    private VelocityEngine velocityEngine;
    
    public boolean sendWithHTMLTemplate(String to, String subject,
            String template, Map<String, Object> model) 
    {
		try {
			String nick = MimeUtility.encodeText("登录提醒");
			InternetAddress from = new InternetAddress(nick + "deeplyvoid@163.com");
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setFrom(from);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(result, true);
			mailSender.send(mimeMessage);
			return true;
		} catch (Exception e) {
			logger.error("发送邮件失败" + e.getMessage());
			return false;
		}
    }
    
	@Override
	public void afterPropertiesSet() throws Exception 
	{
	    mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("deeplyvoid@163.com");
        mailSender.setPassword("fd19940925");
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", true);
        mailSender.setJavaMailProperties(javaMailProperties);
	}
}
