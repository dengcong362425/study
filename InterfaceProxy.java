package cn.sunline.crcs.service.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;

import org.neo4j.cypher.internal.compiler.v2_2.functions.Rand;

public class InterfaceProxy<T> implements InvocationHandler {

	private Class<T> interfaceType;

	public InterfaceProxy(Class<T> intefaceType) {
		this.interfaceType = interfaceType;
	}

	public Class<T> getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(Class<T> interfaceType) {
		this.interfaceType = interfaceType;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		System.out.println(args[0].toString());
		Field field = method.getClass().getDeclaredField("clazz");
		field.setAccessible(true);
		Class<?> clazz = (Class<?>) field.get(method);
		String serviceName = clazz.getSimpleName();
		System.out.println(serviceName);
		System.out.println(method.getName());
		// 可以模拟一个http请求

		try {
			// 创建URL对象
			URL url = new URL("http://127.0.0.1:8022/" + serviceName + "/" + method.getName());
			// 返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			// 在这里设置一些属性，详细见UrlConnection文档，HttpURLConnection是UrlConnection的子类
			// 设置连接超时为5秒
			httpURLConnection.setConnectTimeout(5000);
			// 设定请求方式(默认为get)
			httpURLConnection.setRequestMethod("POST");
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			httpURLConnection.setDoOutput(true);
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpURLConnection.setDoInput(true);
			// Post 请求不能使用缓存
			httpURLConnection.setUseCaches(false);

			// 这边开始设置请求头
			// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
			// 方法setRequestProperty(String key, String value)设置一般请求属性。
			// 连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
			httpURLConnection.connect();

			// 这边设置请内容
			// getOutputStream()里默认就有connect（）了，可以不用写上面的连接
			// 接下来我们设置post的请求参数，可以是JSON数据，也可以是普通的数据类型
			OutputStream outputStream = httpURLConnection.getOutputStream();
			/**
			 * JSON数据的请求 outputStream.write(stringJson.getBytes(), 0,
			 * stringJson.getBytes().length); outputStream.close();
			 **/
			Random random = new Random();
			String requstbody = "RANDOM" + random.nextInt(10000);
			outputStream.write(requstbody.getBytes(), 0, requstbody.getBytes().length);
			outputStream.close();
			/**
			 * 字符串数据的请求 DataOutputStream dataOutputStream = new
			 * DataOutputStream(outputStream); String content = "username=" +
			 * username + "&password=" + password;
			 * dataOutputStream.writeBytes(content); dataOutputStream.flush();
			 * dataOutputStream.close();
			 **/
			// 读取返回的数据
			// 返回打开连接读取的输入流，输入流转化为StringBuffer类型，这一套流程要记住，常用
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpURLConnection.getInputStream()));
			String line = null;
			StringBuffer stringBuffer = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				// 转化为UTF-8的编码格式
				line = new String(line.getBytes("UTF-8"));
				stringBuffer.append(line);
			}
			bufferedReader.close();
			httpURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "批量动态代理";
	}

}
