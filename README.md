# springmail-simple-mail
> A simple spring mail sender,use spring mail to send email,simple mail API
>
> 基于Spring Mail的简单的邮件发送API，有详细的配置介绍

`maven` `spring` `spring mail` `simple mail`

# 快速开始（5分钟上手使用）

> - 在这里，你可以快速完成邮件的发送，只需要一点点必须的设置
>
> - 熟悉项目结构：这是一个maven工程，如果你不会使用maven，对快速开始影响不大

### 1.设置发件箱的域名和密码

**src\main\resources\mail.properties** 

​	1. 参照现有的配置，修改这个文件，填入你的发件箱，建议你163和QQ邮箱都配置上，这样，如果发送失败，程序会自动切换发送器重发，保证成功率，而且切换过程用户没有察觉。

​	2. 如果你只配置了一个发送器，无需修改代码，系统会只用这一个发送器进行发送（但是需要删除一个spring配置，注意下文`A`部分）。

**开启你的邮箱的POP3/SMTP/IMAP**

​	要使用java mail，请先在邮箱设置中开启POP3/SMTP/IMAP，***配置的密码不是你的登录密码***，以163邮箱为例，同样在设置中选择客户端授权密码，获取一份授权密码，放在配置文件*（src\main\resources\mail.properties）*的`mail.password`位置。

***A:如果你只在其中配置了一个邮箱***

​	只想用一个发送器？那请把 `src\main\resources\spring-mailx.xml`文件中不用的发送器删除，注意删除整个`<bean>`标签

> - **强烈建议你两个都配置**，这样系统在其中一个发送失败时会切换发送器重新发送，[点击查看163发送失败的代号对应的原因](http://help.163.com/09/1224/17/5RAJ4LMH00753VB8.html)

----

## 简单的配置之后终于可以开始测试了

> 可以使用两种方式快速上手使用

#### 1.不使用Spring容器

> 这是最简单的方式，直接可以创建mimeMail
>
> 这样只需要在需要的时候用初始化的MimeMail send方法发送邮件即可
>
> **注意 :** 使用这种方式的话，需要配置`mail.properties`文件中的等号（====）下面的内容，*最下面三行内容不要更改*。

```java
	/**
	 * 下面是一个简单的例子，测试发送。<strong>这是最简单的方式</strong>
	 *  - 不使用spring容器如何发送邮件
	 *  	- 弊端：1.发送速度慢，需要几秒钟的时间加载资源
	 *  		   2.这种方式默认只拥有一个发送器，容错率不高
	 * @throws IOException
	 * @throws MailAddressException 自定义异常，邮件地址不正确
	 */
	@Test
	public void testSend() throws IOException, MailAddressException {
		MimeMail mimeMail = MimeMail.Builder.initMailSender();
		List<String> to = new ArrayList<String>();//收件人集合
		to.add("hongshuboy@qq.com");
		mimeMail.sendMail(to, "你有新消息", "请到网站内查看");
	}
```

我们来看一下调用sendMail方法的具体参数是什么意思

```java
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
	public boolean sendMail(List<String> to,String subject,String Text)
```

> ​	如果使用这种方式，建议使用一个静态类来管理，不然每次发送都加载资源速度会有点慢，如果不在乎速度的话当然也可以忽略。

#### 2.使用Spring容器（推荐）

> 这种方式也简单易用，速度上也比上种有显著提升

​	将本项目打包或者下载`Jar`包，将`Jar`包导入到需要使用邮件的工程中，在工程的`spring.xml`中`import`

中导入`simple mail`内的`spring-mailx.xml`配置文件，这样当前项目就可以使用`MimeMail`发送邮件了，在需要用的地方使用`@Autowired`注入就可以使用了。

**在上层项目的spring.xml中导入的例子**

> 请直接复制<import>结点，除非必要不要更改

```xml
	<!-- 导入其他组件的spring -->
	<import resource="classpath*:spring-mailx.xml"/>

  	<bean id="xxx" class="xxx.xxxx">
		...
	</bean>
```

> **注意 :**如果使用上层容器，两个容器内不要同时有`<context:property-placeholder location="xx"/>`

，讲这个结点放到顶层`spring.xml`中

### 问题反馈

```xml
hongshuboy@gmail.com
hongshuboy@qq.com
```

