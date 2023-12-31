/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.4 TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * IMClientManager.java at 2023-9-22 11:55:07, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.conf.ConfigEntity;
import net.x52im.mobileimsdk.java.conf.ConfigEntity.SenseMode;
import net.x52im.mobileimsdk.java.core.LocalSocketProvider;
import net.x52im.mobileimsdk.java.demo.event.ChatBaseEventImpl;
import net.x52im.mobileimsdk.java.demo.event.ChatMessageEventImpl;
import net.x52im.mobileimsdk.java.demo.event.MessageQoSEventImpl;
import net.x52im.mobileimsdk.java.utils.Log;

/**
 * MobileIMSDK的管理类。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
public class IMClientManager
{
	private static String TAG = IMClientManager.class.getSimpleName();
	
	private static IMClientManager instance = null;
	
	/** MobileIMSDK是否已被初始化. true表示已初化完成，否则未初始化. */
	private boolean init = false;
	
	/** 基本连接状态事件监听器 */
	private ChatBaseEventImpl baseEventListener = null;
	/** 数据接收事件监听器 */
	private ChatMessageEventImpl transDataListener = null;
	/** 消息送达保证事件监听器 */
	private MessageQoSEventImpl messageQoSListener = null;

	public static IMClientManager getInstance()
	{
		if(instance == null)
			instance = new IMClientManager();
		return instance;
	}
	
	private IMClientManager()
	{
		initMobileIMSDK();
	}

	/**
	 * MobileIMSDK的初始化方法。
	 */
	public void initMobileIMSDK()
	{
		if(!init)
		{
		
			// 设置服务器ip和服务器端口
//			ConfigEntity.serverIP = "192.168.82.138";
//			ConfigEntity.serverIP = "rbcore.openmob.net";
//			ConfigEntity.serverUDPPort = 7901;
	    
			// MobileIMSDK核心IM框架的敏感度模式设置
			ConfigEntity.setSenseMode(SenseMode.MODE_5S);
			
			// 设置最大TCP帧内容长度（不设置则默认最大是 6 * 1024字节）
//			LocalSocketProvider.TCP_FRAME_MAX_BODY_LENGTH = 60 * 1024;
	    
			// 开启/关闭DEBUG信息输出
	    	ClientCoreSDK.DEBUG = true;
	    	
	    	// 开启SSL/TLS加密传输（请确保服务端也已开启SSL）
//	    	LocalSocketProvider.sslContext = createSslContext();
	    
			// 设置事件回调
			baseEventListener = new ChatBaseEventImpl();
			transDataListener = new ChatMessageEventImpl();
			messageQoSListener = new MessageQoSEventImpl();
			ClientCoreSDK.getInstance().setChatBaseEvent(baseEventListener);
			ClientCoreSDK.getInstance().setChatMessageEvent(transDataListener);
			ClientCoreSDK.getInstance().setMessageQoSEvent(messageQoSListener);
			
			init = true;
		}
	}
	
	/**
	 * 创建SslContext对象，用于开启SSL/TLS加密传输。
	 * 
	 * @return 如果成功创建则返回SslContext对象，否则返回null
	 */
	public SslContext createSslContext() 
	{
		SslContext sslContext = null;
		try {
			sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			Log.d(TAG, "【IMCORE-TCP】已开启SSL/TLS加密(单向认证)，且sslContext创建成功。");
		} catch (Exception e) {
			Log.w(TAG, "【IMCORE-TCP】创建sslContext时出错，原因是：" + e.getMessage(), e);
		}
		
		return sslContext;
	}

	/**
	 * MobileIMSDK的资源释放方法（退出SDK时使用）。
	 */
	public void release()
	{
		ClientCoreSDK.getInstance().release();
		resetInitFlag();
	}

	/**
	 * 重置init标识。
	 * <p>
	 * <b>重要说明：</b>不退出APP的情况下，重新登陆时记得调用一下本方法，不然再
	 * 次调用 {@link #initMobileIMSDK()} 时也不会重新初始化MobileIMSDK（
	 * 详见 {@link #initMobileIMSDK()}代码）而报 code=203错误！
	 * 
	 */
	public void resetInitFlag()
	{
		init = false;
	}

	public ChatMessageEventImpl getChatMessageListener()
	{
		return transDataListener;
	}
	public ChatBaseEventImpl getBaseEventListener()
	{
		return baseEventListener;
	}
	public MessageQoSEventImpl getMessageQoSListener()
	{
		return messageQoSListener;
	}
}
