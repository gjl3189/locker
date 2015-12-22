package com.cyou.cma.clockscreen.event;

/**
 * 邮箱设置完成 事件
 * 
 * @author jiangbin
 * 
 */
public class MailboxEvent {
	/**
	 * 是否发送成功
	 */
	public boolean successful;

	/**
	 * 邮箱
	 */
	public String mailbox;

	public MailboxEvent(boolean successful, String mailbox) {
		this.successful = successful;
		this.mailbox = mailbox;
	}
}
