package top.weweb.hawk.mailx;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author 弘树丶
 *
 */
public class MailUtil {
	/**
	 * 
	 * @param mailString 待判断的mail邮件地址
	 * @return boolean 是否匹配成功
	 */
	public static boolean isMailAddr(String mailString) {
		String pattenString = 
				"^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
		Pattern pattern=Pattern.compile(pattenString);
		Matcher matcher=pattern.matcher(mailString);
		return matcher.matches();
	}
	/**
	 * 
	 * @param num 验证码随机数的位数
	 * @return String 随机数
	 */
	public static String  getVCode(int num) {
		Random random=new Random();
		StringBuffer stringBuffer=new StringBuffer("");
		for (int i = 0; i < num; i++) {
			stringBuffer.append(random.nextInt(10));
		}
		return stringBuffer.toString();
	}
}
