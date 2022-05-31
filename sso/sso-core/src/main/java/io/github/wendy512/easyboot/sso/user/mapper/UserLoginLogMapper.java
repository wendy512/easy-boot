package io.github.wendy512.easyboot.sso.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.wendy512.easyboot.sso.user.entity.UserLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户登录日志表 Mapper 接口
 * </p>
 *
 * @author taowenwu
 * @since 2021-04-18
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {

}
