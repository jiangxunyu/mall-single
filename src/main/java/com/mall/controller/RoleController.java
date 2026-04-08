package com.mall.controller;

import com.mall.po.dto.RolePermissionDTO;
import com.mall.po.entity.Role;
import com.mall.po.vo.Result;
import com.mall.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADD')")
    public Result addRole(@RequestBody Role role) {
        roleService.addRole(role);
        return Result.success("新增成功");
    }

    @PostMapping("/assignPermission")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PERMISSION')")
    public Result assignPermission(@RequestBody RolePermissionDTO dto) {
        roleService.assignPermission(dto);
        return Result.success("分配成功");
    }
}
