package com.cloudstorage.mappers;

import com.cloudstorage.models.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserMapper {
    @Insert("INSERT INTO USERS (username, salt, password, firstName, lastName) VALUES (#{username}, #{salt}, #{password}, #{firstName}, #{lastName})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    public int insert(User user);

    @Select("SELECT * FROM USERS WHERE username = #{username}")
    public User getUser(String username);

    @Select("SELECT * FORM USERS")
    public List<User> getUsers();
}
