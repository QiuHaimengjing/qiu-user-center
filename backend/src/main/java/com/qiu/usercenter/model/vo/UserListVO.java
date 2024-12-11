package com.qiu.usercenter.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "脱敏的用户列表")
public class UserListVO implements Serializable {

    private static final long serialVersionUID = 1684434362158138107L;

    @ApiModelProperty(value = "用户列表")
    private List<UserVO> users;

    @ApiModelProperty(value = "分页总记录数")
    private Long total;
    
}
