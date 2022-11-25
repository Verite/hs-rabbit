package com.hengdao.hs.rabbit.spring.boot.starter.service.dto;

import lombok.Data;

/**
 * @author Verite
 * @date
 * @apiNote
 */
@Data
public class CustomerDTO {
    /**
     * 用户id
     */
    Integer id;
    /**
     * 用户名称
     */
    String name;
}
