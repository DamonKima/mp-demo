package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * 扣减用户余额
     *
     * @param id
     * @param money
     */
    public void deductBalance(Long id, Integer money) {
        //1. 查询用户
        User user = baseMapper.queryById(id);
        //2. 判断用户状态
        if (user == null || user.getStatus() == UserStatus.NORMAL) {
            throw new RuntimeException("用户状态异常");
        }
        //3. 判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        //4. 扣减余额
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getBalance, 2)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance())// 乐观锁
                .update();
//        baseMapper.deductMoneyById(id, money);// 使用service自带的流处理更新操作
    }

    public UserVO queryUserAndAddressById(Long id) {
        User user = getById(id);
        List<Address> addressList = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, id)
                .list();
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setAddress(BeanUtil.copyToList(addressList, AddressVO.class));
        return userVO;
    }

    public List<UserVO> listUserAndAddressByIds(List<Long> ids) {
        List<User> userList = lambdaQuery()
                .in(User::getId, ids)
                .list();
        return userList.stream().map(user -> {
            List<Address> addressList = Db.lambdaQuery(Address.class)
                    .eq(Address::getUserId, user.getId())
                    .list();
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            userVO.setAddress(BeanUtil.copyToList(addressList, AddressVO.class));
            return userVO;
        }).collect(Collectors.toList());
    }

    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        // 1. 构建条件
        // 1.1 分页条件
//        Page<User> page = Page.of(query.getPageNo(), query.getPageSize());
        Page<User> page = query.toMpPage();
        // 1.2 排序条件
        if (query.getSortBy() != null) {
            page.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
        } else {
            // 默认按照更新时间排序
            page.addOrder(new OrderItem("update_time", false));
        }
        // 2. 查询
        page(page);
        // 3. 数据非空校验
        List<User> records = page.getRecords();
        if (records == null || records.size() <= 0) {
            // 无数据，返回空结果
            return new PageDTO<>(page.getTotal(), page.getPages(), Collections.emptyList());
        }
        // 4. 有数据，转换
        List<UserVO> list = BeanUtil.copyToList(records, UserVO.class);
        return new PageDTO<>(page.getTotal(), page.getPages(), list);
    }
}