package top.weweb.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import top.weweb.hawk.mailx.MailAddressException;
import top.weweb.hawk.mailx.MimeMail;

public class Test1 {
	/**
	 * 	下面是一个简单的例子，测试发送。<strong>这是最简单的方式</strong> - 不使用spring容器如何发送邮件 -
	 * 	弊端：1.发送速度慢，需要几秒钟的时间加载资源 2.这种方式默认只拥有一个发送器，容错率不高
	 * 
	 * @throws IOException
	 * @throws MailAddressException 自定义异常，邮件地址不正确
	 */
	@Test
	public void testSendSimple() throws MailAddressException {
		MimeMail mimeMail = MimeMail.Builder.initMailSender("smtp.163.com", "smtp", 465, "hongshuboy@163.com",
				"xxxxxxxxxxx", false);
		List<String> to = new ArrayList<String>();// 收件人集合
		to.add("hongshuboy@qq.com");
		//密集发送时，163会报554 DT:SPM异常
		mimeMail.sendMail(to, "你有新的消息", "请到网站内查看"+new Date());
	}

	/**
	 * 	下面是一个简单的例子，测试发送。<strong>需要配置文件支持</strong> - 不使用spring容器如何发送邮件 -
	 * 	弊端：1.发送速度慢，需要几秒钟的时间加载资源 2.这种方式默认只拥有一个发送器，容错率不高
	 * 
	 * @throws IOException
	 * @throws MailAddressException 自定义异常，邮件地址不正确
	 */
	@Test
	public void testSend() throws IOException, MailAddressException {
		MimeMail mimeMail = MimeMail.Builder.initMailSenderWithProperties();
		List<String> to = new ArrayList<String>();// 收件人集合
		to.add("hongshuboy@qq.com");
		mimeMail.sendMail(to, "你有新消息", "请到网站内查看");
	}

	/**
	 * 	下面是使用Spring容器发送的测试 -注意这只是测试，项目不要这样用，项目中可以使用spring子容器的方式来加载mail
	 * -如果你选择用spring子容器的方式，那么请详细阅读github中相关的文档，可以避免一些问题
	 * 
	 * @throws MailAddressException
	 */
	@Test
	public void testSendWithSpring() throws MailAddressException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:spring-mailx.xml");
		MimeMail mimeMail = applicationContext.getBean(MimeMail.class);
		List<String> to = new ArrayList<String>();// 收件人集合
		to.add("hongshuboy@qq.com");
		mimeMail.sendMail(to, "你有新消息", "请到网站内查看");
		applicationContext.close();
	}

}
