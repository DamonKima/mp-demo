package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "用户管理接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {
    private final IUserService userService;

    @PostMapping
    @ApiOperation("新增用户")
    public void saveUser(@RequestBody UserFormDTO userFormDTO) {
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        user.setUpdateTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        userService.save(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public void removeUserById(@PathVariable("id") Long id) {
        userService.removeById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户")
    public UserVO queryUserById(@PathVariable("id") Long id) {
//        return BeanUtil.copyProperties(userService.getById(id), UserVO.class);
        // 基于自定义service方法查询
        return userService.queryUserAndAddressById(id);
    }

    @GetMapping
    @ApiOperation("根据id集合查询用户并且查询出所有对应地址")
    public List<UserVO> queryUserByIds(@RequestParam("ids") List<Long> ids) {
//        List<User> users = userService.listByIds(ids);
//        return BeanUtil.copyToList(users, UserVO.class);
        return userService.listUserAndAddressByIds(ids);
    }

    @PutMapping("/{id}/deduction/{money}")
    @ApiOperation("扣减用户余额")
    public void deductBalance(@PathVariable("id") Long id, @PathVariable("money") Integer money) {
        userService.deductBalance(id, money);
    }

    @GetMapping("/list")
    @ApiOperation("根据条件查询用户")
    public List<UserVO> queryUsers(UserQuery query) {
        // 1. 组织条件
        String username = query.getName();
        Integer status = query.getStatus();
        Integer minBalance = query.getMinBalance();
        Integer maxBalance = query.getMaxBalance();

        List<User> list = userService.lambdaQuery()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
        // 2. 查询用户
//        List<User> list = userService.list(wrapper);
        // 3. 处理vo
        return BeanUtil.copyToList(list, UserVO.class);
    }
    
    @GetMapping("/page")
    public PageDTO<UserVO>queryUsersPage(UserQuery query){
        return userService.queryUsersPage(query);
    }    
}