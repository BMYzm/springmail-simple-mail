package top.weweb.hawk.mailx;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 弘树丶
 *
 */
@Component
public class MimeMail {
	@Qualifier(value="163MailSender")
	@Autowired(required=false)
	private JavaMailSenderImpl mailSender163;

	@Qualifier(value="qqMailSender")
	@Autowired(required=false)
	private JavaMailSenderImpl qqMailSender;
	
	private JavaMailSenderImpl mailSender=null;
	
	/**
	 *  common mail 本方法抽取出发送邮件公共部分，只需传入必要的三个信息即可发送邮件</br>
	 *  <strong>不要直接调用此方法</strong>
	 *  请调用重载方法 sendMail(List<String> to,String subject,String Text)
	 * 
	 * @param to 收件人地址，List接口形式，支持群发
	 * @param subject 邮件主题
	 * @param Text 邮件内容（邮件的尾巴部分会自动补上）
	 * @param retry 是否是重新尝试
	 * @return boolean 是否发送成功
	 * @throws MessagingException 邮件发送异常
	 * @throws MailAddressException 自定义异常
	 */
	private boolean sendMail(List<String> to,String subject,String Text,boolean retry) throws MailAddressException {
		//合法性检查
		for (String stringto : to) {
			if (!MailUtil.isMailAddr(stringto)) {
				throw new MailAddressException();
			}
		}
		//retry -尝试使用QQ Mail重发
		if (retry || (mailSender163 == null)) {
			mailSender=qqMailSender;
		}else {
			mailSender=mailSender163;
			//不是retry,给自己抄送一份 解决554 DT:SPM 异常
			//这一方法最近可能已经失效，不需要的可以自己删除下面这一行
			to.add(mailSender.getUsername());
		}
		
		MimeMessage mimeMessage=null;
		try {
			mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage, true);
			// 注意下面的是Text+something,Text是传入的等待发送的内容，后面部分是固定格式，比如说“本邮件自动发送，请勿回复”
			//		可以根据需要自行更改固定内容
			String formatText = Text+"<p>本邮件自动发送，请勿回复</p><a href='http://www.weweb.top'><img src='cid:img' style='height:100px;'/></a>";
			//这个是附带发送的图片。不需要可以去掉，但是需要把固定内容的cid:img一起去掉，这是标志位
			ClassPathResource resource=new ClassPathResource("hawk_logo2.png");
			String[] toArray=new String[to.size()];
			to.toArray(toArray);
			messageHelper.setFrom(mailSender.getUsername());
			messageHelper.setBcc(toArray);
			messageHelper.setSubject(subject);
			messageHelper.setText(formatText,true);
			//这个是附带发送的图片，不用可以自行去掉
			messageHelper.addInline("img", resource);
			//下面一行可以添加附件
//			messageHelper.addAttachment(attachmentFilename, file);
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (!retry) {
				sendMail(to, subject, Text, true);
			}else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * common mail 重载方法，其他方法调用此方法来实现send mail<br/>
	 * <strong>发邮件的所有方法必须调用此方法来发送！！！</strong></br>
	 * 	如果你配置了两个mailSender，将会自动切换发送，若只有一个，请务必修改将不用的发送器从spring-mailx.xml中删除
	 * 
	 * @param to 收件人地址，List接口形式，支持群发
	 * @param subject 邮件主题
	 * @param Text 邮件内容（邮件的尾巴部分会自动补上），可以去上面的重载方法自行定制尾巴
	 * @return boolean 是否发送成功
	 * @throws MailAddressException
	 */
	public boolean sendMail(List<String> to,String subject,String Text) throws MailAddressException {
		return sendMail(to, subject, Text, false);
	}
	
	
	/**
	 * send verification code 发送验证码，使用到common mail
	 * 
	 * @param to 收件人地址，List接口形式，支持群发
	 * @param num 验证码随机数的位数
	 * @return vCode 生成的验证码
	 * @throws MessagingException 邮件发送异常
	 * @throws MailAddressException 自定义异常
	 */
	public String sendVCode(List<String> to,int num) throws MessagingException, MailAddressException {
		String vCode=MailUtil.getVCode(num);
		String formatText = MessageFormat.format("<h2>你的验证码是：{0}</h2>",vCode );
		this.sendMail(to,"Hawk:验证码在此，请查收",formatText);
		return vCode;
	}
	
	
	
	public JavaMailSenderImpl getMailSender163() {
		return mailSender163;
	}

	public void setMailSender163(JavaMailSenderImpl mailSender163) {
		this.mailSender163 = mailSender163;
	}

	public JavaMailSenderImpl getQqMailSender() {
		return qqMailSender;
	}

	public void setQqMailSender(JavaMailSenderImpl qqMailSender) {
		this.qqMailSender = qqMailSender;
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}



	public static class Builder {
		public Builder() {
			// TODO Auto-generated constructor stub
		}
		
		public static MimeMail initMailSender() throws IOException {
			Properties properties = new Properties();
			ClassPathResource resource = new ClassPathResource("mail.properties");
			properties.load(resource.getInputStream());
			JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
			javaMailSenderImpl.setHost(properties.getProperty("mail.host"));
			javaMailSenderImpl.setProtocol(properties.getProperty("mail.protocol"));
			javaMailSenderImpl.setPort(Integer.parseInt(properties.get("mail.port").toString()));
			javaMailSenderImpl.setUsername(properties.getProperty("mail.username"));
			javaMailSenderImpl.setPassword(properties.getProperty("mail.password"));
			javaMailSenderImpl.setDefaultEncoding("UTF-8");
			javaMailSenderImpl.setJavaMailProperties(properties);
			MimeMail mimeMail = new MimeMail();
			mimeMail.setQqMailSender(javaMailSenderImpl);
			return mimeMail;
		}
	}
}

