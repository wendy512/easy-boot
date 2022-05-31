package io.github.wendy512.easyboot.test.dao;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author taowenwu
 * @since 2022-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_people")
public class People implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @NotNull
    private Integer id;

    @TableField("first_name")
    private String firstName;

    @TableField("last_name")
    private String lastName;

    @TableField("age")
    private Integer age;

    @TableField("address")
    private String address;

    @TableField("create_time")
    private Date createTime;
}
