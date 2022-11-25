package com.hengdao.hs.rabbit.spring.boot.starter.messages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试消息体
 *
 * @author Verite
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStruct implements Serializable {
	private static final long serialVersionUID = 392365881428311040L;

	private String message;

	private String id;
}
