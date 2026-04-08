package com.mall.controller;

import com.mall.po.vo.Result;
import com.mall.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Permission;
import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('PERMISSION_ADD')")
    public Result addPermission(@RequestBody Permission p) {
        permissionService.addPermission(p);
        return Result.success("新增成功");
    }

    @GetMapping("/list")
    public List<Permission> list() {
        return permissionService.list();
    }
}
