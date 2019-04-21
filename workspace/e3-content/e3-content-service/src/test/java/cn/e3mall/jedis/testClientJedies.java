package cn.e3mall.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClient;

public class testClientJedies {
	@Test
	public void testClientJedis() throws Exception{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		jedisClient.set("test1", "jedisClient");
		String string = jedisClient.get("test1");
		System.out.println(string);
	}
}
