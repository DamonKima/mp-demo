package com.itheima.mp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户状态
 */
@Getter
public enum UserStatus {
    // 定义一个枚举类型，表示状态
    NORMAL(1, "正常"),
    // 定义一个枚举类型，包含两个属性：code和desc
    FROZEN(2, "冻结");

    @EnumValue
    private final int value;
    @JsonValue
    private final String desc;

    UserStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}