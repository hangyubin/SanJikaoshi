package com.smartexam.backend.controller;

import com.smartexam.backend.entity.Menu;
import com.smartexam.backend.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    // 获取所有菜单
    @GetMapping
    public ResponseEntity<?> getAllMenus() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuRepository.findAll();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据ID获取菜单
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Menu> optionalMenu = menuRepository.findById(id);
            if (optionalMenu.isPresent()) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", optionalMenu.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "菜单不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据父菜单ID获取子菜单列表
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getMenusByParentId(@PathVariable Long parentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuRepository.findByParentId(parentId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据状态获取菜单列表
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getMenusByStatus(@PathVariable Integer status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuRepository.findByStatus(status);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 根据角色ID获取菜单列表
    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> getMenusByRoleId(@PathVariable Long roleId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里需要根据实际的角色菜单关联关系来实现
            // 假设存在RoleMenuRepository或者类似的关联表
            // List<Menu> menus = roleMenuRepository.findByRoleId(roleId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", menuRepository.findAll()); // 临时返回所有菜单
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 创建菜单
    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody Menu menu) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置默认值
            if (menu.getCreateTime() == null) {
                menu.setCreateTime(System.currentTimeMillis());
            }
            if (menu.getUpdateTime() == null) {
                menu.setUpdateTime(System.currentTimeMillis());
            }
            if (menu.getStatus() == null) {
                menu.setStatus(1); // 默认启用
            }
            // 顶级菜单没有父菜单
            menu.setParent(null);
            if (menu.getSort() == null) {
                menu.setSort(0); // 默认排序
            }
            
            Menu savedMenu = menuRepository.save(menu);
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", savedMenu);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 更新菜单
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Menu> optionalMenu = menuRepository.findById(id);
            if (optionalMenu.isPresent()) {
                Menu existingMenu = optionalMenu.get();
                
                // 更新菜单信息
                existingMenu.setName(menu.getName());
                existingMenu.setPath(menu.getPath());
                existingMenu.setComponent(menu.getComponent());
                existingMenu.setIcon(menu.getIcon());
                existingMenu.setParent(menu.getParent());
                existingMenu.setSort(menu.getSort());
                existingMenu.setStatus(menu.getStatus());
                existingMenu.setPermissions(menu.getPermissions());
                existingMenu.setUpdateTime(System.currentTimeMillis());
                
                Menu updatedMenu = menuRepository.save(existingMenu);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedMenu);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "菜单不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 删除菜单
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Menu> optionalMenu = menuRepository.findById(id);
            if (optionalMenu.isPresent()) {
                // 检查是否有子菜单
                List<Menu> subMenus = menuRepository.findByParentId(id);
                if (!subMenus.isEmpty()) {
                    response.put("code", 400);
                    response.put("message", "该菜单下存在子菜单，无法删除");
                    return ResponseEntity.ok(response);
                }
                
                menuRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "菜单不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // 批量删除菜单
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteMenus(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查是否有菜单包含子菜单
            for (Long id : ids) {
                List<Menu> subMenus = menuRepository.findByParentId(id);
                if (!subMenus.isEmpty()) {
                    response.put("code", 400);
                    response.put("message", "部分菜单下存在子菜单，无法删除");
                    return ResponseEntity.ok(response);
                }
            }
            
            menuRepository.deleteAllById(ids);
            response.put("code", 200);
            response.put("message", "批量删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}


