package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.vo.UserVO;

import java.util.List;

public interface IUserService extends IService<User> {

    /**
     * 扣减用户余额
     * @param id
     * @param money
     */
    void deductBalance(Long id, Integer money);

    UserVO queryUserAndAddressById(Long id);

    List<UserVO> listUserAndAddressByIds(List<Long> ids);
}