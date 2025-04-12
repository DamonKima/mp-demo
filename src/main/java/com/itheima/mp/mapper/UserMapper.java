package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.logging.log4j.util.Constants;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

//    void saveUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    User queryUserById(@Param("id") Long id);

    List<User> queryUserByIds(@Param("ids") List<Long> ids);

    User queryById(long id);

    @Select("update user set balance = balance - #{money} ${ew.customSqlSegment}")
    void deductBalanceByIds(int money, @Param("ew") QueryWrapper<User> wrapper);

    @Select("select u.* from user u inner join address a on u.id = a.user_id ${ew.customSqlSegment}")
    List<User> queryUserByWrapper(@Param("ew") QueryWrapper<User> wrapper);

    @Update("update user set balance = balance - #{money} where id = #{id}")
    void deductMoneyById(Long id, Integer money);
}