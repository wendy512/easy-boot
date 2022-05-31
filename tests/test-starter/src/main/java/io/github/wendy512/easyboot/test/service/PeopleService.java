package io.github.wendy512.easyboot.test.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.wendy512.easyboot.test.dao.People;
import io.github.wendy512.easyboot.test.dao.PeopleMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author taowenwu
 * @since 2022-03-11
 */
@Service
public class PeopleService extends ServiceImpl<PeopleMapper, People> {

}
