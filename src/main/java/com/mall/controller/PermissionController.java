package com.mall.controller;

import com.mall.po.entity.Permission;
import com.mall.po.vo.Result;
import com.mall.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String keyword) {
        return Result.success(permissionService.listPermissions(keyword, pageNum, pageSize));
    }
}
