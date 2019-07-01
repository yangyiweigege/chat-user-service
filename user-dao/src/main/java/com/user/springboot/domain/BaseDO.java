package com.user.springboot.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础属性 包括ID 时间 名字
 */
@Setter
@Getter
public class BaseDO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建人ID
     */
    private Long creatorUid;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 修改人ID
     */
    private Long ModifierUid;

    /**
     *  创建时间
     */
    private Long gmtCreated;

    /**
     * 修改时间
     */
    private Long GmtModified;

    /**
     *
     */
    private String IsDeleted;
}
