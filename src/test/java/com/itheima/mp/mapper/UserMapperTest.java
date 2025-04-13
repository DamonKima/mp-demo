package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import com.itheima.mp.service.IAddressService;
import com.itheima.mp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserMapperTest {

//    @Autowired
//    private UserMapper userMapper;
    
    private final UserMapper userMapper;

//    @Autowired
//    private IAddressService addressService;

    private final IAddressService addressService;

    private final IUserService userService;
    
    @Test
    void testInsert() {
        User user = new User();
//        user.setId(5L);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(24);
        userInfo.setIntro("英语老师");
        userInfo.setGender("female");
        user.setUsername("Lucy2");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
//        user.setInfo(userInfo);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
//        userMapper.saveUser(user);
    }

    @Test
    void testQuery() {
        User user = userMapper.queryById(5L);
        System.out.println("user = " + user);
    }

    @Test
    void testQueryWrapper() {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .select("id", "username", "info", "balance")
                .like("username", "o")
                .ge("balance", 1000);
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateByQueryWrapper() {
        QueryWrapper<User> eq = new QueryWrapper<User>()
                .eq("username", "Lucy2");
        User user = new User();
        user.setBalance(2000);
        userMapper.update(user, eq);
    }

    @Test
    void testUpdateWrapper() {
        List<Long> ids = List.of(1L, 2L, 5L);
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("balance = balance - 200")
                .in("id", ids);
        userMapper.update(null, wrapper);
    }

    @Test
    void testLambdaQueryWrapper() {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda()
                .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
                .like(User::getUsername, "o")
                .ge(User::getBalance, "1000");
        userMapper.selectList(wrapper).forEach(System.out::println);
    }

    @Test
    void testCustomWrapper() {
        List<Long> ids = List.of(1L, 2L, 5L);
        QueryWrapper<User> wrapper = new QueryWrapper<User>().in("id", ids);
        userMapper.deductBalanceByIds(200, wrapper);
    }

    @Test
    void testSelectById() {
        User user = userMapper.queryUserById(5L);
        System.out.println("user = " + user);
    }

    @Test
    void testCustomJinWrapper() {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .in("u.id", List.of(1L, 5L))
                .eq("a.city", "北京");
        List<User> users = userMapper.queryUserByWrapper(wrapper);
        users.forEach(System.out::println);
    }

    @Test
    void testQueryByIds() {
        List<User> users = userMapper.queryUserByIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        userMapper.updateUser(user);
    }

    @Test
    void testDeleteUser() {
        userMapper.deleteUser(5L);
    }

    @Test
    void testDbGet() {
        System.out.println("Db.getById(1L,User.class) = " + Db.getById(1L, User.class));
    }

    @Test
    void testDbList() {
        List<User> list = Db.lambdaQuery(User.class)
                .like(User::getUsername, "o")
                .ge(User::getBalance, 1000)
                .list();
        System.out.println(list);
    }

    @Test
    void testUpdate() {
        Db.lambdaUpdate(User.class)
                .set(User::getBalance, 99999)
                .eq(User::getId, 1L);
    }

    @Test
    void testDeleteByLogic() {
        addressService.removeById(70L);
    }

    @Test
    void testQuery1() {
        List<Address> list = addressService.list();
        list.forEach(System.out::println);
    }
    
    @Test
    void testQuery2(){
        List<User> list = userService.list();
        list.forEach(System.out::println);
    }
    
    @Test
    void testPageQuery(){
        Page<User> page = Page.of(2, 2);
        page.addOrder(new OrderItem("balance",false));
        userService.page(page);
        // 总条数
        System.out.println("page.getTotal() = " + page.getTotal());
        // 总页数
        System.out.println("page.getPages() = " + page.getPages());
        // 数据
        List<User> records = page.getRecords();
        records.forEach(System.out::println);
    }
}