package io.github.wendy512.easyboot.sso.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.wendy512.easyboot.sso.user.entity.User;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author taowenwu
 * @since 2021-04-08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
